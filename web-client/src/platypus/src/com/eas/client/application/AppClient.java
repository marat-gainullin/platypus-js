/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
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
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

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
	public static final String SCRIPTS_URI = "/scripts";
	public static final String SELF_NAME = "_platypusModuleSelf";

	public static final String SERVER_MODULE_BODY = "function %s () {\n" + "    window.platypus.defineServerModule(\"%s\", this);\n" + "}";
	public static final String SERVER_MODULE_FUNCTION_NAME = "ServerModule";
	public static final String SERVER_REPORT_FUNCTION_NAME = "ServerReport";
	protected static Set<String> attachedCss = new HashSet();
	//
	private static DateTimeFormat defaultDateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601);
	private static AppClient appClient;
	private String baseUrl;
	private Map<String, Document> appElements = new HashMap();
	private Map<String, PlatypusImageResource> iconsCache = new HashMap();
	protected List<Change> changeLog = new ArrayList();
	protected Set<TransactionListener> transactionListeners = new HashSet();

	public static String relativeUri() {
		String pageUrl = GWT.getHostPageBaseURL();
		return pageUrl.substring(0, pageUrl.length() - 1);
	}

	private Map<String, Document> serverElements = new HashMap();

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

	public static void jsLoad(String aResourceName, final JavaScriptObject aCompleteCallback) throws Exception {
		SafeUri uri =  AppClient.getInstance().getResourceUri(aResourceName);
		 AppClient.getInstance().startRequest(uri, new Callback<Response>() {
			@Override
			public void run(Response aResult) throws Exception {
				if (aResult.getStatusCode() == Response.SC_OK) {
					Utils.executeScriptEventVoid(aCompleteCallback, aCompleteCallback, Utils.toJs(aResult.getText()));
				}
			}

			@Override
			public void cancel() {
			}
		}, null);
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
		return aName + "=" + (aValue != null ? URL.encode(aValue) : "");
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

	public static JavaScriptObject jsLogout(final JavaScriptObject onSuccess) throws Exception {
		return Utils.publishCancellable(AppClient.getInstance().logout(new Callback<Response>() {
			@Override
			public void cancel() {
			}

			@Override
			public void run(Response aResult) throws Exception {
				Utils.executeScriptEventVoid(onSuccess, onSuccess, null);
			}
		}));
	}

	public Cancellable hello(Callback<Response> onSuccess) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqHello));
		return startRequest(API_URI, query, null, RequestBuilder.GET, onSuccess, null);
	}

	public Cancellable logout(Callback<Response> onSuccess) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqLogout));
		return startRequest(API_URI, query, null, RequestBuilder.GET, onSuccess, null);
	}

	public Cancellable startRequest(String aUrlPrefix, final String aUrlQuery, String aBody, RequestBuilder.Method aMethod, final Callback<Response> onSuccess, final Callback<Response> onFailure)
	        throws Exception {
		String url = baseUrl + aUrlPrefix + (aUrlQuery != null ? "?" + aUrlQuery : "");
		RequestBuilder rb = new RequestBuilder(aMethod, url);
		if (RequestBuilder.POST.equals(aMethod)) {
			rb.setHeader("Content-Type", "application/json; charset=utf-8");
		}
		interceptRequest(rb);
		rb.setHeader("Pragma", "no-cache");
		rb.setRequestData(aBody);
		return startRequest(rb, onSuccess, onFailure);
	}

	public Cancellable startRequest(SafeUri aUri, final Callback<Response> onSuccess, final Callback<Response> onFailure) throws Exception {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, aUri.asString());
		interceptRequest(rb);
		rb.setHeader("Pragma", "no-cache");
		return startRequest(rb, onSuccess, onFailure);
	}

	public Cancellable startRequest(RequestBuilder rb, final Callback<Response> onSuccess, final Callback<Response> onFailure) throws Exception {
		rb.setCallback(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				try {
					if (response.getStatusCode() == Response.SC_OK) {
						if (onSuccess != null)
							onSuccess.run(response);
					} else {
						if (onFailure != null)
							onFailure.run(response);
					}
				} catch (Exception ex) {
					Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			@Override
			public void onError(Request request, final Throwable exception) {
				Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, exception);
				try {
					if (onFailure != null)
						onFailure.run(new Response() {

							@Override
							public String getHeader(String header) {
								return null;
							}

							@Override
							public Header[] getHeaders() {
								return null;
							}

							@Override
							public String getHeadersAsString() {
								return null;
							}

							@Override
							public int getStatusCode() {
								return 500;
							}

							@Override
							public String getStatusText() {
								return exception.getMessage();
							}

							@Override
							public String getText() {
								return exception.getMessage();
							}

						});
				} catch (Exception ex) {
					Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		final Request r = rb.send();
		return new Cancellable() {

			@Override
			public void cancel() {
				r.cancel();
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
		com.google.gwt.dom.client.Document doc = com.google.gwt.dom.client.Document.get();
		FormElement frm = doc.createFormElement();
		frm.setMethod(aMethod.toString());
		frm.setAction(APPLICATION_URI + aUrlPrefix);
		Iterator<Entry<String, String>> itr = aParams.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> ent = itr.next();
			InputElement text = doc.createHiddenInputElement();
			text.setValue(ent.getValue());
			text.setName(ent.getKey());
			frm.appendChild(text);
		}
		InputElement text = doc.createHiddenInputElement();
		text.setValue(String.valueOf(aRequestType));
		text.setName(PlatypusHttpRequestParams.TYPE);
		frm.appendChild(text);
		frm.submit();
		frm.removeFromParent();
	}

	public String syncRequest(String aUrlPrefix, final String aUrlQuery, String aBody, RequestBuilder.Method aMethod) throws Exception {
		String url = baseUrl + aUrlPrefix + "?" + aUrlQuery;
		final XMLHttpRequest2 req = XMLHttpRequest.create().<XMLHttpRequest2> cast();
		req.open(aMethod.toString(), url, false);
		req.setRequestHeader("Content-type", "application/json; charset=utf-8");
		req.setRequestHeader("Pragma", "no-cache");
		req.send(aBody);
		if (req.getStatus() == Response.SC_OK)
			return req.getResponseText();
		else
			throw new Exception(req.getStatus() + " " + req.getStatusText());
	}

	public static native void defineServerModule(String aModuleName, JavaScriptObject aModule)/*-{
		var moduleData = $wnd.serverModules[aModuleName];
		for ( var i = 0; i < moduleData.functions.length; i++) {
			aModule[moduleData.functions[i]] = function(functionName) {
				return function() {
					var executeCallback = arguments.length > 0 && typeof (arguments[arguments.length - 1]) === "function" ? arguments[arguments.length - 1] : null;
					var params = [];
					var argsLength = executeCallback == null ? arguments.length : arguments.length - 1;
					for ( var j = 0; j < argsLength; j++) {
						params[j] = JSON.stringify(arguments[j]);
					}
					return $wnd.platypus.executeServerModuleMethod(aModuleName, functionName, params, executeCallback);
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

	protected void interceptRequest(RequestBuilder rb) {
		// No-op here. Some implementation is in the tests.
	}

	public Cancellable commit(final CancellableCallback onSuccess, final CancellableCallback onFailure) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqCommit));
		return startRequest(API_URI, query, ChangesWriter.writeLog(changeLog), RequestBuilder.POST, new ResponseCallbackAdapter() {

			@Override
			public void doWork(Response aResponse) throws Exception {
				Logger.getLogger(AppClient.class.getName()).log(Level.INFO, "Commit succeded: " + aResponse.getStatusCode() + " " + aResponse.getStatusText());
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
			public void doWork(Response aResponse) throws Exception {
				Logger.getLogger(AppClient.class.getName()).log(Level.INFO, "Commit failed: " + aResponse.getStatusCode() + " " + aResponse.getStatusText());
				changeLog.clear();
				for (TransactionListener l : transactionListeners.toArray(new TransactionListener[] {})) {
					try {
						l.rolledback();
					} catch (Exception ex) {
						Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				if (onFailure != null)
					onFailure.run();
			}
		});
	}

	public String scriptUrl(String aScriptName) {
		return baseUrl + SCRIPTS_URI + "/" + aScriptName;
	}

	public String resourceUrl(String aResourceName) {
		return baseUrl + RESOURCES_URI + "/" + aResourceName;
	}

	public Cancellable getStartElement(Callback onSuccess) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqStartAppElement));
		return startRequest(API_URI, query, "", RequestBuilder.GET, new ResponseCallbackAdapter() {

			@Override
			protected void doWork(Response aResponse) throws Exception {
			}
		}, null);
	}

	public Cancellable getAppElement(final String appElementName, final Callback<Document> onSuccess, final Callback<Response> onFailure) throws Exception {
		Document doc = appElements.get(appElementName);
		if (doc != null) {
			onSuccess.run(doc);
			return new Cancellable() {

				@Override
				public void cancel() {
					// no op here because of no request have been sent
				}
			};
		} else {
			String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqAppElement)), param(PlatypusHttpRequestParams.ENTITY_ID, appElementName));
			return startRequest(API_URI, query, "", RequestBuilder.GET, new ResponseCallbackAdapter() {

				@Override
				public void doWork(Response aResponse) throws Exception {
					// Some post processing
					String text = aResponse.getText();
					Document doc = text != null && !text.isEmpty() ? XMLParser.parse(text) : null;
					appElements.put(appElementName, doc);
					//
					onSuccess.run(doc);
				}
			}, onFailure);
		}
	}

	public Cancellable createServerModule(final String aModuleName, final Callback<Document> onSuccess, final Callback<String> onFailure) throws Exception {
		Document doc = serverElements.get(aModuleName);
		if (doc != null) {
			onSuccess.run(doc);
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
				public void doWork(Response aResponse) throws Exception {
					// Some post processing
					String appElementName = aModuleName;
					addServerModule(appElementName, aResponse.getText());
					Document doc = XMLParser.createDocument();
					Node nd = doc.createElement("script");
					doc.appendChild(nd);
					Node ndc = doc.createElement(Loader.SCRIPT_SOURCE_TAG_NAME);
					String src = Utils.format(SERVER_MODULE_BODY, AppClient.isReport(appElementName) ? SERVER_REPORT_FUNCTION_NAME + appElementName : SERVER_MODULE_FUNCTION_NAME + appElementName,
					        appElementName);
					ndc.appendChild(doc.createTextNode(src));
					nd.appendChild(ndc);
					serverElements.put(appElementName, doc);
					onSuccess.run(doc);
				}
			}, new ResponseCallbackAdapter() {
				@Override
				protected void doWork(Response aResponse) throws Exception {
					if (onFailure != null) {
						onFailure.run(aResponse.getStatusText());
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

	public static native void publishApi(AppClient aClient) throws Exception /*-{
		$wnd.platypus.defineServerModule = function(aModuleName, aModule) {
			@com.eas.client.application.AppClient::defineServerModule(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aModule);
		}
		$wnd.platypus.executeServerModuleMethod = function(aModuleName, aMethodName, aParams, aCallBack) {
			return JSON
					.parse(aClient.@com.eas.client.application.AppClient::executeServerModuleMethod(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aMethodName, aParams, aCallBack));
		}
		$wnd.platypus.executeServerReport = function(aModuleName, aModule) {
			aClient.@com.eas.client.application.AppClient::executeServerReport(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aModule);
		}
	}-*/;

	public String executeServerModuleMethod(final String aModuleName, final String aMethodName, final JsArrayString aParams, final JavaScriptObject onSuccess) throws Exception {
		String[] convertedParams = new String[aParams.length()];
		for (int i = 0; i < aParams.length(); i++)
			convertedParams[i] = param(PlatypusHttpRequestParams.PARAMS_ARRAY, aParams.get(i));
		String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqExecuteServerModuleMethod)), param(PlatypusHttpRequestParams.MODULE_NAME, aModuleName),
		        param(PlatypusHttpRequestParams.METHOD_NAME, aMethodName), params(convertedParams));
		if (onSuccess != null) {
			startRequest(API_URI, query, null, RequestBuilder.GET, new ResponseCallbackAdapter() {

				@Override
				public void doWork(Response aResponse) throws Exception {
					callBack(onSuccess, aResponse.getText());
				}

				private native void callBack(JavaScriptObject onSuccess, String aData) throws Exception /*-{
		onSuccess(JSON.parse(aData));
	}-*/;

			}, null);
			return null;
		} else {
			return syncRequest(API_URI, query, null, RequestBuilder.GET);
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
			public void doWork(Response aResponse) throws Exception {
				// Some post processing
				Query query = readQuery(aResponse);
				query.setClient(AppClient.this);
				//
				onSuccess.run(query);
			}

			private Query readQuery(Response aResponse) throws Exception {
				return QueryReader.read(JSONParser.parseStrict(aResponse.getText()));
			}
		}, new ResponseCallbackAdapter() {
			@Override
			protected void doWork(Response aResponse) throws Exception {
				if (onFailure != null) {
					onFailure.run(aResponse.getStatusText());
				}
			}
		});
	}

	public Cancellable pollData(String aQueryId, Parameters aParams, final Callback<Rowset> onSuccess, final Callback<String> onFailure) throws Exception {
		String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqExecuteQuery)), param(PlatypusHttpRequestParams.QUERY_ID, aQueryId), params(aParams));
		return startRequest(API_URI, query, "", RequestBuilder.GET, new ResponseCallbackAdapter() {

			@Override
			public void doWork(Response aResponse) throws Exception {
				// Some post processing
				Rowset rowset = readRowset(aResponse);
				//
				onSuccess.run(rowset);
			}

			private Rowset readRowset(Response aResponse) throws Exception {
				return RowsetReader.read(JSONParser.parseStrict(aResponse.getText()));
			}
		}, new ResponseCallbackAdapter() {
			@Override
			protected void doWork(Response aResponse) throws Exception {
				if (onFailure != null) {
					onFailure.run(aResponse.getStatusText());
				}
			}
		});
	}
}
