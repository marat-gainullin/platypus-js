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

import com.eas.client.CallbackAdapter;
import com.eas.client.Cancellable;
import com.eas.client.CumulativeCallbackAdapter;
import com.eas.client.queries.Query;
import com.eas.client.xhr.UrlQueryProcessor;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
	protected Set<String> executed = new HashSet<>();
	protected Map<String, List<Callback<Void, String>>> pending = new HashMap<>();

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

	public void load(final Collection<String> aModulesNames, final Callback<Void, String> aCallback, final Set<String> aCyclic) throws Exception {		
		if (!aModulesNames.isEmpty()) {
			final CumulativeCallbackAdapter<String> process = new CumulativeCallbackAdapter<String>(aModulesNames.size()) {

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
			for (final String moduleName : aModulesNames) {
				if (executed.contains(moduleName) || aCyclic.contains(moduleName)) {
					Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

						@Override
						public void execute() {
							process.onSuccess(null);
						}
					});
				} else {
					aCyclic.add(moduleName);
					List<Callback<Void, String>> pendingOnModule = pending.get(moduleName);
					if (pendingOnModule == null) {
						pendingOnModule = new ArrayList<>();
						pending.put(moduleName, pendingOnModule);
					}
					pendingOnModule.add(process);
					if (!started.contains(moduleName)) {
						client.requestModuleStructure(moduleName, new CallbackAdapter<AppClient.ModuleStructure, XMLHttpRequest>() {

							@Override
							protected void doWork(AppClient.ModuleStructure aStructure) throws Exception {
								final CumulativeCallbackAdapter<String> moduleProcess = new CumulativeCallbackAdapter<String>(2) {

									@Override
									protected void failed(final List<String> aReasons) {
										List<Callback<Void, String>> interestedPendings = new ArrayList<>(pending.get(moduleName));
										pending.get(moduleName).clear();
										for (final Callback<Void, String> interestedPending : interestedPendings) {
											Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

												@Override
												public void execute() {
													interestedPending.onFailure(aReasons.toString());
												}
											});
										}
									}

									@Override
									protected void doWork(Void aResult) throws Exception {
										executed.add(moduleName);
										fireLoaded(moduleName);
										List<Callback<Void, String>> interestedPendings = new ArrayList<>(pending.get(moduleName));
										pending.get(moduleName).clear();
										for (final Callback<Void, String> interestedPending : interestedPendings) {
											Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

												@Override
												public void execute() {
													interestedPending.onSuccess(null);
												}
											});
										}
									}

								};
								final CumulativeCallbackAdapter<String> structureProcess = new CumulativeCallbackAdapter<String>(aStructure.getStructure().size()) {

									@Override
									protected void failed(List<String> aReasons) {
										moduleProcess.onFailure(aReasons.toString());
									}

									@Override
									protected void doWork(Void aResult) throws Exception {
										moduleProcess.onSuccess(null);
									}

								};
								assert !aStructure.getStructure().isEmpty() : "Module [" + moduleName + "] structure should contain at least one element.";
								for (final String part : aStructure.getStructure()) {
									if (part.toLowerCase().endsWith(".js")) {
										final String jsURL = AppClient.checkedCacheBust(AppClient.relativeUri() + AppClient.APP_RESOURCE_PREFIX + part);
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
								final CumulativeCallbackAdapter<String> dependenciesProcess = new CumulativeCallbackAdapter<String>(3) {

									@Override
									protected void failed(List<String> aReasons) {
										moduleProcess.onFailure(aReasons.toString());
									}

									@Override
									protected void doWork(Void aResult) throws Exception {
										moduleProcess.onSuccess(null);
									}

								};
								load(aStructure.getClientDependencies(), dependenciesProcess, aCyclic);
								loadQueries(aStructure.getQueriesDependencies(), dependenciesProcess);
								loadServerModules(aStructure.getServerDependencies(), dependenciesProcess);
							}

							@Override
							public void onFailure(XMLHttpRequest reason) {
								process.onFailure(reason.getStatus() + ": " + reason.getStatusText());
							}
						});
						started.add(moduleName);
						fireStarted(moduleName);
					}
				}
			}
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

				@Override
				public void execute() {
					aCallback.onSuccess(null);
				}
			});
		}
	}

	private void loadServerModules(Collection<String> aServerModulesNames, final Callback<Void, String> aCallback) throws Exception {
		if (!aServerModulesNames.isEmpty()) {
			final CumulativeCallbackAdapter<String> process = new CumulativeCallbackAdapter<String>(aServerModulesNames.size()) {

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
			for (final String appElementName : aServerModulesNames) {
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
				fireStarted(SERVER_MODULE_TOUCHED_NAME + appElementName);
			}
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

				@Override
				public void execute() {
					aCallback.onSuccess(null);
				}
			});
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
		if (!aQueriesNames.isEmpty()) {
			final CumulativeCallbackAdapter<String> process = new CumulativeCallbackAdapter<String>(aQueriesNames.size()) {

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
			for (final String queryName : aQueriesNames) {
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
				fireStarted(queryName);
			}
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

				@Override
				public void execute() {
					aCallback.onSuccess(null);
				}
			});
		}
	}
}
