package com.eas.ui;

import com.google.gwt.core.client.JavaScriptObject;

public class PublishedAbsoluteConstraints extends JavaScriptObject {

	protected PublishedAbsoluteConstraints() {
	}

	public final native String getLeft()/*-{
		if (this.left != undefined && this.left != null) {
			return '' + this.left;
		}
		return null;
	}-*/;

	public final native String getWidth()/*-{
		if (this.width != undefined && this.width != null) {
			return '' + this.width;
		}
		return null;
	}-*/;

	public final native String getTop()/*-{
		if (this.top != undefined && this.top != null) {
			return '' + this.top;
		}
		return null;
	}-*/;

	public final native String getHeight()/*-{
		if (this.height != undefined && this.height != null) {
			return '' + this.height;
		}
		return null;
	}-*/;
	
	public static native PublishedAbsoluteConstraints createDefaultAnchors()/*-{
		return {left: '0px', top: '0px', width: '10px', height: '10px'};
	}-*/;
}
