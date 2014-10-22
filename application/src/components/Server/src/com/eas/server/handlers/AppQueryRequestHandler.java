/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.server.SessionRequestHandler;
import com.eas.client.AppElementFiles;
import com.eas.client.SqlQuery;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.security.AccessControlException;
import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class AppQueryRequestHandler extends SessionRequestHandler<AppQueryRequest, AppQueryRequest.Response> {

    public static final String ACCESS_DENIED_MSG = "Access denied to query  %s for user %s";
    public static final String MISSING_QUERY_MSG = "Query %s not found neither in application database, nor in hand-constructed queries.";
    private static final String PUBLIC_ACCESS_DENIED_MSG = "Public access to query %s is denied.";

    public AppQueryRequestHandler(PlatypusServerCore aServerCore, AppQueryRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<AppQueryRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            ((LocalQueriesProxy) getServerCore().getQueries()).getQuery(getRequest().getQueryName(), (SqlQuery query) -> {
                try {
                    if (query == null || query.getEntityId() == null) {
                        throw new Exception(String.format(MISSING_QUERY_MSG, getRequest().getQueryName()));
                    }
                    if (!query.isPublicAccess()) {
                        throw new AccessControlException(String.format(PUBLIC_ACCESS_DENIED_MSG, getRequest().getQueryName()));//NOI18N
                    }
                    Set<String> rolesAllowed = query.getReadRoles();
                    if (rolesAllowed != null && !aSession.getPrincipal().hasAnyRole(rolesAllowed)) {
                        throw new AccessControlException(String.format(ACCESS_DENIED_MSG, query.getEntityId(), aSession.getPrincipal().getName()));
                    }
                    assert query.getEntityId().equals(getRequest().getQueryName());
                    /**
                     * this code is moved to stored query factory in order to
                     * code abstraction SqlDriver driver =
                     * getServerCore().getDatabasesClient().getDbMetadataCache(query.getDbId()).getConnectionDriver();
                     * Fields queryFields = query.getFields(); if (queryFields
                     * != null) { for (Field field : queryFields.toCollection())
                     * { driver.getTypesResolver().resolve2Application(field); }
                     * }
                     */
                    if (onSuccess != null) {
                        AppQueryRequest.Response resp = new AppQueryRequest.Response(null, null);
                        AppElementFiles files = getServerCore().getIndexer().nameToFiles(getRequest().getQueryName());
                        Date serverQueryTime = files.getLastModified();
                        Date clientQueryTime = getRequest().getTimeStamp();
                        if (clientQueryTime == null || serverQueryTime.after(clientQueryTime)) {
                            PlatypusQuery pQuery = new PlatypusQuery(null);
                            pQuery.setEntityId(query.getEntityId());
                            pQuery.setFields(query.getFields());
                            pQuery.setManual(query.isManual());
                            pQuery.setTitle(query.getTitle());
                            pQuery.setReadRoles(query.getReadRoles());
                            pQuery.setWriteRoles(query.getWriteRoles());
                            query.getParameters().toCollection().stream().forEach((p) -> {
                                pQuery.putParameter(p.getName(), p.getTypeInfo(), ((Parameter) p).getValue());
                            });
                            resp.setAppQuery(pQuery);
                            resp.setTimeStamp(serverQueryTime);
                        }
                        try {
                            onSuccess.accept(resp);
                        } catch (Exception ex) {
                            Logger.getLogger(AppQueryRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            }, onFailure);
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }
}
