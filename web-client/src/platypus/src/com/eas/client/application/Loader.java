/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.CallbackAdapter;
import com.bearsoft.rowset.Cancellable;
import com.eas.client.CumulativeCallbackAdapter;
import com.eas.client.queries.Query;
import com.eas.client.xhr.UrlQueryProcessor;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xml.client.Document;

/**
 * 
 * @author mg
 */
public class Loader {

	public interface LoadHandler {

		public void started(String anItemName);

		public void loaded(String anItemName);
	}

	public static class LoadProcess {

		protected int expected;
		protected Callback<Void, String> callback;
		protected int calls;

		protected Set<String> loaded = new HashSet<>();
		protected Set<String> errors = new HashSet<>();

		public LoadProcess(int aExpected, Callback<Void, String> aCallback) {
			super();
			expected = aExpected;
			callback = aCallback;
		}

		public void complete(String aLoaded, String aFailured) {
			if (aLoaded != null) {
				loaded.add(aLoaded);
			}
			if (aFailured != null)
				errors.add(aFailured);
			if (++calls == expected) {
				if (errors.isEmpty())
					callback.onSuccess(null);
				else
					callback.onFailure(null);
			}
		}
	}

	public static final UrlQueryProcessor URL_QUERY_PROCESSOR = GWT.create(UrlQueryProcessor.class);
	public static final String INJECTED_SCRIPT_CLASS_NAME = "platypus-injected-script";
	public static final String SERVER_MODULE_TOUCHED_NAME = "Proxy-";
	public static final String MODEL_TAG_NAME = "datamodel";
	public static final String ENTITY_TAG_NAME = "entity";
	public static final String QUERY_ID_ATTR_NAME = "queryId";
	public static final String SCRIPT_SOURCE_TAG_NAME = "source";
	public static final String TYPE_JAVASCRIPT = "text/javascript";
	protected static final com.google.gwt.dom.client.Document htmlDom = com.google.gwt.dom.client.Document.get();
	protected AppClient client;
	protected Set<LoadHandler> handlers = new HashSet<LoadHandler>();
	protected Set<String> started = new HashSet<>();

	public Loader(AppClient aClient) {
		client = aClient;
	}

	protected void fireStarted(String anItemName) {
		for (LoadHandler lh : handlers) {
			lh.started(anItemName);
		}
	}

	protected void fireLoaded(String anItemName) {
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

	public void load(final Collection<String> aModulesNames, final Callback<Void, String> aCallback) throws Exception {
		final Collection<Cancellable> loadingsStarted = new ArrayList<Cancellable>();
		List<String> modulesNames = new ArrayList<String>();
		// Recursion handling
		for (String moduleName : aModulesNames) {
			if (!isStarted(moduleName)) {
				modulesNames.add(moduleName);
			}
		}
		if (!modulesNames.isEmpty()) {
			final CumulativeCallbackAdapter<Void, String> process = new CumulativeCallbackAdapter<Void, String>(modulesNames.size()) {
	
				@Override
				protected void failed(List<String> aReasons) {
					if (aCallback != null) {
						aCallback.onFailure(aReasons.toString());
					}
				}
	
				@Override
				protected void doWork(Void aResult) throws Exception {
					if (aCallback != null)
						aCallback.onSuccess(null);
				}
	
			};
			for (final String moduleName : modulesNames) {
				loadingsStarted.add(client.requestModuleStructure(moduleName, new CallbackAdapter<AppClient.ModuleStructure, XMLHttpRequest>() {

					@Override
					protected void doWork(AppClient.ModuleStructure aStructure) throws Exception {
						final CumulativeCallbackAdapter<Void, String> moduleProcess = new CumulativeCallbackAdapter<Void, String>(2) {

							@Override
							protected void failed(List<String> aReasons) {
								process.onFailure(aReasons.toString());
							}

							@Override
							protected void doWork(Void aResult) throws Exception {
								fireLoaded(moduleName);
								process.onSuccess(null);
							}

						};
						final CumulativeCallbackAdapter<Void, String> structureProcess = new CumulativeCallbackAdapter<Void, String>(aStructure.getStructure().size()) {

							@Override
							protected void failed(List<String> aReasons) {
								moduleProcess.onFailure(aReasons.toString());
							}

							@Override
							protected void doWork(Void aResult) throws Exception {
								moduleProcess.onSuccess(null);
							}

						};
						assert !aStructure.getStructure().isEmpty() : "Module ["+moduleName+"] structure should contain at least one element.";
						for (String part : aStructure.getStructure()) {
							if (part.toLowerCase().endsWith(".js")) {
								String jsURL = AppClient.relativeUri() + AppClient.APP_RESOURCE_PREFIX + part + URL_QUERY_PROCESSOR.process("");
								ScriptInjector.fromUrl(jsURL).setCallback(new Callback<Void, Exception>() {

									@Override
									public void onSuccess(Void result) {
										structureProcess.onSuccess(result);
									}

									@Override
									public void onFailure(Exception reason) {
										Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, "Script [" + moduleName + "] is not loaded. Cause is: " + reason.getMessage());
										structureProcess.onFailure(reason.getMessage());
									}

								}).setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
							} else {
								client.requestDocument(part, new CallbackAdapter<Document, XMLHttpRequest>() {

									@Override
									public void onFailure(XMLHttpRequest reason) {
										structureProcess.onFailure(reason.getStatus() + " : " + reason.getStatusText());
									}

									@Override
									protected void doWork(Document aResult) throws Exception {
										structureProcess.onSuccess(null);
									}

								});
							}
						}
						final CumulativeCallbackAdapter<Void, String> dependenciesProcess = new CumulativeCallbackAdapter<Void, String>(3) {

							@Override
							protected void failed(List<String> aReasons) {
								moduleProcess.onFailure(aReasons.toString());
							}

							@Override
							protected void doWork(Void aResult) throws Exception {
								moduleProcess.onSuccess(null);
							}

						};
						load(aStructure.getClientDependencies(), dependenciesProcess);
						loadQueries(aStructure.getQueriesDependencies(), dependenciesProcess);
						loadServerModules(aStructure.getServerDependencies(), dependenciesProcess);
					}

					@Override
					public void onFailure(XMLHttpRequest reason) {
						process.onFailure(reason.getStatus() + ": " + reason.getStatusText());
					}
				}));
				//
				started.add(moduleName);
				fireStarted(moduleName);
			}
		} else {
			aCallback.onSuccess(null);
		}
	}

	private void loadServerModules(Collection<String> aServerModulesNames, final Callback<Void, String> aCallback) throws Exception {
		List<String> serverModulesNames = new ArrayList<String>();
		for (String serverModuleName : aServerModulesNames) {
			if (!isStarted(SERVER_MODULE_TOUCHED_NAME + serverModuleName))
				serverModulesNames.add(serverModuleName);
		}
		if (!serverModulesNames.isEmpty()) {
			final CumulativeCallbackAdapter<Void, String> process = new CumulativeCallbackAdapter<Void, String>(serverModulesNames.size()) {

				@Override
                protected void failed(List<String> aReasons) {
					aCallback.onFailure(aReasons.toString());
                }

				@Override
                protected void doWork(Void aResult) throws Exception {
					aCallback.onSuccess(aResult);
                }
			};
			final Collection<Cancellable> startLoadings = new ArrayList<Cancellable>();
			for (final String appElementName : serverModulesNames) {
				startLoadings.add(client.createServerModule(appElementName, new CallbackAdapter<Void, String>() {

					@Override
					public void doWork(Void aDoc) throws Exception {
						fireLoaded(SERVER_MODULE_TOUCHED_NAME + appElementName);
						process.onSuccess(aDoc);
					}

					@Override
					public void onFailure(String reason) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, reason);
						process.onFailure(reason);
					}
				}));
				started.add(SERVER_MODULE_TOUCHED_NAME + appElementName);
				fireStarted(SERVER_MODULE_TOUCHED_NAME + appElementName);
			}
		} else {
			aCallback.onSuccess(null);
		}
	}

	/**
	 * This method is public ONLY because of tests!
	 * 
	 * @param aQueriesNames
	 * @param onSuccess
	 * @return
	 * @throws Exception
	 */
	public void loadQueries(Collection<String> aQueriesNames, final Callback<Void, String> aCallback) throws Exception {
		List<String> queriesNames = new ArrayList<String>();
		for (String queryName : aQueriesNames) {
			if (!isStarted(queryName))
				queriesNames.add(queryName);
		}
		if (!queriesNames.isEmpty()) {
			final CumulativeCallbackAdapter<Void, String> process = new CumulativeCallbackAdapter<Void, String>(queriesNames.size()) {

				@Override
	            protected void failed(List<String> aReasons) {
					aCallback.onFailure(aReasons.toString());
	            }

				@Override
	            protected void doWork(Void aResult) throws Exception {
					aCallback.onSuccess(aResult);
	            }

			};
			final Collection<Cancellable> startLoadings = new ArrayList<Cancellable>();
			for (final String queryName : queriesNames) {
				startLoadings.add(client.getAppQuery(queryName, new CallbackAdapter<Query, String>() {

					@Override
					public void doWork(Query aQuery) throws Exception {
						Application.putAppQuery(aQuery);
						fireLoaded(queryName);
						process.onSuccess(null);
					}

					@Override
					public void onFailure(String reason) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, reason);
						process.onFailure(reason);
					}
				}));
				started.add(queryName);
				fireStarted(queryName);
			}
		} else {
			aCallback.onSuccess(null);
		}
	}

	public boolean isStarted(String aAppElementId) {
		return started.contains(aAppElementId);
	}

	/*
	 * public boolean isLoaded(Set<String> aAppElementsIds) { return
	 * loadedAppElements.containsAll(aAppElementsIds); }
	 * 
	 * public boolean isLoaded(String aAppElementId) { return
	 * loadedAppElements.contains(aAppElementId); }
	 */
}
