package com.eas.facade;

public class JsFacade {

	public native static void init()/*-{

		function predefine(aDeps, aName, aDefiner) {
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}

		predefine(['environment', 'logger', 'resource', 'id', 'md5', 'invoke', 'orm', 'core/report', 'rpc', 'extend', 'ui', 'forms', 'forms/anchors', 'forms/form'], 'facade', function(Environment, Logger, Resource, Id, Md5,
				Invoke, Orm, Report, Rpc, extend, Ui, Forms, Anchors, Form) {

			var module = {};
			Object.defineProperty(module, "logout", {
				value : function(onSuccess, onFailure) {
					return module.principal.logout(onSuccess, onFailure);
				}
			});
			Object.defineProperty(module, "export", {
				value : function(aTarget) {
					module.require = aTarget.require;
					aTarget.P = module;
				}
			});
			for ( var e in Environment) {
				if (e === '$H')
					continue;
				(function() {
					var p = e;
					Object.defineProperty(module, p, {
						enumerable : true,
						get : function(){ return Environment[p]; }
					});
				}());
			}
			Object.defineProperty(module, 'Logger', {
				enumerable : true,
				value : Logger
			});
			Object.defineProperty(module, 'Resource', {
				enumerable : true,
				value : Resource
			});
			Object.defineProperty(module, 'ID', {
				enumerable : true,
				value : Id
			});
			Object.defineProperty(module, 'IdGenerator', {
				enumerable : true,
				value : Id
			});
			Object.defineProperty(module, 'MD5', {
				enumerable : true,
				value : Md5
			});
			Object.defineProperty(module, 'Form', {
				enumerable : true,
				value : Form
			});
			Object.defineProperty(module, 'MD5Generator', {
				enumerable : true,
				value : Md5
			});
			for ( var o in Orm) {
				if (o === '$H')
					continue;
				(function() {
					var p = o;
					Object.defineProperty(module, p, {
						enumerable : true,
						get : function(){ return Orm[p]; }
					});
				}());
			}
			Object.defineProperty(module, 'Report', {
				enumerable : true,
				value : Report
			});
			Object.defineProperty(module, "invokeDelayed", {
				value : Invoke.delayed
			});
			Object.defineProperty(module, 'invokeLater', {
				value : Invoke.later
			});
			Object.defineProperty(module, 'ServerModule', {
				enumerable : true,
				value : Rpc.Proxy
			});
			Object.defineProperty(module, 'requireRemotes', {
				enumerable : true,
				value : Rpc.requireRemotes
			});
			Object.defineProperty(module, 'extend', {
				enumerable : true,
				value : extend
			});
			// aka forms/index...
			function accept(constructors) {
				for (var c in constructors) {
					if (c === '$H')
						continue;
					Object.defineProperty(module, c, {
						enumerable : true,
						value : constructors[c]
					});
				}
			}
			accept(@com.eas.widgets.WidgetsPublisher::getConstructors()());
			accept(@com.eas.bound.BoundPublisher::getConstructors()());
			accept(@com.eas.grid.GridPublisher::getConstructors()());
			accept(@com.eas.menu.MenuPublisher::getConstructors()());
			accept(@com.eas.ui.EventsPublisher::getConstructors()());
			for (var u in Ui) {
				if (u === '$H')
					continue;
				(function() {
					var p = u;
					Object.defineProperty(module, p, {
						enumerable : true,
						get : function(){ return Ui[p]; }
					});
				}());
			}
			for (var f in Forms) {
				if (f === '$H')
					continue;
				(function() {
					var p = f;
                                        if(module[p] !== Forms[p]){ // See Forms.loadWidgets,Forms.readWidgets and Ui.loadWidgets,Ui.readWidgets
                                                Object.defineProperty(module, p, {
                                                        enumerable : true,
                                                        get : function(){ return Forms[p]; }
                                                });
                                        }
				}());
			}
			Object.defineProperty(module, 'Anchors', {
				enumerable : true,
				value : Anchors
			});			
			return module;
		});

	}-*/;
}
