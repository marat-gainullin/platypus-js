/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.ClientConstants;
import com.eas.client.cache.FilesAppCache;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.store.Dom2ScriptDocument;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.security.AccessControlException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class AppElementRequestHandler extends SessionRequestHandler<AppElementRequest, AppElementRequest.Response> {

    public static final String ACCESS_DENIED_MSG = "Access denied to application element '%s' [ %s ] for user %s";

    public AppElementRequestHandler(PlatypusServerCore aServerCore, AppElementRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(final Session aSession, Consumer<AppElementRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            Consumer<ApplicationElement> doWork = (ApplicationElement appElement) -> {
                if (appElement != null) {
                    try {
                        appElement = checkResourceKindAndRoles(aSession, appElement);
                    } catch (Exception ex) {
                        Logger.getLogger(AppElementRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        if (onFailure != null) {
                            onFailure.accept(ex);
                        }
                    }
                }
                if (onSuccess != null) {
                    onSuccess.accept(new AppElementRequest.Response(appElement));
                }
            };
            if (getServerCore().getDatabasesClient().getAppCache() instanceof FilesAppCache) {
                FilesAppCache filesCache = (FilesAppCache) getServerCore().getDatabasesClient().getAppCache();
                filesCache.get(getRequest().getAppElementId(), doWork, onFailure);
            } else {
                SqlQuery query = new SqlQuery(getServerCore().getDatabasesClient(), ClientConstants.SQL_SELECT_MD);
                query.putParameter(ClientConstants.APP_ELEMENT_SQL_PARAM_NAME, DataTypeInfo.VARCHAR, getRequest().getAppElementId());
                SqlCompiledQuery compiledQuery = query.compile();
                compiledQuery.executeQuery((Rowset rowset) -> {
                    if (rowset != null && !rowset.isEmpty()) {
                        try {
                            rowset.first();
                            doWork.accept(ApplicationElement.read(rowset));
                        } catch (Exception ex) {
                            Logger.getLogger(AppElementRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                            if (onFailure != null) {
                                onFailure.accept(ex);
                            }
                        }
                    }
                }, onFailure);
            }
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }

    protected ApplicationElement checkResourceKindAndRoles(Session aSession, ApplicationElement appElement) throws Exception {
        if (ClientConstants.ET_COMPONENT == appElement.getType()
                || ClientConstants.ET_REPORT == appElement.getType()
                || ClientConstants.ET_FORM == appElement.getType()
                || ClientConstants.ET_RESOURCE == appElement.getType()) {
            checkResourceRoles(aSession, appElement);
        } else {
            // We disallow access of any three-tier client to application
            // design data like folders, queries content or database diagrams.
            // || ClientConstants.ET_DB_SCHEME == appElement.getType()
            // || ClientConstants.ET_FOLDER == appElement.getType()
            // || ClientConstants.ET_OLD_FORM == appElement.getType()
            // || ClientConstants.ET_RECYCLE == appElement.getType()
            // Also, three-tier client doesn't need any query application element content.
            // || ClientConstants.ET_QUERY == appElement.getType()
            appElement = null;
        }
        return appElement;
    }

    private void checkResourceRoles(Session aSession, ApplicationElement aAppElement) throws Exception {
        Set<String> rolesAllowed = null;
        if (aAppElement.getType() == ClientConstants.ET_RESOURCE) {
            if (aAppElement.getName() != null && (aAppElement.getName().endsWith(".js") || aAppElement.getName().endsWith(".json"))) {
                ScriptDocument scriptDoc = new ScriptDocument(new String(aAppElement.getBinaryContent(), SettingsConstants.COMMON_ENCODING));
                rolesAllowed = scriptDoc.getModuleAllowedRoles();
            }
        } else {
            Document doc = aAppElement.getContent();
            ScriptDocument scriptDoc = Dom2ScriptDocument.transform(doc);
            rolesAllowed = scriptDoc.getModuleAllowedRoles();
        }
        if (rolesAllowed != null && !aSession.getPrincipal().hasAnyRole(rolesAllowed)) {
            throw new AccessControlException(String.format(ACCESS_DENIED_MSG, aAppElement.getName(), aAppElement.getId(), aSession.getPrincipal().getName()));
        }
    }
}
