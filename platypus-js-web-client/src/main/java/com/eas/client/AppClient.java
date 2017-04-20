/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.application.Loader;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.published.PublishedFile;
import com.eas.client.queries.Query;
import com.eas.client.serial.QueryJSONReader;
import com.eas.client.xhr.FormData;
import com.eas.client.xhr.ProgressEvent;
import com.eas.client.xhr.ProgressHandler;
import com.eas.client.xhr.ProgressHandlerAdapter;
import com.eas.client.xhr.XMLHttpRequest2;
import com.eas.core.Cancellable;
import com.eas.core.Utils;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

/**
 * 
 * @author mg
 */
public class AppClient {

	//
	private static final RegExp httpPrefixPattern = RegExp.compile("https?://.*");
	public static final String APPLICATION_URI = "/application";
	private static String sourcePath = "/";
	private static boolean simpleModules;
	public static final String REPORT_LOCATION_CONTENT_TYPE = "text/platypus-report-location";
	//
	private static AppClient appClient;
	private boolean cacheBustEnabled;
	private String apiUrl;
	private String principal;
	private Map<String, Document> documents = new HashMap<String, Document>();
	private Map<String, ModuleStructure> modulesStructures = new HashMap<String, ModuleStructure>();
	private Map<String, Query> queries = new HashMap<String, Query>();
	private Map<String, Object> serverModules = new HashMap<>();

	public static class ModuleStructure {

		protected Set<String> structure = new HashSet<>();
		protected Set<String> clientDependencies = new HashSet<>();
		protected Set<String> serverDependencies = new HashSet<>();
		protected Set<String> queriesDependencies = new HashSet<>();

		public ModuleStructure(String aRelativeFileName) {
			super();
			structure.add(aRelativeFileName);
		}

		public ModuleStructure(Set<String> aStructure, Set<String> aClientDependencies, Set<String> aServerDependencies, Set<String> aQueriesDependencies) {
			super();
			structure.addAll(aStructure);
			clientDependencies.addAll(aClientDependencies);
			serverDependencies.addAll(aServerDependencies);
			queriesDependencies.addAll(aQueriesDependencies);
		}

		public ModuleStructure(String aRelativeFileName, JsArrayString prefetchedResources, JsArrayString autoDependencies, JsArrayString rpcDependencies, JsArrayString entitiesDependencies) {
			this(aRelativeFileName);
			if (prefetchedResources != null) {
				for (int i = 0; i < prefetchedResources.length(); i++) {
					structure.add(prefetchedResources.get(i));
				}
			}
			if (autoDependencies != null) {
				for (int i = 0; i < autoDependencies.length(); i++) {
					clientDependencies.add(autoDependencies.get(i));
				}
			}
			if (rpcDependencies != null) {
				for (int i = 0; i < rpcDependencies.length(); i++) {
					serverDependencies.add(rpcDependencies.get(i));
				}
			}
			if (entitiesDependencies != null) {
				for (int i = 0; i < entitiesDependencies.length(); i++) {
					queriesDependencies.add(entitiesDependencies.get(i));
				}
			}
		}

		public Set<String> getStructure() {
			return Collections.unmodifiableSet(structure);
		}

		public Set<String> getClientDependencies() {
			return Collections.unmodifiableSet(clientDependencies);
		}

		public Set<String> getQueriesDependencies() {
			return Collections.unmodifiableSet(queriesDependencies);
		}

		public Set<String> getServerDependencies() {
			return Collections.unmodifiableSet(serverDependencies);
		}
	}

	public static String remoteApiUri() {
		NodeList<com.google.gwt.dom.client.Element> metas = com.google.gwt.dom.client.Document.get().getHead().getElementsByTagName("meta");
		for (int i = 0; i < metas.getLength(); i++) {
			com.google.gwt.dom.client.Element meta = metas.getItem(i);
			if ("platypus-server".equalsIgnoreCase(meta.getAttribute("name"))) {
				return meta.getAttribute("content");
			}
		}
		return relativeUri();
	}

	public static String relativeUri() {
		String pageUrl = GWT.getHostPageBaseURL();
		pageUrl = pageUrl.substring(0, pageUrl.length() - 1);
		return pageUrl;
	}

	public static AppClient getInstance() {
		if (appClient == null) {
			appClient = new AppClient(remoteApiUri() + APPLICATION_URI);
		}
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

	public static boolean isSimpleModules() {
		return simpleModules;
	}

	public static void setSimpleModules(boolean aValue) {
		simpleModules = aValue;
	}

	public static String getSourcePath() {
		return sourcePath;
	}

	public static void setSourcePath(String aValue) {
		if (aValue != null && !aValue.isEmpty()) {
			sourcePath = aValue;
			if (!sourcePath.endsWith("/")) {
				sourcePath = sourcePath + "/";
			}
			if (!sourcePath.startsWith("/")) {
				sourcePath = "/" + sourcePath;
			}
		} else {
			sourcePath = "/";
		}
	}

	public SafeUri getResourceUri(final String aResourceName) {
		return new SafeUri() {

			@Override
			public String asString() {
				MatchResult htppMatch = httpPrefixPattern.exec(aResourceName);
				if (htppMatch != null) {
					return aResourceName;
				} else {
					return relativeUri() + getSourcePath() + aResourceName;
				}
			}
		};
	}

	public static String toFilyAppModuleId(String aRelative, String aStartPoint) {
		Element moduleIdNormalizer = com.google.gwt.dom.client.Document.get().createDivElement();
		moduleIdNormalizer.setInnerHTML("<a href=\"" + aStartPoint + "/" + aRelative + "\">o</a>");
		String mormalizedAbsoluteModuleUrl = URL.decode(moduleIdNormalizer.getFirstChildElement().<AnchorElement> cast().getHref());
		String hostContextPrefix = AppClient.relativeUri() + AppClient.getSourcePath();
		Element hostContextNormalizer = com.google.gwt.dom.client.Document.get().createDivElement();
		hostContextNormalizer.setInnerHTML("<a href=\"" + hostContextPrefix + "\">o</a>");
		String mormalizedHostContextPrefix = URL.decode(hostContextNormalizer.getFirstChildElement().<AnchorElement> cast().getHref());
		return mormalizedAbsoluteModuleUrl.substring(mormalizedHostContextPrefix.length());
	}

	public static Object jsLoad(String aResourceName, final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		return _jsLoad(aResourceName, true, onSuccess, onFailure);
	}

	public static Object jsLoadText(String aResourceName, final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		return _jsLoad(aResourceName, false, onSuccess, onFailure);
	}

	public static Object _jsLoad(final String aResourceName, boolean aBinary, final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		final String callerDir = Utils.lookupCallerJsDir();
		SafeUri uri = AppClient.getInstance().getResourceUri(aResourceName.startsWith("./") || aResourceName.startsWith("../") ? toFilyAppModuleId(aResourceName, callerDir) : aResourceName);
		if (onSuccess != null) {
			AppClient.getInstance().startRequest(uri, aBinary ? ResponseType.ArrayBuffer : ResponseType.Default, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

				@Override
				protected void doWork(XMLHttpRequest aResult) throws Exception {
					if (aResult.getStatus() == Response.SC_OK) {
						String responseType = aResult.getResponseType();
						if (ResponseType.ArrayBuffer.getResponseTypeString().equalsIgnoreCase(responseType)) {
							Utils.JsObject buffer = (Utils.JsObject) Utils.toJs(aResult.getResponseArrayBuffer());
							int length = buffer.getInteger("byteLength");
							buffer.setInteger("length", length);
							Utils.executeScriptEventVoid(onSuccess, onSuccess, buffer);
						} else {
							Utils.executeScriptEventVoid(onSuccess, onSuccess, Utils.toJs(aResult.getResponseText()));
						}
					} else {
						if (onFailure != null) {
							Utils.executeScriptEventVoid(onFailure, onFailure, Utils.toJs(aResult.getStatusText()));
						}
					}
				}

				@Override
				public void onFailure(XMLHttpRequest aResult) {
					if (onFailure != null) {
						try {
							Utils.executeScriptEventVoid(onFailure, onFailure,
							        Utils.toJs(aResult.getStatus() != 0 ? aResult.getStatusText() : "Request has been cancelled. See browser's console for more details."));
						} catch (Exception ex) {
							Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}

			});
		} else {
			XMLHttpRequest2 executed = AppClient.getInstance().syncRequest(uri.asString(), ResponseType.Default);
			if (executed != null) {
				if (executed.getStatus() == Response.SC_OK) {
					String responseType = executed.getResponseType();
					if (ResponseType.ArrayBuffer.getResponseTypeString().equalsIgnoreCase(responseType)) {
						Utils.JsObject buffer = (Utils.JsObject) executed.getResponseArrayBuffer();
						int length = buffer.getInteger("byteLength");
						buffer.setInteger("length", length);
						return buffer;
					} else {
						return Utils.toJs(executed.getResponseText());
					}
				} else {
					throw new Exception(executed.getStatusText());
				}
			}
		}
		return null;
	}

	public static JavaScriptObject jsUpload(PublishedFile aFile, String aName, final JavaScriptObject aCompleteCallback, final JavaScriptObject aProgresssCallback,
	        final JavaScriptObject aErrorCallback) {
		if (aFile != null) {
			Cancellable cancellable = AppClient.getInstance().startUploadRequest(aFile, aName, new Callback<ProgressEvent, String>() {

				protected boolean completed;

				public void onSuccess(ProgressEvent aResult) {
					try {
						if (!completed) {
							if (aProgresssCallback != null) {
								Utils.executeScriptEventVoid(aProgresssCallback, aProgresssCallback, aResult);
							}

							if (aResult.isComplete() && aResult.getRequest() != null) {
								completed = true;
								if (aCompleteCallback != null) {
									Utils.executeScriptEventVoid(aCompleteCallback, aCompleteCallback, Utils.JsObject.parseJSON(aResult.getRequest().getResponseText()));
								}
							}
						}
					} catch (Exception ex) {
						Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				public void onFailure(String reason) {
					if (aErrorCallback != null) {
						try {
							Utils.executeScriptEventVoid(aErrorCallback, aErrorCallback, Utils.toJs(reason));
						} catch (Exception ex) {
							Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}

			});
			return Utils.publishCancellable(cancellable);
		} else
			return null;
	}

	public Cancellable startUploadRequest(final PublishedFile aFile, String aName, final Callback<ProgressEvent, String> aCallback) {
		final XMLHttpRequest2 req = XMLHttpRequest.create().<XMLHttpRequest2> cast();
		req.open("post", apiUrl);
		final ProgressHandler handler = new ProgressHandlerAdapter() {
			@Override
			public void onProgress(ProgressEvent aEvent) {
				try {
					if (aCallback != null)
						aCallback.onSuccess(aEvent);
				} catch (Exception ex) {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			public void onLoadEnd(XMLHttpRequest xhr) {
				try {
					if (aCallback != null)
						aCallback.onSuccess(ProgressEvent.create(aFile.getSize(), aFile.getSize(), null));
				} catch (Exception ex) {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
				}
			};

			@Override
			public void onTimeOut(XMLHttpRequest xhr) {
				if (aCallback != null) {
					try {
						aCallback.onFailure("timeout");
					} catch (Exception ex) {
						Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

			@Override
			public void onAbort(XMLHttpRequest xhr) {
				if (aCallback != null) {
					try {
						aCallback.onFailure("aborted");
					} catch (Exception ex) {
						Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

			@Override
			public void onError(XMLHttpRequest xhr) {
				if (aCallback != null) {
					try {
						aCallback.onFailure(xhr.getStatusText());
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
		fd.append(aFile.getName(), aFile, aName);
		req.overrideMimeType("multipart/form-data");
		// Must set the onreadystatechange handler before calling send().
		req.setOnReadyStateChange(new ReadyStateChangeHandler() {
                        @Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				if (xhr.getReadyState() == XMLHttpRequest.DONE) {
					xhr.clearOnReadyStateChange();
					if (xhr.getStatus() == Response.SC_OK) {
						try {
							if (aCallback != null)
								aCallback.onSuccess(ProgressEvent.create(aFile.getSize(), aFile.getSize(), xhr));
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

	public Cancellable submitForm(String aAction, RequestBuilder.Method aMethod, String aContentType, Map<String, String> aFormData, final Callback<XMLHttpRequest, XMLHttpRequest> aCallback) {
		final XMLHttpRequest req = XMLHttpRequest.create().cast();
		String urlPath = aAction != null ? aAction : "";
		List<String> parameters = new ArrayList<String>();
		for (String paramName : aFormData.keySet()) {
			parameters.add(param(paramName, aFormData.get(paramName)));
		}
                String paramsData = params(parameters.toArray(new String[] {}));
                if(aMethod != RequestBuilder.POST){
                    urlPath += "?" + paramsData;
                }
		req.open(aMethod.toString(), urlPath);
                req.setRequestHeader("Content-Type", aContentType);
		req.setOnReadyStateChange(new ReadyStateChangeHandler() {
                        @Override
			public void onReadyStateChange(final XMLHttpRequest xhr) {
				if (xhr.getReadyState() == XMLHttpRequest.DONE) {
					xhr.clearOnReadyStateChange();
					if (aCallback != null) {
                                                try {
                                                        if (xhr.getStatus() == Response.SC_OK) {
                                                                aCallback.onSuccess(xhr);
                                                        } else {
                                                                aCallback.onFailure(xhr);
                                                        }
                                                } catch (Exception ex) {
                                                        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                                                }
					}
				}
			}
		});
                if(aMethod == RequestBuilder.POST){
                    req.send(paramsData);
                } else{
                    req.send();
                }
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

	public AppClient(String aApiUrl) {
		super();
		apiUrl = aApiUrl;
	}

	public Document getModelDocument(String aModuleName) {
		ModuleStructure structure = modulesStructures.get(aModuleName);
		for (String part : structure.getStructure()) {
			if (part.toLowerCase().endsWith(".model")) {
				return documents.get(part);
			}
		}
		return null;
	}

	public Document getFormDocument(String aModuleName) {
		ModuleStructure structure = modulesStructures.get(aModuleName);
		for (String part : structure.getStructure()) {
			if (part.toLowerCase().endsWith(".layout")) {
				return documents.get(part);
			}
		}
		return null;
	}

	private String params(Parameters parameters) {
		String[] res = new String[parameters.getParametersCount()];
		for (int i = 0; i < parameters.getParametersCount(); i++) {
			Parameter p = parameters.get(i + 1);// parameters and fields are
			                                    // 1-based
			String sv = Utils.jsonStringify(p.getJsValue());
			res[i] = param(p.getName(), sv);
		}
		return params(res);
	}

	public JavaScriptObject jsLogout(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		return Utils.publishCancellable(requestLogout(new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

			@Override
			protected void doWork(XMLHttpRequest aResult) throws Exception {
				Utils.invokeJsFunction(onSuccess);
			}

			@Override
			public void onFailure(XMLHttpRequest reason) {
				try {
					Utils.executeScriptEventVoid(onFailure, onFailure, Utils.toJs(reason.getStatusText()));
				} catch (Exception ex) {
					Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}));
	}

	public Cancellable requestLogout(final Callback<XMLHttpRequest, XMLHttpRequest> aCallback) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqLogout));
		return startApiRequest(null, query, null, RequestBuilder.GET, null, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

			@Override
			protected void doWork(XMLHttpRequest aResult) throws Exception {
				principal = null;
				aCallback.onSuccess(aResult);
			}

			@Override
			public void onFailure(XMLHttpRequest reason) {
				aCallback.onFailure(reason);
			}

		});
	}

	public Cancellable startApiRequest(String aUrlPrefix, final String aUrlQuery, String aBody, RequestBuilder.Method aMethod, String aContentType, Callback<XMLHttpRequest, XMLHttpRequest> aCallback)
	        throws Exception {
		String url = apiUrl + (aUrlPrefix != null ? aUrlPrefix : "") + (aUrlQuery != null ? "?" + aUrlQuery : "");
		final XMLHttpRequest req = XMLHttpRequest.create();
		req.open(aMethod.toString(), url);
		if (aContentType != null && !aContentType.isEmpty()) {
			req.setRequestHeader("Content-Type", aContentType);
		}
		interceptRequest(req);
		req.setRequestHeader("Pragma", "no-cache");
		return startRequest(req, aBody, aCallback);
	}

	public Cancellable startRequest(SafeUri aUri, ResponseType aResponseType, Callback<XMLHttpRequest, XMLHttpRequest> aCallback) throws Exception {
		final XMLHttpRequest req = XMLHttpRequest.create();
		req.open(RequestBuilder.GET.toString(), aUri.asString());
		interceptRequest(req);
		if (aResponseType != null && aResponseType != ResponseType.Default)
			req.setResponseType(aResponseType);
		req.setRequestHeader("Pragma", "no-cache");
		return startRequest(req, null, aCallback);
	}

	public Cancellable startRequest(SafeUri aUri, Callback<XMLHttpRequest, XMLHttpRequest> aCallback) throws Exception {
		return startRequest(aUri, ResponseType.Default, aCallback);
	}

	public Cancellable startRequest(final XMLHttpRequest req, String aBody, final Callback<XMLHttpRequest, XMLHttpRequest> aCallback) throws Exception {
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
					String errorMsg = XMLHttpRequest2.getBrowserSpecificFailure(xhr);
					if (errorMsg != null) {
						Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, errorMsg);
						try {
							if (aCallback != null)
								aCallback.onFailure(xhr);
						} catch (Exception ex) {
							Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
						}
					} else {
						try {
							if (xhr.getStatus() == Response.SC_OK) {
								if (aCallback != null)
									aCallback.onSuccess(xhr);
							} else {
								if (aCallback != null)
									aCallback.onFailure(xhr);
							}
						} catch (Exception ex) {
							Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			}
		});

		if (aBody != null && !aBody.isEmpty())
			req.send(aBody);
		else
			req.send();
		return new Cancellable() {

			@Override
			public void cancel() {
				req.clearOnReadyStateChange();
				req.abort();
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
		for (Map.Entry<String, String> ent : aParams.entrySet()) {
			query += param(ent.getKey(), ent.getValue()) + "&";
		}
		query += param(PlatypusHttpRequestParams.TYPE, String.valueOf(aRequestType));
		frame.setUrl(apiUrl + aUrlPrefix + "?" + query);
		RootPanel.get().add(frame);
	}

	public XMLHttpRequest2 syncRequest(String aUrl, ResponseType aResponseType) throws Exception {
		final XMLHttpRequest2 req = syncRequest(aUrl, aResponseType, null, RequestBuilder.GET);
		if (req.getStatus() == Response.SC_OK)
			return req;
		else
			throw new Exception(req.getStatus() + " " + req.getStatusText());
	}

	public XMLHttpRequest2 syncApiRequest(String aUrlPrefix, final String aUrlQuery, ResponseType aResponseType) throws Exception {
		String url = apiUrl + (aUrlPrefix != null ? aUrlPrefix : "") + "?" + aUrlQuery;
		final XMLHttpRequest2 req = syncRequest(url, aResponseType, null, RequestBuilder.GET);
		if (req.getStatus() == Response.SC_OK)
			return req;
		else
			throw new Exception(req.getStatus() + " " + req.getStatusText());
	}

	public XMLHttpRequest2 syncRequest(String aUrl, ResponseType aResponseType, String aBody, RequestBuilder.Method aMethod) throws Exception {
		final XMLHttpRequest2 req = XMLHttpRequest.create().<XMLHttpRequest2> cast();
		aUrl = Loader.URL_QUERY_PROCESSOR.process(aUrl);
		req.open(aMethod.toString(), aUrl, false);
		interceptRequest(req);
		/*
		 * Since W3C standard about sync XmlHttpRequest and response type. if
		 * (aResponseType != null && aResponseType != ResponseType.Default)
		 * req.setResponseType(aResponseType);
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

	protected void interceptRequest(XMLHttpRequest req) {
		// No-op here. Some implementation is in the tests.
	}

	public Cancellable requestCommit(final JavaScriptObject changeLog, final Callback<Void, String> aCallback) throws Exception {
		String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqCommit));
		return startApiRequest(null, query, Utils.JsObject.writeJSON(changeLog), RequestBuilder.POST, "application/json; charset=utf-8", new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

			@Override
			public void doWork(XMLHttpRequest aResponse) throws Exception {
				Logger.getLogger(AppClient.class.getName()).log(Level.INFO, "Commit succeded: " + aResponse.getStatus() + " " + aResponse.getStatusText());
				if (aCallback != null)
					aCallback.onSuccess(null);
			}

			@Override
			public void onFailure(XMLHttpRequest aResponse) {
				Logger.getLogger(AppClient.class.getName()).log(Level.INFO, "Commit failed: " + aResponse.getStatus() + " " + aResponse.getStatusText());
				if (aCallback != null)
					aCallback.onFailure(aResponse.getStatusText());
			}
		});
	}

	public void jsLoggedInUser(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		requestLoggedInUser(new CallbackAdapter<String, String>() {

			@Override
			protected void doWork(String aResult) throws Exception {
				if (onSuccess != null) {
					onSuccess.<Utils.JsObject> cast().call(null, Utils.toJs(aResult));
				}
			}

			@Override
			public void onFailure(String reason) {
				if (onFailure != null) {
					onFailure.<Utils.JsObject> cast().call(null, Utils.toJs(reason));
				}
			}
		});
	}

	public void requestLoggedInUser(final Callback<String, String> aCallback) throws Exception {
		if (principal == null) {
			String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqCredential));
			startApiRequest(null, query, "", RequestBuilder.GET, null, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

				@Override
				protected void doWork(XMLHttpRequest aResponse) throws Exception {
					if (isJsonResponse(aResponse)) {
						String respText = aResponse.getResponseText();
						Object oResult = respText != null && !respText.isEmpty() ? Utils.toJava(Utils.jsonParse(respText)) : null;
						assert oResult == null || oResult instanceof JavaScriptObject : "Credential request expects null or JavaScriptObject value as a response.";
						JavaScriptObject jsObject = (JavaScriptObject) oResult;
						Object oUserName = jsObject.<Utils.JsObject> cast().getJava("userName");
						assert oUserName == null || oUserName instanceof String : "Credential request expects null or String value as a user name.";
						principal = (String) oUserName;
						if (principal == null || principal.isEmpty())
							principal = "anonymous" + String.valueOf(IdGenerator.genId());
						if (aCallback != null) {
							aCallback.onSuccess(principal);
						}
					} else {
						if (aCallback != null) {
							aCallback.onFailure(aResponse.getResponseText());
						}
					}
				}

				@Override
				public void onFailure(XMLHttpRequest reason) {
					principal = "anonymous" + String.valueOf(IdGenerator.genId());
					if (aCallback != null)
						aCallback.onFailure(reason.getStatus() + " : " + reason.getStatusText());
				}
			});
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

				@Override
				public void execute() {
					if (aCallback != null) {
						aCallback.onSuccess(principal);
					}
				}

			});
		}
	}

	public ModuleStructure getModuleStructure(String aModuleName) {
		return modulesStructures.get(aModuleName);
	}

	public ModuleStructure putModuleStructure(String aModuleName, ModuleStructure aStructure) {
		return modulesStructures.put(aModuleName, aStructure);
	}

	private native static void checkModulesIndex(AppClient aClient)/*-{
		if ($wnd.define) {
			var index = $wnd.define['modules-index'];
			for ( var fileName in index) {
				var structure = index[fileName];
				var mstructure = @com.eas.client.AppClient.ModuleStructure::new(Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JsArrayString;)(fileName, structure.prefetched, structure['global-deps'], structure.rpc, structure.entities);
				var defaultModuleName = fileName;
				if (defaultModuleName.endsWith('.js')) {
					defaultModuleName = defaultModuleName.substring(0, defaultModuleName.length - 3);
				}
				aClient.@com.eas.client.AppClient::putModuleStructure(Ljava/lang/String;Lcom/eas/client/AppClient$ModuleStructure;)(defaultModuleName, mstructure);
				if (structure.modules) {
					for ( var i = 0; i < structure.modules.length; i++) {
						aClient.@com.eas.client.AppClient::putModuleStructure(Ljava/lang/String;Lcom/eas/client/AppClient$ModuleStructure;)(structure.modules[i], mstructure);
					}
				}
			}
			$wnd.define['modules-index'] = {};
		}
	}-*/;

	public Cancellable requestModuleStructure(final String aModuleName, final Callback<ModuleStructure, XMLHttpRequest> aCallback) throws Exception {
		checkModulesIndex(this);
		if (modulesStructures.containsKey(aModuleName)) {
			if (aCallback != null) {
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

					@Override
					public void execute() {
						aCallback.onSuccess(modulesStructures.get(aModuleName));
					}
				});
			}
			return new Cancellable() {
				@Override
				public void cancel() {
				}
			};
		} else if (simpleModules) {
			if (aCallback != null) {
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

					@Override
					public void execute() {
						String fakeRelativeFileName = aModuleName;
						if (!fakeRelativeFileName.toLowerCase().endsWith(".js")) {
							fakeRelativeFileName = fakeRelativeFileName + ".js";
						}
						ModuleStructure s = new ModuleStructure(fakeRelativeFileName);
						aCallback.onSuccess(s);
					}
				});
			}
			return new Cancellable() {
				@Override
				public void cancel() {
				}
			};
		} else {
			String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqModuleStructure)), param(PlatypusHttpRequestParams.MODULE_NAME, aModuleName));
			query = Loader.URL_QUERY_PROCESSOR.process(query);
			return startApiRequest(null, query, "", RequestBuilder.GET, null, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

				@Override
				public void doWork(XMLHttpRequest aResponse) throws Exception {
					if (isJsonResponse(aResponse)) {
						// Some post processing
						String text = aResponse.getResponseText();
						JavaScriptObject _doc = text != null && !text.isEmpty() ? Utils.JsObject.parseJSON(text) : null;
						Utils.JsObject doc = _doc.cast();
						//
						Set<String> structure = new HashSet<String>();
						Set<String> clientDependencies = new HashSet<String>();
						Set<String> queryDependencies = new HashSet<String>();
						Set<String> serverModuleDependencies = new HashSet<String>();
						Utils.JsObject jsStructure = doc.getJs("structure").cast();
						Utils.JsObject jsClientDependencies = doc.getJs("clientDependencies").cast();
						Utils.JsObject jsQueryDependencies = doc.getJs("queryDependencies").cast();
						Utils.JsObject jsServerDependencies = doc.getJs("serverDependencies").cast();
						for (int i = 0; i < jsStructure.length(); i++) {
							structure.add(jsStructure.getStringSlot(i));
						}
						for (int i = 0; i < jsClientDependencies.length(); i++) {
							clientDependencies.add(jsClientDependencies.getStringSlot(i));
						}
						for (int i = 0; i < jsQueryDependencies.length(); i++) {
							queryDependencies.add(jsQueryDependencies.getStringSlot(i));
						}
						for (int i = 0; i < jsServerDependencies.length(); i++) {
							serverModuleDependencies.add(jsServerDependencies.getStringSlot(i));
						}
						ModuleStructure moduleStructure = new ModuleStructure(structure, clientDependencies, serverModuleDependencies, queryDependencies);
						modulesStructures.put(aModuleName, moduleStructure);
						if (aCallback != null) {
							aCallback.onSuccess(moduleStructure);
						}
					} else {
						if (aCallback != null)
							aCallback.onFailure(aResponse);
					}
				}

				@Override
				public void onFailure(XMLHttpRequest reason) {
					if (aCallback != null)
						aCallback.onFailure(reason);
				}
			});
		}
	}

	public Cancellable requestDocument(final String aResourceName, final Callback<Document, XMLHttpRequest> aCallback) throws Exception {
		if (documents.containsKey(aResourceName)) {
			final Document doc = documents.get(aResourceName);
			// doc may be null, because of application elements without a
			// xml-dom, plain scripts for example.
			if (aCallback != null) {
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

					@Override
					public void execute() {
						aCallback.onSuccess(doc);
					}
				});
			}
			return new Cancellable() {

				@Override
				public void cancel() {
					// no op here because of no request have been sent
				}
			};
		} else {
			SafeUri documentUri = new SafeUri() {

				@Override
				public String asString() {
					return checkedCacheBust(relativeUri() + getSourcePath() + aResourceName);
				}

			};
			return startRequest(documentUri, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

				@Override
				public void doWork(XMLHttpRequest aResponse) throws Exception {
					// Some post processing
					String text = aResponse.getResponseText();
					Document doc = text != null && !text.isEmpty() ? XMLParser.parse(text) : null;
					documents.put(aResourceName, doc);
					//
					if (aCallback != null)
						aCallback.onSuccess(doc);
				}

				@Override
				public void onFailure(XMLHttpRequest reason) {
					if (aCallback != null)
						aCallback.onFailure(reason);
				}
			});
		}
	}

	public Cancellable requestServerModule(final String aModuleName, final Callback<Void, String> aCallback) throws Exception {
		if (isServerModule(aModuleName)) {
			if (aCallback != null) {
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

					@Override
					public void execute() {
						aCallback.onSuccess(null);
					}
				});
			}
			return new Cancellable() {

				@Override
				public void cancel() {
					// no op here because of no request have been sent
				}
			};
		} else {
			String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqCreateServerModule)), param(PlatypusHttpRequestParams.MODULE_NAME, aModuleName));
			return startApiRequest(null, query, "", RequestBuilder.GET, null, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

				@Override
				public void doWork(XMLHttpRequest aResponse) throws Exception {
					// Some post processing
					if (isJsonResponse(aResponse)) {
						addServerModule(aModuleName, aResponse.getResponseText());
						if (aCallback != null) {
							aCallback.onSuccess(null);
						}
					} else {
						onFailure(aResponse);
					}
				}

				@Override
				public void onFailure(XMLHttpRequest aResponse) {
					if (aCallback != null) {
						String responseText = aResponse.getResponseText();
						aCallback.onFailure(responseText != null && !responseText.isEmpty() ? responseText : aResponse.getStatusText());
					}
				}

			});
		}
	}

	public Object getServerModule(String aModuleName) {
		return serverModules.get(aModuleName);
	}

	public void addServerModule(String aModuleName, String aStructure) throws Exception {
		serverModules.put(aModuleName, Utils.jsonParse(aStructure));
	}

	public boolean isServerModule(String aModuleName) throws Exception {
		return serverModules.containsKey(aModuleName);
	}

	public static native JavaScriptObject createReport(JavaScriptObject Report, String reportLocation)/*-{
		return new Report(reportLocation);
	}-*/;

	public boolean isCacheBustEnabled() {
		return cacheBustEnabled;
	}

	public void setCacheBustEnabled(boolean aValue) {
		cacheBustEnabled = aValue;
	}

	public Object requestServerMethodExecution(final String aModuleName, final String aMethodName, final JsArrayString aParams, final JavaScriptObject onSuccess, final JavaScriptObject onFailure,
	        final JavaScriptObject aReportConstructor) throws Exception {
		String[] convertedParams = new String[aParams.length()];
		for (int i = 0; i < aParams.length(); i++)
			convertedParams[i] = param(PlatypusHttpRequestParams.PARAMS_ARRAY, aParams.get(i));
		String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqExecuteServerModuleMethod)), param(PlatypusHttpRequestParams.MODULE_NAME, aModuleName),
		        param(PlatypusHttpRequestParams.METHOD_NAME, aMethodName), params(convertedParams));
		if (onSuccess != null) {
			startApiRequest(null, null, query, RequestBuilder.POST, "application/x-www-form-urlencoded; charset=utf-8", new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

				@Override
				public void doWork(XMLHttpRequest aResponse) throws Exception {
					if (isJsonResponse(aResponse)) {
						Utils.executeScriptEventVoid(onSuccess, onSuccess, Utils.toJs(aResponse.getResponseText()));
						// WARNING!!!Don't edit to Utils.jsonParse!
						// It is parsed in high-level js-code.
					} else if (isReportResponse(aResponse)) {
						Utils.executeScriptEventVoid(onSuccess, onSuccess, createReport(aReportConstructor, aResponse.getResponseText()));
					} else {
						Utils.executeScriptEventVoid(onSuccess, onSuccess, Utils.toJs(aResponse.getResponseText()));
					}
				}

				@Override
				public void onFailure(XMLHttpRequest aResponse) {
					if (onFailure != null) {
						try {
							String responseText = aResponse.getResponseText();
							if (isJsonResponse(aResponse)) {
								Utils.executeScriptEventVoid(onFailure, onFailure, Utils.jsonParse(responseText));
							} else {
								Utils.executeScriptEventVoid(onFailure, onFailure, Utils.toJs(responseText != null && !responseText.isEmpty() ? responseText : aResponse.getStatusText()));
							}
						} catch (Exception ex) {
							Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}

			});
			return null;
		} else {
			XMLHttpRequest2 executed = syncApiRequest(null, query, ResponseType.Default);
			if (executed != null) {
				if (executed.getStatus() == Response.SC_OK) {
					String responseType = executed.getResponseHeader("content-type");
					if (responseType != null) {
						if (isJsonResponse(executed)) {
                                                        // WARNING!!!Don't edit to Utils.jsonParse!
                                                        // It is parsed in high-level js-code.
							return Utils.toJs(executed.getResponseText());
						} else if (responseType.toLowerCase().contains(REPORT_LOCATION_CONTENT_TYPE)) {
							return createReport(aReportConstructor, executed.getResponseText());
						} else {
							return Utils.toJs(executed.getResponseText());
						}
					} else {
						return Utils.toJs(executed.getResponseText());
					}
				} else {
					String responseText = executed.getResponseText();
					throw new Exception(responseText != null && !responseText.isEmpty() ? responseText : executed.getStatusText());
				}
			} else {
				return null;
			}
		}
	}

	public Cancellable requestAppQuery(final String queryName, final Callback<Query, String> aCallback) throws Exception {
		final Query alreadyQuery = queries.get(queryName);
		if (alreadyQuery != null) {
			if (aCallback != null) {
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

					@Override
					public void execute() {
						aCallback.onSuccess(alreadyQuery);
					}
				});
			}
			return new Cancellable() {

				@Override
				public void cancel() {
					// no op here because of no request have been sent
				}
			};
		} else {
			String urlQuery = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqAppQuery)), param(PlatypusHttpRequestParams.QUERY_ID, queryName));
			return startApiRequest(null, urlQuery, "", RequestBuilder.GET, null, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

				@Override
				public void doWork(XMLHttpRequest aResponse) throws Exception {
					if (isJsonResponse(aResponse)) {
						// Some post processing
						Query query = readQuery(aResponse);
						query.setClient(AppClient.this);
						//
						queries.put(queryName, query);
						if (aCallback != null)
							aCallback.onSuccess(query);
					} else {
						if (aCallback != null) {
							aCallback.onFailure(aResponse.getResponseText());
						}
					}
				}

				private Query readQuery(XMLHttpRequest aResponse) throws Exception {
					return QueryJSONReader.read(Utils.JsObject.parseJSON(aResponse.getResponseText()));
				}

				@Override
				public void onFailure(XMLHttpRequest aResponse) {
					if (aCallback != null) {
						aCallback.onFailure(aResponse.getStatusText());
					}
				}
			});
		}
	}

	public Query getCachedAppQuery(String aQueryId) {
		Query query = queries.get(aQueryId);
		if (query != null) {
			AppClient client = query.getClient();
			query = query.copy();
			query.setClient(client);
		}
		return query;
	}

	public Cancellable requestData(String aQueryName, Parameters aParams, final Fields aExpectedFields, final Callback<JavaScriptObject, String> aCallback) throws Exception {
		String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqExecuteQuery)), param(PlatypusHttpRequestParams.QUERY_ID, aQueryName), params(aParams));
		return startApiRequest(null, query, "", RequestBuilder.GET, null, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

			@Override
			public void doWork(XMLHttpRequest aResponse) throws Exception {
				if (isJsonResponse(aResponse)) {
					JavaScriptObject parsed = Utils.JsObject.parseJSONDateReviver(aResponse.getResponseText());
					if (aCallback != null)
						aCallback.onSuccess(parsed);
				} else {
					if (aCallback != null)
						aCallback.onFailure(aResponse.getResponseText());
				}
			}

			@Override
			public void onFailure(XMLHttpRequest aResponse) {
				if (aCallback != null) {
					int status = aResponse.getStatus();
					String statusText = aResponse.getStatusText();
					if (statusText == null || statusText.isEmpty())
						statusText = null;
					if (status == 0)
						Logger.getLogger(AppClient.class.getName()).log(Level.WARNING, "Data recieving is aborted");
					aCallback.onFailure(statusText);
				}
			}
		});
	}

	public String checkedCacheBust(String aUrl) {
		return cacheBustEnabled ? aUrl + "?" + PlatypusHttpRequestParams.CACHE_BUSTER + "=" + IdGenerator.genId() : aUrl;
	}

	private boolean isJsonResponse(XMLHttpRequest aResponse) {
		String responseType = aResponse.getResponseHeader("content-type");
		if (responseType != null) {
			responseType = responseType.toLowerCase();
			return responseType.contains("application/json") || responseType.contains("application/javascript") || responseType.contains("text/json") || responseType.contains("text/javascript");
		} else {
			return false;
		}
	}

	private boolean isReportResponse(XMLHttpRequest aResponse) {
		String responseType = aResponse.getResponseHeader("content-type");
		if (responseType != null) {
			responseType = responseType.toLowerCase();
			return responseType.contains(REPORT_LOCATION_CONTENT_TYPE);
		} else {
			return false;
		}
	}
}
