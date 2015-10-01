package com.eas.form;

import com.bearsoft.gwt.ui.containers.window.events.MoveEvent;
import com.bearsoft.gwt.ui.events.HideEvent;
import com.bearsoft.gwt.ui.events.ShowEvent;
import com.eas.client.HasPublished;
import com.eas.form.events.ActionEvent;
import com.eas.form.events.AddEvent;
import com.eas.form.events.RemoveEvent;
import com.eas.form.grid.columns.header.CheckHeaderNode;
import com.eas.form.grid.columns.header.ModelHeaderNode;
import com.eas.form.grid.columns.header.RadioHeaderNode;
import com.eas.form.grid.columns.header.ServiceHeaderNode;
import com.eas.form.published.PublishedCell;
import com.eas.form.published.PublishedComponent;
import com.eas.form.published.containers.AbsolutePane;
import com.eas.form.published.containers.AnchorsPane;
import com.eas.form.published.containers.BorderPane;
import com.eas.form.published.containers.BoxPane;
import com.eas.form.published.containers.ButtonGroup;
import com.eas.form.published.containers.CardPane;
import com.eas.form.published.containers.FlowPane;
import com.eas.form.published.containers.GridPane;
import com.eas.form.published.containers.ScrollPane;
import com.eas.form.published.containers.SplitPane;
import com.eas.form.published.containers.TabbedPane;
import com.eas.form.published.containers.ToolBar;
import com.eas.form.published.menu.PlatypusMenu;
import com.eas.form.published.menu.PlatypusMenuBar;
import com.eas.form.published.menu.PlatypusMenuItemCheckBox;
import com.eas.form.published.menu.PlatypusMenuItemImageText;
import com.eas.form.published.menu.PlatypusMenuItemRadioButton;
import com.eas.form.published.menu.PlatypusMenuItemSeparator;
import com.eas.form.published.widgets.DesktopPane;
import com.eas.form.published.widgets.PlatypusButton;
import com.eas.form.published.widgets.PlatypusCheckBox;
import com.eas.form.published.widgets.PlatypusFormattedTextField;
import com.eas.form.published.widgets.PlatypusHtmlEditor;
import com.eas.form.published.widgets.PlatypusLabel;
import com.eas.form.published.widgets.PlatypusPasswordField;
import com.eas.form.published.widgets.PlatypusProgressBar;
import com.eas.form.published.widgets.PlatypusRadioButton;
import com.eas.form.published.widgets.PlatypusSlider;
import com.eas.form.published.widgets.PlatypusSplitButton;
import com.eas.form.published.widgets.PlatypusTextArea;
import com.eas.form.published.widgets.PlatypusTextField;
import com.eas.form.published.widgets.PlatypusToggleButton;
import com.eas.form.published.widgets.model.ModelCheck;
import com.eas.form.published.widgets.model.ModelCombo;
import com.eas.form.published.widgets.model.ModelDate;
import com.eas.form.published.widgets.model.ModelFormattedField;
import com.eas.form.published.widgets.model.ModelGrid;
import com.eas.form.published.widgets.model.ModelSpin;
import com.eas.form.published.widgets.model.ModelTextArea;
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

public class Publisher {

	public native static JavaScriptObject getFormsClass()/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return Ui.Form;
	}-*/;
	
	public native static JavaScriptObject publishSourcedEvent(JavaScriptObject aSource)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.PublishedSourcedEvent(aSource);
	}-*/;	
	
	public native static JavaScriptObject publishItemEvent(JavaScriptObject aSource, JavaScriptObject aItem)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ItemEvent(aSource, aItem);
	}-*/;	
	
	public native static JavaScriptObject publishOnRenderEvent(JavaScriptObject aSource, Object aRowId, Object aColumnId, JavaScriptObject aRendered, PublishedCell aCell)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.CellRenderEvent(aSource, Ui.boxAsJs(aRowId), Ui.boxAsJs(aColumnId), aRendered, aCell);
	}-*/;
	
	public native static JavaScriptObject publishWindowEvent(Object aEvent, JavaScriptObject aWindow)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.WindowEvent(aWindow);
	}-*/;
	
	public native static JavaScriptObject publish(MouseDownEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MouseEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseUpEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MouseEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseWheelEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MouseEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseMoveEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MouseEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ClickEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MouseEvent(aEvent, 1);
	}-*/;
	
	public native static JavaScriptObject publish(DoubleClickEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MouseEvent(aEvent, 2);
	}-*/;
	
	public native static JavaScriptObject publish(MouseOverEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MouseEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseOutEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MouseEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyDownEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.KeyEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyUpEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.KeyEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyPressEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.KeyEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(FocusEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.FocusEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(BlurEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.FocusEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ResizeEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ComponentEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ShowEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ComponentEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(HideEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ComponentEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MoveEvent<Object> aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ComponentEvent(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(AddEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ContainerEvent(aEvent, true); 
	}-*/;
	
	public native static JavaScriptObject publish(RemoveEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ContainerEvent(aEvent, false);
	}-*/;
	
	public native static JavaScriptObject publish(ActionEvent aEvent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ActionEvent(aEvent);
	}-*/;

	public native static PublishedComponent publish(PlatypusRadioButton aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.RadioButton(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusCheckBox aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.CheckBox(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSlider aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.Slider(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusToggleButton aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ToggleButton(null, null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTextField aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.TextField(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusFormattedTextField aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.FormattedField(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTextArea aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.TextArea(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusHtmlEditor aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.HtmlArea(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusProgressBar aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ProgressBar(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusPasswordField aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.PasswordField(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusButton aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.Button(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSplitButton aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.DropDownButton(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusLabel aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.Label(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemSeparator aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MenuSeparator(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuBar aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MenuBar(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenu aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.Menu(null, aComponent);
	}-*/;

	public native static PublishedComponent publishPopup(PlatypusMenu aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.PopupMenu(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemImageText aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.MenuItem(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemCheckBox aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.CheckMenuItem(null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publish(PlatypusMenuItemRadioButton aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.RadioMenuItem(null, null, null, aComponent);
	}-*/;

	public native static PublishedCell publishCell(Object aData, String aDisplay)/*-{
		var published = {
			data : aData
		};
		var _display = aDisplay;
		var _background = null;
		var _foreground = null;
		var _font = null;
		var _align = null;
		var _icon = null;
		var _folderIcon = null;
		var _openFolderIcon = null;
		var _leafIcon = null;
		
		function displayChanged(){
			if (published.displayCallback != null)
				published.displayCallback.@java.lang.Runnable::run()();
		}
		
		function iconsChanged(){
			if (published.iconCallback)
				published.iconCallback.@java.lang.Runnable::run()();
		}
		
		Object.defineProperty(published, "display", {
			get : function() {
				return _display;
			},
			set : function(aValue) {
				_display = aValue;
				displayChanged();
			}
		});
		Object.defineProperty(published, "background", {
			get : function() {
				return _background;
			},
			set : function(aValue) {
				_background = aValue;
				displayChanged();
			}
		});
		Object.defineProperty(published, "foreground", {
			get : function() {
				return _foreground;
			},
			set : function(aValue) {
				_foreground = aValue;
				displayChanged();
			}
		});
		Object.defineProperty(published, "font", {
			get : function() {
				return _font;
			},
			set : function(aValue) {
				_font = aValue;
				displayChanged();
			}
		});
		Object.defineProperty(published, "align", {
			get : function() {
				return _align;
			},
			set : function(aValue) {
				_align = aValue;
				displayChanged();
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return _icon;
			},
			set : function(aValue) {
				_icon = aValue;
				iconsChanged();
			}
		});
		Object.defineProperty(published, "folderIcon", {
			get : function() {
				return _folderIcon;
			},
			set : function(aValue) {
				_folderIcon = aValue;
				iconsChanged();
			}
		});
		Object.defineProperty(published, "openFolderIcon", {
			get : function() {
				return _openFolderIcon;
			},
			set : function(aValue) {
				_openFolderIcon = aValue;
				iconsChanged();
			}
		});
		Object.defineProperty(published, "leafIcon", {
			get : function() {
				return _leafIcon;
			},
			set : function(aValue) {
				_leafIcon = aValue;
				iconsChanged();
			}
		});
		return published;
	}-*/;

	public native static PublishedComponent publish(ModelGrid aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ModelGrid(aComponent);
	}-*/;

	public native static PublishedComponent publish(CheckHeaderNode aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.CheckGridColumn(aComponent);
	}-*/;

	public native static PublishedComponent publish(RadioHeaderNode aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.RadioGridColumn(aComponent);
	}-*/;

	public native static PublishedComponent publish(ServiceHeaderNode aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ServiceGridColumn(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelHeaderNode aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ModelGridColumn(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelCheck aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ModelCheckBox(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelFormattedField aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ModelFormattedField(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelTextArea aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ModelTextArea(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelDate aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ModelDate(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelSpin aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ModelSpin(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelCombo aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ModelCombo(aComponent);
	}-*/;

	public native static PublishedComponent publish(BorderPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.BorderPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ScrollPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ScrollPane(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(SplitPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.SplitPane(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(BoxPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.BoxPane(Ui.Orientation.HORIZONTAL, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(CardPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.CardPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(AbsolutePane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.AbsolutePane(aComponent);
	}-*/;

	public native static PublishedComponent publish(AnchorsPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.AnchorsPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(DesktopPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.DesktopPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(TabbedPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.TabbedPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(ToolBar aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ToolBar(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(FlowPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.FlowPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(GridPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.GridPane(null, null, null, null, aComponent);
	}-*/;

	protected static JavaScriptObject checkPublishedComponent(Object aCandidate) {
		if (aCandidate instanceof HasPublished) {
			return ((HasPublished) aCandidate).getPublished();
		} else
			return null;
	}

	public native static JavaScriptObject publish(ButtonGroup aButtonGroup)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.ButtonGroup(aButtonGroup);
	}-*/;

}
