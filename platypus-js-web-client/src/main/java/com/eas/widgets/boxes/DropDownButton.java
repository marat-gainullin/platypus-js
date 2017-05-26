package com.eas.widgets.boxes;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.menu.Menu;
import com.eas.ui.CommonResources;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;

/**
 *
 * @author mg
 */
public class DropDownButton extends ImageButton {

    protected Element chevron = Document.get().createDivElement();
    protected Element splitter = Document.get().createDivElement();
    protected Menu menu;

    public DropDownButton() {
        this("", false, null);
    }

    public DropDownButton(String aTitle, boolean asHtml, Menu aMenu) {
        this(aTitle, asHtml, null, aMenu);
    }

    public DropDownButton(String aTitle, boolean asHtml, String aImage, Menu aMenu) {
        super(aTitle, asHtml, aImage);
        menu = aMenu;
        CommonResources.INSTANCE.commons().ensureInjected();
        chevron.setClassName("dropdown");
        menu.getElement().setClassName("dropdown-menu " + CommonResources.INSTANCE.commons().unselectable());
        splitter.setClassName("dropdown-split " + CommonResources.INSTANCE.commons().unselectable());
        chevron.appendChild(splitter);
        element.insertFirst(chevron);

        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Element target = Element.as(event.getEventTarget());
                if (target == chevron || target == splitter) {
                    event.preventDefault();
                    event.stopPropagation();
                    showMenu();
                }
            }
        });
    }

    protected void showMenu() {
        if (menu != null) {
            /*
            final PopupPanel pp = new PopupPanel();
            pp.setAutoHideEnabled(true);
            pp.setAutoHideOnHistoryEventsEnabled(true);
            pp.setAnimationEnabled(true);
            pp.setWidget(menu);
            pp.showRelativeTo(chevronMenu);
             */
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu aMenu) {
        if (menu != aMenu) {
            if (menu != null) {
                // menu.hide();
            }
            menu = aMenu;
            splitter.getStyle().setDisplay(menu != null ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
        }
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        published.opaque = true;

        Object.defineProperty(published, "text", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSplitButton::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSplitButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
        Object.defineProperty(published, "icon", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSplitButton::getImageResource()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSplitButton::setImageResource(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
            }
        });
        Object.defineProperty(published, "iconTextGap", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSplitButton::getIconTextGap()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSplitButton::setIconTextGap(I)(aValue);
            }
        });
        Object.defineProperty(published, "horizontalTextPosition", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSplitButton::getHorizontalTextPosition()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSplitButton::setHorizontalTextPosition(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalTextPosition", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSplitButton::getVerticalTextPosition()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSplitButton::setVerticalTextPosition(I)(+aValue);
            }
        });

        Object.defineProperty(published, "horizontalAlignment", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSplitButton::getHorizontalAlignment()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSplitButton::setHorizontalAlignment(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalAlignment", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSplitButton::getVerticalAlignment()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSplitButton::setVerticalAlignment(I)(+aValue);
            }
        });
        Object.defineProperty(published, "dropDownMenu", {
            get : function(){
                var menu = aWidget.@com.eas.widgets.PlatypusSplitButton::getMenu()();
                return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(menu);
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.PlatypusSplitButton::setMenu(Lcom/google/gwt/user/client/ui/MenuBar;)(aValue != null ? aValue.unwrap() : null);
            }
        });
    }-*/;
}
