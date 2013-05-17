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

import com.eas.client.Cancellable;
import com.eas.client.CancellableCallback;
import com.eas.client.CumulativeCallbackAdapter;
import com.eas.client.DocumentCallbackAdapter;
import com.eas.client.ResponseCallbackAdapter;
import com.eas.client.StringCallbackAdapter;
import com.eas.client.queries.Query;
import com.eas.client.queries.QueryCallbackAdapter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;

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
	protected Set<String> touchedAppElements = new HashSet();
	protected Set<String> loadedAppElements = new HashSet();
	protected Map<String, String> appElementsErrors = new HashMap();
	protected Map<String, ScriptElement> injectedScripts = new HashMap();
	protected Set<LoadHandler> handlers = new HashSet();

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

	public Cancellable load(final Collection<String> aAppElementNames, final CancellableCallback onSuccess) throws Exception {
		final Collection<Cancellable> loadingsStarted = new ArrayList();
		List<String> appElementNames = new ArrayList<String>();
		for (String appElementName : aAppElementNames) {
			if (!isTouched(appElementName))
				appElementNames.add(appElementName);
		}
		final CancellableCallback loaded = new CumulativeCallbackAdapter(appElementNames.size() * 2) {

			@Override
			protected void doWork() throws Exception {
				onSuccess.run();
			}

			@Override
			public void cancel() {
				for (Cancellable toCancel : loadingsStarted)
					toCancel.cancel();
			}
		};

		for (final String appElementName : appElementNames) {
			loadingsStarted.add(client.getAppElement(appElementName, new DocumentCallbackAdapter() {

				Set<Cancellable> loadings = new HashSet();

				@Override
				protected void doWork(Document aDoc) throws Exception {
					Set<String> dependencies = new HashSet();
					Set<String> queryDependencies = new HashSet();
					Set<String> serverModuleDependencies = new HashSet();
					if (aDoc != null)// There are some application element
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
					fireLoaded(appElementName);
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
				protected void doWork(Response aResponse) throws Exception {
					// Erroneous dependencies and other erroneous application
					// elements
					// should be memorized as notifying about the error.
					assert !loadedAppElements.contains(appElementName);
					appElementsErrors.put(appElementName, aResponse.getStatusText());
					Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, aResponse.getStatusText());
					loaded.run();
				}

			}));
			//
			String jsURL = client.scriptUrl(appElementName);
			ScriptElement oldScriptTag = injectedScripts.get(jsURL);
			if (oldScriptTag != null)
				oldScriptTag.removeFromParent();
			ScriptElement scriptTag = ScriptInjector.fromUrl(jsURL).setCallback(new Callback<Void, Exception>() {

				@Override
				public void onFailure(Exception reason) {
					try {
						assert !loadedAppElements.contains(appElementName);
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, reason.getMessage());
						// appElementsErrors.put(appElementName,
						// aResponse.getStatusText());
						loaded.run();
					} catch (Exception ex) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, ex.getMessage());
					}
				}

				@Override
				public void onSuccess(Void result) {
					try {
						loaded.run();
					} catch (Exception ex) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}).setWindow(ScriptInjector.TOP_WINDOW).inject().cast();
			scriptTag.addClassName(INJECTED_SCRIPT_CLASS_NAME);
			injectedScripts.put(jsURL, scriptTag);
			//
			fireStarted(appElementName);
		}
		return loaded;
	}

	private Cancellable loadServerModules(Collection<String> aAppElementNames, final CancellableCallback onSuccess) throws Exception {
		List<String> appElementNames = new ArrayList<String>();
		for (String appElementName : aAppElementNames) {
			if (!isTouched(appElementName))
				appElementNames.add(appElementName);
		}
		final Collection<Cancellable> startLoadings = new ArrayList();
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
		final Collection<Cancellable> startLoadings = new ArrayList();
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

	public boolean isLoaded(String aAppElementId) {
		return loadedAppElements.contains(aAppElementId);
	}

	public void prepareOptimistic() {
		touchedAppElements.removeAll(appElementsErrors.keySet());
	}

}
