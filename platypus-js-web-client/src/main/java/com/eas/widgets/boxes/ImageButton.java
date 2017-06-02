package com.eas.widgets.boxes;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.HasActionHandlers;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ImageButton extends ImageParagraph implements HasActionHandlers {

    public ImageButton() {
        this("", false);
    }

    public ImageButton(String aTitle, boolean asHtml, String aImage) {
        this(Document.get().createPushButtonElement(), aTitle, asHtml, aImage);
    }

    public ImageButton(Element aContainer, String aTitle, boolean asHtml, String aImage) {
        super(aContainer, aTitle, asHtml, aImage);
        horizontalAlignment = ImageParagraph.CENTER;
        element.setClassName("btn btn-default");
    }

    public ImageButton(String aTitle, boolean asHtml) {
        this(aTitle, asHtml, null);
    }

    protected Set<ActionHandler> actionHandlers = new HashSet<>();
    private HandlerRegistration clickReg;
    private HandlerRegistration keyReg;

    @Override
    public HandlerRegistration addActionHandler(ActionHandler handler) {
        actionHandlers.add(handler);
        if (actionHandlers.size() == 1) {
            clickReg = element.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

                @Override
                public void on(NativeEvent event) {
                    event.stopPropagation();
                    fireActionPerformed();
                }

            });
            keyReg = element.<XElement>cast().addEventListener(BrowserEvents.KEYPRESS, new XElement.NativeHandler() {

                @Override
                public void on(NativeEvent event) {
                    if (event.getKeyCode() == KeyCodes.KEY_ENTER || event.getKeyCode() == KeyCodes.KEY_SPACE) {
                        event.stopPropagation();
                        fireActionPerformed();
                    }
                }

            });

        }
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                actionHandlers.remove(handler);
                if (actionHandlers.isEmpty()) {
                    clickReg.removeHandler();
                    keyReg.removeHandler();
                }
            }

        };
    }

    protected void fireActionPerformed() {
        ActionEvent event = new ActionEvent(this);
        for (ActionHandler h : actionHandlers) {
            h.onAction(event);
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
                return aWidget.@com.eas.widgets.PlatypusButton::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
        Object.defineProperty(published, "icon", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusButton::getImageResource()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusButton::setImageResource(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
            }
        });
        Object.defineProperty(published, "iconTextGap", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusButton::getIconTextGap()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusButton::setIconTextGap(I)(aValue);
            }
        });
        Object.defineProperty(published, "horizontalTextPosition", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusButton::getHorizontalTextPosition()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusButton::setHorizontalTextPosition(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalTextPosition", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusButton::getVerticalTextPosition()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusButton::setVerticalTextPosition(I)(+aValue);
            }
        });

        Object.defineProperty(published, "horizontalAlignment", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusButton::getHorizontalAlignment()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusButton::setHorizontalAlignment(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalAlignment", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusButton::getVerticalAlignment()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusButton::setVerticalAlignment(I)(+aValue);
            }
        });
    }-*/;
}
