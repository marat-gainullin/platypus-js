/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.report.Report;
import com.eas.server.RequestHandler;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.function.Consumer;

/**
 *
 * @author pk
 */
public class RPCRequestHandler extends RequestHandler<RPCRequest, RPCRequest.Response> {

    public static final String EXECUTING_METHOD_TRACE_MSG = "Executing method {0} of module {1}";
    public static final String MODEL_SAVE_ERROR_MSG = "While attempting to save model of unactual server module %s";
    public static final String MODULE_MISSING_MSG = "No module %s";
    public static final String METHOD_MISSING_MSG = "No method %s in module %s";
    public static final String RERUN_MSG = "About to re-run server module {0}";

    /**
     * @param aServerCore
     * @param aRequest
     */
    public RPCRequestHandler(PlatypusServerCore aServerCore, RPCRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Session aSession, Consumer<RPCRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        String[] jsons = getRequest().getArgumentsJsons();
        Object[] arguments = new Object[jsons.length];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = aSession.getSpace().parseJsonWithDates(jsons[i]);
        }
        serverCore.executeMethod(getRequest().getModuleName(), getRequest().getMethodName(), arguments, true, (Object result) -> {
            onSuccess.accept(new RPCRequest.Response(result instanceof Report ? result : aSession.getSpace().toJson(result)));
        }, onFailure);
    }

    public static final String MODULE_MISSING_OR_NOT_A_MODULE = "No module %s, or it is not a module";
    public static final String BOTH_IO_MODELS_MSG = "Method {0} in module {1} attempts to call a callback more than once or it returns value and calls a callback. Sync and async IO models both are not allowed. You should make a choice.";
}
