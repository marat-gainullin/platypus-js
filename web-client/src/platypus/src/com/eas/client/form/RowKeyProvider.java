package com.eas.client.form;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class RowKeyProvider implements ProvidesKey<JavaScriptObject> {

	@Override
	public JavaScriptObject getKey(JavaScriptObject item) {
		return item;
	}
}
