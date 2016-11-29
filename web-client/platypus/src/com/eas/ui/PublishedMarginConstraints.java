package com.eas.ui;

import com.google.gwt.core.client.JavaScriptObject;

public class PublishedMarginConstraints extends JavaScriptObject {

	protected PublishedMarginConstraints() {
	}

	public final native String getLeft()/*-{
		if (this.left != undefined && this.left != null) {
			return '' + this.left;
		}
		return null;
	}-*/;

	public final native String getRight()/*-{
		if (this.right != undefined && this.right != null) {
			return '' + this.right;
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

	public final native String getBottom()/*-{
		if (this.bottom != undefined && this.bottom != null) {
			return '' + this.bottom;
		}
		return null;
	}-*/;

	public final native String getHeight()/*-{
		if (this.height != undefined && this.height != null) {
			return '' + this.height;
		}
		return null;
	}-*/;
	
	public static native PublishedMarginConstraints createDefaultAnchors()/*-{
		return {left: '0px', right: '0px', top: '0px', bottom: '0px'};
	}-*/;	
}
