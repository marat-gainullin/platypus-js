package com.eas.client.xhr;

import com.google.gwt.core.client.JavaScriptObject;

public class XmlHttpRequestUpload extends JavaScriptObject {
	protected XmlHttpRequestUpload() {
	}
	
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

}