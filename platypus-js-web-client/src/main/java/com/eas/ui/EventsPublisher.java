package com.eas.ui;

import com.eas.core.Utils;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.AddEvent;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.RemoveEvent;
import com.eas.ui.events.ShowEvent;
import com.eas.window.events.MoveEvent;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;

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
	
	public native static JavaScriptObject publish(MouseDownEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseUpEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseWheelEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseMoveEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ClickEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent, 1);
	}-*/;
	
	public native static JavaScriptObject publish(DoubleClickEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent, 2);
	}-*/;
	
	public native static JavaScriptObject publish(MouseOverEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseOutEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyDownEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('KeyEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyUpEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('KeyEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyPressEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('KeyEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(FocusEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('FocusEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(BlurEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('FocusEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ResizeEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ComponentEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ShowEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ComponentEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(HideEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ComponentEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MoveEvent<Object> aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ComponentEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(AddEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ContainerEvent');
		return new constr(aEvent, true);
	}-*/;
	
	public native static JavaScriptObject publish(RemoveEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ContainerEvent');
		return new constr(aEvent, false);
	}-*/;
	
	public native static JavaScriptObject publish(ActionEvent aEvent)/*-{
		var constr = @com.eas.ui.EventsPublisher::getPublisher(Ljava/lang/String;)('ActionEvent');
		return new constr(aEvent);
	}-*/;
}
