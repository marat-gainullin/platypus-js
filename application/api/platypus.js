load("classpath:internals.js");
/** 
 * Platypus library namespace global variable.
 * @namespace P
 */
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

P.HTML5 = "Html5 client";
P.J2SE = "Java SE client/server environment";
P.agent = P.J2SE;

/** 
 * invokeLater - invokes given function in AWT event thread
 */
Function.prototype.invokeLater = function() {
    var SwingUtilities = Java.type("javax.swing.SwingUtilities");
    var func = this;
    var args = arguments;
    SwingUtilities.invokeLater(function() {
        func.apply(func, args);
    });
};

/** 
 * Thread - schedules given function in the pool thread
 */
Function.prototype.invokeDelayed = function() {
    var SwingUtilities = Java.type("javax.swing.SwingUtilities");
    var ScriptTimerTask = Java.type("com.eas.client.scripts.ScriptTimerTask");

    var func = this;
    var args = arguments;
    if (!args || !args.length || args.length < 1)
        throw "schedule needs at least 1 argument - timeout value.";
    var userArgs = [];
    for (var i = 1; i < args.length; i++) {
        userArgs.push(args[i]);
    }
    ScriptTimerTask.schedule(function() {
        SwingUtilities.invokeLater(function() {
            try {
                func.apply(func, userArgs);
            } catch (e) {
                P.Logger.severe(e);
            }
        });
    }, args[0]);
    // HTML5 client doesn't support cancel feature and so, we don't support it too.
};

(function() {
    var Executors = Java.type("java.util.concurrent.Executors");
    var DeamonThreadFactory = Java.type("com.eas.concurrent.DeamonThreadFactory");
    var fixedThreadPool = null;
    /** 
     * Thread - schedules given function in the pool thread
     */
    Function.prototype.invokeBackground = function() {
        if (!fixedThreadPool) {
            fixedThreadPool = Executors.newFixedThreadPool(10, new DeamonThreadFactory());
        }
        var func = this;
        var args = arguments;
        fixedThreadPool.execute(function() {
            func.apply(func, args);
        });
    };
})();

/** 
 * Swing invokeAndWait - invokes given function in AWT event thread
 * and waits for it's completion
 */
Function.prototype.invokeAndWait = function() {
    var SwingUtilities = Java.type("javax.swing.SwingUtilities");
    var func = this;
    var args = arguments;
    SwingUtilities.invokeAndWait(function() {
        func.apply(func, args);
    });
};

load("classpath:deps.js");

/**
 * @static
 * @param {type} deps
 * @param {type} aOnSuccess
 * @param {type} aOnFailure
 * @returns {undefined}
 */
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

/**
 * @static
 * @param {type} aName
 * @param {type} aTarget
 * @returns {P.loadModel.publishTo}
 */
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

/**
 * @static
 * @param {type} aName
 * @param {type} aModel
 * @param {type} aTarget
 * @returns {P.loadForm.publishTo}
 */
P.loadForm = function(aName, aModel, aTarget) {
    var Executor = Java.type('com.eas.client.scripts.PlatypusScriptedResource');
    var Loader = Java.type('com.eas.client.forms.store.Dom2FormDocument');
    var Form = Java.type('com.eas.client.forms.Form');

    var designInfo = Loader.load(Executor.getClient(), aName);
    var form = new Form(aName, designInfo, aModel.unwrap());
    if (aTarget) {
        P.Form.call(aTarget, form);
    } else {
        aTarget = new P.Form(form);
    }
    if(!form.title)
        form.title = aName;
    var comps = form.publishedComponents;
    for (var c = 0; c < comps.length; c++) {
        (function() {
            var comp = comps[c];
            if (comp.name) {
                Object.defineProperty(aTarget, comp.name, {
                    get: function() {
                        return comp;
                    }
                });
            }
        })();
    }
    return aTarget;
};

/**
 * @static
 * @param {type} aName
 * @param {type} aModel
 * @param {type} aTarget
 * @returns {P.loadTemplate.publishTo}
 */
P.loadTemplate = function(aName, aModel, aTarget) {
    var publishTo = aTarget ? aTarget : {};
    var Executor = Java.type('com.eas.client.scripts.PlatypusScriptedResource');
    var Loader = Java.type('com.eas.client.reports.store.Dom2ReportDocument');
    var template = Loader.load(Executor.getClient(), aName, aModel.unwrap());
    // publish
    publishTo.generateReport = function() {
        return template.generateReport();
    };
    return publishTo;
};

/**
 * Constructs server module network proxy.
 * @constructor
 * @param {String} aModuleName Name of server module (session stateless or statefull or rezident).
 */
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

(function() {
    var ScriptUtils = Java.type('com.eas.script.ScriptUtils');
    var JavaDate = Java.type("java.util.Date");
    var JavaArray = Java.type("java.lang.Object[]");
    var toPrimitive = ScriptUtils.getToPrimitiveFunc();

    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    P.boxAsJava = function(aValue) {
        if (arguments.length > 0 && aValue != null) {
            if (aValue.unwrap) {
                aValue = aValue.unwrap();
            } else {
                aValue = toPrimitive(aValue);
            }
            return aValue;
        } else {// undefined -> null
            return null;
        }
    };

    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    P.boxAsJs = function(aValue) {
        if (aValue) {
            if (aValue.getPublished) {
                if (arguments.length > 1) {
                    var elementClass = arguments[1];
                    aValue = aValue.getPublished(new elementClass());
                } else {
                    aValue = aValue.getPublished();
                }
            } else if (aValue instanceof JavaDate) {
                aValue = new Date(aValue.time);
            } else if (aValue instanceof JavaArray) {
                var converted = [];
                for (var i = 0; i < aValue.length; i++) {
                    if (arguments.length > 1) {
                        converted[converted.length] = P.boxAsJs(aValue[i], arguments[1]);
                    } else {
                        converted[converted.length] = P.boxAsJs(aValue[i]);
                    }
                }
                return converted;
            }
        }
        return aValue;
    };
})();
