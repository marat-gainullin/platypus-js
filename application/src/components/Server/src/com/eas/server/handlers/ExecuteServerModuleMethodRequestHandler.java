/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.AppElementFiles;
import com.eas.client.cache.ScriptDocument;
import com.eas.server.SessionRequestHandler;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.Undefined;

/**
 *
 * @author pk
 */
public class ExecuteServerModuleMethodRequestHandler extends SessionRequestHandler<ExecuteServerModuleMethodRequest, ExecuteServerModuleMethodRequest.Response> {

    public static final String EXECUTING_METHOD_TRACE_MSG = "Executing method {0} of module {1}";
    public static final String MODEL_SAVE_ERROR_MSG = "While attempting to save model of unactual server module %s";
    public static final String MODULE_MISSING_MSG = "No module %s";
    public static final String METHOD_MISSING_MSG = "No method %s in module %s";
    public static final String RERUN_MSG = "About to re-run server module {0}";

    /**
     * @param aServerCore
     * @param aRequest
     */
    public ExecuteServerModuleMethodRequestHandler(PlatypusServerCore aServerCore, ExecuteServerModuleMethodRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<ExecuteServerModuleMethodRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        String moduleName = getRequest().getModuleName();
        if (moduleName == null || moduleName.isEmpty()) {
            onFailure.accept(new Exception("Module name is missing. Unnamed server modules are not allowed."));
        } else {
            String methodName = getRequest().getMethodName();
            if (methodName == null || methodName.isEmpty()) {
                onFailure.accept(new Exception("Module's method name is missing."));
            } else {
                try {
                    ScriptedResource._require(new String[]{moduleName}, new ConcurrentSkipListSet<>(), (Void v) -> {
                        try {
                            AppElementFiles files = serverCore.getIndexer().nameToFiles(moduleName);
                            JSObject constr = ScriptUtils.lookupInGlobal(moduleName);
                            if (files != null && files.isModule() && constr != null) {
                                String waitFor;
                                ScriptDocument config = serverCore.getScriptsConfigs().get(moduleName, files);
                                // Let's perform security checks
                                CreateServerModuleRequestHandler.checkPrincipalPermission(aSession, config.getModuleAllowedRoles(), moduleName);
                                // Let's check the if module is resident
                                JSObject moduleInstance = serverCore.getSessionManager().getSystemSession().getModule(moduleName);
                                if (moduleInstance == null) {
                                    if (aSession.containsModule(moduleName)) {
                                        waitFor = SESSION_WAIT_OPTION;
                                        moduleInstance = aSession.getModule(moduleName);
                                    } else {
                                        if (config.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                                            if (config.hasModuleAnnotation(JsDoc.Tag.STATELESS_TAG)) {
                                                moduleInstance = (JSObject) constr.newObject(new Object[]{});
                                                waitFor = SELF_WAIT_OPTION;
                                                Logger.getLogger(CreateServerModuleRequestHandler.class.getName()).log(Level.FINE, "Created server module for script {0} with name {1}", new Object[]{getRequest().getModuleName(), moduleName});
                                            } else {
                                                throw new IllegalArgumentException(String.format("@stateless annotation is needed for module ( %s ), to be created dynamically in user's session context.", moduleName));
                                            }
                                        } else {
                                            throw new AccessControlException(String.format("Public access to module %s is denied.", moduleName));//NOI18N
                                        }
                                    }
                                } else {
                                    waitFor = SERVER_WAIT_OPTION;
                                }
                                if (config.hasModuleAnnotation(JsDoc.Tag.WAIT_TAG)) {
                                    JsDoc.Tag waitTag = config.getModuleAnnotation(JsDoc.Tag.WAIT_TAG);
                                    String waitOption = waitTag.getParams() != null && !waitTag.getParams().isEmpty() ? waitTag.getParams().get(0) : SELF_WAIT_OPTION;
                                    switch (waitOption.toLowerCase()) {
                                        case SELF_WAIT_OPTION:
                                        case SESSION_WAIT_OPTION:
                                        case SERVER_WAIT_OPTION:
                                            waitFor = waitOption.toLowerCase();
                                            break;
                                        default:
                                            Logger.getLogger(ExecuteServerModuleMethodRequestHandler.class.getName()).log(Level.WARNING, "Unknown {0} option {1} in module {2}. Falling back to default parallelism.", new String[]{JsDoc.Tag.WAIT_TAG, waitOption, moduleName});
                                    }
                                }
                                if (moduleInstance != null) {
                                    Logger.getLogger(ExecuteQueryRequestHandler.class.getName()).log(Level.FINE, EXECUTING_METHOD_TRACE_MSG, new Object[]{getRequest().getMethodName(), getRequest().getModuleName()});
                                    Object oFun = moduleInstance.getMember(methodName);
                                    if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                                        List<Object> args = new ArrayList<>(Arrays.asList(getRequest().getArguments()));
                                        args.add(new AbstractJSObject() {
                                            @Override
                                            public Object call(final Object thiz, final Object... largs) {
                                                if (!args.isEmpty()) {
                                                    Object returned = largs.length > 0 ? largs[0] : null;
                                                    onSuccess.accept(new ExecuteServerModuleMethodRequest.Response(ScriptUtils.toJava(returned)));
                                                } else {
                                                    Logger.getLogger(ExecuteServerModuleMethodRequestHandler.class.getName()).log(Level.WARNING, BOTH_IO_MODELS_MSG, new Object[]{methodName, moduleName});
                                                }
                                                return null;
                                            }

                                        });
                                        args.add(new AbstractJSObject() {
                                            @Override
                                            public Object call(final Object thiz, final Object... largs) {
                                                if (!args.isEmpty()) {
                                                    Object reason = largs.length > 0 ? ScriptUtils.toJava(largs[0]) : null;
                                                    if (reason instanceof Exception) {
                                                        onFailure.accept((Exception) reason);
                                                    } else {
                                                        onFailure.accept(new Exception(String.valueOf(reason)));
                                                    }
                                                } else {
                                                    Logger.getLogger(ExecuteServerModuleMethodRequestHandler.class.getName()).log(Level.WARNING, BOTH_IO_MODELS_MSG, new Object[]{methodName, moduleName});
                                                }
                                                return null;
                                            }

                                        });
                                        final Object leveledLock;
                                        switch (waitFor) {
                                            case SELF_WAIT_OPTION:
                                                leveledLock = jdk.nashorn.api.scripting.ScriptUtils.unwrap(moduleInstance);
                                                break;
                                            case SESSION_WAIT_OPTION:
                                                leveledLock = aSession;
                                                break;
                                            case SERVER_WAIT_OPTION:
                                                leveledLock = serverCore;
                                                break;
                                            default:
                                                throw new IllegalStateException("moduleLock must be already known value.");
                                        }
                                        ScriptUtils.initAsyncs(0);
                                        try {
                                            synchronized (leveledLock) {
                                                ScriptUtils.setLock(leveledLock);// provide lock to callback threads
                                                try {
                                                    CreateServerModuleRequestHandler.checkPrincipalPermission(aSession, config.getPropertyAllowedRoles().get(methodName), methodName);
                                                    Object result = ((JSObject) oFun).call(moduleInstance, args.toArray());
                                                    int asyncs = ScriptUtils.getAsyncsCount();
                                                    if (!(result instanceof Undefined) || asyncs == 0) {
                                                        onSuccess.accept(new ExecuteServerModuleMethodRequest.Response(result));
                                                        args.clear();
                                                    }
                                                } finally {
                                                    ScriptUtils.setLock(null);
                                                }
                                            }
                                        } finally {
                                            ScriptUtils.initAsyncs(null);
                                        }
                                    } else {
                                        onFailure.accept(new Exception(String.format(METHOD_MISSING_MSG, getRequest().getMethodName(), getRequest().getModuleName())));
                                    }
                                } else {
                                    onFailure.accept(new Exception(String.format(MODULE_MISSING_MSG, getRequest().getModuleName())));
                                }
                            } else {
                                onFailure.accept(new IllegalArgumentException(String.format(MODULE_MISSING_OR_NOT_A_MODULE, moduleName)));
                            }
                        } catch (Exception ex) {
                            onFailure.accept(ex);
                        }
                    }, onFailure);
                } catch (Exception ex) {
                    onFailure.accept(ex);
                }
            }
        }
    }
    protected static final String SERVER_WAIT_OPTION = "server";
    protected static final String SESSION_WAIT_OPTION = "session";
    protected static final String SELF_WAIT_OPTION = "self";
    protected static final String MODULE_MISSING_OR_NOT_A_MODULE = "No module: %s, or it is not a module";
    protected static final String BOTH_IO_MODELS_MSG = "Method {0} in module {1} attempts to return value and call a callback. Sync and async io models both are not allowed. You should make a choice.";
}
