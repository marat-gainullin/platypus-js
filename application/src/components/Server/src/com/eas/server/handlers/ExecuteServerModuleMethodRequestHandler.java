/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.scripts.PlatypusScriptedResource;
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
    public ExecuteServerModuleMethodRequestHandler(PlatypusServerCore aServerCore, Session aSession, ExecuteServerModuleMethodRequest aRequest) {
        super(aServerCore, aSession, aRequest);
    }

    @Override
    public Response handle2() throws Exception {
        String moduleName = getRequest().getModuleName();
        if (moduleName == null || moduleName.isEmpty()) {
            throw new Exception("Module name is missing. Unnamed server modules are not allowed.");
        }
        String methodName = getRequest().getMethodName();
        if (methodName == null || methodName.isEmpty()) {
            throw new Exception("Module's method name is missing.");
        }
        PlatypusScriptedResource.executeScriptResource(moduleName);
        JSObject jsConstr = ScriptUtils.lookupInGlobal(moduleName);
        if (jsConstr != null) {
            if (jsConstr instanceof SecuredJSConstructor) {
                SecuredJSConstructor sjsConstr = (SecuredJSConstructor) jsConstr;
                // Let's check the if module is resident
                JSObject moduleInstance = getServerCore().getSessionManager().getSystemSession().getModule(moduleName);
                if (moduleInstance != null) {
                    // Resident module roles need to be checked against a current user.
                    CreateServerModuleRequestHandler.checkPrincipalPermission(getServerCore(), sjsConstr.getModuleAllowedRoles(), moduleName);
                } else {
                    if (getSession().containsModule(moduleName)) {
                        moduleInstance = getSession().getModule(moduleName);
                    } else {
                        if (sjsConstr.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                            if (sjsConstr.hasModuleAnnotation(JsDoc.Tag.STATELESS_TAG)) {
                                moduleInstance = (JSObject) sjsConstr.newObject(new Object[]{});
                                Logger.getLogger(CreateServerModuleRequestHandler.class.getName()).log(Level.FINE, "Created server module for script {0} with name {1} on request {2}", new Object[]{getRequest().getModuleName(), moduleName, getRequest().getID()});
                            } else {
                                throw new IllegalArgumentException(String.format("@stateless annotation is needed for module ( %s ), to be created dynamically in user's session context.", moduleName));
                            }
                        } else {
                            throw new AccessControlException(String.format("Public access to module %s is denied.", moduleName));//NOI18N
                        }
                    }
                }
                if (moduleInstance != null) {
                    Logger.getLogger(ExecuteQueryRequestHandler.class.getName()).log(Level.FINE, EXECUTING_METHOD_TRACE_MSG, new Object[]{getRequest().getMethodName(), getRequest().getModuleName()});
                    Object oFun = moduleInstance.getMember(methodName);
                    if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                        Object result = ((JSObject) oFun).call(moduleInstance, getRequest().getArguments());
                        return new ExecuteServerModuleMethodRequest.Response(getRequest().getID(), result);
                    } else {
                        throw new Exception(String.format(METHOD_MISSING_MSG, getRequest().getMethodName(), getRequest().getModuleName()));
                    }
                } else {
                    throw new Exception(String.format(MODULE_MISSING_MSG, getRequest().getModuleName()));
                }
            } else {
                throw new AccessControlException(String.format("Access to unsecured module %s is denied.", moduleName));//NOI18N
            }
        } else {
            throw new IllegalArgumentException(String.format("No module: %s, or it is not a module", moduleName));
        }
    }
}
