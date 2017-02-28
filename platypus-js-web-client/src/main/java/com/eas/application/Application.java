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
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;

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
		NodeList<Element> scriptTags = Document.get().getElementsByTagName("script");
		String init = null;
		String entryPoint = null;
		String sourcePrefix = null;
		boolean simpleModules = false;
		int gwtScripts = 0;
		for (int s = 0; s < scriptTags.getLength() && gwtScripts < 2; s++) {
			ScriptElement script = scriptTags.getItem(s).cast();
			if (script.getSrc().endsWith("pwc.nocache.js")) {
				gwtScripts++;
				if (script.hasAttribute("init")) {
					init = script.getAttribute("init");
					if (!init.toLowerCase().endsWith(".js")) {
						init += ".js";
					}
				}
				if (script.hasAttribute("entry-point")) {
					entryPoint = script.getAttribute("entry-point");
					if (!entryPoint.toLowerCase().endsWith(".js")) {
						entryPoint += ".js";
					}
				}
				if (script.hasAttribute("source-path")) {
					sourcePrefix = script.getAttribute("source-path").toLowerCase();
				}
				simpleModules = script.hasAttribute("simple-modules");
			}
		}
		AppClient.setSimpleModules(simpleModules);
		AppClient.setSourcePath(sourcePrefix);
		if (entryPoint != null && !entryPoint.isEmpty()) {
			if (init != null && !init.isEmpty()) {
				final String _entryPoint = entryPoint;
				ScriptInjector.fromUrl(AppClient.relativeUri() + AppClient.getSourcePath() + init).setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).setCallback(new Callback<Void, Exception>() {
					@Override
					public void onFailure(Exception reason) {
						Logger.getLogger(Application.class.getName()).log(Level.SEVERE, "Error while initializing modules loader", reason);
					}

					@Override
					public void onSuccess(Void result) {
						ScriptInjector.fromUrl(AppClient.relativeUri() + AppClient.getSourcePath() + _entryPoint).setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
					}
				}).inject();
			} else {
				ScriptInjector.fromUrl(AppClient.relativeUri() + AppClient.getSourcePath() + entryPoint).setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
			}
		} else {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, "\"entry-point\" attribute missing while initializing modules loader");
		}
	}

	private static JavaScriptObject lookupResolved(List<String> deps) {
		Map<String, JavaScriptObject> defined = Loader.getDefined();
		Utils.JsObject resolved = JavaScriptObject.createArray().cast();
		for (int d = 0; d < deps.size(); d++) {
			String mName = deps.get(d);
			JavaScriptObject m = defined.get(mName);
			resolved.setSlot(d, m != null ? m : Loader.lookupInGlobal(mName));
		}
		return resolved;
	}

	public static JavaScriptObject require(final Utils.JsObject aDeps, final Utils.JsObject aOnSuccess, final Utils.JsObject aOnFailure) throws Exception {
		String calledFromDir = Utils.lookupCallerJsDir();
		final List<String> deps = new ArrayList<String>();
		for (int i = 0; i < aDeps.length(); i++) {
			String dep = aDeps.getString(i);
			if (calledFromDir != null && dep.startsWith("./") || dep.startsWith("../")) {
				dep = AppClient.toFilyAppModuleId(dep, calledFromDir);
			}
			if (dep.toLowerCase().endsWith(".js")) {
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

	public static void define(String aModuleName, final Utils.JsObject aDeps, final Utils.JsObject aModuleDefiner) {
		String calledFromFile = Utils.lookupCallerJsFile();
		int lastSlashIndex = calledFromFile.lastIndexOf('/');
		String calledFromDir = calledFromFile.substring(0, lastSlashIndex);
		String calledFromFileShort = calledFromFile.substring(lastSlashIndex + 1, calledFromFile.length());
		final List<String> deps = new ArrayList<String>();
		for (int i = 0; i < aDeps.length(); i++) {
			String dep = aDeps.getString(i);
			if (calledFromDir != null && dep.startsWith("./") || dep.startsWith("../")) {
				dep = AppClient.toFilyAppModuleId(dep, calledFromDir);
			}
			if (dep.endsWith(".js")) {
				dep = dep.substring(0, dep.length() - 3);
			}
			deps.add(dep);
		}
		if (aModuleName == null) {
			aModuleName = AppClient.toFilyAppModuleId("./" + calledFromFileShort, calledFromDir);
			if (aModuleName.endsWith(".js")) {
				aModuleName = aModuleName.substring(0, aModuleName.length() - 3);
			}
		}
		Loader.addAmdDefine(aModuleName, deps, new Callback<String, Void>() {

			@Override
			public void onSuccess(String aModuleName) {
				Map<String, JavaScriptObject> defined = Loader.getDefined();
				Utils.JsObject resolved = JavaScriptObject.createArray().cast();
				for (int d = 0; d < deps.size(); d++) {
					String mName = deps.get(d);
					JavaScriptObject m = defined.get(mName);
					resolved.setSlot(d, m != null ? m : Loader.lookupInGlobal(mName));
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
