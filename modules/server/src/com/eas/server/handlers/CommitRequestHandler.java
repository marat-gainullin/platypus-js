/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.RequestHandler;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.changes.Change;
import com.eas.client.changes.Command;
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
 * @author pk, mg refactoring
 */
public class CommitRequestHandler extends RequestHandler<CommitRequest, CommitRequest.Response> {

    protected static class ChangesSortProcess {

        private final List<Change> expectedChanges;
        private final String defaultDatasource;
        private int factCalls;
        private final Consumer<Map<String, List<Change>>> onSuccess;
        private final Consumer<Exception> onFailure;

        private final Map<String, SqlCompiledQuery> entities = new HashMap<>();
        private final List<AccessControlException> accessDeniedEntities = new ArrayList<>();
        private final List<Exception> notRetrievedEntities = new ArrayList<>();

        public ChangesSortProcess(List<Change> aChanges, String aDefaultDatasource, Consumer<Map<String, List<Change>>> aOnSuccess, Consumer<Exception> aOnFailure) {
            super();
            expectedChanges = aChanges;
            defaultDatasource = aDefaultDatasource;
            onSuccess = aOnSuccess;
            onFailure = aOnFailure;
        }

        protected String assembleErrors() {
            if (accessDeniedEntities != null && !accessDeniedEntities.isEmpty()) {
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
            }
            return null;
        }

        public void complete(Change aChange, SqlQuery aQuery, AccessControlException accessDenied, Exception failed) {
            if (aChange != null && aQuery != null) {
                try {
                    SqlCompiledQuery entity = entities.get(aChange.entityName);
                    if (entity == null) {
                        entity = aQuery.compile();
                        entities.put(aChange.entityName, entity);
                    }
                    if (aChange instanceof Command) {
                        ((Command) aChange).command = entity.getSqlClause();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(CommitRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                    notRetrievedEntities.add(ex);
                }
            }
            if (accessDenied != null) {
                accessDeniedEntities.add(accessDenied);
            }
            if (failed != null) {
                notRetrievedEntities.add(failed);
            }

            if (++factCalls == expectedChanges.size()) {
                if (accessDeniedEntities.isEmpty() && notRetrievedEntities.isEmpty()) {
                    if (onSuccess != null) {
                        Map<String, List<Change>> changeLogs = new HashMap<>();
                        expectedChanges.stream().forEach((Change aSortedChange) -> {
                            SqlCompiledQuery entity = entities.get(aSortedChange.entityName);
                            String datasourceName = entity.getDatasourceName();
                            // defaultDatasource is needed here to avoid multi transaction
                            // actions against the same datasource, leading to unexpected
                            // row-level locking and deadlocks in two phase transaction commit process
                            if (datasourceName == null || datasourceName.isEmpty()) {
                                datasourceName = defaultDatasource;
                            }
                            List<Change> targetChangeLog = changeLogs.get(datasourceName);
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

    @Override
    public void handle(Session aSession, Consumer<CommitRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            DatabasesClient client = getServerCore().getDatabasesClient();
            List<Change> changes = ChangesJSONReader.read(getRequest().getChangesJson(), Scripts.getSpace());
            ChangesSortProcess process = new ChangesSortProcess(changes, client.getDefaultDatasourceName(), (Map<String, List<Change>> changeLogs) -> {
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
                        ((LocalQueriesProxy) serverCore.getQueries()).getQuery(change.entityName, Scripts.getSpace(), (SqlQuery aQuery) -> {
                            if (aQuery.isPublicAccess()) {
                                AccessControlException aex = checkWritePrincipalPermission((PlatypusPrincipal) Scripts.getContext().getPrincipal(), change.entityName, aQuery.getWriteRoles());
                                if (aex != null) {
                                    process.complete(null, null, aex, null);
                                } else {
                                    process.complete(change, aQuery, null, null);
                                }
                            } else {
                                process.complete(null, null, new AccessControlException(String.format("Public access to query %s is denied while commiting changes for it's entity.", change.entityName)), null);
                            }
                        }, (Exception ex) -> {
                            process.complete(null, null, null, ex);
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

    private AccessControlException checkWritePrincipalPermission(PlatypusPrincipal aPrincipal, String aEntityId, Set<String> writeRoles) {
        if (writeRoles != null && !writeRoles.isEmpty()) {
            if (aPrincipal == null || !aPrincipal.hasAnyRole(writeRoles)) {
                return new AccessControlException(String.format("Access denied for write (entity: %s) for '%s'.", aEntityId != null ? aEntityId : "", aPrincipal != null ? aPrincipal.getName() : null), aPrincipal instanceof AnonymousPlatypusPrincipal ? new AuthPermission("*") : null);
            }
        }
        return null;
    }
}
