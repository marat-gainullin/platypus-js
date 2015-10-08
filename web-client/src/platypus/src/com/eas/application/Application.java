/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.application;

import java.util.ArrayList;
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
import com.eas.client.PlatypusLogFormatter;
import com.eas.core.Predefine;
import com.eas.core.Utils;
import com.eas.ui.JsUi;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;

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
			Predefine.platypusApplicationLogger.log(Level.INFO, message);
		}

		@Override
		public void loaded(String anItemName) {
			final String message = anItemName + " - Loaded";
			Predefine.platypusApplicationLogger.log(Level.INFO, message);
		}
	}

	protected static GroupingHandlerRegistration loaderHandlerRegistration = new GroupingHandlerRegistration();

	public static void run() throws Exception {
		Formatter f = new PlatypusLogFormatter(true);
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (Handler h : handlers) {
			h.setFormatter(f);
		}
		Predefine.init();
		JsApi.init();
		JsUi.init();
		loaderHandlerRegistration.add(Loader.addHandler(new LoggingLoadHandler()));
		Utils.JsObject window = ScriptInjector.TOP_WINDOW.cast();
		Utils.JsObject onReady = window.getJs("onPlatypusReady");
		if (onReady == null)
			onReady = window.getJs("onPlatypusReady1");
		if (onReady == null)
			onReady = window.getJs("onPlatypusReady2");
		if (onReady == null)
			onReady = window.getJs("onPlatypusReady3");
		if (onReady != null)
			onReady.apply(null, null);
	}

	protected static native JavaScriptObject lookupInGlobal(String aModuleName)/*-{
		return $wnd[aModuleName];
	}-*/;

	private static JavaScriptObject lookupResolved(List<String> deps) {
		Map<String, JavaScriptObject> defined = Loader.getDefined();
		Utils.JsObject resolved = JavaScriptObject.createArray().cast();
		for (int d = 0; d < deps.size(); d++) {
			String mName = deps.get(d);
			JavaScriptObject m = defined.get(mName);
			resolved.setSlot(d, m != null ? m : lookupInGlobal(mName));
		}
		return resolved;
	}

	public static JavaScriptObject require(final Utils.JsObject aDeps, final Utils.JsObject aOnSuccess, final Utils.JsObject aOnFailure) throws Exception {
		String calledFromDir = Utils.lookupCallerJsDir();
		final List<String> deps = new ArrayList<String>();
		for (int i = 0; i < aDeps.length(); i++) {
			String dep = aDeps.getString(i);
			if (calledFromDir != null && dep.startsWith("./") || dep.startsWith("../")) {
				dep = AppClient.toAppModuleId(dep, calledFromDir);
			}
			if (dep.endsWith(".js")) {
				dep = dep.substring(0, dep.length() - 3);
			}
			deps.add(dep);
		}
		Loader.load(deps, new CallbackAdapter<Void, String>() {

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

			@Override
			protected void doWork(Void aResult) throws Exception {
				if (aOnSuccess != null) {
					JavaScriptObject resolved = lookupResolved(deps);
					aOnSuccess.apply(null, resolved);
				} else
					Logger.getLogger(Application.class.getName()).log(Level.WARNING, "Require succeded, but callback is missing. Required modules are: " + aDeps.toString());
			}
		}, new HashSet<String>());
		return lookupResolved(deps);
	}

	public static void define(final Utils.JsObject aDeps, final Utils.JsObject aModuleDefiner) {
		String calledFromDir = Utils.lookupCallerJsDir();
		final List<String> deps = new ArrayList<String>();
		for (int i = 0; i < aDeps.length(); i++) {
			String dep = aDeps.getString(i);
			if (calledFromDir != null && dep.startsWith("./") || dep.startsWith("../")) {
				dep = AppClient.toAppModuleId(dep, calledFromDir);
			}
			if (dep.endsWith(".js")) {
				dep = dep.substring(0, dep.length() - 3);
			}
			deps.add(dep);
		}
		Loader.setAmdDefine(deps, new Callback<String, Void>() {

			protected final native JavaScriptObject lookupInGlobal(String aModuleName)/*-{
		return $wnd[aModuleName];
	}-*/;

			@Override
			public void onSuccess(String aModuleName) {
				Map<String, JavaScriptObject> defined = Loader.getDefined();
				Utils.JsObject resolved = JavaScriptObject.createArray().cast();
				for (int d = 0; d < deps.size(); d++) {
					String mName = deps.get(d);
					JavaScriptObject m = defined.get(mName);
					resolved.setSlot(d, m != null ? m : lookupInGlobal(mName));
				}
				resolved.setSlot(deps.size(), aModuleName);
				JavaScriptObject module = (JavaScriptObject) aModuleDefiner.apply(null, resolved);
				defined.put(aModuleName, module);
			}

			@Override
			public void onFailure(Void reason) {
				// no op
			}

		});
	}
}
