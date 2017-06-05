package com.eas.widgets.boxes;

import java.text.ParseException;

import com.google.gwt.core.client.JavaScriptObject;

public class FormattedDecoratorField extends ValueDecoratorField {

    public FormattedDecoratorField() {
        super(new FormattedField());
    }

    public String getFormat() {
        return ((FormattedField) decorated).getPattern();
    }

    public void setFormat(String aValue) throws ParseException {
        ((FormattedField) decorated).setPattern(aValue);
    }

    public int getValueType() {
        return ((FormattedField) decorated).getValueType();
    }

    public void setValueType(int aValue) throws ParseException {
        ((FormattedField) decorated).setValueType(aValue);
    }

    public JavaScriptObject getOnParse() {
        return ((FormattedField) decorated).getOnParse();
    }

    public void setOnParse(JavaScriptObject aValue) {
        ((FormattedField) decorated).setOnParse(aValue);
    }

    public JavaScriptObject getOnFormat() {
        return ((FormattedField) decorated).getOnFormat();
    }

    public void setOnFormat(JavaScriptObject aValue) {
        ((FormattedField) decorated).setOnFormat(aValue);
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(FormattedDecoratorField aWidget, JavaScriptObject aPublished)/*-{
        var B = @com.eas.core.Predefine::boxing;
        aPublished.redraw = function(){
            aWidget.@com.eas.bound.ModelFormattedField::rebind()();
        };
        Object.defineProperty(aPublished, "emptyText", {
            get : function() {
                return aWidget.@com.eas.ui.HasEmptyText::getEmptyText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(aPublished, "value", {
            get : function() {
                return B.boxAsJs(aWidget.@com.eas.bound.ModelFormattedField::getJsValue()());
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelFormattedField::setJsValue(Ljava/lang/Object;)(B.boxAsJava(aValue));
            }
        });
        Object.defineProperty(aPublished, "valueType", {
            get : function() {
                var typeNum = aWidget.@com.eas.bound.ModelFormattedField::getValueType()()
                var type;
                if (typeNum === @com.eas.widgets.boxes.ObjectFormat::NUMBER ){
                    type = $wnd.Number;
                } else if (typeNum === @com.eas.widgets.boxes.ObjectFormat::DATE ){
                    type = $wnd.Date;
                } else if (typeNum === @com.eas.widgets.boxes.ObjectFormat::REGEXP ){
                    type = $wnd.RegExp;
                } else {
                    type = $wnd.String;
                }
                return type;
            },
            set : function(aValue) {
                   var typeNum;
                   if (aValue === $wnd.Number ){
                           typeNum = @com.eas.widgets.boxes.ObjectFormat::NUMBER;
                   } else if (aValue === $wnd.Date ){
                           typeNum = @com.eas.widgets.boxes.ObjectFormat::DATE;
                   } else if (aValue === $wnd.RegExp ){
                           typeNum = @com.eas.widgets.boxes.ObjectFormat::REGEXP;
                   } else {
                           typeNum = @com.eas.widgets.boxes.ObjectFormat::TEXT;
                   }
                   aWidget.@com.eas.bound.ModelFormattedField::setValueType(I)(typeNum);
            }
        });
        Object.defineProperty(aPublished, "text", {
            get : function() {
                return aWidget.@com.eas.bound.ModelFormattedField::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelFormattedField::setText(Ljava/lang/String;)(B.boxAsJava(aValue));
            }
        });
        Object.defineProperty(aPublished, "format", {
            get : function() {
                return aWidget.@com.eas.bound.ModelFormattedField::getFormat()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelFormattedField::setFormat(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(aPublished, "onFormat", {
            get : function() {
                return aWidget.@com.eas.bound.ModelFormattedField::getOnFormat()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelFormattedField::setOnFormat(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "onParse", {
            get : function() {
                return aWidget.@com.eas.bound.ModelFormattedField::getOnParse()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelFormattedField::setOnParse(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
    }-*/;
}
