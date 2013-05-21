package com.eas.client.xhr;

import com.google.gwt.xhr.client.XMLHttpRequest;

public class XMLHttpRequest2 extends XMLHttpRequest {

	protected XMLHttpRequest2() {
	}

	public static native XMLHttpRequest2 createAnon() /*-{
		if ($wnd.AnonXMLHttpRequest) {
			return new $wnd.AnonXMLHttpRequest();
		} else {
			return @com.google.gwt.xhr.client.XMLHttpRequest::create();
		}
	}-*/;

    public final native void open(String httpMethod, String url, String user, String password, boolean async) /*-{
		this.open(httpMethod, url, async, user, password);
    }-*/;
	  
    public final native void open(String httpMethod, String url, boolean async) /*-{
		this.open(httpMethod, url, async);
    }-*/;
    
	public final native <T> T getResponse() /*-{
		return this.response;
	}-*/;

	public final native int getTimeOut() /*-{
		return this.timeout;
	}-*/;

	public final native XmlHttpRequestUpload getUpload() /*-{
		return this.upload;
	}-*/;

	public final native boolean getWithCredentials() /*-{
		return this.withCredentials;
	}-*/;

	public final native void overrideMimeType(String mimeType) /*-{
		this.overrideMimeType(mimeType);
	}-*/;

	public final native <T> void send(T requestData) /*-{
		this.send(requestData);
	}-*/;

	public final native void setOnLoadStart(ProgressHandler handler) /*-{
		var _this = this;
		this.onloadstart = function() {
			handler.@com.eas.client.xhr.ProgressHandler::onLoadStart(Lcom/google/gwt/xhr/client/XMLHttpRequest;)(_this);
		};
	}-*/;

	public final native void setOnProgress(ProgressHandler handler) /*-{
		var _this = this;
		this.onprogress = function(evt) {
			handler.@com.eas.client.xhr.ProgressHandler::onProgress(Lcom/eas/client/xhr/ProgressEvent;)(evt);
		};
	}-*/;

	public final native void setOnAbort(ProgressHandler handler) /*-{
		var _this = this;
		this.onabort = function() {
			handler.@com.eas.client.xhr.ProgressHandler::onAbort(Lcom/google/gwt/xhr/client/XMLHttpRequest;)(_this);
		};
	}-*/;

	public final native void setOnError(ProgressHandler handler) /*-{
		var _this = this;
		this.onerror = function() {
			handler.@com.eas.client.xhr.ProgressHandler::onError(Lcom/google/gwt/xhr/client/XMLHttpRequest;)(_this);
		};
	}-*/;

	public final native void setOnLoad(ProgressHandler handler) /*-{
		var _this = this;
		this.onload = function() {
			handler.@com.eas.client.xhr.ProgressHandler::onLoad(Lcom/google/gwt/xhr/client/XMLHttpRequest;)(_this);
		};
	}-*/;

	public final native void setOnTimeOut(ProgressHandler handler) /*-{
		var _this = this;
		this.ontimeout = function() {
			handler.@com.eas.client.xhr.ProgressHandler::onTimeOut(Lcom/google/gwt/xhr/client/XMLHttpRequest;)(_this);
		};
	}-*/;

	public final native void setOnLoadEnd(ProgressHandler handler) /*-{
		var _this = this;
		this.onloadend = function() {
			handler.@com.eas.client.xhr.ProgressHandler::onLoadEnd(Lcom/google/gwt/xhr/client/XMLHttpRequest;)(_this);
		};
	}-*/;

	public final native void setTimeOut(int timeout) /*-{
		this.timeout = timeout;
	}-*/;

	public final native void setUpload(XmlHttpRequestUpload upload) /*-{
		this.upload = upload;
	}-*/;
}