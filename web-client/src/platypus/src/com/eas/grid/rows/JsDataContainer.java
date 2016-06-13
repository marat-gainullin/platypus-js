package com.eas.grid.rows;

import com.eas.ui.HasJsData;
import com.google.gwt.core.client.JavaScriptObject;

public interface JsDataContainer extends HasJsData {

	public void changedItems(JavaScriptObject anArray);
	
	public void addedItems(JavaScriptObject anArray);
	
	public void removedItems(JavaScriptObject anArray);
	
}
