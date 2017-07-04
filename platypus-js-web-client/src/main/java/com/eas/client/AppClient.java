package com.eas.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.published.PublishedFile;
import com.eas.client.serial.QueryJSONReader;
import com.eas.client.xhr.FormData;
import com.eas.client.xhr.ProgressEvent;
import com.eas.client.xhr.ProgressHandler;
import com.eas.client.xhr.ProgressHandlerAdapter;
import com.eas.client.xhr.XMLHttpRequest2;
import com.eas.core.Cancellable;
import com.eas.core.Logger;
import com.eas.core.Utils;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.xml.client.Document;

/**
 *
 * @author mg
 */
public class AppClient {

    //
    private static final RegExp httpPrefixPattern = RegExp.compile("https?://.*");
    public static final String REPORT_LOCATION_CONTENT_TYPE = "text/platypus-report-location";

    private String principal;
    
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
        String mormalizedAbsoluteModuleUrl = URL.decode(moduleIdNormalizer.getFirstChildElement().<AnchorElement>cast().getHref());
        String hostContextPrefix = AppClient.relativeUri() + AppClient.getSourcePath();
        Element hostContextNormalizer = com.google.gwt.dom.client.Document.get().createDivElement();
        hostContextNormalizer.setInnerHTML("<a href=\"" + hostContextPrefix + "\">o</a>");
        String mormalizedHostContextPrefix = URL.decode(hostContextNormalizer.getFirstChildElement().<AnchorElement>cast().getHref());
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
            AppClient.getInstance().startRequest(uri, aBinary ? XMLHttpRequest.ResponseType.ArrayBuffer : XMLHttpRequest.ResponseType.Default, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

                @Override
                protected void doWork(XMLHttpRequest aResult) throws Exception {
                    if (aResult.getStatus() == Response.SC_OK) {
                        String responseType = aResult.getResponseType();
                        if (XMLHttpRequest.ResponseType.ArrayBuffer.getResponseTypeString().equalsIgnoreCase(responseType)) {
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
                            Logger.severe(ex);
                        }
                    }
                }

            });
        } else {
            XMLHttpRequest2 executed = AppClient.getInstance().syncRequest(uri.asString(), XMLHttpRequest.ResponseType.Default);
            if (executed != null) {
                if (executed.getStatus() == Response.SC_OK) {
                    String responseType = executed.getResponseType();
                    if (XMLHttpRequest.ResponseType.ArrayBuffer.getResponseTypeString().equalsIgnoreCase(responseType)) {
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
                        Logger.severe(ex);
                    }
                }

                public void onFailure(String reason) {
                    if (aErrorCallback != null) {
                        try {
                            Utils.executeScriptEventVoid(aErrorCallback, aErrorCallback, Utils.toJs(reason));
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }
                }

            });
            return Utils.publishCancellable(cancellable);
        } else {
            return null;
        }
    }

    public Cancellable startUploadRequest(final PublishedFile aFile, String aName, final Callback<ProgressEvent, String> aCallback) {
        final XMLHttpRequest2 req = XMLHttpRequest.create().<XMLHttpRequest2>cast();
        req.open("post", remoteApiUri() + APPLICATION_URI);
        final ProgressHandler handler = new ProgressHandlerAdapter() {
            @Override
            public void onProgress(ProgressEvent aEvent) {
                try {
                    if (aCallback != null) {
                        aCallback.onSuccess(aEvent);
                    }
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
            }

            public void onLoadEnd(XMLHttpRequest xhr) {
                try {
                    if (aCallback != null) {
                        aCallback.onSuccess(ProgressEvent.create(aFile.getSize(), aFile.getSize(), null));
                    }
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
            }

            @Override
            public void onTimeOut(XMLHttpRequest xhr) {
                if (aCallback != null) {
                    try {
                        aCallback.onFailure("timeout");
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }

            @Override
            public void onAbort(XMLHttpRequest xhr) {
                if (aCallback != null) {
                    try {
                        aCallback.onFailure("aborted");
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }

            @Override
            public void onError(XMLHttpRequest xhr) {
                if (aCallback != null) {
                    try {
                        aCallback.onFailure(xhr.getStatusText());
                    } catch (Exception ex) {
                        Logger.severe(ex);
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
                            if (aCallback != null) {
                                aCallback.onSuccess(ProgressEvent.create(aFile.getSize(), aFile.getSize(), xhr));
                            }
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    } else {
                        if (xhr.getStatus() == 0) {
                            handler.onAbort(xhr);
                        } else {
                            handler.onError(xhr);
                        }
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
        String paramsData = params(parameters.toArray(new String[]{}));
        if (aMethod != RequestBuilder.POST) {
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
                            Logger.severe(ex);
                        }
                    }
                }
            }
        });
        if (aMethod == RequestBuilder.POST) {
            req.send(paramsData);
        } else {
            req.send();
        }
        return new Cancellable() {
            @Override
            public void cancel() {
                req.abort();
            }
        };
    }

    public AppClient() {
        super();
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
                    Logger.severe(ex);
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
        frame.setUrl(remoteApiUri() + APPLICATION_URI + aUrlPrefix + "?" + query);
        RootPanel.get().add(frame);
    }

    public XMLHttpRequest2 syncRequest(String aUrl, XMLHttpRequest.ResponseType aResponseType) throws Exception {
        final XMLHttpRequest2 req = syncRequest(aUrl, aResponseType, null, RequestBuilder.GET);
        if (req.getStatus() == Response.SC_OK) {
            return req;
        } else {
            throw new Exception(req.getStatus() + " " + req.getStatusText());
        }
    }

    public XMLHttpRequest2 syncApiRequest(String aUrlPrefix, final String aUrlQuery, XMLHttpRequest.ResponseType aResponseType) throws Exception {
        String url = remoteApiUri() + APPLICATION_URI + (aUrlPrefix != null ? aUrlPrefix : "") + "?" + aUrlQuery;
        final XMLHttpRequest2 req = syncRequest(url, aResponseType, null, RequestBuilder.GET);
        if (req.getStatus() == Response.SC_OK) {
            return req;
        } else {
            throw new Exception(req.getStatus() + " " + req.getStatusText());
        }
    }

    public XMLHttpRequest2 syncRequest(String aUrl, XMLHttpRequest.ResponseType aResponseType, String aBody, RequestBuilder.Method aMethod) throws Exception {
        final XMLHttpRequest2 req = XMLHttpRequest.create().<XMLHttpRequest2>cast();
        aUrl = Loader.URL_QUERY_PROCESSOR.process(aUrl);
        req.open(aMethod.toString(), aUrl, false);
        /*
         * Since W3C standard about sync XmlHttpRequest and response type. if
         * (aResponseType != null && aResponseType != ResponseType.Default)
         * req.setResponseType(aResponseType);
         */
        req.setRequestHeader("Pragma", "no-cache");
        if (aBody != null) {
            req.send(aBody);
        } else {
            req.send();
        }
        if (req.getStatus() == Response.SC_OK) {
            return req;
        } else {
            throw new Exception(req.getStatus() + " " + req.getStatusText());
        }
    }

    public Cancellable requestCommit(final JavaScriptObject changeLog, final Callback<Void, String> aCallback) throws Exception {
        String query = param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqCommit));
        return startApiRequest(null, query, Utils.JsObject.writeJSON(changeLog), RequestBuilder.POST, "application/json; charset=utf-8", new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

            @Override
            public void doWork(XMLHttpRequest aResponse) throws Exception {
                Logger.info("Commit succeded: " + aResponse.getStatus() + " " + aResponse.getStatusText());
                if (aCallback != null) {
                    aCallback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(XMLHttpRequest aResponse) {
                Logger.info("Commit failed: " + aResponse.getStatus() + " " + aResponse.getStatusText());
                if (aCallback != null) {
                    aCallback.onFailure(aResponse.getStatusText());
                }
            }
        });
    }

    public void jsLoggedInUser(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
        requestLoggedInUser(new CallbackAdapter<String, String>() {

            @Override
            protected void doWork(String aResult) throws Exception {
                if (onSuccess != null) {
                    onSuccess.<Utils.JsObject>cast().call(null, Utils.toJs(aResult));
                }
            }

            @Override
            public void onFailure(String reason) {
                if (onFailure != null) {
                    onFailure.<Utils.JsObject>cast().call(null, Utils.toJs(reason));
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
                        Object oUserName = jsObject.<Utils.JsObject>cast().getJava("userName");
                        assert oUserName == null || oUserName instanceof String : "Credential request expects null or String value as a user name.";
                        principal = (String) oUserName;
                        if (principal == null || principal.isEmpty()) {
                            principal = "anonymous" + String.valueOf(IdGenerator.genId());
                        }
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
                    if (aCallback != null) {
                        aCallback.onFailure(reason.getStatus() + " : " + reason.getStatusText());
                    }
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

    public static native JavaScriptObject createReport(JavaScriptObject Report, String reportLocation)/*-{
        return new Report(reportLocation);
    }-*/;

    public Object requestServerMethodExecution(final String aModuleName, final String aMethodName, final JsArrayString aParams, final JavaScriptObject onSuccess, final JavaScriptObject onFailure,
            final JavaScriptObject aReportConstructor) throws Exception {
        String[] convertedParams = new String[aParams.length()];
        for (int i = 0; i < aParams.length(); i++) {
            convertedParams[i] = param(PlatypusHttpRequestParams.PARAMS_ARRAY, aParams.get(i));
        }
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
                            Logger.severe(ex);
                        }
                    }
                }

            });
            return null;
        } else {
            XMLHttpRequest2 executed = syncApiRequest(null, query, XMLHttpRequest.ResponseType.Default);
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
    
    function getCachedAppQuery(aQueryName) {
        var queryBody = queries.get(aQueryName);
        if (queryBody) {
            var query = QueryJSONReader.read(queryBody);
            query.setClient(this);
            return query;
        } else {
            return null;
        }
    }

    public Cancellable requestData(String aQueryName, Parameters aParams, final Fields aExpectedFields, final Callback<JavaScriptObject, String> aCallback) throws Exception {
        String query = params(param(PlatypusHttpRequestParams.TYPE, String.valueOf(Requests.rqExecuteQuery)), param(PlatypusHttpRequestParams.QUERY_ID, aQueryName), params(aParams));
        return startApiRequest(null, query, "", RequestBuilder.GET, null, new CallbackAdapter<XMLHttpRequest, XMLHttpRequest>() {

            @Override
            public void doWork(XMLHttpRequest aResponse) throws Exception {
                if (isJsonResponse(aResponse)) {
                    JavaScriptObject parsed = Utils.JsObject.parseJSONDateReviver(aResponse.getResponseText());
                    if (aCallback != null) {
                        aCallback.onSuccess(parsed);
                    }
                } else {
                    if (aCallback != null) {
                        aCallback.onFailure(aResponse.getResponseText());
                    }
                }
            }

            @Override
            public void onFailure(XMLHttpRequest aResponse) {
                if (aCallback != null) {
                    int status = aResponse.getStatus();
                    String statusText = aResponse.getStatusText();
                    if (statusText == null || statusText.isEmpty()) {
                        statusText = null;
                    }
                    if (status == 0) {
                        Logger.warning("Data recieving is aborted");
                    }
                    aCallback.onFailure(statusText);
                }
            }
        });
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
