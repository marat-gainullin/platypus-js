package com.eas.client.xhr;

import com.google.gwt.core.client.JavaScriptObject;

public class FormData extends JavaScriptObject {
	protected FormData() {
	}

	public final native void append(String name, String value) /*-{
		this.append(name, value);
	}-*/;

	public final native void append(String name, JavaScriptObject value) /*-{
		this.append(name, value);
	}-*/;

	public final native void append(String name, JavaScriptObject value, String filename) /*-{
		this.append(name, value, filename);
	}-*/;
	
	public static native FormData create() /*-{
		return new FormData();
	}-*/;
}
