/*global Java, Function*/
(function (aDefiner) {
    try {
        Java.type('com.eas.server.PlatypusServerCore');
        // in server
        try {
            Java.type('com.eas.server.httpservlet.PlatypusHttpServlet');
            // EE server
            define(['boxing', 'reports/report-template', 'logger', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'server/index', 'servlet-support/index', 'http-context'], function (B, ReportTemplate, Logger, Orm, Core, Datamodel, Reports, Server, Servlet, Http) {
                var module = aDefiner(B, ReportTemplate);
                Object.defineProperty(module, 'Logger', {value: Logger});
                for (var o in Orm) {
                    Object.defineProperty(module, o, {value: Orm[o]});
                }
                for (var c in Core) {
                    Object.defineProperty(module, c, {value: Core[c]});
                }
                for (var d in Datamodel) {
                    Object.defineProperty(module, d, {value: Datamodel[d]});
                }
                for (var r in Reports) {
                    Object.defineProperty(module, r, {value: Reports[r]});
                }
                for (var s in Server) {
                    Object.defineProperty(module, s, {value: Server[s]});
                }
                for (var ss in Servlet) {
                    Object.defineProperty(module, ss, {value: Servlet[ss]});
                }
                for (var h in Http) {
                    Object.defineProperty(module, h, {value: Http[h]});
                }
                return module;
            });
        } catch (se) {
            // TSA server
            define(['boxing', 'reports/report-template', 'logger', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'server/index'], function (B, ReportTemplate, Logger, Orm, Core, Datamodel, Reports, Server) {
                var module = aDefiner(B, ReportTemplate);
                Object.defineProperty(module, 'Logger', {value: Logger});
                for (var o in Orm) {
                    Object.defineProperty(module, o, {value: Orm[o]});
                }
                for (var c in Core) {
                    Object.defineProperty(module, c, {value: Core[c]});
                }
                for (var d in Datamodel) {
                    Object.defineProperty(module, d, {value: Datamodel[d]});
                }
                for (var r in Reports) {
                    Object.defineProperty(module, r, {value: Reports[r]});
                }
                for (var s in Server) {
                    Object.defineProperty(module, s, {value: Server[s]});
                }
                return module;
            });
        }
    } catch (e) {
        // in client
        define(['boxing', 'reports/report-template', 'logger', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'ui'], function (B, ReportTemplate, Logger, Orm, Core, Datamodel, Reports, Ui) {
            var module = aDefiner(B, ReportTemplate);
            Object.defineProperty(module, 'Logger', {value: Logger});
            for (var o in Orm) {
                Object.defineProperty(module, o, {value: Orm[o]});
            }
            for (var c in Core) {
                Object.defineProperty(module, c, {value: Core[c]});
            }
            for (var d in Datamodel) {
                Object.defineProperty(module, d, {value: Datamodel[d]});
            }
            for (var r in Reports) {
                Object.defineProperty(module, r, {value: Reports[r]});
            }
            for (var i in Ui) {
                Object.defineProperty(module, i, {value: Ui[i]});
            }
            return module;
        });
    }
}(function (B, ReportTemplate) {
    // core imports
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var JavaArrayClass = Java.type("java.lang.Object[]");
    var JavaStringArrayClass = Java.type("java.lang.String[]");
    var FileClass = Java.type("java.io.File");
    var IDGeneratorClass = Java.type("com.eas.util.IDGenerator");
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    var PlatypusPrincipalClass = Java.type("com.eas.client.login.PlatypusPrincipal");
    var FileUtilsClass = Java.type("com.eas.util.FileUtils");
    var MD5GeneratorClass = Java.type("com.eas.client.login.MD5Generator");

    var space = ScriptsClass.getSpace();
    //

    function invokeDelayed(aTimeout, aTarget) {
        if (arguments.length < 2)
            throw "invokeDelayed needs 2 arguments - timeout, callback.";
        space.schedule(aTarget, aTimeout);
    }

    function invokeLater(aTarget) {
        space.enqueue(aTarget);
    }


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
        return calledFromFile + '.js';
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

    function loadRemotes(aRemotesNames, aOnSuccess, aOnFailure) {
        var remotesNames = Array.isArray(aRemotesNames) ? aRemotesNames : [aRemotesNames];
        ScriptedResourceClass.loadRemotes(Java.to(remotesNames, JavaStringArrayClass), aOnSuccess ? aOnSuccess : null, aOnFailure ? aOnFailure : null);
    }
    /**
     * @static
     * @param {type} aName
     * @param {type} aData data
     * @param {type} aTarget
     * @returns {P.loadTemplate.publishTo}
     */
    function loadTemplate(aName, aData, aTarget) {
        var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
        if (files) {
            var reportConfig = ScriptedResourceClass.getApp().getReports().get(aName, files);
            if (aTarget) {
                ReportTemplate.call(aTarget, reportConfig.getTemplateContent(), reportConfig.getNameTemplate(), reportConfig.getFormat(), aData);
            } else {
                aTarget = new ReportTemplate(reportConfig.getTemplateContent(), reportConfig.getNameTemplate(), reportConfig.getFormat(), aData);
            }
            return aTarget;
        } else {
            throw "Report template '" + aName + "' missing.";
        }
    }
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
            throw "Module Name could not be empty.";
        }
    }

    var Resource = {};
    Object.defineProperty(Resource, "load", {
        value: function (aResName, onSuccess, onFailure) {
            var calledFromFile = lookupCallerFile();
            return B.boxAsJs(ScriptedResourceClass.load(B.boxAsJava(aResName), B.boxAsJava(calledFromFile), B.boxAsJava(onSuccess), B.boxAsJava(onFailure)));
        }
    });
    Object.defineProperty(Resource, "loadText", {
        value: function (aResName, onSuccess, onFailure) {
            var calledFromFile = lookupCallerFile();
            return B.boxAsJs(ScriptedResourceClass.load(B.boxAsJava(aResName), B.boxAsJava(calledFromFile), B.boxAsJava(onSuccess), B.boxAsJava(onFailure)));
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

    var IDGenerator = {};

    Object.defineProperty(IDGenerator, "genID", {
        value: function () {
            return IDGeneratorClass.genID();
        }
    });
    Object.defineProperty(IDGenerator, "generate", {
        value: function () {
            return IDGeneratorClass.genID();
        }
    });
    var MD5Generator = {};
    Object.defineProperty(MD5Generator, "generate", {
        value: function (aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });
    var MD5 = {};
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

    function writeString(aFileName, aText, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        FileUtilsClass.writeString(new FileClass(aFileName), aText, encoding);
    }

    var HTML5 = "HTML5 client";
    var J2SE = "Java SE environment";

    var module = {};
    Object.defineProperty(module, "HTML5", {
        value: HTML5
    });
    Object.defineProperty(module, "J2SE", {
        value: J2SE
    });
    Object.defineProperty(module, "agent", {
        value: J2SE
    });
    Object.defineProperty(module, "invokeDelayed", {
        value: invokeDelayed
    });
    Object.defineProperty(module, "invokeLater", {
        value: invokeLater
    });
    Object.defineProperty(module, "extend", {
        value: extend
    });
    Object.defineProperty(module, "Resource", {
        value: Resource
    });
    Object.defineProperty(module, "ServerModule", {
        value: ServerModule
    });
    Object.defineProperty(module, "loadTemplate", {
        value: loadTemplate
    });
    Object.defineProperty(module, "loadEntities", {
        value: loadEntities
    });
    Object.defineProperty(module, "loadRemotes", {
        value: loadRemotes
    });
    Object.defineProperty(module, "IDGenerator", {
        value: IDGenerator
    });
    Object.defineProperty(module, "ID", {
        value: IDGenerator
    });
    Object.defineProperty(module, "MD5Generator", {
        value: MD5Generator
    });
    Object.defineProperty(module, "MD5", {
        value: MD5Generator
    });
    Object.defineProperty(module, "readString", {
        value: readString
    });
    Object.defineProperty(module, "writeString", {
        value: writeString
    });
    Object.defineProperty(module, "principal", {
        get: function () {
            var clientSpacePrincipal = PlatypusPrincipalClass.getClientSpacePrincipal();
            var tlsPrincipal = ScriptsClass.getContext().getPrincipal();
            return clientSpacePrincipal !== null ? clientSpacePrincipal : tlsPrincipal;
        }
    });
    Object.defineProperty(module, "logout", {
        value: function (onSuccess, onFailure) {
            return module.principal.logout(onSuccess, onFailure);
        }
    });
    Object.defineProperty(module, "export", {
        value: function (aTarget) {
            module.require = aTarget.require;
            aTarget.P = module;
        }
    });
    return module;
}
));
