package com.eas.client.form;

import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.containers.AbsolutePane;
import com.eas.client.form.published.containers.AnchorsPane;
import com.eas.client.form.published.containers.BorderPane;
import com.eas.client.form.published.containers.ButtonGroup;
import com.eas.client.form.published.containers.CardPane;
import com.eas.client.form.published.containers.FlowPane;
import com.eas.client.form.published.containers.GridPane;
import com.eas.client.form.published.containers.HBoxPane;
import com.eas.client.form.published.containers.ScrollPane;
import com.eas.client.form.published.containers.SplitPane;
import com.eas.client.form.published.containers.TabbedPane;
import com.eas.client.form.published.containers.ToolBar;
import com.eas.client.form.published.containers.VBoxPane;
import com.eas.client.form.published.menu.PlatypusMenu;
import com.eas.client.form.published.menu.PlatypusMenuBar;
import com.eas.client.form.published.menu.PlatypusMenuItemCheckBox;
import com.eas.client.form.published.menu.PlatypusMenuItemImageText;
import com.eas.client.form.published.menu.PlatypusMenuItemRadioButton;
import com.eas.client.form.published.menu.PlatypusMenuItemSeparator;
import com.eas.client.form.published.widgets.DesktopPane;
import com.eas.client.form.published.widgets.PlatypusButton;
import com.eas.client.form.published.widgets.PlatypusCheckBox;
import com.eas.client.form.published.widgets.PlatypusFormattedTextField;
import com.eas.client.form.published.widgets.PlatypusHtmlEditor;
import com.eas.client.form.published.widgets.PlatypusLabel;
import com.eas.client.form.published.widgets.PlatypusPasswordField;
import com.eas.client.form.published.widgets.PlatypusProgressBar;
import com.eas.client.form.published.widgets.PlatypusRadioButton;
import com.eas.client.form.published.widgets.PlatypusSlider;
import com.eas.client.form.published.widgets.PlatypusSplitButton;
import com.eas.client.form.published.widgets.PlatypusTextArea;
import com.eas.client.form.published.widgets.PlatypusTextField;
import com.eas.client.form.published.widgets.PlatypusToggleButton;
import com.eas.client.form.published.widgets.model.ModelCheck;
import com.eas.client.form.published.widgets.model.ModelCombo;
import com.eas.client.form.published.widgets.model.ModelDate;
import com.eas.client.form.published.widgets.model.ModelFormattedField;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.form.published.widgets.model.ModelSpin;
import com.eas.client.form.published.widgets.model.ModelTextArea;
import com.google.gwt.core.client.JavaScriptObject;

public class Publisher {

	public native static PublishedComponent publish(PlatypusRadioButton aComponent)/*-{
		return new $wnd.RadioButton(null, null, null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusCheckBox aComponent)/*-{
		return new $wnd.CheckBox(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSlider aComponent)/*-{
		return new $wnd.Slider(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusToggleButton aComponent)/*-{
		return new $wnd.ToggleButton(null, null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTextField aComponent)/*-{
		return new $wnd.TextField(null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusFormattedTextField aComponent)/*-{
		return new $wnd.FormattedField(null, aComponent);
	}-*/;	

	public native static PublishedComponent publish(PlatypusTextArea aComponent)/*-{
		return new $wnd.TextArea(null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusHtmlEditor aComponent)/*-{
		return new $wnd.HtmlArea(null, aComponent);
	}-*/;	

	public native static PublishedComponent publish(PlatypusProgressBar aComponent)/*-{
		return new $wnd.ProgressBar(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusPasswordField aComponent)/*-{
		return new $wnd.PasswordField(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusButton aComponent)/*-{
		return new $wnd.Button(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSplitButton aComponent)/*-{
		return new $wnd.DropDownButton(null, null, null, null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusLabel aComponent)/*-{
		return new $wnd.Label(null, null, null, aComponent);

	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemSeparator aComponent)/*-{
		return new $wnd.MenuSeparator(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuBar aComponent)/*-{
		return new $wnd.MenuBar(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenu aComponent)/*-{
		return new $wnd.Menu(null, aComponent);
	}-*/;

	public native static PublishedComponent publishPopup(PlatypusMenu aComponent)/*-{
		return new $wnd.PopupMenu(aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusMenuItemImageText aComponent)/*-{
		return new $wnd.MenuItem(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemCheckBox aComponent)/*-{
		return new $wnd.CheckMenuItem(null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publish(PlatypusMenuItemRadioButton aComponent)/*-{
		return new $wnd.RadioMenuItem(null, null, null, aComponent);
	}-*/;
	
	public native static PublishedCell publishCell(Object aData, String aDisplay)/*-{
		var published = {
					data : $wnd.boxAsJs(aData)
		};
		var _display = aDisplay;
		var _style = new $wnd.Style();
		Object.defineProperty(published, "display", {
			get: function(){
				return _display;
			},
			set: function(aValue){
				_display = aValue;
				if(published.displayCallback != null)
					published.displayCallback.@java.lang.Runnable::run()();
			}
		});
		Object.defineProperty(published, "style", {
			get: function(){
				return _style;
			},
			set: function(aValue){
				_style = aValue;
				if(published.displayCallback != null)
					published.displayCallback.@java.lang.Runnable::run()();
			}
		});
		return published;
	}-*/;
	
	public native static PublishedComponent publish(ModelGrid aComponent)/*-{
		return new $wnd.ModelGrid(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelCheck aComponent)/*-{
		return new $wnd.ModelCheckBox(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelFormattedField aComponent)/*-{
		return new $wnd.ModelFormattedField(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelTextArea aComponent)/*-{
		return new $wnd.ModelTextArea(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelDate aComponent)/*-{
		return new $wnd.ModelDate(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelSpin aComponent)/*-{
		return new $wnd.ModelSpin(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelCombo aComponent)/*-{
		return new $wnd.ModelCombo(aComponent);
	}-*/;

	public native static PublishedComponent publish(BorderPane aComponent)/*-{
		return new $wnd.BorderPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ScrollPane aComponent)/*-{
		return new $wnd.ScrollPane(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(SplitPane aComponent)/*-{
		return new $wnd.SplitPane(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(VBoxPane aComponent)/*-{
		return new $wnd.BoxPane($wnd.Orientation.VERTICAL, aComponent);
	}-*/;

	public native static PublishedComponent publish(HBoxPane aComponent)/*-{
		return new $wnd.BoxPane($wnd.Orientation.HORIZONTAL, aComponent);
	}-*/;

	public native static PublishedComponent publish(CardPane aComponent)/*-{
		return new $wnd.CardPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(AbsolutePane aComponent)/*-{
		return new $wnd.AbsolutePane(aComponent);
	}-*/;

	public native static PublishedComponent publish(AnchorsPane aComponent)/*-{
		return new $wnd.AnchorsPane(aComponent);
	}-*/;
	
	public native static PublishedComponent publish(DesktopPane aComponent)/*-{
		return new $wnd.DesktopPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(TabbedPane aComponent)/*-{
		return new $wnd.TabbedPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(ToolBar aComponent)/*-{
		return new $wnd.ToolBar(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(FlowPane aComponent)/*-{
		return new $wnd.FlowPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(GridPane aComponent)/*-{
		return new $wnd.GridPane(null, null, null, null, aComponent);
	}-*/;

	protected static JavaScriptObject checkPublishedComponent(Object aCandidate) {
		if (aCandidate instanceof HasPublished) {
			return ((HasPublished)aCandidate).getPublished();
		}else
			return null;
	}

	public native static JavaScriptObject publishExecutor(JavaScriptObject published)/*-{
		if (published && published.unwrap) {
			var comp = published.unwrap();
			var executor = comp.@com.eas.client.form.published.HasEventsExecutor::getEventsExecutor()();
			if(executor == null){
				executor = @com.eas.client.form.EventsExecutor::new(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/core/client/JavaScriptObject;)(comp, published);
				comp.@com.eas.client.form.published.HasEventsExecutor::setEventsExecutor(Lcom/eas/client/form/EventsExecutor;)(executor);
			} else {
				executor.@com.eas.client.form.EventsExecutor::setEventThis(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			Object.defineProperty(published, "onActionPerformed", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getActionPerformed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setActionPerformed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});

			Object.defineProperty(published, "onMouseExited", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseExited()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseExited(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseClicked", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseClicked()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseClicked(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMousePressed", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMousePressed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMousePressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseReleased", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseEntered", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseEntered()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseEntered(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseWheelMoved", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseWheelMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseWheelMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseDragged", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseDragged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseDragged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseMoved", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentResized", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentResized()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentResized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentMoved", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentShown", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentShown()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentShown(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentHidden", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentHidden()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentHidden(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentAdded", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentAdded()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentAdded(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentRemoved", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentRemoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentRemoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});			
			Object.defineProperty(published, "onFocusGained", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getFocusGained()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setFocusGained(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onFocusLost", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getFocusLost()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setFocusLost(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyTyped", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getKeyTyped()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setKeyTyped(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyPressed", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getKeyPressed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setKeyPressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyReleased", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getKeyReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setKeyReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onStateChanged", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getStateChanged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setStateChanged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
		}

	}-*/;

	public native static JavaScriptObject publish(ButtonGroup aButtonGroup)/*-{
		return new $wnd.ButtonGroup(aButtonGroup);
	}-*/;

}
