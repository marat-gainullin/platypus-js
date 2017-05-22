package com.eas.widgets;

import com.eas.core.Utils;
import com.eas.ui.ButtonGroup;
import com.eas.ui.PublishedComponent;
import com.eas.widgets.containers.Absolute;
import com.eas.widgets.containers.Anchors;
import com.eas.widgets.containers.Borders;
import com.eas.widgets.containers.Box;
import com.eas.widgets.containers.Cards;
import com.eas.widgets.containers.Cells;
import com.eas.widgets.containers.Desktop;
import com.eas.widgets.containers.Flow;
import com.eas.widgets.containers.Scroll;
import com.eas.widgets.containers.Split;
import com.eas.widgets.containers.Tabs;
import com.eas.widgets.containers.Toolbar;
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

	public native static PublishedComponent publish(Borders aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('BorderPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(Scroll aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ScrollPane');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(Split aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('SplitPane');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(Box aComponent)/*-{
		var Orientation = @com.eas.ui.JsUi::Orientation;
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('BoxPane');
		return new constr(Orientation.HORIZONTAL, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(Cards aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('CardPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(Absolute aComponent);

	public native static PublishedComponent publish(Anchors aComponent);

	public native static PublishedComponent publish(Desktop aComponent);

	public native static PublishedComponent publish(Tabs aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('TabbedPane');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(Toolbar aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ToolBar');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(Flow aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('FlowPane');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(Cells aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('GridPane');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publish(ButtonGroup aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ButtonGroup');
		return new constr(aComponent);
	}-*/;

}
