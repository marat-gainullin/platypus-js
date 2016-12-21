package com.eas.client.published;

import com.google.gwt.core.client.JavaScriptObject;

public class PublishedFile extends JavaScriptObject {

	protected PublishedFile() {
		super();
	}

	public native final String getName()/*-{
		return this.name;
	}-*/;

	public native final double getSize()/*-{
		return this.size;
	}-*/;
}
