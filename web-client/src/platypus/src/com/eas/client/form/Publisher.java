package com.eas.client.form;

import com.eas.client.form.grid.ModelGrid;
import com.eas.client.form.grid.Resumable;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.containers.BorderPane;
import com.eas.client.form.published.containers.CardPane;
import com.eas.client.form.published.containers.ButtonGroup;
import com.eas.client.form.published.containers.FlowPane;
import com.eas.client.form.published.containers.GridPane;
import com.eas.client.form.published.containers.HBoxPane;
import com.eas.client.form.published.containers.MarginsPane;
import com.eas.client.form.published.containers.ScrollPane;
import com.eas.client.form.published.containers.SplitPane;
import com.eas.client.form.published.containers.TabbedPane;
import com.eas.client.form.published.containers.VBoxPane;
import com.eas.client.form.published.menu.PlatypusMenu;
import com.eas.client.form.published.widgets.DesktopPane;
import com.eas.client.form.published.widgets.PlatypusCheckBox;
import com.eas.client.form.published.widgets.PlatypusComboBox;
import com.eas.client.form.published.widgets.PlatypusDateField;
import com.eas.client.form.published.widgets.PlatypusFormattedTextField;
import com.eas.client.form.published.widgets.PlatypusHtmlEditor;
import com.eas.client.form.published.widgets.PlatypusLabel;
import com.eas.client.form.published.widgets.PlatypusProgressBar;
import com.eas.client.form.published.widgets.PlatypusSlider;
import com.eas.client.form.published.widgets.PlatypusSpinnerField;
import com.eas.client.form.published.widgets.PlatypusTextArea;
import com.eas.client.form.published.widgets.PlatypusTextField;
import com.eas.client.form.published.widgets.PlatypusToggleButton;
import com.eas.client.form.published.widgets.model.ModelCheck;
import com.eas.client.form.published.widgets.model.ModelCombo;
import com.eas.client.form.published.widgets.model.ModelDate;
import com.eas.client.form.published.widgets.model.ModelFormattedField;
import com.eas.client.form.published.widgets.model.ModelSpin;
import com.eas.client.form.published.widgets.model.ModelTextArea;
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

	public native static JavaScriptObject publishColumnEditor(PlatypusTextField aComponent, Resumable aResumable)/*-{
		var published = {};
		published.unwrap = function() {
			return aComponent;
		};
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/form/layout/published/PublishedComponent;)(published);
	
		Object.defineProperty(published, "value", {
			get: function(){
				return aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusTextField::getValue()();
			},
			set: function(aValue){
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				if(aValue != null){
					aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusTextField::setValue(Ljava/lang/String;Z)(aValue+"", true);
				}else{
					aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusTextField::setValue(Ljava/lang/String;Z)(null, true);
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
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/form/layout/published/PublishedComponent;)(published);
	
		Object.defineProperty(published, "value", {
			get: function(){
				return $wnd.boxAsJs(aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusFormattedTextField::getJsValue()());
			},
			set: function(aValue){
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusFormattedTextField::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
			}
		});
		return published;
	}-*/;
	
	public native static JavaScriptObject publishColumnEditor(PlatypusTextArea aComponent, Resumable aResumable)/*-{
		var published = {};
		published.unwrap = function() {
			return aComponent;
		};
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/form/layout/published/PublishedComponent;)(published);
	
		Object.defineProperty(published, "value", {
			get: function(){
				return aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusTextArea::getValue()();
			},
			set: function(aValue){
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				if(aValue != null){
					aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusTextArea::setValue(Ljava/lang/String;Z)(aValue+"", true);
				}else{
					aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusTextArea::setValue(Ljava/lang/String;Z)(null, true);
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
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/form/layout/published/PublishedComponent;)(published);

		Object.defineProperty(published, "value", {
			get: function(){
				var v = aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusDateField::getValue()();
				if(v != null){
					return new Date(@com.bearsoft.rowset.Utils::date2Double(Ljava/util/Date;)(v));
				}else
					return null;
			},
			set: function(aValue){
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				if(aValue != null){
					var javaValue   = @com.bearsoft.rowset.Utils::toJava(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
					var isDateValue = @com.bearsoft.rowset.Utils::isDate(Ljava/lang/Object;)(javaValue);						
					if(isDateValue)
						aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusDateField::setValue(Ljava/util/Date;Z)(javaValue, true);
					else
						throw "Value of type Date expected!";
				}else{
					aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusDateField::setValue(Ljava/util/Date;Z)(null, true);
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
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/form/layout/published/PublishedComponent;)(published);

		Object.defineProperty(published, "value", {
			get : function() {
				var v = aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusSpinnerField::getValue()();
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
					aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusSpinnerField::setValue(Ljava/lang/Double;Z)(d, true);
				} else {
					aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusSpinnerField::setValue(Ljava/lang/Double;Z)(null, true);
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
		@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/form/layout/published/PublishedComponent;)(published);

		Object.defineProperty(published, "value", {
			get : function() {
				return $wnd.boxAsJs(@com.bearsoft.rowset.Utils::toJs(Ljava/lang/Object;)(aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusComboBox::getValue()()));
			},
			set : function(aValue) {
				aResumable.@com.eas.client.gxtcontrols.grid.wrappers.Resumable::resume()();
				aComponent.@com.eas.client.gxtcontrols.published.widgets.PlatypusComboBox::setValue(Ljava/lang/Object;Z)(@com.bearsoft.rowset.Utils::toJava(Ljava/lang/Object;)($wnd.boxAsJava(aValue)), true);
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

	public native static PublishedComponent publishAbsolute(MarginsPane aComponent)/*-{
		return new $wnd.AbsolutePane(aComponent);
	}-*/;

	public native static PublishedComponent publish(MarginsPane aComponent)/*-{
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
		if (aCandidate instanceof Component) {
			Component c = (Component) aCandidate;
			Object oPublished = c.getData(Form.PUBLISHED_DATA_KEY);
			if (oPublished instanceof JavaScriptObject)
				return (JavaScriptObject) oPublished;
		}else if(aCandidate instanceof ButtonGroup){
			return ((ButtonGroup)aCandidate).getPublished();
		}
		return null;
	}

	public native static JavaScriptObject publishExecutor(JavaScriptObject published)/*-{
		if (published && published.unwrap) {
			var comp = published.unwrap();
			var executor = comp.@com.sencha.gxt.widget.core.client.Component::getData(Ljava/lang/String;)("handler");
			if(executor == null)
				executor = @com.eas.client.form.GxtEventsExecutor::createExecutor(Lcom/sencha/gxt/widget/core/client/Component;Lcom/google/gwt/core/client/JavaScriptObject;)(comp, published);
			else
				executor.@com.eas.client.form.GxtEventsExecutor::setEventThis(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			Object.defineProperty(published, "onActionPerformed", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getActionPerformed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setActionPerformed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});

			Object.defineProperty(published, "onMouseExited", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getMouseExited()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setMouseExited(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseClicked", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getMouseClicked()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setMouseClicked(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMousePressed", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getMousePressed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setMousePressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseReleased", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getMouseReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setMouseReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseEntered", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getMouseEntered()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setMouseEntered(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseWheelMoved", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getMouseWheelMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setMouseWheelMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseDragged", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getMouseDragged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setMouseDragged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseMoved", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getMouseMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setMouseMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentResized", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getComponentResized()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setComponentResized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentMoved", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getComponentMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setComponentMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentShown", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getComponentShown()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setComponentShown(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentHidden", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getComponentHidden()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setComponentHidden(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentAdded", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getComponentAdded()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setComponentAdded(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentRemoved", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getComponentRemoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setComponentRemoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});			
			Object.defineProperty(published, "onFocusGained", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getFocusGained()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setFocusGained(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onFocusLost", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getFocusLost()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setFocusLost(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyTyped", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getKeyTyped()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setKeyTyped(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyPressed", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getKeyPressed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setKeyPressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyReleased", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getKeyReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setKeyReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onStateChanged", {
				get : function() {
					return executor.@com.eas.client.form.GxtEventsExecutor::getStateChanged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.GxtEventsExecutor::setStateChanged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
		}

	}-*/;

	public native static JavaScriptObject publish(ButtonGroup aButtonGroup)/*-{
		return new $wnd.ButtonGroup(aButtonGroup);
	}-*/;

}
