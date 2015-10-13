package com.eas.grid;

import com.eas.core.Utils;
import com.eas.grid.ModelGrid;
import com.eas.grid.columns.header.CheckHeaderNode;
import com.eas.grid.columns.header.ModelHeaderNode;
import com.eas.grid.columns.header.RadioHeaderNode;
import com.eas.grid.columns.header.ServiceHeaderNode;
import com.eas.ui.PublishedComponent;
import com.google.gwt.core.client.JavaScriptObject;

public class GridPublisher {

	private static Utils.JsObject constructors = JavaScriptObject.createObject().cast();
	
	private static JavaScriptObject getPublisher(String aClassName){
		JavaScriptObject constr = constructors.getJs(aClassName);
		if(constr == null)
			throw new IllegalStateException("Constructor for " + aClassName + " was not found.");
		return constr;
	}
	
	public static native JavaScriptObject getConstructors()/*-{
		return @com.eas.grid.GridPublisher::constructors;
	}-*/;

	public static void putPublisher(String aClassName, JavaScriptObject aPublisher){
		constructors.setJs(aClassName, aPublisher);
	}
	
	public native static PublishedComponent publish(ModelGrid aComponent)/*-{
		var constr = @com.eas.grid.GridPublisher::getPublisher(Ljava/lang/String;)('ModelGrid');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(CheckHeaderNode aComponent)/*-{
		var constr = @com.eas.grid.GridPublisher::getPublisher(Ljava/lang/String;)('CheckGridColumn');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(RadioHeaderNode aComponent)/*-{
		var constr = @com.eas.grid.GridPublisher::getPublisher(Ljava/lang/String;)('RadioGridColumn');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ServiceHeaderNode aComponent)/*-{
		var constr = @com.eas.grid.GridPublisher::getPublisher(Ljava/lang/String;)('ServiceGridColumn');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelHeaderNode aComponent)/*-{
		var constr = @com.eas.grid.GridPublisher::getPublisher(Ljava/lang/String;)('ModelGridColumn');
		return new constr(aComponent);
	}-*/;

}
