package com.eas.form;

import com.bearsoft.gwt.ui.containers.window.events.MoveEvent;
import com.bearsoft.gwt.ui.events.HideEvent;
import com.bearsoft.gwt.ui.events.ShowEvent;
import com.eas.client.Utils;
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

	private static Utils.JsObject constructors = JavaScriptObject.createObject().cast();
	
	private static JavaScriptObject getPublisher(String aClassName){
		JavaScriptObject constr = constructors.getJs(aClassName);
		if(constr == null)
			throw new IllegalStateException("Constructor for " + aClassName + " was not found.");
		return constr;
	}
	
	public static void putPublisher(String aClassName, JavaScriptObject aPublisher){
		constructors.setJs(aClassName, aPublisher);
	}
	
	public native static JavaScriptObject publishSourcedEvent(JavaScriptObject aSource)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('PublishedSourcedEvent');
		return new constr(aSource);
	}-*/;	
	
	public native static JavaScriptObject publishItemEvent(JavaScriptObject aSource, JavaScriptObject aItem)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ItemEvent');
		return new constr(aSource, aItem);
	}-*/;	
	
	public native static JavaScriptObject publishOnRenderEvent(JavaScriptObject aSource, Object aRowId, Object aColumnId, JavaScriptObject aRendered, PublishedCell aCell)/*-{
		var B = @com.eas.predefine.Predefine::boxing;
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('CellRenderEvent');
		return new constr(aSource, B.boxAsJs(aRowId), B.boxAsJs(aColumnId), aRendered, aCell);
	}-*/;
	
	public native static JavaScriptObject publishWindowEvent(Object aEvent, JavaScriptObject aWindow)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('WindowEvent');
		return new constr(aWindow);
	}-*/;
	
	public native static JavaScriptObject publish(MouseDownEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseUpEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseWheelEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseMoveEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ClickEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent, 1);
	}-*/;
	
	public native static JavaScriptObject publish(DoubleClickEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent, 2);
	}-*/;
	
	public native static JavaScriptObject publish(MouseOverEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MouseOutEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MouseEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyDownEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('KeyEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyUpEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('KeyEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(KeyPressEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('KeyEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(FocusEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('FocusEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(BlurEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('FocusEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ResizeEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ComponentEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(ShowEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ComponentEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(HideEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ComponentEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(MoveEvent<Object> aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ComponentEvent');
		return new constr(aEvent);
	}-*/;
	
	public native static JavaScriptObject publish(AddEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ContainerEvent');
		return new constr(aEvent, true);
	}-*/;
	
	public native static JavaScriptObject publish(RemoveEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ContainerEvent');
		return new constr(aEvent, false);
	}-*/;
	
	public native static JavaScriptObject publish(ActionEvent aEvent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ActionEvent');
		return new constr(aEvent);
	}-*/;

	public native static PublishedComponent publish(PlatypusRadioButton aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('RadioButton');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusCheckBox aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('CheckBox');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSlider aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('Slider');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusToggleButton aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ToggleButton');
		return new constr(null, null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTextField aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('TextField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusFormattedTextField aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('FormattedField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTextArea aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('TextArea');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusHtmlEditor aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('HtmlArea');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusProgressBar aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ProgressBar');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusPasswordField aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('PasswordField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusButton aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('Button');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSplitButton aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('DropDownButton');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusLabel aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('Label');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemSeparator aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MenuSeparator');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuBar aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MenuBar');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenu aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('Menu');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publishPopup(PlatypusMenu aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('PopupMenu');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemImageText aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('MenuItem');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemCheckBox aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('CheckMenuItem');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publish(PlatypusMenuItemRadioButton aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('RadioMenuItem');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelGrid aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ModelGrid');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(CheckHeaderNode aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('CheckGridColumn');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(RadioHeaderNode aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('RadioGridColumn');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ServiceHeaderNode aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ServiceGridColumn');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelHeaderNode aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ModelGridColumn');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelCheck aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ModelCheckBox');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelFormattedField aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ModelFormattedField');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelTextArea aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ModelTextArea');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelDate aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ModelDate');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelSpin aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ModelSpin');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelCombo aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ModelCombo');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(BorderPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('BorderPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ScrollPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ScrollPane');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(SplitPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('SplitPane');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(BoxPane aComponent)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('BoxPane');
		return new constr(Ui.Orientation.HORIZONTAL, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(CardPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('CardPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(AbsolutePane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('AbsolutePane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(AnchorsPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('AnchorsPane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(DesktopPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('DesktopPane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(TabbedPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('TabbedPane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ToolBar aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ToolBar');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(FlowPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('FlowPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(GridPane aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('GridPane');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publish(ButtonGroup aComponent)/*-{
		var constr = @com.eas.form.Publisher::getPublisher(Ljava/lang/String;)('ButtonGroup');
		return new constr(aComponent);
	}-*/;

}
