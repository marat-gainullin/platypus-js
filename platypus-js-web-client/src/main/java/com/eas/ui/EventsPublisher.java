package com.eas.ui;

import com.eas.core.Utils;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ContainerEvent;
import com.eas.ui.events.ComponentEvent;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;

public class EventsPublisher {

	private static Utils.JsObject constructors = JavaScriptObject.createObject().cast();
	
	private static JavaScriptObject getPublisher(String aClassName){
		JavaScriptObject constr = constructors.getJs(aClassName);
		if(constr == null)
			throw new IllegalStateException("Constructor for " + aClassName + " was not found.");
		return constr;
	}
	
	public static native JavaScriptObject getConstructors()/*-{
		return @com.eas.ui.EventsPublisher::constructors;
	}-*/;

	public static void putPublisher(String aClassName, JavaScriptObject aPublisher){
		constructors.setJs(aClassName, aPublisher);
	}
	
	public native static JavaScriptObject publishSourcedEvent(JavaScriptObject aSource)/*-{
		return {source: aSource};
	}-*/;	
	
	public native static JavaScriptObject publishItemEvent(JavaScriptObject aSource, JavaScriptObject aItem)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ItemEvent');
		return new constr(aSource, aItem);
	}-*/;	
	
	public native static JavaScriptObject publishOnRenderEvent(JavaScriptObject aSource, Object aRowId, Object aColumnId, JavaScriptObject aRendered, PublishedCell aCell)/*-{
		var B = @com.eas.core.Predefine::boxing;
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('CellRenderEvent');
		return new constr(aSource, B.boxAsJs(aRowId), B.boxAsJs(aColumnId), aRendered, aCell);
	}-*/;
	
	public native static JavaScriptObject publishWindowEvent(Object aEvent, JavaScriptObject aWindow)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('WindowEvent');
		return new constr(aWindow);
	}-*/;
	
	public native static JavaScriptObject publishMouseEvent(NativeEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publishMouseEvent(NativeEvent aEvent, int clickCount)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent, clickCount);
	}-*/;
	
	public native static JavaScriptObject publishKeyEvent(NativeEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('KeyEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publishFocusEvent(NativeEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('FocusEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publishComponentEvent(ComponentEvent aEvent);
	
	public native static JavaScriptObject publishContainerEvent(ContainerEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ContainerEvent');
		return new constr(aEvent, true);
	}-*/;
	
	public native static JavaScriptObject publishActionEvent(ActionEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ActionEvent');
		return new constr(aEvent);
	}-*/;
}
