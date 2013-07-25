package com.eas.client.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.data.shared.ModelKeyProvider;

public class JavaScriptObjectKeyProvider implements ModelKeyProvider<JavaScriptObject> {

	@Override
	public String getKey(JavaScriptObject item) {
		return String.valueOf(item.hashCode());
	}

}
