package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.CommonResources;
import com.eas.ui.FocusEvent;
import com.eas.ui.Focusable;
import com.eas.ui.HasEmptyText;
import com.eas.ui.ValueWidget;
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
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasText;
import java.util.HashSet;
import java.util.Set;

public class TextField extends ValueWidget implements HasText, HasEmptyText,
        Focusable, HasFocusHandlers, HasBlurHandlers,
        HasKeyDownHandlers, HasKeyUpHandlers, HasKeyPressHandlers {

    // TODO: change most protected members to private
    protected String emptyText;
    private XElement.NativeHandler onChange;
    private HandlerRegistration onChangeReg;

    public TextField() {
        this(Document.get().createTextInputElement());
    }

    protected TextField(Element aElement) {
        super(aElement);
        element.setClassName("form-control");
        // TODO: evict such injection from all sources
        CommonResources.INSTANCE.commons().ensureInjected();
        element.addClassName(CommonResources.INSTANCE.commons().borderSized());
        onChange = new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                Object oldValue = value;
                String text = element.<InputElement>cast().getValue();
                value = parse(text);
                // TODO: Check all widgets against null to empty strings conversion
                fireValueChange(oldValue);
                // TODO: Check all widgets against value changes / action performed
                fireActionPerformed();
            }
        };
        onChangeReg = element.<XElement>cast().addEventListener(BrowserEvents.CHANGE, onChange);
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
    
    private Set<FocusHandler> focusHandlers = new Set();

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

    private Set<BlurHandler> blurHandlers = new Set();

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

    private Set<KeyUpHandler> keyUpHandlers = new Set();

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

    private Set<KeyDownHandler> keyDownHandlers = new Set();

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

    private Set<KeyPressHandler> keyPressHandlers = new Set();

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

    @Override
    public String getText() {
        return element.<InputElement>cast().getValue();
    }

    @Override
    public void setText(String text) {
        Object oldValue = value;
        value = parse(text);
        // TODO: Check all widgets against null to empty strings conversion
        fireValueChange(oldValue);
    }

    protected Object parse(String aText) {
        return aText;
    }

    protected String format(Object aValue) {
        return aValue != null ? aValue + "" : "";
    }

    @Override
    public String getEmptyText() {
        return emptyText;
    }

    @Override
    public void setEmptyText(String aValue) {
        emptyText = aValue;
        element.setAttribute("placeholder", aValue);
    }

    @Override
    public void setName(String aValue) {
        super.setName(aValue);
        element.setAttribute("name", aValue); // For HTML forms
    }

    @Override
    public void setValue(Object aValue) {
        if (value != aValue) {
            Object oldValue = value;
            value = (String) aValue;
            onChangeReg.removeHandler();
            try {
                // TODO: Check all widgets againts null to empty string conversion
                String text = format(value);
                element.<InputElement>cast().setValue(text);
            } finally {
                onChangeReg = element.<XElement>cast().addEventListener(BrowserEvents.CHANGE, onChange);
            }
            fireValueChange(oldValue);
        }
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "value", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusTextField::getValue()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusTextField::setValue(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
        Object.defineProperty(published, "text", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusTextField::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusTextField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
        Object.defineProperty(published, "emptyText", {
            get : function() {
                return aWidget.@com.eas.ui.HasEmptyText::getEmptyText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
    }-*/;
}
