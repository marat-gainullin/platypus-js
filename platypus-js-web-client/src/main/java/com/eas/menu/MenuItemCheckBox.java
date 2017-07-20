package com.eas.menu;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.ButtonGroup;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import java.util.HashSet;
import java.util.Set;
import com.eas.ui.HasButtonGroup;
import com.eas.ui.HasValue;

/**
 *
 * @author mg
 */
public class MenuItemCheckBox extends MenuItem implements HasButtonGroup, HasValue, HasValueChangeHandlers, HasText, HasHTML {

    protected Boolean value = Boolean.FALSE;
    protected Element leftMark;
    protected InputElement input;
    protected Element field;
    protected ButtonGroup group;

    public MenuItemCheckBox() {
        this(false, "", false);
    }

    public MenuItemCheckBox(Boolean aValue, String aText, boolean asHtml) {
        super();
        element.setClassName("menu-item");
        leftMark = Document.get().createDivElement();
        leftMark.getStyle().display =Style.Display.INLINE);
        leftMark.getStyle().position = 'relative';
        leftMark.getStyle().height =100 + '%');
        leftMark.setClassName("menu-left-mark");
        leftMark.setInnerHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
        element.appendChild(leftMark);
        input = Document.get().createCheckInputElement();
        input.getStyle().position = 'absolute';
        input.getStyle().width =100 + '%');
        input.setClassName("menu-left-check-radio");
        leftMark.appendChild(input);
        field = Document.get().createDivElement();
        field.getStyle().display =Style.Display.INLINE);
        field.getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        field.setClassName("menu-field");
        setText(aText, asHtml);
        element.appendChild(field);
        setValue(aValue);
        element.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                setValue(value != null ? !value : true);
                fireActionPerformed();
            }
        });
    }

    @Override
    public String getText() {
        return field.getInnerText();
    }

    @Override
    public void setText(String aText) {
        setText(aText, false);
    }

    @Override
    public String getHTML() {
        return field.getInnerHTML();
    }

    @Override
    public void setHTML(String html) {
        setText(html, true);
    }

    public void setText(String aText, boolean asHtml) {
        String lText = aText != null ? aText : "";
        if (asHtml) {
            field.setInnerHTML(lText);
        } else {
            field.setInnerText(lText);
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object aValue) {
        Boolean oldValue = value;
        value = (Boolean) aValue;
        if (aValue == null) {
            input.setPropertyBoolean("indeterminate", true);
        } else {
            input.setPropertyBoolean("indeterminate", false);
            input.setChecked(Boolean.TRUE.equals(aValue));
        }
        fireValueChange(oldValue);
    }

    protected final Set<ValueChangeHandler> valueChangeHandlers = new Set();

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler handler) {
        valueChangeHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                valueChangeHandlers.remove(handler);
            }

        };
    }

    protected void fireValueChange(Boolean oldValue) {
        ValueChangeEvent event = new ValueChangeEvent(this, oldValue, value);
        for (ValueChangeHandler h : valueChangeHandlers) {
            h.onValueChange(event);
        }
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
                return aWidget.@com.eas.menu.PlatypusMenuItemRadioButton::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.menu.PlatypusMenuItemRadioButton::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(published, "selected", {
            get : function() {
                return aWidget.@com.eas.menu.PlatypusMenuItemRadioButton::getPlainValue()();
            },
            set : function(aValue) {
                aWidget.@com.eas.menu.PlatypusMenuItemRadioButton::setPlainValue(Z)(aValue != null && !!aValue);
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
