/*global Java, Function*/
(function (aInitializer, aCommonDefiner) {
    try {
        Java.type('com.eas.server.PlatypusServerCore');
        // Server ...
        try {
            Java.type('com.eas.server.httpservlet.PlatypusHttpServlet');
            // Servlet container (may be EE server)
            define(['environment', 'logger', 'resource', 'id', 'md5', 'template', 'invoke', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'rpc', 'extend', 'server/index', 'servlet-support/index', 'http-context'], function (Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Server, Servlet, Http) {
                var module = aInitializer();
                aCommonDefiner(module, Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend);
                for (var s in Server) {
                    Object.defineProperty(module, s, {
                        enumerable: true,
                        value: Server[s]
                    });
                }
                for (var ss in Servlet) {
                    Object.defineProperty(module, ss, {
                        enumerable: true,
                        value: Servlet[ss]
                    });
                }
                for (var h in Http) {
                    Object.defineProperty(module, h, {
                        enumerable: true,
                        value: Http[h]
                    });
                }
                return module;
            });
        } catch (se) {
            // TSA server
            define(['environment', 'logger', 'resource', 'id', 'md5', 'template', 'invoke', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'rpc', 'extend', 'server/index'], function (Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Server) {
                var module = aInitializer();
                aCommonDefiner(module, Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend);
                for (var s in Server) {
                    Object.defineProperty(module, s, {
                        enumerable: true,
                        value: Server[s]
                    });
                }
                return module;
            });
        }
    } catch (e) {
        // SE client
        define(['environment', 'logger', 'resource', 'id', 'md5', 'template', 'invoke', 'orm', 'core/index', 'datamodel/index', 'reports/index', 'rpc', 'extend', 'ui'], function (Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend, Ui) {
            var module = aInitializer();
            aCommonDefiner(module, Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend);
            for (var i in Ui) {
                Object.defineProperty(module, i, {
                    enumerable: true,
                    value: Ui[i]
                });
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
    return module;
}, function (module, Environment, Logger, Resource, Id, Md5, loadTemplate, Invoke, Orm, Core, Datamodel, Reports, Rpc, extend) {
    for (var e in Environment) {
        Object.defineProperty(module, e, {
            enumerable: true,
            value: Orm[e]
        });
    }
    Object.defineProperty(module, 'Logger', {
        enumerable: true,
        value: Logger
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
        Object.defineProperty(module, o, {
            enumerable: true,
            value: Orm[o]
        });
    }
    for (var c in Core) {
        Object.defineProperty(module, c, {
            enumerable: true,
            value: Core[c]
        });
    }
    for (var d in Datamodel) {
        Object.defineProperty(module, d, {
            enumerable: true,
            value: Datamodel[d]
        });
    }
    for (var r in Reports) {
        Object.defineProperty(module, r, {
            enumerable: true,
            value: Reports[r]
        });
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
