/* global Java*/
define('logger', function () {
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    var JavaStringArrayClass = Java.type("java.lang.String[]");
    var JavaArrayClass = Java.type("java.lang.Object[]");
    var SystemCallbackClass = Java.type("com.eas.script.SystemJSCallback");
    var space = ScriptsClass.getSpace();
    
    function isFunction(aValue){
        return typeof aValue === 'function' || aValue instanceof SystemCallbackClass;
    }
    
    /**
     * Server modules remote network or local script spaces proxy.
     * If it is used on client (J2SE, or in Browser) it is remote proxy thransforming methods' stubs calls into
     * network requests.
     * If it is used in server environment it is local proxy thransforming methods' stubs calls into neighbor script space's queue.
     * In this case, results, calculated on the other side of LPC are placed into initial script space's queue in form of success callback call.
     * @constructor
     * @param {String} aModuleName Name of a server module(session stateless or statefull or rezident), this proxy is used for as communication interface.
     * @returns {Proxy}
     */
    function RpcProxy(aModuleName) {
        if (aModuleName) {
            var app = ScriptedResourceClass.getApp();
            if (app) {
                var proxy = app.getServerModules();
                if (proxy) {
                    var moduleInfo = proxy.getCachedStructure(aModuleName);
                    if (moduleInfo.isPermitted()) {
                        var functions = moduleInfo.getFunctionsNames();
                        var currentObject = this;
                        functions.forEach(function (aFunctionName) {
                            currentObject[aFunctionName] = function () {
                                var onSuccess = null;
                                var onFailure = null;
                                var argsLength = arguments.length;
                                if (argsLength > 1 && isFunction(arguments[argsLength - 1]) && isFunction(arguments[argsLength - 2])) {
                                    onSuccess = arguments[argsLength - 2];
                                    onFailure = arguments[argsLength - 1];
                                    argsLength -= 2;
                                } else if (argsLength > 0 && isFunction(arguments[argsLength - 1])) {
                                    onSuccess = arguments[argsLength - 1];
                                    argsLength -= 1;
                                }
                                var params = new JavaArrayClass(argsLength);
                                for (var j = 0; j < argsLength; j++) {
                                    params[j] = arguments[j];
                                }
                                if (onSuccess) {
                                    proxy.callServerModuleMethod(aModuleName, aFunctionName, space, onSuccess, onFailure, params);
                                } else {
                                    var result = proxy.callServerModuleMethod(aModuleName, aFunctionName, space, null, null, params);
                                    return result && result.getPublished ? result.getPublished() : result;
                                }
                            };
                        });
                    } else {
                        throw 'Access denied for module "' + aModuleName + '". May be denied public access.';
                    }
                } else {
                    throw 'This architecture does not support server modules.';
                }
            }
        } else {
            throw "Module name could not be empty.";
        }
    }
    /**
     * Loads server modules structure (list of functions), constructs Proxies for them and
     * passes them to aOnSuccess callback.
     * @param {String|Array} aRemotesNames Names(s) of server modules.
     * @param {Function} aOnSuccess Success callback. Accepts proxies, constrcuted upon loaded structure of server modules.
     * @param {Function} aOnFailure Failure callback. Called if some problem occurs while loading modules structure from a server.
     * @returns {undefined}
     */
    function requireRemotes(aRemotesNames, aOnSuccess, aOnFailure) {
        var remotesNames = Array.isArray(aRemotesNames) ? aRemotesNames : [aRemotesNames];
        ScriptedResourceClass.loadRemotes(Java.to(remotesNames, JavaStringArrayClass), aOnSuccess ? function () {
            var proxies = [];
            for (var r = 0; r < remotesNames.length; r++) {
                proxies.push(new RpcProxy(remotesNames[r]));
            }
            aOnSuccess.apply(null, proxies);
        } : null, aOnFailure ? aOnFailure : null);
    }
    var module = {
        Proxy: RpcProxy,
        requireRemotes: requireRemotes
    };
    Object.defineProperty(module, 'Proxy', {
        enumerable: true,
        value: RpcProxy
    });
    Object.defineProperty(module, 'requireRemotes', {
        enumerable: true,
        value: requireRemotes
    });
    return module;
});