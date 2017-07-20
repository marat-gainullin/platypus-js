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

    public int getCount() {
        return element.<SelectElement>cast().getOptions().getLength();
    }

    public void addItem(String aLabel, String aKey, Object aAssociatedValue, String aClassName) {
        addItem(getCount(), aLabel, aKey, aAssociatedValue, aClassName);
    }

    public void addItem(int index, String aLabel, String aKey, Object aAssociatedValue, String aClassName) {
        if (index >= 0 && index <= getCount()) {
            OptionElement item = Document.get().createOptionElement();
            item.setClassName(aClassName);
            item.setInnerText(aLabel);
            item.setValue(aKey);
            item.setPropertyObject("js-value", aAssociatedValue);
            boolean wasUnselected = element.<SelectElement>cast().getSelectedIndex() == -1;
            if (index == getCount()) {
                element.appendChild(item);
            } else {
                element.insertBefore(getItem(index), item);
            }
            if (wasUnselected) {
                element.<SelectElement>cast().setSelectedIndex(-1);
            }
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < getCount()) {
            OptionElement item = element.<SelectElement>cast().getOptions().getItem(index);
            item.removeFromParent();
        }
    }

    public OptionElement getItem(int index) {
        if (index >= 0 && index < getCount()) {
            return element.<SelectElement>cast().getOptions().getItem(index);
        } else {
            return null;
        }
    }

    public Object getAssociatedValue(int index) {
        OptionElement item = getItem(index);
        return item != null ? item.getPropertyObject("js-value") : null;
    }

    public void setAssociatedValue(int index, Object aValue) {
        OptionElement item = getItem(index);
        if (item != null) {
            item.setPropertyObject("js-value", aValue);
        }
    }

    public int indexOf(Object aValue) {
        for (int i = 0; i < getCount(); i++) {
            if (getAssociatedValue(i) == aValue) {
                return i;
            }
        }
        return -1;
    }

    public String getKey(int aIndex) {
        OptionElement option = getItem(aIndex);
        return option.getValue();
    }

    public String getItemStyleName(int aIndex) {
        OptionElement item = getItem(aIndex);
        return item != null ? item.getClassName() : null;
    }

    public void setItemStyleName(int aIndex, String aValue) {
        OptionElement item = getItem(aIndex);
        if (item != null) {
            item.setClassName(aValue);
        }
    }

    public void clear() {
        for (int i = getCount(); i >= 0; i--) {
            removeItem(i);
        }
    }

    public int getSelectedIndex() {
        return element.<SelectElement>cast().getSelectedIndex();
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < getCount()) {
            element.<SelectElement>cast().setSelectedIndex(index);
        } else {
            element.<SelectElement>cast().setSelectedIndex(-1);
        }
    }
    
    @Override
    public void setValue(Object aValue) {
        if (value != aValue) {
            int index = indexOf(aValue);
            setSelectedIndex(index);
            super.setValue(aValue);
        }
    }

    public void setVisibleItemCount(int count) {
        element.<SelectElement>cast().setSize(count);
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
    }

}
