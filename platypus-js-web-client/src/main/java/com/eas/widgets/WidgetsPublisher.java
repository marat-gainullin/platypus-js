package com.eas.widgets;

import com.eas.widgets.boxes.RichTextArea;
import com.eas.widgets.boxes.TextArea;
import com.eas.widgets.boxes.TextField;
import com.eas.widgets.boxes.CheckBox;
import com.eas.core.Utils;
import com.eas.ui.ButtonGroup;
import com.eas.ui.PublishedComponent;
import com.eas.widgets.boxes.DropDownButton;
import com.eas.widgets.boxes.FormattedField;
import com.eas.widgets.boxes.ImageButton;
import com.eas.widgets.boxes.ImageLabel;
import com.eas.widgets.boxes.ImageToggleButton;
import com.eas.widgets.boxes.PasswordField;
import com.eas.widgets.boxes.ProgressBar;
import com.eas.widgets.boxes.RadioButton;
import com.eas.widgets.boxes.Slider;
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
	
	public native static PublishedComponent publish(RadioButton aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('RadioButton');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(CheckBox aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('CheckBox');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(Slider aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('Slider');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ImageToggleButton aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ToggleButton');
		return new constr(null, null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(TextField aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('TextField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(FormattedField aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('FormattedField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(TextArea aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('TextArea');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(RichTextArea aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('HtmlArea');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ProgressBar aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('ProgressBar');
		return new constr(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PasswordField aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('PasswordField');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ImageButton aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('Button');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(DropDownButton aComponent)/*-{
		var constr = @com.eas.widgets.WidgetsPublisher::getPublisher(Ljava/lang/String;)('DropDownButton');
		return new constr(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ImageLabel aComponent)/*-{
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
