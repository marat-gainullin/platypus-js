package com.eas.widgets;

import com.eas.core.XElement;
import com.eas.ui.FocusEvent;
import com.eas.ui.Focusable;

import com.eas.ui.HasDecorationsWidth;
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
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
// TODO: Investigate empty text and selectedIndex == -1 state of <select> tag
public class DropDownList extends ValueWidget implements HasDecorationsWidth,
        Focusable, HasFocusHandlers, HasBlurHandlers,
        HasKeyDownHandlers, HasKeyUpHandlers, HasKeyPressHandlers {

    public DropDownList() {
        super(Document.get().createSelectElement());
        element.setClassName("form-control");
        element.<SelectElement>cast().setMultiple(false);
        element.<XElement>cast().addEventListener(BrowserEvents.CHANGE, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                Object oldValue = value;
                if (oldValue != value) {
                    int selectedIndex = getSelectedIndex();
                    if (selectedIndex == -1) {
                        value = null;
                    } else {
                        value = getAssociatedValue(selectedIndex);
                    }
                    fireValueChange(oldValue);
                    fireActionPerformed();
                }
            }

        });
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
    public void setDecorationsWidth(int aDecorationsWidth) {
        element.getStyle().setPaddingRight(aDecorationsWidth+ 'px');
    }


    @Override
    protected void publish(JavaScriptObject aValue) {
    }

}
