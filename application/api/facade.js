/*global Java, Function*/
(function (aInitializer, aCommonDefiner) {
    try {
        Java.type('com.eas.server.PlatypusServerCore');
        // Server ...
        try {
            Java.type('com.eas.server.httpservlet.PlatypusHttpServlet');
            // Servlet container (may be EE server)
            define(['environment', 'logger', 'resource', 'id', 'md5', 'template', 'invoke', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'rpc', 'extend', 'security', 'server/index', 'servlet-support/index', 'http-context'], function (Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Security, Server, Servlet, HttpContext) {
                var module = aInitializer();
                aCommonDefiner(module, Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Security);
                for (var s in Server) {
                    (function () {
                        var p = s;
                        Object.defineProperty(module, p, {
                            enumerable: true,
                            get: function(){ return Server[p]; }
                        });
                    }());
                }
                for (var ss in Servlet) {
                    (function () {
                        var p = ss;
                        Object.defineProperty(module, p, {
                            enumerable: true,
                            get: function(){ return Servlet[p]; }
                        });
                    }());
                }
                Object.defineProperty(module, 'HttpContext', {
                    enumerable: true,
                    value: HttpContext
                });
                
                return module;
            });
        } catch (se) {
            // TSA server
            define(['environment', 'logger', 'resource', 'id', 'md5', 'template', 'invoke', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'rpc', 'extend', 'security', 'server/index'], function (Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Security, Server) {
                var module = aInitializer();
                aCommonDefiner(module, Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Security);
                for (var s in Server) {
                    (function () {
                        var p = s;
                        Object.defineProperty(module, p, {
                            enumerable: true,
                            get: function(){ return Server[p]; }
                        });
                    }());
                }
                return module;
            });
        }
    } catch (e) {
        // SE client
        define(['environment', 'logger', 'resource', 'id', 'md5', 'template', 'invoke', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'rpc', 'extend', 'security', 'forms/index', 'grid/index', 'ui', 'forms'], function (Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Security, FormsIndex, GridIndex, Ui, Forms) {
            var module = aInitializer();
            aCommonDefiner(module, Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Security);
            for (var f in FormsIndex) {
                (function () {
                    var p = f;
                    Object.defineProperty(module, p, {
                        enumerable: true,
                        get: function(){ return FormsIndex[p]; }
                    });
                }());
            }
            for (var g in GridIndex) {
                (function () {
                    var p = g;
                    Object.defineProperty(module, p, {
                        enumerable: true,
                        get: function(){ return GridIndex[p]; }
                    });
                }());
            }
            for (var u in Ui) {
                (function () {
                    var p = u;
                    Object.defineProperty(module, p, {
                        enumerable: true,
                        get: function(){ return Ui[p]; }
                    });
                }());
            }
            for (var f in Forms) {
                (function () {
                    var p = f;
                    Object.defineProperty(module, p, {
                        enumerable: true,
                        get: function(){ return Forms[p]; }
                    });
                }());
            }
            return module;
        });
    }
}(function () {
    var module = {};
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
    Object.defineProperty(module, "cacheBust", {
        value: function (aValue) {
            // not supported in SE client yet
        }
    });
    return module;
}, function (module, Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Security) {
    for (var e in Environment) {
        (function () {
            var p = e;
            Object.defineProperty(module, p, {
                enumerable: true,
                get: function(){ return Environment[p]; }
            });
        }());
    }
    Object.defineProperty(module, 'Logger', {
        enumerable: true,
        value: Logger
    });
    Object.defineProperty(module, 'principal', {
        enumerable: true,
        get: function () {
            return Security.principal();
        }
    });
    Object.defineProperty(module, 'Resource', {
        enumerable: true,
        value: Resource
    });
    Object.defineProperty(module, 'ID', {
        enumerable: true,
        value: Id
    });
    Object.defineProperty(module, 'IDGenerator', {
        enumerable: true,
        value: Id
    });
    Object.defineProperty(module, 'MD5', {
        enumerable: true,
        value: Md5
    });
    Object.defineProperty(module, 'MD5Generator', {
        enumerable: true,
        value: Md5
    });
    for (var o in Orm) {
        (function () {
            var p = o;
            Object.defineProperty(module, p, {
                enumerable: true,
                get: function(){ return Orm[p]; }
            });
        }());
    }
    for (var c in Core) {
        (function () {
            var p = c;
            Object.defineProperty(module, p, {
                enumerable: true,
                get: function(){ return Core[p]; }
            });
        }());
    }
    for (var d in Datamodel) {
        (function () {
            var p = d;
            Object.defineProperty(module, p, {
                enumerable: true,
                get: function(){ return Datamodel[p]; }
            });
        }());
    }
    for (var r in Reports) {
        (function () {
            var p = r;
            Object.defineProperty(module, p, {
                enumerable: true,
                get: function(){ return Reports[p]; }
            });
        }());
    }
    Object.defineProperty(module, 'loadTemplate', {
        enumerable: true,
        value: loadTemplate
    });
    Object.defineProperty(module, "invokeDelayed", {
        value: Invoke.delayed
    });
    Object.defineProperty(module, 'invokeLater', {
        value: Invoke.later
    });
    Object.defineProperty(module, 'ServerModule', {
        enumerable: true,
        value: Rpc.Proxy
    });
    Object.defineProperty(module, 'requireRemotes', {
        enumerable: true,
        value: Rpc.requireRemotes
    });
    Object.defineProperty(module, 'extend', {
        enumerable: true,
        value: extend
    });
}));
