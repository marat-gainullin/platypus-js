package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.FocusEvent;
import com.eas.ui.Focusable;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.BlurEvent;
import com.eas.ui.events.BlurHandler;
import com.eas.ui.events.FocusHandler;
import com.eas.ui.events.HasBlurHandlers;
import com.eas.ui.events.HasFocusHandlers;
import com.eas.ui.events.HasKeyDownHandlers;
import com.eas.ui.events.HasKeyPressHandlers;
import com.eas.ui.events.HasKeyUpHandlers;
import com.eas.ui.events.KeyDownEvent;
import com.eas.ui.events.KeyDownHandler;
import com.eas.ui.events.KeyPressEvent;
import com.eas.ui.events.KeyPressHandler;
import com.eas.ui.events.KeyUpEvent;
import com.eas.ui.events.KeyUpHandler;
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
public class ImageButton extends ImageParagraph implements
        Focusable, HasFocusHandlers, HasBlurHandlers,
        HasKeyDownHandlers, HasKeyUpHandlers, HasKeyPressHandlers {

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
    
    @Override
    public void setFocus(boolean aValue) {
        if (aValue) {
            element.focus();
        } else {
            element.blur();
        }
    }

    @Override
    public int getTabIndex() {
        return element.getTabIndex();
    }

    @Override
    public void setTabIndex(int index) {
        element.setTabIndex(index);
    }

    private Set<FocusHandler> focusHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        focusHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                focusHandlers.remove(handler);
            }

        };
    }

    private void fireFocus() {
        FocusEvent event = new FocusEvent(this);
        for (FocusHandler h : focusHandlers) {
            h.onFocus(event);
        }
    }

    private Set<BlurHandler> blurHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        blurHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                blurHandlers.remove(handler);
            }

        };
    }

    private void fireBlur() {
        BlurEvent event = new BlurEvent(this);
        for (BlurHandler h : blurHandlers) {
            h.onBlur(event);
        }
    }

    private Set<KeyUpHandler> keyUpHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        keyUpHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyUpHandlers.remove(handler);
            }

        };
    }

    private void fireKeyUp(NativeEvent nevent) {
        KeyUpEvent event = new KeyUpEvent(this, nevent);
        for (KeyUpHandler h : keyUpHandlers) {
            h.onKeyUp(event);
        }
    }

    private Set<KeyDownHandler> keyDownHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        keyDownHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyDownHandlers.remove(handler);
            }

        };
    }

    private void fireKeyDown(NativeEvent nevent) {
        KeyDownEvent event = new KeyDownEvent(this, nevent);
        for (KeyDownHandler h : keyDownHandlers) {
            h.onKeyDown(event);
        }
    }

    private Set<KeyPressHandler> keyPressHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        keyPressHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyPressHandlers.remove(handler);
            }

        };
    }

    private void fireKeyPress(NativeEvent nevent) {
        KeyPressEvent event = new KeyPressEvent(this, nevent);
        for (KeyPressHandler h : keyPressHandlers) {
            h.onKeyPress(event);
        }
    }

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
