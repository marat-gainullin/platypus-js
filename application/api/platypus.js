load("classpath:internals.js");

(function() {
    // this === global;
    var global = this;
    var oldP = global.P;
    global.P = {};
    global.P.restore = function() {
        var ns = global.P;
        global.P = oldP;
        return ns;
    };
    /*
     global.P = this; // global scope of api - for legacy applications
     global.P.restore = function() {
     throw "Legacy api can't restore the global namespace.";
     };
     */
})();

load("classpath:forms/BorderPane.js");
//...

P.require = function(deps, aOnSuccess, aOnFailure) {
    var Executor = Java.type('com.eas.client.scripts.PlatypusScriptedResource');
    try {
        if (deps) {
            if (Array.isArray(deps)) {
                for (var i = 0; i < deps.length; i++) {
                    Executor.executeScriptResource(deps[i]);
                }
            } else {
                Executor.executeScriptResource(deps);
            }
        }
        if (aOnSuccess) {
            aOnSuccess();
        }
    } catch (e) {
        if (aOnFailure)
            aOnFailure(e);
        else
            throw e;
    }
};

(function() {
    var cached = {};
    var g = this;
    var getModule = function(aName) {
        if (!cached[aName]) {
            var c = g[aName];
            if (c) {
                cached[aName] = new c();
            } else {
                var Executor = Java.type('com.eas.client.scripts.PlatypusScriptedResource');
                Executor.executeScriptResource(aName);
                c = g[aName];
                if (c) {
                    cached[aName] = new c();
                } else {
                    throw 'No function: ' + aName + ' found while Modules.get(...).';
                }
            }
        }
        return cached[aName];
    };
    var ScriptUtils = Java.type('com.eas.script.ScriptUtils');
    ScriptUtils.setGetModuleFunc(getModule);
    var PModules = {};
    Object.defineProperty(P, "Modules", {
        configurable: false,
        get: function() {
            return PModules;
        }
    });
    Object.defineProperty(PModules, "get", {
        configurable: false,
        get: function() {
            return getModule;
        }
    });
})();

P.loadModel = function(aName, aTarget) {
    var publishTo = aTarget ? aTarget : {};
    var Executor = Java.type('com.eas.client.scripts.PlatypusScriptedResource');
    var Loader = Java.type('com.eas.client.scripts.store.Dom2ModelDocument');
    var model = Loader.load(Executor.getClient(), aName);
    // publish
    publishTo.unwrap = function() {
        return model;
    };
    model.setPublished(publishTo);
    return publishTo;
};

P.loadForm = function(aName, aModel, aTarget) {
    var publishTo = aTarget ? aTarget : {};
    var Executor = Java.type('com.eas.client.scripts.PlatypusScriptedResource');
    var Loader = Java.type('com.eas.client.forms.store.Dom2FormDocument');
    var Form = Java.type('com.eas.client.forms.Form');

    var designInfo = Loader.load(Executor.getClient(), aName);
    var form = new Form(aName, designInfo, aModel.unwrap());
    // publish
    publishTo.show = function() {
        form.show();
    };
    publishTo.close = function(aValue) {
        form.close(aValue);
    };
    return publishTo;
};

P.loadTemplate = function(aName, aModel, aTarget) {
    var publishTo = aTarget ? aTarget : {};
    var Executor = Java.type('com.eas.client.scripts.PlatypusScriptedResource');
    var Loader = Java.type('com.eas.client.reports.store.Dom2ReportDocument');
    var template = Loader.load(Executor.getClient(), aName, aModel.unwrap());
    // publish
    publishTo.generateReport = function() {
        template.generateReport();
    };
    return publishTo;
};

P.ServerModule = function(aModuleName) {
    var clientHost = Java.type('com.eas.client.scripts.PlatypusScriptedResource');
    var client = clientHost.getPlatypusClient();
    if (client) {
        var CreateRequest = Java.type('com.eas.client.threetier.requests.CreateServerModuleRequest');
        var Generator = Java.type('com.bearsoft.rowset.utils.IDGenerator');
        var request = new CreateRequest(Generator.genID(), aModuleName);
        client.executeRequest(request);
        var responce = request.getResponse();
        if (responce.isPermitted()) {
            var functions = responce.getFunctionsNames();
            var currentObject = this;
            functions.forEach(function(aFunctionName) {
                currentObject[aFunctionName] = function() {
                    var onSuccess = null;
                    var onFailure = null;
                    var argsLength = arguments.length;
                    if (arguments.length > 1 && typeof arguments[arguments.length - 1] == "function" && typeof arguments[arguments.length - 2] == "function") {
                        onSuccess = arguments[arguments.length - 2];
                        onFailure = arguments[arguments.length - 1];
                        argsLength -= 2;
                    } else if (arguments.length > 0 && typeof arguments[arguments.length - 1] == "function") {
                        onSuccess = arguments[arguments.length - 1];
                        argsLength -= 1;
                    }
                    try {
                        var ObjectsArray = Java.type("java.lang.Object[]");
                        var params = new ObjectsArray(argsLength);
                        for (var j = 0; j < argsLength; j++) {
                            params[j] = arguments[j];
                        }
                        var result = client.executeServerModuleMethod(aModuleName, aFunctionName, params);
                        if (onSuccess) {
                            onSuccess(result && result.getPublished ? result.getPublished() : result);
                        } else {
                            return result && result.getPublished ? result.getPublished() : result;
                        }
                    } catch (e) {
                        if (onFailure) {
                            onFailure(e);
                        } else {
                            throw e;
                        }
                    }
                };
            });
        } else {
            throw "Access denied for module" + aModuleName + ". May be denied public access."
        }
    } else {
        throw "This architecture does not support server modules."
    }
};