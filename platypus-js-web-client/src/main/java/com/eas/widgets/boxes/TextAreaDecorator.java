package com.eas.widgets.boxes;

import com.google.gwt.core.client.JavaScriptObject;
import com.eas.ui.HasScroll;
import com.eas.ui.HorizontalScrollFiller;
import com.eas.ui.VerticalScrollFiller;

public class TextAreaDecorator extends ValueDecoratorField implements HasScroll, HorizontalScrollFiller, VerticalScrollFiller {

    public TextAreaDecorator() {
        super(new TextArea());
        //((TextArea) decorated).getElement().getStyle().setProperty("wordWrap", "normal");
        ((TextArea) decorated).getElement().getStyle().setProperty("resize", "none");
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    protected native static void publish(TextAreaDecorator aWidget, JavaScriptObject aPublished)/*-{
        aPublished.redraw = function(){
            aWidget.@com.eas.bound.ModelTextArea::rebind()();
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
                return aWidget.@com.eas.bound.ModelTextArea::getValue()();
            },
            set : function(aValue) {
                if (aValue != null)
                    aWidget.@com.eas.bound.ModelTextArea::setValue(Ljava/lang/String;)('' + aValue);
                else
                    aWidget.@com.eas.bound.ModelTextArea::setValue(Ljava/lang/String;)(null);
            }
        });
        Object.defineProperty(aPublished, "text", {
            get : function() {
                return aWidget.@com.eas.bound.ModelTextArea::getText()();
            },
            set : function(aValue) {
                if (aValue != null)
                    aWidget.@com.eas.bound.ModelTextArea::setValue(Ljava/lang/String;)('' + aValue);
                else
                    aWidget.@com.eas.bound.ModelTextArea::setValue(Ljava/lang/String;)(null);
            }
        });
    }-*/;
}
