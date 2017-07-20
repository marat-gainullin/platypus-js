package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.ButtonGroup;
import com.eas.ui.FocusEvent;
import com.eas.ui.Focusable;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.eas.ui.HasButtonGroup;
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
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasText;
import java.util.HashSet;
import java.util.Set;

public class CheckBox extends ValueWidget implements HasButtonGroup, HasText,
        Focusable, HasFocusHandlers, HasBlurHandlers,
        HasKeyDownHandlers, HasKeyUpHandlers, HasKeyPressHandlers {
    protected Element anchor = Document.get().createDivElement();
    protected InputElement input;
    protected Element label;

    protected ButtonGroup group;

    public CheckBox() {
        this(Document.get().createCheckInputElement(), Document.get().createLabelElement());
        input.setClassName("check-box");
        label.classList.add("check-label");
    }

    protected CheckBox(InputElement aInput, Element aLabel) {
        super();
        input = aInput;
        label = aLabel;
        label.appendChild(input);
        element.appendChild(label);
        anchor.getStyle().display ='inline-block');
        anchor.getStyle().position = 'relative';
        anchor.getStyle().height =100 + '%');
        anchor.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        element.appendChild(anchor);
        element.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                setValue(value != null ? !Boolean.TRUE.equals(value) /*!!!value*/ : true);
                fireActionPerformed();
            }
        });
    }

    @Override
    public void setFocus(boolean aValue) {
        if (aValue) {
            input.focus();
        } else {
            input.blur();
        }
    }

    @Override
    public int getTabIndex() {
        return input.getTabIndex();
    }

    @Override
    public void setTabIndex(int index) {
        input.setTabIndex(index);
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
    public void setValue(Object aValue) {
        if (aValue == null) {
            input.setPropertyBoolean("indeterminate", true);
        } else {
            input.setPropertyBoolean("indeterminate", false);
            input.setChecked(Boolean.TRUE.equals(aValue));
        }
        super.setValue(aValue);
    }

    @Override
    public String getText() {
        return label.getInnerText();
    }

    @Override
    public void setText(String text) {
        label.setInnerText(text);
    }

    @Override
    public ButtonGroup getButtonGroup() {
        return group;
    }

    @Override
    public void setButtonGroup(ButtonGroup aValue) {
        group = aValue;
    }

    @Override
    public void mutateButtonGroup(ButtonGroup aGroup) {
        if (group != aGroup) {
            if (group != null) {
                group.remove(this);
            }
            group = aGroup;
            if (group != null) {
                group.add(this);
            }
        }
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "text", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusCheckBox::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusCheckBox::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(published, "selected", {
            get : function() {
                var value = aWidget.@com.eas.widgets.PlatypusCheckBox::getValue()();
                if (value == null)
                        return null;
                else
                        return aWidget.@com.eas.widgets.PlatypusCheckBox::getPlainValue()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusCheckBox::setPlainValue(Z)(aValue!=null && (false != aValue));
            }
        });
        Object.defineProperty(published, "buttonGroup", {
            get : function() {
                var buttonGroup = aWidget.@com.eas.ui.HasPlatypusButtonGroup::getButtonGroup()();
                return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(buttonGroup);					
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasPlatypusButtonGroup::mutateButtonGroup(Lcom/eas/ui/ButtonGroup;)(aValue != null ? aValue.unwrap() : null);
            }
        });
    }-*/;
}
