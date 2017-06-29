package com.eas.menu;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;

/**
 *
 * @author mg
 */
public class MenuItemImageText extends MenuItem implements HasText, HasHTML {

    protected String imageUri;
    //
    protected Element leftMark;
    protected Element field;

    public MenuItemImageText() {
        this("", false, null);
    }
    
    public MenuItemImageText(String aText, boolean asHtml, String aImageUri) {
        super();
        element.setClassName("menu-item");
        leftMark = Document.get().createDivElement();
        leftMark.setClassName("menu-left-mark");
        leftMark.getStyle().setDisplay(Style.Display.INLINE);
        leftMark.getStyle().setPosition(Style.Position.RELATIVE);
        leftMark.getStyle().setHeight(100, Style.Unit.PCT);
        leftMark.setInnerHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
        setImageUri(aImageUri);
        element.appendChild(leftMark);

        field = Document.get().createDivElement();
        field.setClassName("menu-field");
        field.getStyle().setDisplay(Style.Display.INLINE);
        field.getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        if (asHtml) {
            setHTML(aText);
        } else {
            setText(aText);
        }
        element.appendChild(field);
        element.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                fireActionPerformed();
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.TOUCHSTART, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                fireActionPerformed();
            }
        });
    }

    @Override
    public String getText() {
        return field.getInnerText();
    }

    @Override
    public void setText(String text) {
        field.setInnerText(text != null ? text : "");
    }

    @Override
    public String getHTML() {
        return field.getInnerHTML();
    }

    @Override
    public void setHTML(String html) {
        field.setInnerHTML(html != null ? html : "");
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String aValue) {
        imageUri = aValue;
        if (imageUri != null && !imageUri.isEmpty()) {
            leftMark.getStyle().setBackgroundImage("url(" + imageUri + ")");
            leftMark.getStyle().setProperty("background-repeat", "no-repeat");
        } else {
            leftMark.getStyle().clearBackgroundImage();
            leftMark.getStyle().clearProperty("background-repeat");
        }
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
        Object.defineProperty(aPublished, "text", {
            get : function() {
                return aWidget.@com.eas.menu.PlatypusMenuItemImageText::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.menu.PlatypusMenuItemImageText::setText(Ljava/lang/String;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "icon", {
            get : function() {
                return aWidget.@com.eas.menu.PlatypusMenuItemImageText::getIcon()();
            },
            set : function(aValue) {
                aWidget.@com.eas.menu.PlatypusMenuItemImageText::setIcon(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
            }
        });			
    }-*/;
}
