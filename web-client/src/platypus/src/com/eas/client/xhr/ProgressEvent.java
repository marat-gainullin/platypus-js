package com.eas.client.xhr;

import com.google.gwt.core.client.JavaScriptObject;

public class ProgressEvent extends JavaScriptObject {
	protected ProgressEvent() {
	}

	public static native ProgressEvent create(double aLoaded, double aTotal)/*-{
		return {loaded : aLoaded, total: aTotal, lengthComputable : true};
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

}
