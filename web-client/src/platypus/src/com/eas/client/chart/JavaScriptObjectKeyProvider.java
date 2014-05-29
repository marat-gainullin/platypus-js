package com.eas.client.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JavaScriptObjectKeyProvider implements ProvidesKey<JavaScriptObject> {

	@Override
	public String getKey(JavaScriptObject item) {
		return String.valueOf(item.hashCode());
	}

}
