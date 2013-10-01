/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.Cancellable;
import com.eas.client.CancellableCallback;
import com.eas.client.CumulativeCallbackAdapter;
import com.eas.client.DocumentCallbackAdapter;
import com.eas.client.PlatypusHttpRequestParams;
import com.eas.client.ResponseCallbackAdapter;
import com.eas.client.StringCallbackAdapter;
import com.eas.client.Utils;
import com.eas.client.queries.Query;
import com.eas.client.queries.QueryCallbackAdapter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * @author mg
 */
public class Loader {

	public interface LoadHandler {
		public void started(String anItemName);

		public void loaded(String anItemName);
	}

	public static final String INJECTED_SCRIPT_CLASS_NAME = "platypus-injected-script";
	public static final String DEPENDENCY_TAG_NAME = "dependency";
	public static final String SERVER_DEPENDENCY_TAG_NAME = "serverDependency";
	public static final String MODEL_TAG_NAME = "datamodel";
	public static final String ENTITY_TAG_NAME = "entity";
	public static final String QUERY_ID_ATTR_NAME = "queryId";
	public static final String SCRIPT_SOURCE_TAG_NAME = "source";
	public static final String TYPE_JAVASCRIPT = "text/javascript";
	protected static final com.google.gwt.dom.client.Document htmlDom = com.google.gwt.dom.client.Document.get();
	protected AppClient client;
	protected Set<String> touchedAppElements = new HashSet<String>();
	protected Set<String> loadedAppElements = new HashSet<String>();
	protected Map<String, String> appElementsErrors = new HashMap<String, String>();
	//protected Map<String, ScriptElement> injectedScripts = new HashMap<String, ScriptElement>();
	protected Set<LoadHandler> handlers = new HashSet<LoadHandler>();

	public Loader(AppClient aClient) {
		client = aClient;
	}

	public String getAppElementError(String aAppElementId) {
		return appElementsErrors.get(aAppElementId);
	}

	protected void fireStarted(String anItemName) {
		touchedAppElements.add(anItemName);
		for (LoadHandler lh : handlers) {
			lh.started(anItemName);
		}
	}

	protected void fireLoaded(String anItemName) {
		appElementsErrors.remove(anItemName);
		loadedAppElements.add(anItemName);
		for (LoadHandler lh : handlers) {
			lh.loaded(anItemName);
		}
	}

	public HandlerRegistration addHandler(final LoadHandler aHandler) {
		handlers.add(aHandler);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				handlers.remove(aHandler);
			}
		};
	}

	protected static native void injectPlaypusModuleCallback(String aAppElementName, JavaScriptObject aCallback)/*-{
		if(!$wnd.platypusModulesOnLoad)
			$wnd.platypusModulesOnLoad = {};
		$wnd.platypusModulesOnLoad[aAppElementName] = aCallback;
	}-*/;
	
	public Cancellable load(final Collection<String> aAppElementNames, final CancellableCallback onEnd) throws Exception {
		final Collection<Cancellable> loadingsStarted = new ArrayList<Cancellable>();
		List<String> appElementNames = new ArrayList<String>();
		for (String appElementName : aAppElementNames) {
			if (!isTouched(appElementName)){
				appElementNames.add(appElementName);
			}
		}
		final CancellableCallback loaded = new CumulativeCallbackAdapter(appElementNames.size() * 2) {

			@Override
			protected void doWork() throws Exception {
				onEnd.run();
			}

			@Override
			public void cancel() {
				for (Cancellable toCancel : loadingsStarted)
					toCancel.cancel();
			}
		};

		for (final String appElementName : appElementNames) {
			
			final CancellableCallback fireLoaded = new CumulativeCallbackAdapter(2) {

				@Override
				protected void doWork() throws Exception {
					fireLoaded(appElementName);
				}

				@Override
				public void cancel() {
				}
			};
			
			loadingsStarted.add(client.getAppElement(appElementName, new DocumentCallbackAdapter() {

				Set<Cancellable> loadings = new HashSet<Cancellable>();

				@Override
				protected void doWork(Document aDoc) throws Exception {
					Set<String> dependencies = new HashSet<String>();
					Set<String> queryDependencies = new HashSet<String>();
					Set<String> serverModuleDependencies = new HashSet<String>();
					if (aDoc != null)// There are some application elements
					                 // without xml-dom (plain scripts for
					                 // example)
					{
						Element rootNode = aDoc.getDocumentElement();
						NodeList docNodes = rootNode.getChildNodes();
						for (int i = 0; i < docNodes.getLength(); i++) {
							Node docNode = docNodes.item(i);
							// Don't refactor to switch, since GWT2.4
							// doesn't support it, applied to strings.
							// In further versions it seems to be, but
							// unfortunately not yet.
							if (DEPENDENCY_TAG_NAME.equals(docNode.getNodeName())) {
								String dependency = docNode.getFirstChild().getNodeValue();
								if (dependency != null && !dependency.isEmpty() && !touchedAppElements.contains(dependency))
									dependencies.add(dependency);
							} else if (SERVER_DEPENDENCY_TAG_NAME.equals(docNode.getNodeName())) {
								String dependency = docNode.getFirstChild().getNodeValue();
								if (dependency != null && !dependency.isEmpty() && !touchedAppElements.contains(dependency))
									serverModuleDependencies.add(dependency);
							} else if (MODEL_TAG_NAME.equals(docNode.getNodeName())) {
								assert docNode instanceof Element;
								Element modelTag = (Element) docNode;
								NodeList entitiesList = modelTag.getElementsByTagName(ENTITY_TAG_NAME);
								for (int j = 0; j < entitiesList.getLength(); j++) {
									Node entityNode = entitiesList.item(j);
									assert entityNode instanceof Element;
									Element entityTag = (Element) entityNode;
									String dependency = entityTag.getAttribute(QUERY_ID_ATTR_NAME);
									if (dependency != null && !dependency.isEmpty() && !touchedAppElements.contains(dependency)) {
										queryDependencies.add(dependency);
									}
								}
							}
						}
					}
					fireLoaded.run();
					if (!dependencies.isEmpty() || !serverModuleDependencies.isEmpty() || !queryDependencies.isEmpty()) {
						int accumulate = 0;
						if (!dependencies.isEmpty())
							++accumulate;
						if (!queryDependencies.isEmpty())
							++accumulate;
						if (!serverModuleDependencies.isEmpty())
							++accumulate;
						CancellableCallback onDependenciesLoaded = new CumulativeCallbackAdapter(accumulate) {

							@Override
							protected void doWork() throws Exception {
								loaded.run();
							}
						};
						if (!dependencies.isEmpty()) {
							loadings.add(load(dependencies, onDependenciesLoaded));
						}
						if (!queryDependencies.isEmpty()) {
							loadings.add(loadQueries(queryDependencies, onDependenciesLoaded));
						}
						if (!serverModuleDependencies.isEmpty()) {
							loadings.add(loadServerModules(serverModuleDependencies, onDependenciesLoaded));
						}
					} else {
						loaded.run();
					}
				}

				@Override
				public void cancel() {
					super.cancel();
					for (Cancellable loading : loadings) {
						loading.cancel();
					}
					loadings.clear();
				}
			}, new ResponseCallbackAdapter() {

				@Override
				protected void doWork(XMLHttpRequest aResponse) throws Exception {
					Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, aResponse.getStatusText());
					assert !loadedAppElements.contains(appElementName);
					// Erroneous dependencies and other erroneous application
					// elements should be memorized as notifying about the
					// error.
					if(!appElementsErrors.containsKey(appElementName))
						appElementsErrors.put(appElementName, aResponse.getStatusText());
					loaded.run();
				}

			}));
			//
			String jsURL = client.resourceUrl(appElementName);// + "?"+PlatypusHttpRequestParams.CACHE_BUSTER+"=" + IDGenerator.genId();
			injectPlaypusModuleCallback(appElementName, Utils.publishRunnable(new Runnable(){

				@Override
                public void run() {
					try {
						injectPlaypusModuleCallback(appElementName, null);
						fireLoaded.run();
						loaded.run();
					} catch (Exception ex) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
					}
                }
				
			}));
			/*
			ScriptElement oldScriptTag = injectedScripts.get(jsURL);
			if (oldScriptTag != null)
				oldScriptTag.removeFromParent();
			ScriptElement scriptTag = 
			*/	
			ScriptInjector.fromUrl(jsURL)
			.setCallback(new Callback<Void, Exception>() {

				@Override
				public void onSuccess(Void result) {
					// app element script-calling callback is used
				}
				
				@Override
				public void onFailure(Exception reason) {
					try {
						injectPlaypusModuleCallback(appElementName, null);
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, "Script [" + appElementName + "] is not loaded. Cause is: " + reason.getMessage());
						assert !loadedAppElements.contains(appElementName);
						// Erroneous dependencies and other erroneous application
						// elements should be memorized as notifying about the
						// error.
						appElementsErrors.put(appElementName, reason.getMessage());
						loaded.run();
					} catch (Exception ex) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

			})
			.setWindow(ScriptInjector.TOP_WINDOW)
			.setRemoveTag(false)
			.inject();
			/*
			scriptTag.addClassName(INJECTED_SCRIPT_CLASS_NAME);
			injectedScripts.put(jsURL, scriptTag);
			*/
			fireStarted(appElementName);
		}
		return loaded;
	}

	private Cancellable loadServerModules(Collection<String> aAppElementNames, final CancellableCallback onEnd) throws Exception {
		List<String> appElementNames = new ArrayList<String>();
		for (String appElementName : aAppElementNames) {
			if (!isTouched(appElementName))
				appElementNames.add(appElementName);
		}
		final Collection<Cancellable> startLoadings = new ArrayList<Cancellable>();
		final CancellableCallback loaded = new CumulativeCallbackAdapter(appElementNames.size()) {

			@Override
			protected void doWork() throws Exception {
				onEnd.run();
			}

			@Override
			public void cancel() {
				for (Cancellable toCancel : startLoadings)
					toCancel.cancel();
			}
		};

		for (final String appElementName : appElementNames) {
			
			startLoadings.add(client.createServerModule(appElementName, new DocumentCallbackAdapter() {

				@Override
				protected void doWork(Document aDoc) throws Exception {
					String source = null;
					Element rootNode = aDoc.getDocumentElement();
					NodeList docNodes = rootNode.getChildNodes();
					for (int i = 0; i < docNodes.getLength(); i++) {
						Node docNode = docNodes.item(i);
						// Don't refactor to switch, since GWT2.4
						// doesn't support it, applied to strings.
						// In further versions it seems to be, but
						// unfortunately not yet.
						if (SCRIPT_SOURCE_TAG_NAME.equals(docNode.getNodeName())) {
							source = docNode.getFirstChild().getNodeValue();
						}
					}

					if (source != null && !source.isEmpty()) {
						ScriptElement scriptElement = htmlDom.createScriptElement(source);
						scriptElement.setType(TYPE_JAVASCRIPT);
						htmlDom.getBody().appendChild(scriptElement);
					}
					fireLoaded(appElementName);
					loaded.run();
				}

			}, new StringCallbackAdapter() {
				@Override
				protected void doWork(String aResult) throws Exception {
					assert !loadedAppElements.contains(appElementName);
					appElementsErrors.put(appElementName, aResult);
					loaded.run();
					Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, aResult);
				}
			}));
			fireStarted(appElementName);
		}
		return loaded;
	}

	/**
	 * This method is public ONLY because of tests!
	 * 
	 * @param aAppElementNames
	 * @param onSuccess
	 * @return
	 * @throws Exception
	 */
	public Cancellable loadQueries(Collection<String> aAppElementNames, final CancellableCallback onSuccess) throws Exception {
		List<String> appElementNames = new ArrayList<String>();
		for (String appElementName : aAppElementNames) {
			if (!isTouched(appElementName))
				appElementNames.add(appElementName);
		}
		final Collection<Cancellable> startLoadings = new ArrayList<Cancellable>();
		final CancellableCallback loaded = new CumulativeCallbackAdapter(appElementNames.size()) {

			@Override
			protected void doWork() throws Exception {
				onSuccess.run();
			}

			@Override
			public void cancel() {
				for (Cancellable toCancel : startLoadings)
					toCancel.cancel();
			}
		};

		for (final String appElementName : appElementNames) {
			startLoadings.add(client.getAppQuery(appElementName, new QueryCallbackAdapter() {

				@Override
				public void run(Query aQuery) throws Exception {
					Application.putAppQuery(aQuery);
					fireLoaded(appElementName);
					loaded.run();
				}

			}, new StringCallbackAdapter() {
				@Override
				protected void doWork(String aResult) throws Exception {
					assert !loadedAppElements.contains(appElementName);
					appElementsErrors.put(appElementName, aResult);
					Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, aResult);
				}
			}));
			fireStarted(appElementName);
		}
		return loaded;
	}

	public boolean isTouched(String aAppElementId) {
		return touchedAppElements.contains(aAppElementId);
	}

	public boolean isLoaded(Set<String> aAppElementsIds) {
		return loadedAppElements.containsAll(aAppElementsIds);
	}
	
	public boolean isLoaded(String aAppElementId) {
		return loadedAppElements.contains(aAppElementId);
	}

	public void prepareOptimistic() {
		touchedAppElements.removeAll(appElementsErrors.keySet());
	}

}
