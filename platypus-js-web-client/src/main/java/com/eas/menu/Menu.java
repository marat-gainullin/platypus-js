package com.eas.menu;

import com.eas.core.HasPublished;
import com.eas.widgets.containers.Container;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.HasText;

public class Menu extends Container implements HasText {

    protected MenuItem parentItem;
    protected String text; // Used only as extra text, for example for left bar decoration.

    public Menu() {
        super();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String aValue) {
        text = aValue;
    }

    public MenuItem getParentItem() {
        return parentItem;
    }

    public void setParentItem(MenuItem aItem) {
        parentItem = aItem;
    }

    public void show(int hostX, int hostY, int hostWidth, int hostHeight) {
    }
    
    public void showRelativeTo(Element aAnchor){
    }
    
    private Point calcHorizontal(int hostX, int hostY, int hostWidth, int hostHeight){
        return new Point(hostX, hostY);
    }
    
    private Point calcVertical(int hostX, int hostY, int hostWidth, int hostHeight, boolean alignTopBottom){
        return new Point(hostX, hostY);
    }
    
    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "text", {
            get : function() {
                return aWidget.@com.eas.menu.PlatypusMenu::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.menu.PlatypusMenu::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
    }-*/;

}
