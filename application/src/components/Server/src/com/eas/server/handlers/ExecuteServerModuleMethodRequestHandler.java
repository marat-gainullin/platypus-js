/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.scripts.SecuredJSConstructor;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.security.AccessControlException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author pk
 */
public class ExecuteServerModuleMethodRequestHandler extends SessionRequestHandler<ExecuteServerModuleMethodRequest> {

    public static final String EXECUTING_METHOD_TRACE_MSG = "Executing method {0} of module {1}";
    public static final String MODEL_SAVE_ERROR_MSG = "While attempting to save model of unactual server module %s";
    public static final String MODULE_MISSING_MSG = "No module %s";
    public static final String METHOD_MISSING_MSG = "No method %s in module %s";
    public static final String RERUN_MSG = "About to re-run server module {0}";
    /**
     * There are cases when a request needs to be complemented by temporarily
     * environment. Such as http request/response facades of servlet container
     */
    private ExecuteEventsCallback executeCallback;

    public ExecuteServerModuleMethodRequestHandler(PlatypusServerCore aServerCore, Session aSession, ExecuteServerModuleMethodRequest aRequest) {
        super(aServerCore, aSession, aRequest);
    }

    public void setExecuteEventsCallback(ExecuteEventsCallback anexecuteCallback) {
        executeCallback = anexecuteCallback;
    }

    @Override
    public Response handle2() throws Exception {
        Session systemSession = getServerCore().getSessionManager().getSystemSession();
        Session moduleSession = null;
        String moduleName = getRequest().getModuleName();
        JSObject runner = getSession().getModule(moduleName);
        if (runner == null) {
            // It's seems client wants a background module.
            // Let's try to look up it in system session.
            runner = systemSession.getModule(getRequest().getModuleName());
            if (runner != null) {
                moduleSession = systemSession;
            }
        } else {
            moduleSession = getSession();
        }
        if (runner == null) {
            runner = CreateServerModuleRequestHandler.runModule(getServerCore().getDatabasesClient().getAppCache(), moduleName);
            moduleSession = getSession();
        }
        if (runner != null) {
            assert moduleSession != null;
            JSObject jsConstr = ScriptUtils.lookupInGlobal(moduleName);
            assert jsConstr instanceof SecuredJSConstructor;
            SecuredJSConstructor sjsConstr = (SecuredJSConstructor) jsConstr;
            if (!sjsConstr.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                throw new AccessControlException(String.format("Public access to module %s is denied.", moduleName));//NOI18N
            }
            if (executeCallback != null) {
                executeCallback.beforeExecute(runner);
            }
            Logger.getLogger(ExecuteQueryRequestHandler.class.getName()).log(Level.FINE, EXECUTING_METHOD_TRACE_MSG, new Object[]{getRequest().getMethodName(), getRequest().getModuleName()});
            Object oFun = runner.getMember(getRequest().getMethodName());
            if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                Object result = ScriptUtils.toJava(((JSObject) oFun).call(runner, ScriptUtils.toJs(getRequest().getArguments())));
                if (executeCallback != null) {
                    executeCallback.afterExecute(runner);
                }
                if (moduleSession != systemSession
                        && sjsConstr.hasModuleAnnotation(JsDoc.Tag.STATELESS_TAG)) {
                    moduleSession.unregisterModule(moduleName);
                }
                return new ExecuteServerModuleMethodRequest.Response(getRequest().getID(), result);
            } else {
                throw new Exception(String.format(METHOD_MISSING_MSG, getRequest().getMethodName(), getRequest().getModuleName()));
            }
        } else {
            throw new Exception(String.format(MODULE_MISSING_MSG, getRequest().getModuleName()));
        }
    }

    /**
     * There are cases when a request needs to be complemented by temporarily
     * environment. Such as http request/response facades of servlet container.
     * Because of temporarily nature of such environment, we have to inject some
     * properties into ServerScriptRunner before a method execution and delete
     * it from there after a method execution.
     */
    public interface ExecuteEventsCallback {

        void beforeExecute(JSObject runner);

        void afterExecute(JSObject runner);
    }
}
