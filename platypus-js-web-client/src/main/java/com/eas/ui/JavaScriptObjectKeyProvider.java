package com.eas.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JavaScriptObjectKeyProvider implements ProvidesKey<JavaScriptObject> {

	@Override
	public JavaScriptObject getKey(JavaScriptObject item) {
		return item;
	}
}
