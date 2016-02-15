/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.RequestHandler;
import com.eas.client.SqlQuery;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.metadata.Parameter;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.threetier.json.QueryJSONWriter;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.script.Scripts;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.AuthPermission;

/**
 *
 * @author mg
 */
public class AppQueryRequestHandler extends RequestHandler<AppQueryRequest, AppQueryRequest.Response> {

    public static final String ACCESS_DENIED_MSG = "Access denied to query  %s for user %s";
    public static final String MISSING_QUERY_MSG = "Query %s not found neither in application database, nor in hand-constructed queries.";
    public static final String PUBLIC_ACCESS_DENIED_MSG = "Public access to query %s is denied.";

    public AppQueryRequestHandler(PlatypusServerCore aServerCore, AppQueryRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Session aSession, Consumer<AppQueryRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            ((LocalQueriesProxy) getServerCore().getQueries()).getQuery(getRequest().getQueryName(), Scripts.getSpace(), (SqlQuery query) -> {
                try {
                    if (query == null || query.getEntityName() == null) {
                        throw new FileNotFoundException(String.format(MISSING_QUERY_MSG, getRequest().getQueryName()));
                    }
                    if (!query.isPublicAccess()) {
                        throw new AccessControlException(String.format(PUBLIC_ACCESS_DENIED_MSG, getRequest().getQueryName()));//NOI18N
                    }
                    Set<String> rolesAllowed = query.getReadRoles();
                    PlatypusPrincipal principal = (PlatypusPrincipal)Scripts.getContext().getPrincipal();
                    if (rolesAllowed != null && !principal.hasAnyRole(rolesAllowed)) {
                        throw new AccessControlException(String.format(ACCESS_DENIED_MSG, query.getEntityName(), principal.getName()), principal instanceof AnonymousPlatypusPrincipal ? new AuthPermission("*") : null);
                    }
                    assert query.getEntityName().equals(getRequest().getQueryName());
                    if (onSuccess != null) {
                        AppQueryRequest.Response resp = new AppQueryRequest.Response(null, null);
                        File file = getServerCore().getIndexer().nameToFile(getRequest().getQueryName());
                        Date serverQueryTime = new Date(file.lastModified());
                        Date clientQueryTime = getRequest().getTimeStamp();
                        if (clientQueryTime == null || serverQueryTime.after(clientQueryTime)) {
                            PlatypusQuery pQuery = new PlatypusQuery(null);
                            pQuery.setEntityName(query.getEntityName());
                            pQuery.setFields(query.getFields());
                            pQuery.setTitle(query.getTitle());
                            pQuery.setReadRoles(query.getReadRoles());
                            pQuery.setWriteRoles(query.getWriteRoles());
                            query.getParameters().toCollection().stream().forEach((p) -> {
                                pQuery.putParameter(p.getName(), p.getType(), ((Parameter) p).getValue());
                            });
                            resp.setAppQueryJson(QueryJSONWriter.write(pQuery));
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
