package com.eas.client.form.published;

import com.google.gwt.core.client.JavaScriptObject;

public interface HasBinding {

	public JavaScriptObject getData();

	public void setData(JavaScriptObject aValue);

	public String getField();

	public void setField(String aField);
}
