package com.eas.widgets;

import com.eas.ui.Widget;
import com.google.gwt.core.client.JavaScriptObject;

public class BooleanDecoratorField extends ValueDecoratorField {

    public BooleanDecoratorField() {
        super(new CheckBox());
        element.setClassName("decorator-check");
    }

    @Override
    public void setPublished(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private static native void publish(Widget aField, JavaScriptObject aPublished)/*-{
        var B = @com.eas.core.Predefine::boxing;
        aPublished.redraw = function(){
            aField.@com.eas.bound.ModelCheck::rebind()();
        };
        Object.defineProperty(aPublished, "text", {
            get : function() {
                return aField.@com.eas.bound.ModelCheck::getText()();
            },
            set : function(aValue) {
                if (aValue != null)
                    aField.@com.eas.bound.ModelCheck::setText(Ljava/lang/String;)('' + aValue);
                else
                    aField.@com.eas.bound.ModelCheck::setText(Ljava/lang/String;)(null);
            }
        });
        Object.defineProperty(aPublished, "value", {
            get : function() {
                var javaValue = aField.@com.eas.bound.ModelCheck::getValue()();
                if (javaValue == null)
                    return null;
                else
                    return javaValue.@java.lang.Boolean::booleanValue()();
            },
            set : function(aValue) {
                if (aValue != null) {
                    var javaValue = B.boxAsJava((false != aValue));
                    aField.@com.eas.bound.ModelCheck::setValue(Ljava/lang/Boolean;Z)(javaValue, true);
                } else {
                    aField.@com.eas.bound.ModelCheck::setValue(Ljava/lang/Boolean;Z)(null, true);
                }
            }
        });
    }-*/;
}
