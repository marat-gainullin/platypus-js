/*global P, space, Java, Function*/
(function (aSpace) {
    if (typeof Set === 'undefined') {
        var LinkedHashSetClass = Java.type('java.util.LinkedHashSet');
        Set = function () {
            var container = new LinkedHashSetClass();
            this.add = function (aValue) {
                container.add(aValue);
            };
            this.delete = function (aValue) {
                container.remove(aValue);
            };
            Object.defineProperty(this, 'size', {get: function () {
                    return container.size();
                }});
            this.forEach = function (aCallback) {
                container.forEach(aCallback);
            };
        };
    }

    //this === global;
    var global = this;
    aSpace.setGlobal(global);
    global.P = {};
    global['-platypus-scripts-space'] = aSpace;

    // core imports
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var JavaArrayClass = Java.type("java.lang.Object[]");
    var JavaStringArrayClass = Java.type("java.lang.String[]");
    var JavaCollectionClass = Java.type("java.util.Collection");
    var FileClass = Java.type("java.io.File");
    var JavaDateClass = Java.type("java.util.Date");
    var JavaStringClass = Java.type("java.lang.String");
    var LoggerClass = Java.type("java.util.logging.Logger");
    var IDGeneratorClass = Java.type("com.eas.util.IDGenerator");
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    var PlatypusPrincipalClass = Java.type("com.eas.client.login.PlatypusPrincipal");
    var FileUtilsClass = Java.type("com.eas.util.FileUtils");
    var MD5GeneratorClass = Java.type("com.eas.client.login.MD5Generator");

    //
    Object.defineProperty(P, "HTML5", {value: "HTML5 client"});
    Object.defineProperty(P, "J2SE", {value: "Java SE environment"});
    Object.defineProperty(P, "agent", {value: P.J2SE});

    function toPrimitive(aValue) {
        if (aValue && aValue.constructor) {
            var cName = aValue.constructor.name;
            if (cName === 'Date') {
                var converted = new JavaDateClass(aValue.getTime());
                return converted;
            } else if (cName === 'String') {
                return aValue + '';
            } else if (cName === 'Number') {
                return +aValue;
            } else if (cName === 'Boolean') {
                return !!aValue;
            }
        }
        return aValue;
    }
    aSpace.setToPrimitiveFunc(toPrimitive);

    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    function boxAsJava(aValue) {
        //---------------------- Don't change to !== because of undefined 
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
    }
    ;
    Object.defineProperty(P, "boxAsJava", {
        value: boxAsJava
    });
    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    function boxAsJs(aValue) {
        if (aValue) {
            if (aValue.getPublished) {
                aValue = aValue.getPublished();
            } else if (aValue instanceof JavaDateClass) {
                aValue = new Date(aValue.time);
            } else if (aValue instanceof JavaStringClass) {
                aValue += '';
            } else if (aValue instanceof JavaArrayClass) {
                var converted = [];
                for (var i = 0; i < aValue.length; i++) {
                    converted[converted.length] = boxAsJs(aValue[i]);
                }
                return converted;
            } else if (aValue instanceof JavaCollectionClass) {
                var converted = [];
                for each (var v in aValue) {
                    converted[converted.length] = boxAsJs(v);
                }
                return converted;
            }
            aValue = EngineUtilsClass.unwrap(aValue);
        }
        return aValue;
    }

    Object.defineProperty(P, "boxAsJs", {
        value: boxAsJs
    });

    function invokeDelayed(aTimeout, aTarget) {
        if (arguments.length < 2)
            throw "invokeDelayed needs 2 arguments - timeout, callback.";
        aSpace.schedule(aTarget, aTimeout);
    }

    Object.defineProperty(P, "invokeDelayed", {get: function () {
            return invokeDelayed;
        }});

    function invokeLater(aTarget) {
        aSpace.enqueue(aTarget);
    }

    Object.defineProperty(P, "invokeLater", {get: function () {
            return invokeLater;
        }});

    var applicationLogger = LoggerClass.getLogger("Application");
    var Logger = {};
    Object.defineProperty(P, "Logger", {value: Logger});
    Object.defineProperty(Logger, "config", {value: function (aMessage) {
            applicationLogger.config("" + aMessage);
        }});
    Object.defineProperty(Logger, "severe", {value: function (aMessage) {
            applicationLogger.severe("" + aMessage);
        }});
    Object.defineProperty(Logger, "warning", {value: function (aMessage) {
            applicationLogger.warning("" + aMessage);
        }});
    Object.defineProperty(Logger, "info", {value: function (aMessage) {
            applicationLogger.info("" + aMessage);
        }});
    Object.defineProperty(Logger, "fine", {value: function (aMessage) {
            applicationLogger.fine("" + aMessage);
        }});
    Object.defineProperty(Logger, "finer", {value: function (aMessage) {
            applicationLogger.finer("" + aMessage);
        }});
    Object.defineProperty(Logger, "finest", {value: function (aMessage) {
            applicationLogger.finest("" + aMessage);
        }});

    function lookupCallerFile() {
        var calledFromFile = null;
        var error = new Error('path test error');
        if (error.stack) {
            var stack = error.stack.split('\n');
            if (stack.length > 1) {
                var sourceCall = stack[3];
                var stackFrameParsed = /\((.+):\d+\)/.exec(sourceCall);
                if (stackFrameParsed && stackFrameParsed.length > 1) {
                    calledFromFile = stackFrameParsed[1];
                }
            }
        }
        return calledFromFile;
    }

    /**
     * @static
     * @param {type} deps
     * @param {type} aOnSuccess
     * @param {type} aOnFailure
     * @returns {undefined}
     */
    function require(deps, aOnSuccess, aOnFailure) {
        if (!Array.isArray(deps))
            deps = [deps];
        var strArray = new JavaStringArrayClass(deps.length);
        for (var i = 0; i < deps.length; i++)
            strArray[i] = deps[i] ? deps[i] + '' : null;
        var calledFromFile = lookupCallerFile();
        if (aOnSuccess) {
            ScriptedResourceClass.require(strArray, calledFromFile, P.boxAsJava(aOnSuccess), P.boxAsJava(aOnFailure));
        } else {
            ScriptedResourceClass.require(strArray, calledFromFile);
        }
    }
    Object.defineProperty(P, "require", {value: require});

    P.require('internals.js');
    P.require('orm.js');
    var serverCoreClass;
    try {
        serverCoreClass = Java.type('com.eas.server.PlatypusServerCore');
        // in server (EE or standalone)

        P.require(['reports/index.js', 'server/index.js']);
        try {
            Java.type('com.eas.server.httpservlet.PlatypusHttpServlet');
            // EE server
            P.require(['servlet-support/index.js', 'http-context.js']);
        } catch (se) {
            // TSA server
        }
    } catch (e) {
        serverCoreClass = null;
        // in client
        P.require('ui.js');
    }
    function extend(Child, Parent) {
        var prevChildProto = {};
        for (var m in Child.prototype) {
            var member = Child.prototype[m];
            if (typeof member === 'function') {
                prevChildProto[m] = member;
            }
        }
        var F = function () {
        };
        F.prototype = Parent.prototype;
        Child.prototype = new F();
        for (var m in prevChildProto)
            Child.prototype[m] = prevChildProto[m];
        Child.prototype.constructor = Child;
        Child.superclass = Parent.prototype;
    }
    Object.defineProperty(P, "extend", {value: extend});

    Object.defineProperty(P, "principal", {
        get: function () {
            var clientSpacePrincipal = PlatypusPrincipalClass.getClientSpacePrincipal();
            var tlsPrincipal = ScriptsClass.getContext().getPrincipal();
            return clientSpacePrincipal !== null ? clientSpacePrincipal : tlsPrincipal;
        }
    });

    function loadEntities(aEntities, aOnSuccess, aOnFailure) {
        var entities;
        if (!Array.isArray(aEntities)) {
            aEntities = aEntities + "";
            if (aEntities.length > 5 && aEntities.trim().substring(0, 5).toLowerCase() === "<?xml") {
                entities = [];
                var pattern = /queryId="(.+?)"/ig;
                var groups;
                while ((groups = pattern.exec(aEntities)) != null) {
                    if (groups.length > 1) {
                        entities.push(groups[1]);
                    }
                }
            } else {
                entities = [aEntities];
            }
        } else {
            entities = aEntities;
        }
        ScriptedResourceClass.loadEntities(Java.to(entities, JavaStringArrayClass), aOnSuccess ? aOnSuccess : null, aOnFailure ? aOnFailure : null);
    }
    Object.defineProperty(P, "requireEntities", {value: loadEntities});

    function loadRemotes(aRemotesNames, aOnSuccess, aOnFailure) {
        var remotesNames = Array.isArray(aRemotesNames) ? aRemotesNames : [aRemotesNames];
        ScriptedResourceClass.loadRemotes(Java.to(remotesNames, JavaStringArrayClass), aOnSuccess ? aOnSuccess : null, aOnFailure ? aOnFailure : null);
    }
    Object.defineProperty(P, "requireRemotes", {value: loadRemotes});
    /**
     * @static
     * @param {type} aName
     * @param {type} aData data
     * @param {type} aTarget
     * @returns {P.loadTemplate.publishTo}
     */
    function loadTemplate(aName, aData, aTarget) {
        P.require(['core/index.js', 'reports/index.js']);
        var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
        if (files) {
            var reportConfig = ScriptedResourceClass.getApp().getReports().get(aName, files);
            if (aTarget) {
                P.ReportTemplate.call(aTarget, reportConfig.getTemplateContent(), reportConfig.getNameTemplate(), reportConfig.getFormat(), aData);
            } else {
                aTarget = new P.ReportTemplate(reportConfig.getTemplateContent(), reportConfig.getNameTemplate(), reportConfig.getFormat(), aData);
            }
            return aTarget;
        } else {
            throw "Report template '" + aName + "' missing.";
        }
    }
    Object.defineProperty(P, "loadTemplate", {value: loadTemplate});
    /**
     * Constructs server module network proxy.
     * @constructor
     * @param {String} aModuleName Name of server module (session stateless or statefull or rezident).
     */
    function ServerModule(aModuleName) {
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
                                while (argsLength > 0 && !arguments[argsLength - 1]) {
                                    argsLength--;
                                }
                                if (argsLength > 1 && typeof arguments[argsLength - 1] === "function" && typeof arguments[argsLength - 2] === "function") {
                                    onSuccess = arguments[argsLength - 2];
                                    onFailure = arguments[argsLength - 1];
                                    argsLength -= 2;
                                } else if (argsLength > 0 && typeof arguments[argsLength - 1] === "function") {
                                    onSuccess = arguments[argsLength - 1];
                                    argsLength -= 1;
                                }
                                var params = new JavaArrayClass(argsLength);
                                for (var j = 0; j < argsLength; j++) {
                                    params[j] = arguments[j];
                                }
                                if (onSuccess) {
                                    proxy.callServerModuleMethod(aModuleName, aFunctionName, aSpace, onSuccess, onFailure, params);
                                } else {
                                    var result = proxy.callServerModuleMethod(aModuleName, aFunctionName, aSpace, null, null, params);
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
            throw "Module Name could not be empty.";
        }
    }

    Object.defineProperty(P, "ServerModule", {value: ServerModule});

    var Resource = {};
    Object.defineProperty(Resource, "load", {
        value: function (aResName, onSuccess, onFailure) {
            var calledFromFile = lookupCallerFile();
            return boxAsJs(ScriptedResourceClass.load(boxAsJava(aResName), boxAsJava(calledFromFile), boxAsJava(onSuccess), boxAsJava(onFailure)));
        }
    });
    Object.defineProperty(Resource, "loadText", {
        value: function (aResName, onSuccess, onFailure) {
            var calledFromFile = lookupCallerFile();
            return boxAsJs(ScriptedResourceClass.load(boxAsJava(aResName), boxAsJava(calledFromFile), boxAsJava(onSuccess), boxAsJava(onFailure)));
        }
    });
    Object.defineProperty(Resource, "upload", {
        value: function (aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback) {
            print("upload() is not implemented for J2SE");
        }
    });
    Object.defineProperty(Resource, "applicationPath", {
        get: function () {
            return ScriptedResourceClass.getApplicationPath();
        }
    });
    Object.defineProperty(P, "Resource", {
        value: Resource
    });
    Object.defineProperty(P, "logout", {
        value: function (onSuccess, onFailure) {
            return P.principal.logout(onSuccess, onFailure);
        }
    });

    var IDGenerator = {};

    Object.defineProperty(P, "IDGenerator", {value: IDGenerator});
    Object.defineProperty(IDGenerator, "genID", {
        value: function () {
            return IDGeneratorClass.genID();
        }
    });
    Object.defineProperty(P, "ID", {value: IDGenerator});
    Object.defineProperty(IDGenerator, "generate", {
        value: function () {
            return IDGeneratorClass.genID();
        }
    });
    var MD5Generator = {};
    Object.defineProperty(P, "MD5Generator", {value: MD5Generator});
    Object.defineProperty(MD5Generator, "generate", {
        value: function (aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });
    var MD5 = {};
    Object.defineProperty(P, "MD5", {value: MD5});
    Object.defineProperty(MD5, "generate", {
        value: function (aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });

    function readString(aFileName, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        return FileUtilsClass.readString(new FileClass(aFileName), encoding);
    }

    Object.defineProperty(P, "readString", {
        value: readString
    });
    function writeString(aFileName, aText, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        FileUtilsClass.writeString(new FileClass(aFileName), aText, encoding);
    }
    Object.defineProperty(P, "writeString", {
        value: writeString
    });
})(space);