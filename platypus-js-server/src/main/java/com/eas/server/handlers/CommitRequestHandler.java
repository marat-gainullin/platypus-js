package com.eas.server.handlers;

import com.eas.server.RequestHandler;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.changes.Change;
import com.eas.client.changes.CommandRequest;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.threetier.json.ChangesJSONReader;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.script.Scripts;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.AuthPermission;

/**
 *
 * @author mg
 */
public class CommitRequestHandler extends RequestHandler<CommitRequest, CommitRequest.Response> {

    protected static class ChangesSortProcess {

        private final List<Change.Applicable> expectedChanges = new ArrayList<>();
        private final String defaultDatasource;
        private int factCalls;
        private final Consumer<Map<String, List<Change.Applicable>>> onSuccess;
        private final Consumer<Exception> onFailure;

        private final List<AccessControlException> accessDeniedEntities = new ArrayList<>();
        private final List<Exception> notRetrievedEntities = new ArrayList<>();
        private final Map<String, String> datasourcesOfEntities = new HashMap();

        public ChangesSortProcess(String aDefaultDatasource, Consumer<Map<String, List<Change.Applicable>>> aOnSuccess, Consumer<Exception> aOnFailure) {
            super();
            defaultDatasource = aDefaultDatasource;
            onSuccess = aOnSuccess;
            onFailure = aOnFailure;
        }

        public void datasourceDescovered(String aEntityName, String aDatasourceName) {
            datasourcesOfEntities.put(aEntityName, aDatasourceName);
        }

        protected String assembleErrors() {
            if (!notRetrievedEntities.isEmpty() || !accessDeniedEntities.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                Consumer<Exception> appender = (ex) -> {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(ex.getMessage());
                };
                accessDeniedEntities.stream().forEach(appender);
                notRetrievedEntities.stream().forEach(appender);
                return sb.toString();
            } else {
                return "Unknown error";
            }
        }

        public void complete(Change.Applicable aChange, AccessControlException accessDenied, Exception failed) {
            expectedChanges.add(aChange);
            if (accessDenied != null) {
                accessDeniedEntities.add(accessDenied);
            }
            if (failed != null) {
                notRetrievedEntities.add(failed);
            }
            if (++factCalls == expectedChanges.size()) {
                if (accessDeniedEntities.isEmpty() && notRetrievedEntities.isEmpty()) {
                    if (onSuccess != null) {
                        Map<String, List<Change.Applicable>> changeLogs = new HashMap<>();
                        expectedChanges.stream().forEach((Change.Applicable aSortedChange) -> {
                            String datasourceName = datasourcesOfEntities.get(aSortedChange.getEntity());
                            // defaultDatasource is needed here to avoid multi transaction
                            // actions against the same datasource, leading to unexpected
                            // row level locking and deadlocks in two phase transaction commit process
                            if (datasourceName == null || datasourceName.isEmpty()) {
                                datasourceName = defaultDatasource;
                            }
                            List<Change.Applicable> targetChangeLog = changeLogs.get(datasourceName);
                            if (targetChangeLog == null) {
                                targetChangeLog = new ArrayList<>();
                                changeLogs.put(datasourceName, targetChangeLog);
                            }
                            targetChangeLog.add(aSortedChange);
                        });
                        onSuccess.accept(changeLogs);
                    }
                } else {
                    if (onFailure != null) {
                        onFailure.accept(new IllegalStateException(assembleErrors()));
                    }
                }
            }
        }
    }

    public CommitRequestHandler(PlatypusServerCore aServerCore, CommitRequest aRequest) {
        super(aServerCore, aRequest);
    }

    private AccessControlException checkWritePrincipalPermission(PlatypusPrincipal aPrincipal, String aEntityName, Set<String> writeRoles) {
        if (writeRoles != null && !writeRoles.isEmpty()
                && (aPrincipal == null || !aPrincipal.hasAnyRole(writeRoles))) {
            return new AccessControlException(String.format("Access denied for write (entity: %s) for '%s'.", aEntityName != null ? aEntityName : "", aPrincipal != null ? aPrincipal.getName() : null), aPrincipal instanceof AnonymousPlatypusPrincipal ? new AuthPermission("*") : null);
        } else {
            return null;
        }
    }

    @Override
    public void handle(Session aSession, Consumer<CommitRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            List<Change.Transferable> changes = ChangesJSONReader.read(getRequest().getChangesJson(), Scripts.getSpace());
            DatabasesClient client = getServerCore().getDatabasesClient();
            Map<String, SqlCompiledQuery> compiledEntities = new HashMap<>();

            ChangesSortProcess process = new ChangesSortProcess(client.getDefaultDatasourceName(), (Map<String, List<Change.Applicable>> changeLogs) -> {
                try {
                    client.commit(changeLogs, (Integer aUpdated) -> {
                        if (onSuccess != null) {
                            onSuccess.accept(new CommitRequest.Response(aUpdated));
                        }
                    }, onFailure);
                } catch (Exception ex) {
                    Logger.getLogger(CommitRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }, onFailure);
            if (changes.isEmpty()) {
                if (onSuccess != null) {
                    onSuccess.accept(new CommitRequest.Response(0));
                }
            } else {
                changes.stream().forEach((change) -> {
                    try {
                        ((LocalQueriesProxy) serverCore.getQueries()).getQuery(change.getEntity(), Scripts.getSpace(), (SqlQuery query) -> {
                            if (query != null) {
                                process.datasourceDescovered(change.getEntity(), query.getDatasourceName());
                                if (query.isPublicAccess()) {
                                    AccessControlException accessControlEx = checkWritePrincipalPermission((PlatypusPrincipal) Scripts.getContext().getPrincipal(), change.getEntity(), query.getWriteRoles());
                                    if (accessControlEx != null) {
                                        process.complete(null, accessControlEx, null);
                                    } else {
                                        try {
                                            if (change instanceof CommandRequest) {
                                                CommandRequest commandRequest = (CommandRequest)change;
                                                SqlCompiledQuery compiled = compiledEntities.computeIfAbsent(change.getEntity(), en -> {
                                                    try {
                                                        return query.compile();
                                                    } catch (Exception ex) {
                                                        throw new IllegalStateException(ex);
                                                    }
                                                });
                                                process.complete(compiled.prepareCommand(commandRequest.getParameters()), null, null);
                                            } else {
                                                process.complete((Change.Applicable)change, null, null);
                                            }
                                        } catch (Exception ex) {
                                            Logger.getLogger(CommitRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                                            process.complete(null, null, ex);
                                        }
                                    }
                                } else {
                                    process.complete(null, new AccessControlException(String.format("Public access to entity '%s' is denied while commiting changes for it.", change.getEntity())), null);
                                }
                            } else {
                                process.complete(null, null, new IllegalArgumentException(String.format("Entity '%s' is not found", change.getEntity())));
                            }
                        }, (Exception ex) -> {
                            process.complete(null, null, ex);
                        });
                    } catch (Exception ex) {
                        Logger.getLogger(CommitRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        } catch (Exception ex) {
            onFailure.accept(ex);
        }
    }

}
