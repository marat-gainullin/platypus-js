define([
    'core/report',
    'core/invoke',
    './requests',
    'core/utils'], function (
        Report,
        Invoke,
        Requests,
        Utils) {
    function requireRemotes(aRemotesNames, aOnSuccess, aOnFailure) {
        var remotesNames = Array.isArray(aRemotesNames) ? aRemotesNames : [aRemotesNames];
        var proxies = [];
        for (var r = 0; r < remotesNames.length; r++) {
            proxies.push(new RpcProxy(remotesNames[r]));
        }
        Invoke.later(function () {
            aOnSuccess(proxies);
        });
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
                Requests.requestServerMethodExecution(aModuleName, aFunctionName, params,
                        function (aResult) {
                            if (typeof aResult === 'object' && aResult instanceof Report)
                                onSuccess(aResult);
                            else {
                                var parsed;
                                try {
                                    parsed = JSON.parse(aResult, Requests.dateReviver);
                                } catch (ex) {
                                    parsed = aResult;
                                }
                                onSuccess(parsed);
                            }
                        }, onFailure, Report);
            } else {
                var result = Requests.requestServerMethodExecution(aModuleName, aFunctionName, params, null, null, Report);
                if (typeof result === 'object' && result instanceof Report)
                    return result;
                else {
                    try {
                        return JSON.parse(result, Requests.dateReviver);
                    } catch (ex) {
                        return result;
                    }
                }
            }
        };
    }

    function isValidFunctionName(name) {
        return /^[_a-zA-Z][_a-zA-Z0-9]+$/.test(name);
    }

    function RpcProxy(aModuleName) {
        if (!(this instanceof RpcProxy))
            throw 'use new Rpc.Proxy() please.';
        var self = this;
        return new Proxy(this, {
            has: function (name) {
                return name in self || isValidFunctionName(name);
            },
            get: function (name) {
                if (!(name in self) && isValidFunctionName(name)) {
                    self.name = generateFunction(aModuleName, name);
                }
                return self.name;
            }
        });
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