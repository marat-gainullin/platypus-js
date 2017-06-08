package com.eas.bound;

import com.eas.widgets.boxes.DropDownListDecoratorField;
import com.eas.widgets.boxes.FormattedDecoratorField;
import com.eas.widgets.boxes.TextAreaDecoratorField;
import com.eas.widgets.boxes.BooleanDecoratorField;
import com.eas.core.Utils;
import com.eas.ui.PublishedComponent;
import com.eas.widgets.boxes.DateDecoratorField;
import com.eas.widgets.boxes.NumberDecoratorField;
import com.google.gwt.core.client.JavaScriptObject;

public class BoundPublisher {

	private static Utils.JsObject constructors = JavaScriptObject.createObject().cast();
	
	private static JavaScriptObject getPublisher(String aClassName){
		JavaScriptObject constr = constructors.getJs(aClassName);
		if(constr == null)
			throw new IllegalStateException("Constructor for " + aClassName + " was not found.");
		return constr;
	}
	
	public static native JavaScriptObject getConstructors()/*-{
		return @com.eas.bound.BoundPublisher::constructors;
	}-*/;

	public static void putPublisher(String aClassName, JavaScriptObject aPublisher){
		constructors.setJs(aClassName, aPublisher);
	}
	
	public native static PublishedComponent publish(BooleanDecoratorField aComponent)/*-{
		var constr = @com.eas.bound.BoundPublisher::getPublisher(Ljava/lang/String;)('ModelCheckBox');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(FormattedDecoratorField aComponent);

	public native static PublishedComponent publish(TextAreaDecoratorField aComponent);

	public native static PublishedComponent publish(DateDecoratorField aComponent)/*-{
		var constr = @com.eas.bound.BoundPublisher::getPublisher(Ljava/lang/String;)('ModelDate');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(NumberDecoratorField aComponent)/*-{
		var constr = @com.eas.bound.BoundPublisher::getPublisher(Ljava/lang/String;)('ModelSpin');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(DropDownListDecoratorField aComponent)/*-{
		var constr = @com.eas.bound.BoundPublisher::getPublisher(Ljava/lang/String;)('ModelCombo');
		return new constr(aComponent);
	}-*/;
}
