define(['core/report'], function (Report) {
    //var nativeClient = @com.eas.client.AppClient::getInstance()();
    function requireRemotes(aRemotesNames, aOnSuccess, aOnFailure) {
        var remotesNames = Array.isArray(aRemotesNames) ? aRemotesNames : [aRemotesNames];
        /*@com.eas.application.Loader::*/jsLoadServerModules(remotesNames, function () {
            try {
                var proxies = [];
                for (var r = 0; r < remotesNames.length; r++) {
                    proxies.push(new RpcProxy(remotesNames[r]));
                }
                aOnSuccess.apply(null, proxies);
            } catch (ex) {
                aOnFailure(ex); // This is because of exceptions in RpcProxy constructor. They are related to server response and so, they are should be passed to failure callback.
            }
        }, aOnFailure);
    }
    function generateFunction(aModuleName, aFunctionName) {
        return function () {
            var onSuccess = null;
            var onFailure = null;
            var argsLength = arguments.length;
            if (arguments.length > 1 && typeof arguments[arguments.length - 1] === "function" && typeof arguments[arguments.length - 2] === "function") {
                onSuccess = arguments[arguments.length - 2];
                onFailure = arguments[arguments.length - 1];
                argsLength -= 2;
            } else if (arguments.length > 1 && typeof arguments[arguments.length - 1] === "undefined" && typeof arguments[arguments.length - 2] === "function") {
                onSuccess = arguments[arguments.length - 2];
                argsLength -= 2;
            } else if (arguments.length > 0 && typeof arguments[arguments.length - 1] === "function") {
                onSuccess = arguments[arguments.length - 1];
                argsLength -= 1;
            }
            var params = [];
            for (var j = 0; j < argsLength; j++) {
                var to = typeof arguments[j];
                if (to !== 'undefined' && to !== 'function') {
                    params[j] = JSON.stringify(arguments[j]);
                } else {
                    break;
                }
            }
            if (onSuccess) {
                /*nativeClient.@com.eas.client.AppClient::*/requestServerMethodExecution(aModuleName, aFunctionName, params,
                        function (aResult) {
                            if (typeof aResult === 'object' && aResult instanceof Report)
                                onSuccess(aResult);
                            else {
                                var parsed;
                                try {
                                    parsed = JSON.parse(aResult, /*@com.eas.core.Utils.JsObject::*/dateReviver());
                                } catch (ex) {
                                    parsed = aResult;
                                }
                                onSuccess(parsed);
                            }
                        }, onFailure, Report);
            } else {
                var result = /*nativeClient.@com.eas.client.AppClient::*/requestServerMethodExecution(aModuleName, aFunctionName, params, null, null, Report);
                if (typeof result === 'object' && result instanceof Report)
                    return result;
                else {
                    try {
                        return JSON.parse(result, /*@com.eas.core.Utils.JsObject::*/dateReviver());
                    } catch (ex) {
                        return result;
                    }
                }
            }
        };
    }
    function RpcProxy(aModuleName) {
        if (!(this instanceof RpcProxy))
            throw 'use new Rpc.Proxy() please.';
        var moduleData = /*nativeClient.@com.eas.client.AppClient::*/getServerModule('' + aModuleName);
        if (!moduleData)
            throw 'No server module proxy for module: ' + aModuleName;
        if (!moduleData.isPermitted)
            throw "Access to server module '" + aModuleName + "' functions list is not permitted.";
        var self = this;
        for (var i = 0; i < moduleData.functions.length; i++) {
            var funcName = moduleData.functions[i];
            self[funcName] = generateFunction(aModuleName, funcName);
        }
    }
    var module = {};
    Object.defineProperty(module, "Proxy", {
        enumerable: true,
        value: RpcProxy
    });
    Object.defineProperty(module, "requireRemotes", {
        enumerable: true,
        value: requireRemotes
    });
    return module;
});