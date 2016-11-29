package com.eas.client.xhr;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class ProgressEvent extends JavaScriptObject {
	protected ProgressEvent() {
	}

	public static native ProgressEvent create(double aLoaded, double aTotal, XMLHttpRequest xhr)/*-{
		return {loaded : aLoaded, total: aTotal, lengthComputable : true, request: xhr};
	}-*/;
	
	public native final boolean isLengthComputable()/*-{
		return this.lengthComputable;
	}-*/;

	public native final double getLoaded()/*-{
		return this.loaded;
	}-*/;

	public native final double getTotal()/*-{
		return this.total;
	}-*/;

	public native final boolean isComplete()/*-{
		return this.lengthComputable && this.total == this.loaded;
	}-*/;

	public native final XMLHttpRequest getRequest()/*-{
		return this.request;
	}-*/;
}
