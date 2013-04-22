/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.ExecuteServerReportRequest;
import com.eas.script.JsDoc;
import com.eas.server.PlatypusServerCore;
import com.eas.server.ServerScriptRunner;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.security.AccessControlException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pk
 */
public class ExecuteServerReportRequestHandler extends SessionRequestHandler<ExecuteServerReportRequest> {

    public static final String EXECUTE_REPORT_MSG = "Executing report {0}";
    public static final String MODEL_SAVE_ERROR_MSG = "While attempting to save model of unactual server report %s";
    public static final String MODULE_MISSING_MSG = "No report %s";
    public static final String RERUN_MSG = "About to re-run server report {0}";

    public ExecuteServerReportRequestHandler(PlatypusServerCore aServerCore, Session aSession, ExecuteServerReportRequest aRequest) {
        super(aServerCore, aSession, aRequest);
    }

    @Override
    public Response handle2() throws Exception {
        Session systemSession = getServerCore().getSessionManager().getSystemSession();
        Session moduleSession = null;
        String moduleName = getRequest().getModuleName();
        ServerScriptRunner runner = getSession().getModule(moduleName);
        if (runner == null) {
            // It's seems client wants background module.
            // Let's try to look up it in system session.
            runner = systemSession.getModule(getRequest().getModuleName());
            if (runner != null) {
                moduleSession = systemSession;
            }
        } else {
            moduleSession = getSession();
        }
        if (runner == null) {
            runner = CreateServerModuleRequestHandler.runModule(getServerCore(), getSession(), moduleName);
            moduleSession = getSession();
        }
        if (runner != null) {
            assert moduleSession != null;
            synchronized (runner) {// Same synchronization object as in module method executing code
                if (getRequest().getArguments() != null) {
                    for (ExecuteServerReportRequest.NamedArgument arg : getRequest().getArguments()) {
                        runner.setValue(arg.getName(), arg.getValue());
                    }
                }
                if (!getServerCore().getDatabasesClient().getAppCache().isActual(runner.getApplicationElementId(), runner.getTxtContentLength(), runner.getTxtCrc32())) {
                    Logger.getLogger(ExecuteServerReportRequest.class.getName()).log(Level.FINE, RERUN_MSG, new Object[]{getRequest().getModuleName()});
                    try {
                        runner.getModel().save();
                    } catch (Exception ex) {
                        Logger.getLogger(ExecuteServerReportRequest.class.getName()).log(Level.SEVERE, String.format(MODEL_SAVE_ERROR_MSG, getRequest().getModuleName()), ex);
                    }
                    runner.refresh();
                }
                if (!runner.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                    throw new AccessControlException(String.format("Public access to report %s is denied.", moduleName));//NOI18N
                }
                Logger.getLogger(ExecuteServerReportRequest.class.getName()).log(Level.FINE, EXECUTE_REPORT_MSG, new Object[]{getRequest().getModuleName()});
                byte[] result = runner.executeReport();
                if (moduleSession != systemSession) {// reports are allways stateless, but system session.
                    moduleSession.unregisterModule(moduleName);
                }
                return new ExecuteServerReportRequest.Response(getRequest().getID(), result);
                //TODO: Передать в параметрах размер файла отчета и тип отчета
            }
        } else {
            throw new Exception(String.format(MODULE_MISSING_MSG, getRequest().getModuleName()));
        }
    }
}
