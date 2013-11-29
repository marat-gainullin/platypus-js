/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.IDGenerator;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.Callback;
import com.eas.client.Cancellable;
import com.eas.client.CancellableCallback;
import com.eas.client.PlatypusHttpRequestParams;
import com.eas.client.Requests;
import com.eas.client.ResponseCallbackAdapter;
import com.eas.client.Utils;
import com.eas.client.published.PublishedFile;
import com.eas.client.queries.Query;
import com.eas.client.serial.ChangesWriter;
import com.eas.client.serial.QueryReader;
import com.eas.client.serial.RowsetReader;
import com.eas.client.xhr.FormData;
import com.eas.client.xhr.ProgressEvent;
import com.eas.client.xhr.ProgressHandler;
import com.eas.client.xhr.ProgressHandlerAdapter;
import com.eas.client.xhr.XMLHttpRequest2;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.sencha.gxt.core.client.GXT;

/**
 * 
 * @author mg
 */
public class AppClient {

	//
	private static final RegExp httpPrefixPattern = RegExp.compile("https?://.*");
	public static final String APPLICATION_URI = "/application";
	public static final String RESOURCES_URI = "/resources";
	public static final String API_URI = "/api";
	public static final String SELF_NAME = "_platypusModuleSelf";

	/*
	public static final String SERVER_MODULE_BODY = "function %s () {\n" + "    window.platypus.defineServerModule(\"%s\", this);\n" + "}";
	public static final String SERVER_MODULE_FUNCTION_NAME = "ServerModule";
	public static final String SERVER_REPORT_FUNCTION_NAME = "ServerReport";
	*/
	protected static Set<String> attachedCss = new HashSet<String>();
	//
	private static DateTimeFormat defaultDateFormat = RowsetReader.ISO_DATE_FORMAT;// DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601);
	private static AppClient appClient;
	private String baseUrl;
	private Map<String, Document> appElements = new HashMap<String, Document>();
	private Map<String, PlatypusImageResource> iconsCache = new HashMap<String, PlatypusImageResource>();
	protected List<Change> changeLog = new ArrayList<Change>();
	protected Set<TransactionListener> transactionListeners = new HashSet<TransactionListener>();

	public static String relativeUri() {
		String pageUrl = GWT.getHostPageBaseURL();
		return pageUrl.substring(0, pageUrl.length() - 1);
	}

	public static void init() {
		if (appClient == null) {
			appClient = new AppClient(relativeUri() + APPLICATION_URI);
		}
	}

	public static AppClient getInstance() {
		init();
		return appClient;
	}

	/**
	 * Only for tests! Don't call this method from application code!
	 * 
	 * @param aClient
	 */
	public static void setInstance(AppClient aClient) {
		appClient = aClient;
	}

	public SafeUri getResourceUri(final String imageName) {
		return new SafeUri() {

			@Override
			public String asString() {
				MatchResult htppMatch = httpPrefixPattern.exec(imageName);
				if (htppMatch != null) {
					return imageName;
				} else
					return resourceUrl(imageName);
			}
		};
	}

	public PlatypusImageResource getImageResource(final String imageName) {
		PlatypusImageResource res = iconsCache.get(imageName);
		if (res == null) {
			res = new PlatypusImageResource(this, imageName);
			iconsCache.put(imageName, res);
		}
		return res;
	}

	public static Object jsLoad(String aResourceName, final JavaScriptObject onSuccess, final JavaScriptObject onFailure, final boolean text) throws Exception {
		SafeUri uri = AppClient.getInstance().getResourceUri(aResourceName);
		if (onSuccess != null) {
			AppClient.getInstance().startRequest(uri, text ? ResponseType.Default : ResponseType.ArrayBuffer, new Callback<XMLHttpRequest>() {
				@Override
				public void run(XMLHttpRequest aResult) throws Exception {
					if (aResult.getStatus() == Response.SC_OK) {
						if (onSuccess != null)
							Utils.executeScriptEventVoid(onSuccess, onSuccess, text ? Utils.toJs(aResult.getResponseText()) : aResult.<XMLHttpRequest2> cast().getResponse());
					} else {
						if (onFailure != null)
							Utils.executeScriptEventVoid(onFailure, onFailure, Utils.toJs(aResult.getStatusText()));
					}
				}

				@Override
				public void cancel() {
				}
			}, new Callback<XMLHttpRequest>() {

				@Override
				public void run(XMLHttpRequest aResult) throws Exception {
					if (onFailure != null)
						Utils.executeScriptEventVoid(onFailure, onFailure, Utils.toJs(aResult.getStatus() != 0 ? aResult.getStatusText() : "Request has been cancelled. See browser's console."));
				}

				@Override
				public void cancel() {
				}
			});
		} else {
			XMLHttpRequest2 executed = AppClient.getInstance().syncRequest(uri.asString(), text ? ResponseType.Default : ResponseType.ArrayBuffer);
			if (executed != null) {
				if (executed.getStatus() == Response.SC_OK)
					if(text)
						return Utils.toJs(executed.getResponseText());
					else{
						return Utils.stringToArrayBuffer(executed.getResponseText());
					}
				else
					throw new Exception(executed.getStatusText());
			}
		}
		return null;
	}

	public static JavaScriptObject jsUpload(PublishedFile aFile, final JavaScriptObject aCompleteCallback, final JavaScriptObject aProgresssCallback, final JavaScriptObject aErrorCallback) {
		if (aFile != null) {
			Cancellable cancellable = upload(aFile, aCompleteCallback == null ? null : new Callback<String>() {
				@Override
				public void run(String aResult) throws Exception {
					Utils.executeScriptEventVoid(aCompleteCallback, aCompleteCallback, Utils.toJs(aResult));
				}

				@Override
				public void cancel() {
				}
			}, aProgresssCallback == null ? null : new Callback<ProgressEvent>() {
				@Override
				public void run(ProgressEvent aResult) throws Exception {
					Utils.executeScriptEventVoid(aProgresssCallback, aProgresssCallback, aResult);
				}

				@Override
				public void cancel() {
				}
			}, aErrorCallback == null ? null : new Callback<String>() {
				@Override
				public void run(String aResult) throws Exception {
					Utils.executeScriptEventVoid(aErrorCallback, aErrorCallback, Utils.toJs(aResult));
				}

				@Override
				public void cancel() {
				}
			});
			return Utils.publishCancellable(cancellable);
		} else
			return null;
	}

	public static Cancellable upload(final PublishedFile aFile, final Callback<String> aCompleteCallback, final Callback<ProgressEvent> aProgresssCallback, final Callback<String> aErrorCallback) {
		final XMLHttpRequest2 req = XMLHttpRequest.create().<XMLHttpRequest2> cast();
		req.open("post", AppClient.getInstance().getBaseUrl());
		final ProgressHandler handler = new ProgressHandlerAdapter() {
			@Override
			public void onProgress(ProgressEvent aEvent) {
				try {
					aProgresssCallback.run(aEvent);
				} catch (Exception ex) {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			public void onLoadEnd(XMLHttpRequest xhr) {
				try {
					aProgresssCallback.run(ProgressEvent.create(aFile.getSize(), aFile.getSize()));
				} catch (Exception ex) {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
				}
			};

			@Override
			public void onAbort(XMLHttpRequest xhr) {
				if (aErrorCallback != null) {
					try {
						aErrorCallback.run("aborted");
					} catch (Exception ex) {
						Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

			@Override
			public void onError(XMLHttpRequest xhr) {
				if (aErrorCallback != null) {
					try {
						aErrorCallback.run(xhr.getStatusText());
					} catch (Exception ex) {
						Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

			@Override
			public void onTimeOut(XMLHttpRequest xhr) {
				if (aErrorCallback != null) {
					try {
						aErrorCallback.run("timeout");
					} catch (Exception ex) {
						Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		};
		if (req.getUpload() != null) {
			req.getUpload().setOnProgress(handler);
			req.getUpload().setOnLoadEnd(handler);
			req.getUpload().setOnAbort(handler);
			req.getUpload().setOnError(handler);
			req.getUpload().setOnTimeOut(handler);
		}
		FormData fd = FormData.create();
		fd.append(aFile.getName(), aFile);
		req.overrideMimeType("multipart/form-data");
		// Must set the onreadystatechange handler before calling send().
		req.setOnReadyStateChange(new ReadyStateChangeHandler() {
			public void onReadyStateChange(XMLHttpRequest xhr) {
				if (xhr.getReadyState() == XMLHttpRequest.DONE) {
					xhr.clearOnReadyStateChange();
					if (xhr.getStatus() == Response.SC_OK) {
						try {
							aCompleteCallback.run(xhr.getResponseText());
						} catch (Exception ex) {
							Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
						}
					} else {
						if (xhr.getStatus() == 0)
							handler.onAbort(xhr);
						else
							handler.onError(xhr);
					}
				}
			}
		});
		req.send(fd);
		return new Cancellable() {
			@Override
			public void cancel() {
				req.abort();
			}
		};
	}

	public Cancellable submitForm(String aAction, Map<String, String> aFormData, final Callback<XMLHttpRequest> onDone) {
		final XMLHttpRequest req = XMLHttpRequest.create().cast();
		aAction = (aAction != null ? aAction : "");
		if (!aAction.startsWith("/"))
			aAction = "/" + aAction;
		String url = relativeUri() + aAction;
		List<String> parameters = new ArrayList<String>();
		for (String paramName : aFormData.keySet()) {
			parameters.add(param(paramName, aFormData.get(paramName)));
		}
		url += "?" + params(parameters.toArray(new String[] {}));
		req.open("get", url);
		req.setOnReadyStateChange(new ReadyStateChangeHandler() {
			public void onReadyStateChange(final XMLHttpRequest xhr) {
				if (xhr.getReadyState() == XMLHttpRequest.DONE) {
					xhr.clearOnReadyStateChange();
					if (onDone != null) {
						try {
							onDone.run(xhr);
						} catch (Exception ex) {
							Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			}
		});
		req.send();
		return new Cancellable() {
			@Override
			public void cancel() {
				req.abort();
			}
		};
	}

	public static String param(String aName, String aValue) {
		return aName + "=" + (aValue != null ? URL.encodePathSegment(aValue) : "");
	}

	public static String params(String... aParams) {
		String res = "";
		for (int i = 0; i < aParams.length; i++) {
			if (aParams[i] != null && !aParams[i].isEmpty()) {
				if (!res.isEmpty()) {
					res += "&";
				}
				res += aParams[i];
			}
		}
		return res;
	}

	public AppClient(String aBaseUrl) {
		baseUrl = aBaseUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public TransactionListener.Registration addTransactionListener(final TransactionListener aListener) {
		transactionListeners.add(aListener);
		return new TransactionListener.Registration() {
			@Override
			public void remove() {
				transactionListeners.remove(aListener);
			}
		};
	}

	public List<Change> getChangeLog() {
		return changeLog;
	}

	public void enqueueUpdate(String aQueryId, Parameters aParams) throws Exception {
		Command command = new Command(aQueryId);
		command.parameters = new Change.Value[aParams.getParametersCount()];
		for (int i = 0; i < command.parameters.length; i++) {
			Parameter p = aParams.get(i + 1);
			command.parameters[i] = new Change.Value(p.getName(), p.getValue(), p.getTypeInfo());
		}
		changeLog.add(command);
	}

	public Document getCachedAppElement(String aAppElementName) {
		return appElements.get(aAppElementName);
	}

	private String params(Parameters parameters) {
		String[] res = new String[parameters.getParametersCount()];
		for (int i = 0; i < parameters.getParametersCount(); i++) {
			Parameter p = parameters.get(i + 1);// parameters and fields are
			                                    // 1-based
			String sv = "null";
			if (p.getValue() != null && p.getValue() != RowsetUtils.UNDEFINED_SQL_VALUE) {
				if (p.getValue() instanceof Date) {
					sv = defaultDateFormat.format((Date) p.getValue());
				} else {
					sv = p.getValue().toString();
				}
			}
			res[i] = param(p.getName(), sv);
		}
		return params(res);
	}

	public static JavaScriptObject jsLogout(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		return Utils.publishCancellable(AppClient.getInstance().logout(new Callback<XMLHttpRequest>() {
			@Override
			public void run(XMLHttpRequest aResult) throws Exception {
				Utils.invokeJsFunction(onSuccess);
				Location.reload();
			}

			@Override
			public void cancel() {
			}
		}, new Callback<XMLHttpRequest>() {

			@Override
			public void run(XMLHttpRequest aResult) throws Exception {
				Utils.executeScriptEventVoid(onFailure, onFailure, Utils.toJs(aResult.getStatusText()));
			}

			@Override
			public void cancel() {
			}

		}));
	}

	public Cancellable hello(Callback<XMLHttpRequest> onSuccess) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqHello));
		return startRequest(API_URI, query, null, RequestBuilder.GET, onSuccess, null);
	}

	public Cancellable logout(Callback<XMLHttpRequest> onSuccess, Callback<XMLHttpRequest> onFailure) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqLogout));
		return startRequest(API_URI, query, null, RequestBuilder.GET, onSuccess, onFailure);
	}

	public Cancellable startRequest(String aUrlPrefix, final String aUrlQuery, String aBody, RequestBuilder.Method aMethod, final Callback<XMLHttpRequest> onSuccess,
	        final Callback<XMLHttpRequest> onFailure) throws Exception {
		String url = baseUrl + aUrlPrefix + (aUrlQuery != null ? "?" + aUrlQuery : "");
		final XMLHttpRequest req = XMLHttpRequest.create();
		req.open(aMethod.toString(), url);
		if (RequestBuilder.POST.equals(aMethod)) {
			req.setRequestHeader("Content-Type", "application/json; charset=utf-8");
		}
		interceptRequest(req);
		req.setRequestHeader("Pragma", "no-cache");
		return startRequest(req, aBody, onSuccess, onFailure);
	}

	public Cancellable startRequest(SafeUri aUri, ResponseType aResponseType, final Callback<XMLHttpRequest> onSuccess, final Callback<XMLHttpRequest> onFailure) throws Exception {
		final XMLHttpRequest req = XMLHttpRequest.create();
		req.open(RequestBuilder.GET.toString(), aUri.asString());
		interceptRequest(req);
		if (aResponseType != null && aResponseType != ResponseType.Default)
			req.setResponseType(aResponseType);
		req.setRequestHeader("Pragma", "no-cache");
		return startRequest(req, null, onSuccess, onFailure);
	}

	public Cancellable startRequest(SafeUri aUri, final Callback<XMLHttpRequest> onSuccess, final Callback<XMLHttpRequest> onFailure) throws Exception {
		return startRequest(aUri, ResponseType.Default, onSuccess, onFailure);
	}

	public Cancellable startRequest(final XMLHttpRequest req, String requestData, final Callback<XMLHttpRequest> onSuccess, final Callback<XMLHttpRequest> onFailure) throws Exception {
		// Must set the onreadystatechange handler before calling send().
		req.setOnReadyStateChange(new ReadyStateChangeHandler() {
			public void onReadyStateChange(XMLHttpRequest xhr) {
				if (xhr.getReadyState() == XMLHttpRequest.DONE) {
					xhr.clearOnReadyStateChange();
					/*
					 * We cannot use cancel here because it would clear the
					 * contents of the JavaScript XmlHttpRequest object so we
					 * manually null out our reference to the JavaScriptObject
					 */
					String errorMsg = XMLHttpRequest2.getBrowserSpecificFailure(req);
					if (errorMsg != null) {
						Throwable exception = new RuntimeException(errorMsg);
						Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, exception);
						try {
							if (onFailure != null)
								onFailure.run(xhr);
						} catch (Exception ex) {
							Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
						}
					} else {
						try {
							if (xhr.getStatus() == Response.SC_OK) {
								if (onSuccess != null)
									onSuccess.run(xhr);
							} else {
								if (onFailure != null)
									onFailure.run(xhr);
							}
						} catch (Exception ex) {
							Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			}
		});

		if (requestData != null && !requestData.isEmpty())
			req.send(requestData);
		else
			req.send();
		return new Cancellable() {

			@Override
			public void cancel() {
				req.clearOnReadyStateChange();
				req.abort();
				if (onSuccess != null) {
					onSuccess.cancel();
				}
				if (onFailure != null) {
					onFailure.cancel();
				}
			}
		};
	}

	public void startDownloadRequest(String aUrlPrefix, final int aRequestType, Map<String, String> aParams, RequestBuilder.Method aMethod) throws Exception {
		final Frame frame = new Frame();
		frame.setVisible(false);
		
		frame.addLoadHandler(new LoadHandler() {
			
			@Override
			public void onLoad(LoadEvent event) {
					Timer timer = new Timer() {
						
						@Override
						public void run() {
							frame.removeFromParent();
						}
					};
					timer.schedule(2000);
			}
		});
		String query = "";
		for (Entry<String, String> ent : aParams.entrySet()) {
			query += param(ent.getKey(), ent.getValue()) + "&";
		}
		query += param(PlatypusHttpRequestParams.TYPE, String.valueOf(aRequestType));
		frame.setUrl(baseUrl + aUrlPrefix + "?" + query);
		RootPanel.get().add(frame);
	}

	public XMLHttpRequest2 syncRequest(String aUrl, ResponseType aResponseType) throws Exception {
		final XMLHttpRequest2 req = syncRequest(aUrl, aResponseType, null, RequestBuilder.GET);
		if (req.getStatus() == Response.SC_OK)
			return req;
		else
			throw new Exception(req.getStatus() + " " + req.getStatusText());
	}

	public XMLHttpRequest2 syncRequest(String aUrlPrefix, final String aUrlQuery, ResponseType aResponseType) throws Exception {
		String url = baseUrl + aUrlPrefix + "?" + aUrlQuery;
		final XMLHttpRequest2 req = syncRequest(url, aResponseType, null, RequestBuilder.GET);
		if (req.getStatus() == Response.SC_OK)
			return req;
		else
			throw new Exception(req.getStatus() + " " + req.getStatusText());
	}

	public XMLHttpRequest2 syncRequest(String aUrl, ResponseType aResponseType, String aBody, RequestBuilder.Method aMethod) throws Exception {
		final XMLHttpRequest2 req = XMLHttpRequest.create().<XMLHttpRequest2> cast();
		req.open(aMethod.toString(), aUrl, false);
		interceptRequest(req);
		/* Since W3C standard about sync XmlHttpRequest and response type.
		if (aResponseType != null && aResponseType != ResponseType.Default)
			req.setResponseType(aResponseType);
		*/	
		req.setRequestHeader("Pragma", "no-cache");
		if (aBody != null)
			req.send(aBody);
		else
			req.send();
		if (req.getStatus() == Response.SC_OK)
			return req;
		else
			throw new Exception(req.getStatus() + " " + req.getStatusText());
	}

	public static native void defineServerModule(String aModuleName, JavaScriptObject aModule)/*-{
		var moduleData = $wnd.serverModules[aModuleName];
		for ( var i = 0; i < moduleData.functions.length; i++) {
			aModule[moduleData.functions[i]] = function(functionName) {
				return function() {
					var onSuccess = null;
					var onFailure = null;
					var argsLength = arguments.length;
					if(arguments.length > 1 && typeof arguments[arguments.length-1] == "function" && typeof arguments[arguments.length-2] == "function"){
						onSuccess = arguments[arguments.length-2];
						onFailure = arguments[arguments.length-1];
						argsLength -= 2;
					}else if(arguments.length > 0 && typeof arguments[arguments.length-1] == "function"){
						onSuccess = arguments[arguments.length-1];
						argsLength -= 1;
					}
					var params = [];
					for ( var j = 0; j < argsLength; j++) {
						params[j] = JSON.stringify(arguments[j]);
					}
					return $wnd.platypus.executeServerModuleMethod(aModuleName, functionName, params, onSuccess, onFailure);
				}
			}(moduleData.functions[i]);
		}
		if (moduleData.isReport) {
			aModule.show = function() {
				$wnd.platypus.executeServerReport(aModuleName, aModule);
			}
			aModule.print = aModule.show;
		}
	}-*/;

	protected void interceptRequest(XMLHttpRequest req) {
		// No-op here. Some implementation is in the tests.
	}

	public Cancellable commit(final CancellableCallback onSuccess, final Callback<String> onFailure) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqCommit));
		return startRequest(API_URI, query, ChangesWriter.writeLog(changeLog), RequestBuilder.POST, new ResponseCallbackAdapter() {

			@Override
			public void doWork(XMLHttpRequest aResponse) throws Exception {
				Logger.getLogger(AppClient.class.getName()).log(Level.INFO, "Commit succeded: " + aResponse.getStatus() + " " + aResponse.getStatusText());
				changeLog.clear();
				for (TransactionListener l : transactionListeners.toArray(new TransactionListener[] {})) {
					try {
						l.commited();
					} catch (Exception ex) {
						Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				if (onSuccess != null)
					onSuccess.run();
			}
		}, new ResponseCallbackAdapter() {

			@Override
			public void doWork(XMLHttpRequest aResponse) throws Exception {
				Logger.getLogger(AppClient.class.getName()).log(Level.INFO, "Commit failed: " + aResponse.getStatus() + " " + aResponse.getStatusText());
				changeLog.clear();
				for (TransactionListener l : transactionListeners.toArray(new TransactionListener[] {})) {
					try {
						l.rolledback();
					} catch (Exception ex) {
						Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				if (onFailure != null)
					onFailure.run(aResponse.getStatusText());
			}
		});
	}

	public String resourceUri(String aResourceName) {
		return RESOURCES_URI + "/" + aResourceName;
	}

	public String resourceUrl(String aResourceName) {
		return baseUrl + RESOURCES_URI + "/" + aResourceName;
	}

	public Cancellable getStartElement(final Callback<String> onSuccess) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqStartAppElement));
		return startRequest(API_URI, query, "", RequestBuilder.GET, new ResponseCallbackAdapter() {

			@Override
			protected void doWork(XMLHttpRequest aResponse) throws Exception {
				if(onSuccess != null){
					String respText = aResponse.getResponseText();
					Object oResult = respText != null && !respText.isEmpty() ? Utils.toJava(Utils.jsonParse(respText)) : null;
					assert oResult == null || oResult instanceof String : "getStartElement request expects null or string value as a response.";
					onSuccess.run((String)oResult);
				}
			}
		}, null);
	}

	public Cancellable getAppElementXml(final String appElementName, final Callback<Document> onSuccess, final Callback<XMLHttpRequest> onFailure) throws Exception {
		if (appElements.containsKey(appElementName)) {
			Document doc = appElements.get(appElementName);
			// doc may be null, because of application elements without a xml-dom, plain scripts for example.
			onSuccess.run(doc);
			return new Cancellable() {

				@Override
				public void cancel() {
					// no op here because of no request have been sent
				}
			};
		} else {
			String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqAppElement)));
			if(GXT.isChrome())// remove if chrome bug 266971 is fixed
				query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqAppElement)), param(PlatypusHttpRequestParams.CACHE_BUSTER, String.valueOf(IDGenerator.genId())));
			return startRequest(resourceUri(appElementName), query, "", RequestBuilder.GET, new ResponseCallbackAdapter() {

				@Override
				public void doWork(XMLHttpRequest aResponse) throws Exception {
					// Some post processing
					String text = aResponse.getResponseText();
					Document doc = text != null && !text.isEmpty() ? XMLParser.parse(text) : null;
					appElements.put(appElementName, doc);
					//
					onSuccess.run(doc);
				}
			}, onFailure);
		}
	}

	public Cancellable createServerModule(final String aModuleName, final Callback<Void> onSuccess, final Callback<String> onFailure) throws Exception {
		if (isServerModule(aModuleName)) {
			onSuccess.run(null);
			return new Cancellable() {

				@Override
				public void cancel() {
					// no op here because of no request have been sent
				}
			};
		} else {
			String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqCreateServerModule)), param(PlatypusHttpRequestParams.MODULE_NAME, aModuleName));
			return startRequest(API_URI, query, "", RequestBuilder.GET, new ResponseCallbackAdapter() {

				@Override
				public void doWork(XMLHttpRequest aResponse) throws Exception {
					// Some post processing
					String appElementName = aModuleName;
					addServerModule(appElementName, aResponse.getResponseText());
					/*
					Document doc = XMLParser.createDocument();
					Node nd = doc.createElement("script");
					doc.appendChild(nd);
					Node ndc = doc.createElement(Loader.SCRIPT_SOURCE_TAG_NAME);
					String src = Utils.format(SERVER_MODULE_BODY, AppClient.isReport(appElementName) ? SERVER_REPORT_FUNCTION_NAME + appElementName : SERVER_MODULE_FUNCTION_NAME + appElementName,
					        appElementName);
					ndc.appendChild(doc.createTextNode(src));
					nd.appendChild(ndc);
					serverElements.put(appElementName, doc);
					*/
					onSuccess.run(null);
				}
			}, new ResponseCallbackAdapter() {
				@Override
				protected void doWork(XMLHttpRequest aResponse) throws Exception {
					if (onFailure != null) {
						onFailure.run(aResponse.getResponseText() != null ? aResponse.getResponseText() : aResponse.getStatusText());
					}
				}
			});
		}
	}

	public static native void addServerModule(String aModuleName, String aStructure) throws Exception /*-{
		if (!$wnd.serverModules) {
			$wnd.serverModules = {};
		}
		$wnd.serverModules[aModuleName] = JSON.parse(aStructure);
	}-*/;

	public static native boolean isReport(String aModuleName) throws Exception /*-{
		if ($wnd.serverModules && $wnd.serverModules[aModuleName]) {
			return $wnd.serverModules[aModuleName].isReport;
		} else
			return false;
	}-*/;

	public static native boolean isServerModule(String aModuleName) throws Exception /*-{
		return ($wnd.serverModules && $wnd.serverModules[aModuleName]) ? true : false;
	}-*/;
	
	public static native void publishApi(AppClient aClient) throws Exception /*-{
		$wnd.platypus.defineServerModule = function(aModuleName, aModule) {
			@com.eas.client.application.AppClient::defineServerModule(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aModule);
		}
		$wnd.platypus.executeServerModuleMethod = function(aModuleName, aMethodName, aParams, aOnSuccess, aOnFailure) {
			return $wnd
					.boxAsJs(aClient.@com.eas.client.application.AppClient::executeServerModuleMethod(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aMethodName, aParams, aOnSuccess, aOnFailure));
		}
		$wnd.platypus.executeServerReport = function(aModuleName, aModule) {
			aClient.@com.eas.client.application.AppClient::executeServerReport(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aModule);
		}
	}-*/;

	public Object executeServerModuleMethod(final String aModuleName, final String aMethodName, final JsArrayString aParams, final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		String[] convertedParams = new String[aParams.length()];
		for (int i = 0; i < aParams.length(); i++)
			convertedParams[i] = param(PlatypusHttpRequestParams.PARAMS_ARRAY, aParams.get(i));
		String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqExecuteServerModuleMethod)), param(PlatypusHttpRequestParams.MODULE_NAME, aModuleName),
		        param(PlatypusHttpRequestParams.METHOD_NAME, aMethodName), params(convertedParams));
		if (onSuccess != null) {
			startRequest(API_URI, query, null, RequestBuilder.GET, new ResponseCallbackAdapter() {

				@Override
				public void doWork(XMLHttpRequest aResponse) throws Exception {
					String responseType = aResponse.getResponseHeader("content-type");
					if (responseType != null) {
						responseType = responseType.toLowerCase();
						if (responseType.contains("text/json") || responseType.contains("text/javascript")) {
							Utils.executeScriptEventVoid(onSuccess, onSuccess, Utils.toJs(Utils.jsonParse(aResponse.getResponseText())));
						} else {
							Utils.executeScriptEventVoid(onSuccess, onSuccess, Utils.toJs(aResponse.getResponseText()));
						}
					} else {
						Utils.executeScriptEventVoid(onSuccess, onSuccess, Utils.toJs(aResponse.getResponseText()));
					}
				}
			}, new ResponseCallbackAdapter() {

				@Override
                protected void doWork(XMLHttpRequest aResponse) throws Exception {
					if(onFailure != null)
						Utils.executeScriptEventVoid(onSuccess, onFailure, Utils.toJs(aResponse.getStatusText()));
                }
				
			});
			return null;
		} else {
			XMLHttpRequest2 executed = syncRequest(API_URI, query, ResponseType.Default);
			if (executed != null) {
				if (executed.getStatus() == Response.SC_OK) {
					String responseType = executed.getResponseHeader("content-type");
					if (responseType != null) {
						responseType = responseType.toLowerCase();
						if (responseType.contains("text/json") || responseType.contains("text/javascript")) {
							return Utils.toJs(Utils.jsonParse(executed.getResponseText()));
						} else {
							return Utils.toJs(executed.getResponseText());
						}
					} else {
						return Utils.toJs(executed.getResponseText());
					}
				} else
					throw new Exception(executed.getStatusText());
			} else {
				return null;
			}
		}
	}

	public void executeServerReport(final String appElementName, JavaScriptObject aParams) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put(PlatypusHttpRequestParams.MODULE_NAME, appElementName);
		params.putAll(Utils.extractSimpleProperties(aParams));
		startDownloadRequest(API_URI, Requests.rqExecuteReport, params, RequestBuilder.GET);
	}

	public Cancellable getAppQuery(String queryId, final Callback<Query> onSuccess, final Callback<String> onFailure) throws Exception {
		String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqAppQuery)), param(PlatypusHttpRequestParams.QUERY_ID, queryId));
		return startRequest(API_URI, query, "", RequestBuilder.GET, new ResponseCallbackAdapter() {

			@Override
			public void doWork(XMLHttpRequest aResponse) throws Exception {
				// Some post processing
				Query query = readQuery(aResponse);
				query.setClient(AppClient.this);
				//
				onSuccess.run(query);
			}

			private Query readQuery(XMLHttpRequest aResponse) throws Exception {
				return QueryReader.read(JSONParser.parseStrict(aResponse.getResponseText()));
			}
		}, new ResponseCallbackAdapter() {
			@Override
			protected void doWork(XMLHttpRequest aResponse) throws Exception {
				if (onFailure != null) {
					onFailure.run(aResponse.getStatusText());
				}
			}
		});
	}

	public Cancellable pollData(String aQueryId, Parameters aParams, final Fields aExpectedFields, final Callback<Rowset> onSuccess, final Callback<String> onFailure) throws Exception {
		String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqExecuteQuery)), param(PlatypusHttpRequestParams.QUERY_ID, aQueryId), params(aParams));
		return startRequest(API_URI, query, "", RequestBuilder.GET, new ResponseCallbackAdapter() {

			@Override
			public void doWork(XMLHttpRequest aResponse) throws Exception {
				// Some post processing
				Rowset rowset = readRowset(aResponse);
				//
				onSuccess.run(rowset);
			}

			private Rowset readRowset(XMLHttpRequest aResponse) throws Exception {
				try {
					return RowsetReader.read(JSONParser.parseStrict(aResponse.getResponseText()), aExpectedFields);
				} catch (Exception ex) {
					String respText = aResponse.getResponseText();
					Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, "Rowset response parse error: " + respText + "\n; Status:" + aResponse.getStatus());
					throw ex;
				}
			}
		}, new ResponseCallbackAdapter() {
			@Override
			protected void doWork(XMLHttpRequest aResponse) throws Exception {
				if (onFailure != null) {
					int status = aResponse.getStatus();
					String statusText = aResponse.getStatusText();
					if (statusText == null || statusText.isEmpty())
						statusText = null;
					if (status == 0)
						Logger.getLogger(AppClient.class.getName()).log(Level.WARNING, "rowset recieving is aborted");
					onFailure.run(statusText);
				}
			}
		});
	}
}
