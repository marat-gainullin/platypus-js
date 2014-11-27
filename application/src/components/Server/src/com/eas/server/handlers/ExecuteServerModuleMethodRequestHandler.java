/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.SessionRequestHandler;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.function.Consumer;

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
        serverCore.executeMethod(getRequest().getModuleName(), getRequest().getMethodName(), getRequest().getArguments(), aSession, (Object result) -> {
            onSuccess.accept(new ExecuteServerModuleMethodRequest.Response(result));
        }, onFailure);
    }

    public static final String SERVER_WAIT_OPTION = "server";
    public static final String SESSION_WAIT_OPTION = "session";
    public static final String SELF_WAIT_OPTION = "self";
    public static final String MODULE_MISSING_OR_NOT_A_MODULE = "No module: %s, or it is not a module";
    public static final String BOTH_IO_MODELS_MSG = "Method {0} in module {1} attempts to return value and call a callback. Sync and async io models both are not allowed. You should make a choice.";
}
