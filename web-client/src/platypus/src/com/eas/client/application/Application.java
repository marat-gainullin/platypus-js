/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.AppClient;
import com.eas.client.CallbackAdapter;
import com.eas.client.GroupingHandlerRegistration;
import com.eas.client.Loader;
import com.eas.client.PlatypusLogFormatter;
import com.eas.client.Utils;
import com.eas.client.application.js.JsContainers;
import com.eas.client.application.js.JsEvents;
import com.eas.client.application.js.JsApi;
import com.eas.client.application.js.JsModelWidgets;
import com.eas.client.application.js.JsWidgets;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * 
 * @author mg
 */
public class Application {

	protected static class LoggingLoadHandler implements Loader.LoadHandler {

		public LoggingLoadHandler() {
			super();
		}

		@Override
		public void started(String anItemName) {
			final String message = "Loading... " + anItemName;
			platypusApplicationLogger.log(Level.INFO, message);
		}

		@Override
		public void loaded(String anItemName) {
			final String message = anItemName + " - Loaded";
			platypusApplicationLogger.log(Level.INFO, message);
		}
	}

	public static Logger platypusApplicationLogger;
	protected static Map<String, Query> appQueries = new HashMap<String, Query>();
	protected static Loader loader;
	protected static GroupingHandlerRegistration loaderHandlerRegistration = new GroupingHandlerRegistration();

	public static Query getAppQuery(String aQueryId) {
		Query query = appQueries.get(aQueryId);
		if (query != null) {
			AppClient client = query.getClient();
			query = query.copy();
			query.setClient(client);
		}
		return query;
	}

	public static Query putAppQuery(Query aQuery) {
		return appQueries.put(aQuery.getEntityName(), aQuery);
	}
	
	public static void jsLoadQueries(JavaScriptObject aQueries, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
		List<String> queries = new ArrayList<>();
		Utils.JsObject jsQueries = aQueries.cast();
		for(int i = 0; i < jsQueries.length(); i++){
			queries.add(jsQueries.getString(i));
		}
		loader.loadQueries(queries, new Callback<Void, String>(){

			@Override
            public void onSuccess(Void result) {
				if(aOnSuccess != null){
					aOnSuccess.<Utils.JsObject>cast().apply(null, null);
				}
            }

			@Override
            public void onFailure(String aReason) {
				if(aOnFailure != null){
					aOnFailure.<Utils.JsObject>cast().call(null, aReason);
				}
            }
		});
	}
	
	public static void jsLoadServerModules(JavaScriptObject aModulesNames, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
		List<String> modulesNames = new ArrayList<>();
		Utils.JsObject jsModulesNames = aModulesNames.cast();
		for(int i = 0; i < jsModulesNames.length(); i++){
			modulesNames.add(jsModulesNames.getString(i));
		}
		loader.loadServerModules(modulesNames, new Callback<Void, String>(){

			@Override
            public void onSuccess(Void result) {
				if(aOnSuccess != null){
					aOnSuccess.<Utils.JsObject>cast().apply(null, null);
				}
            }

			@Override
            public void onFailure(String aReason) {
				if(aOnFailure != null){
					aOnFailure.<Utils.JsObject>cast().call(null, aReason);
				}
            }
		});
	}
	
	public static native boolean isCacheBustEnabled() /*-{
		return !!$wnd.P.cacheBust;
	}-*/;
	
	public static native JavaScriptObject createReport(String reportLocation)/*-{
		return new $wnd.P.Report(reportLocation);
	}-*/;

	public static void run() throws Exception {
		run(AppClient.getInstance());
	}

	public static void run(AppClient client) throws Exception {
		if (LogConfiguration.loggingIsEnabled()) {
			platypusApplicationLogger = Logger.getLogger("platypusApplication");
			Formatter f = new PlatypusLogFormatter(true);
			Handler[] handlers = Logger.getLogger("").getHandlers();
			for (Handler h : handlers) {
				h.setFormatter(f);
			}
		}
		JsApi.init();
		JsWidgets.init();
		JsContainers.init();
		JsModelWidgets.init();
		JsEvents.init();
		loader = new Loader(client);
		loaderHandlerRegistration.add(loader.addHandler(new LoggingLoadHandler()));
		client.requestLoggedInUser(new CallbackAdapter<String, String>() {

			@Override
			protected void doWork(String aResult) throws Exception {
				onReady();
			}

			@Override
			public void onFailure(String reason) {
				onError(reason);
				Logger.getLogger(Application.class.getName()).log(Level.SEVERE, reason);
			}
		});
	}

	public static JavaScriptObject prerequire(String aModuleName) {
		return loader.getDefined().get(aModuleName);
	}

	public static void predefine(String aModuleName, JavaScriptObject aModule) {
		loader.getDefined().put(aModuleName, aModule);
	}

	public static void require(final Utils.JsObject aDeps, final Utils.JsObject aOnSuccess, final Utils.JsObject aOnFailure) {
		String calledFromDir = Utils.lookupCallerJsDir();
		final List<String> deps = new ArrayList<String>();
		for (int i = 0; i < aDeps.length(); i++) {
			String dep = aDeps.getString(i);
			if (calledFromDir != null && dep.startsWith("./") || dep.startsWith("../")) {
				dep = AppClient.toAppModuleId(dep, calledFromDir);
			}
			if(dep.endsWith(".js")){
				dep = dep.substring(0, dep.length() - 3);
			}
			deps.add(dep);
		}
		try {
			loader.load(deps, new CallbackAdapter<Void, String>() {

				@Override
				public void onFailure(String reason) {
					if (aOnFailure != null) {
						try {
							Utils.executeScriptEventString(aOnFailure, aOnFailure, reason);
						} catch (Exception ex) {
							Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
						}
					} else {
						Logger.getLogger(Application.class.getName()).log(Level.WARNING, "Require failed and callback is missing. Required modules are: " + aDeps.toString());
					}
				}

	        	protected final native JavaScriptObject lookupInGlobal(String aModuleName)/*-{
        			return $wnd[aModuleName];
        		}-*/;
        	
				@Override
				protected void doWork(Void aResult) throws Exception {
					if (aOnSuccess != null){
		                Map<String, JavaScriptObject> defined = loader.getDefined();
		                Utils.JsObject resolved = JavaScriptObject.createArray().cast();
		                for (int d = 0; d < deps.size(); d++) {
		                	String mName = deps.get(d);
		                	JavaScriptObject m = defined.get(mName);
		                    resolved.setSlot(d, m != null ? m : lookupInGlobal(mName));
		                }
						aOnSuccess.apply(null, resolved);
					} else
						Logger.getLogger(Application.class.getName()).log(Level.WARNING, "Require succeded, but callback is missing. Required modules are: " + aDeps.toString());
				}
			}, new HashSet<String>());
		} catch (Exception ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void define(final Utils.JsObject aDeps, final Utils.JsObject aModuleDefiner) {
		String calledFromDir = Utils.lookupCallerJsDir();
		final List<String> deps = new ArrayList<String>();
		for (int i = 0; i < aDeps.length(); i++) {
			String dep = aDeps.getString(i);
			if (calledFromDir != null && dep.startsWith("./") || dep.startsWith("../")) {
				dep = AppClient.toAppModuleId(dep, calledFromDir);
			}
			if(dep.endsWith(".js")){
				dep = dep.substring(0, dep.length() - 3);
			}
			deps.add(dep);
		}
        loader.setAmdDefine(deps, new Callback<String, Void> () {
        	
        	protected final native JavaScriptObject lookupInGlobal(String aModuleName)/*-{
        		return $wnd[aModuleName];
        	}-*/;
        	
        	@Override
        	public void onSuccess(String aModuleName) {
                Map<String, JavaScriptObject> defined = loader.getDefined();
                Utils.JsObject resolved = JavaScriptObject.createArray().cast();
                for (int d = 0; d < deps.size(); d++) {
                	String mName = deps.get(d);
                	JavaScriptObject m = defined.get(mName);
                    resolved.setSlot(d, m != null ? m : lookupInGlobal(mName));
                }
                resolved.setSlot(deps.size(), aModuleName);
                JavaScriptObject module = (JavaScriptObject)aModuleDefiner.apply(null, resolved);
                defined.put(aModuleName, module);
        	}
        	
        	@Override
        	public void onFailure(Void reason) {
        		// no op
        	}
        	
        });
	}	
}
