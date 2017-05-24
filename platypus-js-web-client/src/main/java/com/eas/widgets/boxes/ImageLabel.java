package com.eas.widgets.boxes;

import com.eas.core.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;

/**
 *
 * @author mg
 */
public class ImageLabel extends ImageParagraph {

    public ImageLabel(String aTitle, boolean asHtml, String aImage) {
        super(Document.get().createDivElement(), aTitle, asHtml, aImage);
        element.setClassName("label");
        element.getStyle().setProperty("outline", "none");
    }

    public ImageLabel(String aTitle, boolean asHtml) {
        this(aTitle, asHtml, null);
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        published.opaque = false;

        Object.defineProperty(published, "text", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusLabel::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusLabel::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
        Object.defineProperty(published, "icon", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusLabel::getImageResource()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusLabel::setImageResource(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
            }
        });
        Object.defineProperty(published, "iconTextGap", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusLabel::getIconTextGap()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusLabel::setIconTextGap(I)(aValue);
            }
        });
        Object.defineProperty(published, "horizontalTextPosition", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusLabel::getHorizontalTextPosition()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusLabel::setHorizontalTextPosition(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalTextPosition", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusLabel::getVerticalTextPosition()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusLabel::setVerticalTextPosition(I)(+aValue);
            }
        });

        Object.defineProperty(published, "horizontalAlignment", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusLabel::getHorizontalAlignment()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusLabel::setHorizontalAlignment(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalAlignment", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusLabel::getVerticalAlignment()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusLabel::setVerticalAlignment(I)(+aValue);
            }
        });
    }-*/;
}
