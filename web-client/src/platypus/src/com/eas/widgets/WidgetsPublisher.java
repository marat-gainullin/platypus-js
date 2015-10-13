package com.eas.widgets;

import com.eas.core.Utils;
import com.eas.ui.ButtonGroup;
import com.eas.ui.PublishedComponent;
import com.eas.widgets.AbsolutePane;
import com.eas.widgets.AnchorsPane;
import com.eas.widgets.BorderPane;
import com.eas.widgets.BoxPane;
import com.eas.widgets.CardPane;
import com.eas.widgets.DesktopPane;
import com.eas.widgets.FlowPane;
import com.eas.widgets.GridPane;
import com.eas.widgets.PlatypusButton;
import com.eas.widgets.PlatypusCheckBox;
import com.eas.widgets.PlatypusFormattedTextField;
import com.eas.widgets.PlatypusHtmlEditor;
import com.eas.widgets.PlatypusLabel;
import com.eas.widgets.PlatypusPasswordField;
import com.eas.widgets.PlatypusProgressBar;
import com.eas.widgets.PlatypusRadioButton;
import com.eas.widgets.PlatypusSlider;
import com.eas.widgets.PlatypusSplitButton;
import com.eas.widgets.PlatypusTextArea;
import com.eas.widgets.PlatypusTextField;
import com.eas.widgets.PlatypusToggleButton;
import com.eas.widgets.ScrollPane;
import com.eas.widgets.SplitPane;
import com.eas.widgets.TabbedPane;
import com.eas.widgets.ToolBar;
import com.google.gwt.core.client.JavaScriptObject;

public class WidgetsPublisher {

	private static Utils.JsObject constructors = JavaScriptObject.createObject().cast();
	
	private static JavaScriptObject getPublisher(String aClassName){
		JavaScriptObject constr = constructors.getJs(aClassName);
		if(constr == null)
			throw new IllegalStateException("Constructor for " + aClassName + " was not found.");
		return constr;
	}
	
	public static native JavaScriptObject getConstructors()/*-{
		return @com.eas.widgets.WidgetsPublisher::constructors;
	}-*/;
	
	public static void putPublisher(String aClassName, JavaScriptObject aPublisher){
		constructors.setJs(aClassName, aPublisher);
	}
	
	public native static PublishedComponent publish(PlatypusRadioButton aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('RadioButton');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusCheckBox aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('CheckBox');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSlider aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('Slider');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusToggleButton aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ToggleButton');
		return new constr(null, null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTextField aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('TextField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusFormattedTextField aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('FormattedField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTextArea aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('TextArea');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusHtmlEditor aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('HtmlArea');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusProgressBar aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ProgressBar');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusPasswordField aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('PasswordField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusButton aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('Button');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSplitButton aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('DropDownButton');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusLabel aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('Label');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(BorderPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('BorderPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ScrollPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ScrollPane');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(SplitPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('SplitPane');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(BoxPane aComponent)/*-{
		var Orientation = @com.eas.ui.JsUi::Orientation;
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('BoxPane');
		return new constr(Orientation.HORIZONTAL, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(CardPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('CardPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(AbsolutePane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('AbsolutePane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(AnchorsPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('AnchorsPane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(DesktopPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('DesktopPane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(TabbedPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('TabbedPane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(ToolBar aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ToolBar');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(FlowPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('FlowPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(GridPane aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('GridPane');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publish(ButtonGroup aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ButtonGroup');
		return new constr(aComponent);
	}-*/;

}
