/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.reports.ReportDocument;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.CreateServerModuleResponse;
import com.eas.script.JsDoc;
import com.eas.server.*;
import java.security.AccessControlException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pk, mg, ab
 */
public class CreateServerModuleRequestHandler extends SessionRequestHandler<CreateServerModuleRequest> {

    public CreateServerModuleRequestHandler(PlatypusServerCore server, Session session, CreateServerModuleRequest rq) {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception {
        String moduleId = getRequest().getModuleName();
        if (moduleId == null || moduleId.isEmpty()) {
            throw new Exception("Module name is missing. Unnamed server modules are not allowed.");
        }
        // Let's check the module is already created
        ServerScriptRunner serverModule = getServerCore().getSessionManager().getSystemSession().getModule(moduleId);
        if (serverModule == null) {
            serverModule = getSession().getModule(moduleId);
        }
        if (serverModule == null) {
            serverModule = runModule(getServerCore(), getSession(), moduleId);
            Logger.getLogger(CreateServerModuleRequestHandler.class.getName()).log(Level.FINE, "Created server module for script {0} with id {1} on request {2}", new Object[]{getRequest().getModuleName(), serverModule.getModuleId(), getRequest().getID()});
        }
        boolean permitted = true;
        try {
            serverModule.checkPrincipalPermission();
        } catch (AccessControlException ex) {
            permitted = false;
        }
        return new CreateServerModuleResponse(getRequest().getID(), serverModule.getModuleId(), serverModule.getFunctionsNames(), serverModule instanceof ServerReportRunner, permitted);
    }

    static ServerScriptRunner runModule(PlatypusServerCore aServerCore, Session aSession, String aModuleId) throws Exception {
        ServerScriptRunner serverModule = null;
        ScriptDocument scriptDoc = aServerCore.getDocuments().compileScriptDocument(aModuleId);
        if (scriptDoc != null) {
            if (scriptDoc instanceof ReportDocument) {
                serverModule = new ServerReportRunner(
                        aServerCore,
                        aSession,
                        aModuleId,
                        ScriptRunner.initializePlatypusStandardLibScope(),
                        aServerCore,
                        aServerCore,
                        new Object[]{});
            } else {
                serverModule = new ServerScriptRunner(
                        aServerCore,
                        aSession,
                        aModuleId,
                        ScriptRunner.initializePlatypusStandardLibScope(),
                        aServerCore,
                        aServerCore,
                        new Object[]{});
            }
        } else {
            throw new IllegalArgumentException(String.format("Can't obtain content of %s", aModuleId));
        }
        if (!serverModule.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
            throw new AccessControlException(String.format("Public access to module %s is denied.", aModuleId));//NOI18N
        }
        if (serverModule.getModel() instanceof ApplicationDbModel) {
            ((ApplicationDbModel) serverModule.getModel()).setSessionId(aSession.getId());
        }
        serverModule.execute();

        return serverModule;
    }
}
