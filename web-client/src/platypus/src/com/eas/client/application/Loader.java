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

import com.bearsoft.rowset.CallbackAdapter;
import com.bearsoft.rowset.Cancellable;
import com.eas.client.CumulativeRunnableAdapter;
import com.eas.client.queries.Query;
import com.eas.client.xhr.UrlQueryProcessor;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
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

	public static final UrlQueryProcessor URL_PROCESSOR = GWT.create(UrlQueryProcessor.class);
	public static final String INJECTED_SCRIPT_CLASS_NAME = "platypus-injected-script";
	public static final String SERVER_MODULE_TOUCHED_NAME = "Proxy-";
	public static final String DEPENDENCY_TAG_NAME = "dependency";
	public static final String QUERY_DEPENDENCY_TAG_NAME = "entityDependency";
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
	// protected Map<String, ScriptElement> injectedScripts = new
	// HashMap<String, ScriptElement>();
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

	public void load(final Collection<String> aAppElementNames, final Runnable onEnd) throws Exception {
		final Collection<Cancellable> loadingsStarted = new ArrayList<Cancellable>();
		List<String> appElementNames = new ArrayList<String>();
		// Recursion handling
		for (String appElementName : aAppElementNames) {
			if (!isTouched(appElementName)) {
				appElementNames.add(appElementName);
			}
		}
		final Runnable onEndCaller = new CumulativeRunnableAdapter(appElementNames.size() * 2) {

			@Override
			protected void doWork(){
				if(onEnd != null){
					onEnd.run();
				}
			}
		};
		if (!appElementNames.isEmpty()) {
			for (final String appElementName : appElementNames) {

				final Runnable fireLoadedCaller = new CumulativeRunnableAdapter(2) {

					@Override
					protected void doWork(){
						fireLoaded(appElementName);
					}
					
				};

				loadingsStarted.add(client.getAppElementXml(appElementName, new CallbackAdapter<Document, XMLHttpRequest>() {

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
									if (dependency != null && !dependency.isEmpty() && !isLoaded(dependency))
										dependencies.add(dependency);
								} else if (SERVER_DEPENDENCY_TAG_NAME.equals(docNode.getNodeName())) {
									String dependency = docNode.getFirstChild().getNodeValue();
									if (dependency != null && !dependency.isEmpty() && !isTouched(SERVER_MODULE_TOUCHED_NAME + dependency))
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
										if (dependency != null && !dependency.isEmpty() && !isTouched(dependency)) {
											queryDependencies.add(dependency);
										}
									}
								} else if (QUERY_DEPENDENCY_TAG_NAME.equals(docNode.getNodeName())) {
									String dependency = docNode.getFirstChild().getNodeValue();
									if (dependency != null && !dependency.isEmpty() && !isTouched(dependency))
										queryDependencies.add(dependency);
								}
							}
						}					
						fireLoadedCaller.run();
						if (!dependencies.isEmpty() || !serverModuleDependencies.isEmpty() || !queryDependencies.isEmpty()) {
							int accumulate = 0;
							if (!dependencies.isEmpty())
								++accumulate;
							if (!queryDependencies.isEmpty())
								++accumulate;
							if (!serverModuleDependencies.isEmpty())
								++accumulate;
							Runnable onDependenciesLoaded = new CumulativeRunnableAdapter(accumulate) {

								@Override
								protected void doWork() {
									onEndCaller.run();
								}
							};
							if (!dependencies.isEmpty()) {
								load(dependencies, onDependenciesLoaded);
							}
							if (!queryDependencies.isEmpty()) {
								loadQueries(queryDependencies, onDependenciesLoaded);
							}
							if (!serverModuleDependencies.isEmpty()) {
								loadServerModules(serverModuleDependencies, onDependenciesLoaded);
							}
						} else {
							onEndCaller.run();
						}
					}
					
					@Override
					public void onFailure(XMLHttpRequest aResponse) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, appElementName + " loading response is: " + aResponse.getStatus() + " (" + aResponse.getStatusText() + ")");
						assert !loadedAppElements.contains(appElementName);
						// Erroneous dependencies and other erroneous
						// application
						// elements should be memorized as notifying about the
						// error.
						if (!appElementsErrors.containsKey(appElementName))
							appElementsErrors.put(appElementName, aResponse.getStatusText());
						onEndCaller.run();
					}
				}));
				//
				String jsURL = client.resourceUrl(appElementName);
				jsURL += URL_PROCESSOR.process("");
				ScriptInjector.fromUrl(jsURL).setCallback(new Callback<Void, Exception>() {

					@Override
					public void onSuccess(Void result) {
						try {
							fireLoadedCaller.run();
							onEndCaller.run();
						} catch (Exception ex) {
							Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

					@Override
					public void onFailure(Exception reason) {
						try {
							Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, "Script [" + appElementName + "] is not loaded. Cause is: " + reason.getMessage());
							assert !loadedAppElements.contains(appElementName);
							// Erroneous dependencies and other erroneous
							// application
							// elements should be memorized as notifying about
							// the
							// error.
							appElementsErrors.put(appElementName, reason.getMessage());
							onEndCaller.run();
						} catch (Exception ex) {
							Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

				}).setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true)// Another
				                                                          // attempts
				                                                          // may
				                                                          // occur.
				                                                          // So,
				                                                          // we
				                                                          // have
				                                                          // to
				                                                          // remove
				                                                          // the
				                                                          // tag.
				        .inject();
				fireStarted(appElementName);
			}
		} else{
			onEnd.run();
		}
	}

	private void loadServerModules(Collection<String> aAppElementNames, final Runnable onEnd) throws Exception {
		List<String> appElementNames = new ArrayList<String>();
		for (String appElementName : aAppElementNames) {
			if (!isTouched(SERVER_MODULE_TOUCHED_NAME + appElementName))
				appElementNames.add(appElementName);
		}
		final Collection<Cancellable> startLoadings = new ArrayList<Cancellable>();
		final Runnable onEndCaller = new CumulativeRunnableAdapter(appElementNames.size()) {

			@Override
			protected void doWork(){
				onEnd.run();
			}
		};
		if (!appElementNames.isEmpty()) {
			for (final String appElementName : appElementNames) {
				startLoadings.add(client.createServerModule(appElementName, new CallbackAdapter<Void, String>() {

					@Override
					public void doWork(Void aDoc) throws Exception {
						/*
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
						*/
						fireLoaded(SERVER_MODULE_TOUCHED_NAME + appElementName);
						onEndCaller.run();
					}

					@Override
					public void onFailure(String reason) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, reason);
						assert !loadedAppElements.contains(SERVER_MODULE_TOUCHED_NAME + appElementName);
						appElementsErrors.put(SERVER_MODULE_TOUCHED_NAME + appElementName, reason);
						onEndCaller.run();
					}
				}));
				fireStarted(SERVER_MODULE_TOUCHED_NAME + appElementName);
			}
		} else{
			onEnd.run();
		}
	}

	/**
	 * This method is public ONLY because of tests!
	 * 
	 * @param aAppElementNames
	 * @param onSuccess
	 * @return
	 * @throws Exception
	 */
	public void loadQueries(Collection<String> aAppElementNames, final Runnable onEnd) throws Exception {
		List<String> appElementNames = new ArrayList<String>();
		for (String appElementName : aAppElementNames) {
			if (!isTouched(appElementName))
				appElementNames.add(appElementName);
		}
		final Collection<Cancellable> startLoadings = new ArrayList<Cancellable>();
		final Runnable onEndInvoker = new CumulativeRunnableAdapter(appElementNames.size()) {

			@Override
			protected void doWork() {
				if(onEnd != null){
					onEnd.run();
				}
			}
		};
		if (!appElementNames.isEmpty()) {
			for (final String appElementName : appElementNames) {
				startLoadings.add(client.getAppQuery(appElementName, new CallbackAdapter<Query, String>() {

					@Override
					public void doWork(Query aQuery) throws Exception {
						Application.putAppQuery(aQuery);
						fireLoaded(appElementName);
						onEndInvoker.run();
					}
					
					@Override
					public void onFailure(String reason) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, reason);
						assert !loadedAppElements.contains(appElementName);
						appElementsErrors.put(appElementName, reason);
						onEndInvoker.run();
					}
				}));
				fireStarted(appElementName);
			}
		} else {
			onEnd.run();
		}
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
