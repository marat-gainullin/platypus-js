package com.eas.client.gxtcontrols;

import com.eas.client.form.Form;
import com.eas.client.gxtcontrols.grid.ModelGrid;
import com.eas.client.gxtcontrols.grid.wrappers.Resumable;
import com.eas.client.gxtcontrols.model.ModelCheck;
import com.eas.client.gxtcontrols.model.ModelCombo;
import com.eas.client.gxtcontrols.model.ModelDate;
import com.eas.client.gxtcontrols.model.ModelSpin;
import com.eas.client.gxtcontrols.model.ModelFormattedField;
import com.eas.client.gxtcontrols.model.ModelTextArea;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.published.PublishedComponent;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusButtonGroup;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusComboBox;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusDateField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusHtmlEditor;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusSpinnerField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusTextArea;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusTextField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusToggleButton;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusCardLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusFlowLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusHBoxLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusScrollContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusVBoxLayoutContainer;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.button.SplitButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class Publisher {

	public native static PublishedComponent publishRadio(PlatypusCheckBox aComponent)/*-{
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

	public native static PublishedComponent publish(PasswordField aComponent)/*-{
		return new $wnd.PasswordField(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(TextButton aComponent)/*-{
		return new $wnd.Button(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(SplitButton aComponent)/*-{
		return new $wnd.DropDownButton(null, null, null, null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusLabel aComponent)/*-{
		return new $wnd.Label(null, null, null, aComponent);

	}-*/;

	public native static PublishedComponent publish(SeparatorMenuItem aComponent)/*-{
		return new $wnd.MenuSeparator(aComponent);
	}-*/;

	public native static PublishedComponent publish(MenuBar aComponent)/*-{
		return new $wnd.MenuBar(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenu aComponent)/*-{
		return new $wnd.Menu(null, aComponent);
	}-*/;

	public native static PublishedComponent publishPopup(PlatypusMenu aComponent)/*-{
		return new $wnd.PopupMenu(aComponent);
	}-*/;
	
	public native static PublishedComponent publish(MenuItem aComponent)/*-{
		return new $wnd.MenuItem(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(CheckMenuItem aComponent)/*-{
		return new $wnd.CheckMenuItem(null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publishRadio(CheckMenuItem aComponent)/*-{
		return new $wnd.RadioMenuItem(null, null, null, aComponent);
	}-*/;
	
	public native static void publishTextBorder(JavaScriptObject aPublished, FieldSet fs)/*-{
		Object.defineProperty(aPublished, "borderText", {
			get : function() {
				return fs.@com.sencha.gxt.widget.core.client.form.FieldSet::getHeadingText()();
			},
			set : function(aValue) {
				fs.@com.sencha.gxt.widget.core.client.form.FieldSet::setHeadingText(Ljava/lang/String;)(''+aValue);
			}
		});
	}-*/;

	public native static PublishedCell publishCell(Object aData, String aDisplay)/*-{
		var published = {
					data : $wnd.boxAsJs(aData)
				}
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

	public native static JavaScriptObject publishColumnEditor(PlatypusTextField aComponent, Resumable aResumable)/*-{
		var published = {};
		published.unwrap = function() {
			return aComponent;
		};
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(published);
	
		Object.defineProperty(published, "value", {
			get: function(){
				return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextField::getValue()();
			},
			set: function(aValue){
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				if(aValue != null){
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextField::setValue(Ljava/lang/String;Z)(aValue+"", true);
				}else{
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextField::setValue(Ljava/lang/String;Z)(null, true);
				}
			}
		});
		return published;
	}-*/;
	
	public native static JavaScriptObject publishColumnEditor(PlatypusFormattedTextField aComponent, Resumable aResumable)/*-{
		var published = {};
		published.unwrap = function() {
			return aComponent;
		};
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(published);
	
		Object.defineProperty(published, "value", {
			get: function(){
				return $wnd.boxAsJs(aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::getJsValue()());
			},
			set: function(aValue){
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
			}
		});
		return published;
	}-*/;
	
	public native static JavaScriptObject publishColumnEditor(PlatypusTextArea aComponent, Resumable aResumable)/*-{
		var published = {};
		published.unwrap = function() {
			return aComponent;
		};
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(published);
	
		Object.defineProperty(published, "value", {
			get: function(){
				return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextArea::getValue()();
			},
			set: function(aValue){
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				if(aValue != null){
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextArea::setValue(Ljava/lang/String;Z)(aValue+"", true);
				}else{
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextArea::setValue(Ljava/lang/String;Z)(null, true);
				}
			}
		});
		return published;
	}-*/;

	public native static JavaScriptObject publishColumnEditor(PlatypusDateField aComponent, Resumable aResumable)/*-{
		var published = {};
		published.unwrap = function() {
			return aComponent;
		};
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(published);

		Object.defineProperty(published, "value", {
			get: function(){
				var v = aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusDateField::getValue()();
				if(v != null){
					return new Date(@com.eas.client.Utils::date2Double(Ljava/util/Date;)(v));
				}else
					return null;
			},
			set: function(aValue){
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				if(aValue != null){
					var javaValue   = @com.eas.client.Utils::toJava(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
					var isDateValue = @com.eas.client.Utils::isDate(Ljava/lang/Object;)(javaValue);						
					if(isDateValue)
						aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusDateField::setValue(Ljava/util/Date;Z)(javaValue, true);
					else
						throw "Value of type Date expected!";
				}else{
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusDateField::setValue(Ljava/util/Date;Z)(null, true);
				}
			}
		});
		return published;
	}-*/;
	
	public native static JavaScriptObject publishColumnEditor(PlatypusSpinnerField aComponent, Resumable aResumable)/*-{
		var published = {};
		published.unwrap = function() {
			return aComponent;
		};
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(published);

		Object.defineProperty(published, "value", {
			get : function() {
				var v = aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSpinnerField::getValue()();
				if (v != null) {
					return v.@java.lang.Number::doubleValue()();
				} else
					return null;
			},
			set : function(aValue) {
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				if (aValue != null) {
					var v = aValue * 1;
					var d = @java.lang.Double::new(D)(v);
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSpinnerField::setValue(Ljava/lang/Double;Z)(d, true);
				} else {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSpinnerField::setValue(Ljava/lang/Double;Z)(null, true);
				}
			}
		});
		return published;
	}-*/;
	
	public native static JavaScriptObject publishColumnEditor(PlatypusComboBox aComponent, Resumable aResumable)/*-{
		var published = {};
		published.unwrap = function() {
			return aComponent;
		};
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(published);

		Object.defineProperty(published, "value", {
			get : function() {
				return $wnd.boxAsJs(@com.eas.client.Utils::toJs(Ljava/lang/Object;)(aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusComboBox::getValue()()));
			},
			set : function(aValue) {
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusComboBox::setValue(Ljava/lang/Object;Z)(@com.eas.client.Utils::toJava(Ljava/lang/Object;)($wnd.boxAsJava(aValue)), true);
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

	public native static PublishedComponent publish(PlatypusBorderLayoutContainer aComponent)/*-{
		return new $wnd.BorderPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusScrollContainer aComponent)/*-{
		return new $wnd.ScrollPane(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSplitContainer aComponent)/*-{
		return new $wnd.SplitPane(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusVBoxLayoutContainer aComponent)/*-{
		return new $wnd.BoxPane($wnd.Orientation.VERTICAL, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusHBoxLayoutContainer aComponent)/*-{
		return new $wnd.BoxPane($wnd.Orientation.HORIZONTAL, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusCardLayoutContainer aComponent)/*-{
		return new $wnd.CardPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publishAbsolute(PlatypusMarginLayoutContainer aComponent)/*-{
		return new $wnd.AbsolutePane(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMarginLayoutContainer aComponent)/*-{
		return new $wnd.AnchorsPane(aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusDesktopContainer aComponent)/*-{
		return new $wnd.DesktopPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTabsContainer aComponent)/*-{
		return new $wnd.TabbedPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(ToolBar aComponent)/*-{
		return new $wnd.ToolBar(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusFlowLayoutContainer aComponent)/*-{
		return new $wnd.FlowPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusGridLayoutContainer aComponent)/*-{
		return new $wnd.GridPane(null, null, null, null, aComponent);
	}-*/;

	protected static PublishedComponent checkPublishedComponent(Object aCandidate) {
		if (aCandidate instanceof Component) {
			Component c = (Component) aCandidate;
			Object oPublished = c.getData(Form.PUBLISHED_DATA_KEY);
			if (oPublished instanceof PublishedComponent)
				return (PublishedComponent) oPublished;
		}
		return null;
	}

	public native static JavaScriptObject publishExecutor(JavaScriptObject published)/*-{
		if (published && published.unwrap) {
			var comp = published.unwrap();
			var executor = comp.@com.sencha.gxt.widget.core.client.Component::getData(Ljava/lang/String;)("handler");
			if(executor == null)
				executor = @com.eas.client.gxtcontrols.events.GxtEventsExecutor::createExecutor(Lcom/sencha/gxt/widget/core/client/Component;Lcom/google/gwt/core/client/JavaScriptObject;)(comp, published);
			else
				executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setEventThis(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			Object.defineProperty(published, "onActionPerformed", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getActionPerformed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setActionPerformed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});

			Object.defineProperty(published, "onMouseExited", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getMouseExited()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setMouseExited(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseClicked", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getMouseClicked()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setMouseClicked(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMousePressed", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getMousePressed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setMousePressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseReleased", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getMouseReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setMouseReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseEntered", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getMouseEntered()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setMouseEntered(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseWheelMoved", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getMouseWheelMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setMouseWheelMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseDragged", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getMouseDragged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setMouseDragged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseMoved", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getMouseMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setMouseMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentResized", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getComponentResized()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setComponentResized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentMoved", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getComponentMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setComponentMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentShown", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getComponentShown()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setComponentShown(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentHidden", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getComponentHidden()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setComponentHidden(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentAdded", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getComponentAdded()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setComponentAdded(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentRemoved", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getComponentRemoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setComponentRemoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});			
			Object.defineProperty(published, "onFocusGained", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getFocusGained()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setFocusGained(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onFocusLost", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getFocusLost()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setFocusLost(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyTyped", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getKeyTyped()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setKeyTyped(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyPressed", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getKeyPressed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setKeyPressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyReleased", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getKeyReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setKeyReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onStateChanged", {
				get : function() {
					return executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::getStateChanged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.gxtcontrols.events.GxtEventsExecutor::setStateChanged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
		}

	}-*/;

	public native static JavaScriptObject publish(PlatypusButtonGroup aButtonGroup)/*-{
		return new $wnd.ButtonGroup(aButtonGroup);
	}-*/;

}
