package com.eas.client.form.api;

import com.eas.client.gxtcontrols.published.PublishedCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
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
import com.sencha.gxt.widget.core.client.event.AddEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.MoveEvent;
import com.sencha.gxt.widget.core.client.event.RemoveEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;

public class JSEvents {

	public native static JavaScriptObject getFormsClass()/*-{
		return $wnd.Form;
	}-*/;
	
	public native static JavaScriptObject publishScriptSourcedEvent(JavaScriptObject aSource)/*-{
		var published = {};
		Object.defineProperty(published, "source", {
			get : function(){
				return aSource;
			}
		});
		return published;
	}-*/;	
	
	public native static JavaScriptObject publishCursorPositionWillChangeEvent(JavaScriptObject aSource, int aNewIndex)/*-{
		var published = {};
		Object.defineProperty(published, "source", {
			get : function(){
				return aSource;
			}
		});
		Object.defineProperty(published, "newIndex", {
			get : function(){
				return aNewIndex;
			}
		});
		return published;
	}-*/;	
	
	public native static JavaScriptObject publishCursorPositionChangedEvent(JavaScriptObject aSource, int aOldIndex, int aNewIndex)/*-{
		var published = {};
		Object.defineProperty(published, "source", {
			get : function(){
				return aSource;
			}
		});
		Object.defineProperty(published, "oldIndex", {
			get : function(){
				return aOldIndex;
			}
		});
		Object.defineProperty(published, "newIndex", {
			get : function(){
				return aNewIndex;
			}
		});
		return published;
	}-*/;	
	
	
	public native static JavaScriptObject publishEntityInstanceChangeEvent(JavaScriptObject aSource, JavaScriptObject aPublishedField, Object aOldValue, Object aNewValue)/*-{
		var published = {};
		Object.defineProperty(published, "source", {
			get : function(){
				return aSource;
			}
		});
		Object.defineProperty(published, "object", {
			get : function(){
				return aSource;
			}
		});
		Object.defineProperty(published, "field", {
			get : function(){
				return aPublishedField;
			}
		});
		Object.defineProperty(published, "oldValue", {
			get : function(){
				return $wnd.boxAsJs(aOldValue);
			}
		});
		Object.defineProperty(published, "newValue", {
			get : function(){
				return $wnd.boxAsJs(aNewValue);
			}
		});
		return published;
	}-*/;
	
	public native static JavaScriptObject publishEntityInstanceDeleteEvent(JavaScriptObject aSource, JavaScriptObject aPublishedRow)/*-{
		var published = {};
		Object.defineProperty(published, "source", {
			get : function(){
				return aSource;
			}
		});
		Object.defineProperty(published, "deleted", {
			get : function(){
				return aPublishedRow;
			}
		});
		return published;
	}-*/;
	
	public native static JavaScriptObject publishEntityInstanceInsertEvent(JavaScriptObject aSource, JavaScriptObject aPublishedRow)/*-{
		var published = {};
		Object.defineProperty(published, "source", {
			get : function(){
				return aSource;
			}
		});
		Object.defineProperty(published, "inserted", {
			get : function(){
				return aPublishedRow;
			}
		});
		Object.defineProperty(published, "object", {
			get : function(){
				return aPublishedRow;
			}
		});
		return published;
	}-*/;

	public native static JavaScriptObject publishOnRenderEvent(JavaScriptObject aSource, Object aRowId, Object aColumnId, JavaScriptObject aPublishedRow, PublishedCell aCell)/*-{
		var published = {};
		Object.defineProperty(published, "source", {
			get : function(){
				return aSource;
			}
		});
		Object.defineProperty(published, "id", {
			get : function(){
				return $wnd.boxAsJs(aRowId);
			}
		});
		Object.defineProperty(published, "columnId", {
			get : function(){
				return $wnd.boxAsJs(aColumnId);
			}
		});
		Object.defineProperty(published, "object", {
			get : function(){
				return aPublishedRow;
			}
		});
		Object.defineProperty(published, "cell", {
			get : function(){
				return aCell;
			}
		});
		return published;
	}-*/;
	
	public native static JavaScriptObject publishWindowEvent(Object aEvent, JavaScriptObject aPublishedForm)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		Object.defineProperty(published, "source", {
			get : function() {
				return aPublishedForm;
			}
		});
		return published;
	}-*/;

	public native static JavaScriptObject publishMouseDownEvent(MouseDownEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishMouseEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishMouseUpEvent(MouseUpEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishMouseEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishMouseWheelEvent(MouseWheelEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishMouseEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishMouseMoveEvent(MouseMoveEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishMouseEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishClickEvent(ClickEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishMouseEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		Object.defineProperty(published, "clickCount", {
			get : function() {
				return 1;
			}
		});
		return published;
	}-*/;

	public native static JavaScriptObject publishDoubleClickEvent(DoubleClickEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishMouseEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		Object.defineProperty(published, "clickCount", {
			get : function() {
				return 2;
			}
		});
		return published;
	}-*/;

	public native static JavaScriptObject publishMouseOverEvent(MouseOverEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishMouseEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;
 
	public native static JavaScriptObject publishMouseOutEvent(MouseOutEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishMouseEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;
	
	public native static JavaScriptObject publishKeyDownEvent(KeyDownEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishKeyEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishKeyUpEvent(KeyUpEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishKeyEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;
	
	public native static JavaScriptObject publishKeyPressEvent(KeyPressEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		@com.eas.client.form.api.JSEvents::publishKeyEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;
	
	public native static JavaScriptObject publishSelectEvent(SelectEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishFocusEvent(FocusEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishBlurEvent(BlurEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishShowEvent(ShowEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishResizeEvent(ResizeEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishHideEvent(HideEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishAddEvent(AddEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		
		Object.defineProperty(published, "child", {
			get : function()
			{
				var comp = aEvent.@com.sencha.gxt.widget.core.client.event.AddEvent::getWidget()();
				
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			}
		});
		return published; 
	}-*/;

	public native static JavaScriptObject publishRemoveEvent(RemoveEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		Object.defineProperty(published, "child", {
			get : function()
			{
				var comp = aEvent.@com.sencha.gxt.widget.core.client.event.RemoveEvent::getWidget()();
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			}
		});
		return published;
	}-*/;

	public native static JavaScriptObject publishMoveEvent(MoveEvent aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static JavaScriptObject publishChangeEvent(Object aEvent)/*-{
		var published = {
			unwrap : function() {
				return aEvent;
			}
		};
		@com.eas.client.form.api.JSEvents::publishEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		return published;
	}-*/;

	public native static void publishEvent(JavaScriptObject aPublishedEvent)/*-{
		var aEvent = aPublishedEvent.unwrap();
		Object.defineProperty(aPublishedEvent, "source", {
			get : function() {
				var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
				var jsSource = @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(source);
				return jsSource;
			}
		});
	}-*/;
	
	public native static void publishMouseEvent(JavaScriptObject aPublishedEvent)/*-{
		var aEvent = aPublishedEvent.unwrap();
		Object.defineProperty(aPublishedEvent, "x", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.MouseEvent::getX()();
			}
		});
		Object.defineProperty(aPublishedEvent, "y", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.MouseEvent::getY()();
			}
		});
		Object.defineProperty(aPublishedEvent, "screenX", {
			get : function() {
				aEvent.@com.google.gwt.event.dom.client.MouseEvent::getScreenX()();
			}
		});
		Object.defineProperty(aPublishedEvent, "screenY", {
			get : function() {
				aEvent.@com.google.gwt.event.dom.client.MouseEvent::getScreenY()();
			}
		});
		Object.defineProperty(aPublishedEvent, "altDown", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.MouseEvent::isAltKeyDown()();
			}
		});
		Object.defineProperty(aPublishedEvent, "controlDown", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.MouseEvent::isControlKeyDown()();
			}
		});
		Object.defineProperty(aPublishedEvent, "shiftDown", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.MouseEvent::isShiftKeyDown()();
			}
		});
		Object.defineProperty(aPublishedEvent, "metaDown", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.MouseEvent::isMetaKeyDown()();
			}
		});
		Object.defineProperty(aPublishedEvent, "button", {
			get : function() {
				var button = aEvent.@com.google.gwt.event.dom.client.MouseEvent::getNativeButton()();
//??????????????????????? ввести константу ??????
				switch (button) {
					case @com.google.gwt.dom.client.NativeEvent::BUTTON_LEFT : return 1; 
					case @com.google.gwt.dom.client.NativeEvent::BUTTON_RIGHT : return 2; 
					case @com.google.gwt.dom.client.NativeEvent::BUTTON_MIDDLE : return 3;
					default : return 0;
				} 
			}
		});
	}-*/;
	
	public native static void publishKeyEvent(JavaScriptObject aPublishedEvent)/*-{
		var aEvent = aPublishedEvent.unwrap();
		Object.defineProperty(aPublishedEvent, "altDown", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.KeyEvent::isAltKeyDown()();
			}
		});
		Object.defineProperty(aPublishedEvent, "controlDown", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.KeyEvent::isControlKeyDown()();
			}
		});
		Object.defineProperty(aPublishedEvent, "shiftDown", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.KeyEvent::isShiftKeyDown()();
			}
		});
		Object.defineProperty(aPublishedEvent, "metaDown", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.KeyEvent::isMetaKeyDown()();
			}
		});
		Object.defineProperty(aPublishedEvent, "key", {
			get : function() {
				return aEvent.@com.google.gwt.event.dom.client.KeyCodeEvent::getNativeKeyCode()();
			}
		});
	}-*/;
	
}
