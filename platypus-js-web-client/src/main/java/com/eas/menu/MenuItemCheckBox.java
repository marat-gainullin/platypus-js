package com.eas.menu;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.ButtonGroup;
import com.eas.ui.HasJsValue;
import com.eas.ui.Widget;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.HasActionHandlers;
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

/**
 *
 * @author mg
 */
public class MenuItemCheckBox extends MenuItem implements HasButtonGroup, HasActionHandlers, HasJsValue, HasValueChangeHandlers, HasText, HasHTML {

    protected Boolean value = Boolean.FALSE;
    protected Element leftMark;
    protected InputElement inputElem;
    protected Element field;
    protected ButtonGroup group;

    public MenuItemCheckBox(Boolean aValue, String aText, boolean asHtml) {
        super();
        element.setClassName("menu-item");
        leftMark = Document.get().createDivElement();
        leftMark.getStyle().setDisplay(Style.Display.INLINE);
        leftMark.getStyle().setPosition(Style.Position.RELATIVE);
        leftMark.getStyle().setHeight(100, Style.Unit.PCT);
        leftMark.setClassName("menu-left-mark");
        leftMark.setInnerHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
        element.appendChild(leftMark);
        inputElem = Document.get().createCheckInputElement();
        inputElem.getStyle().setPosition(Style.Position.ABSOLUTE);
        inputElem.getStyle().setWidth(100, Style.Unit.PCT);
        inputElem.setClassName("menu-left-check-radio");
        leftMark.appendChild(inputElem);
        field = Document.get().createDivElement();
        field.getStyle().setDisplay(Style.Display.INLINE);
        field.getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        field.setClassName("menu-field");
        setText(aText, asHtml);
        element.appendChild(field);
        setJsValue(aValue);
        element.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                fireActionPerformed();
                setJsValue(value != null ? !value : true);
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.TOUCHSTART, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                fireActionPerformed();
                setJsValue(value != null ? !value : true);
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
    public Object getJsValue() {
        return value;
    }

    @Override
    public void setJsValue(Object aValue) {
        if (aValue == null) {
            aValue = Boolean.FALSE;
        }
        value = (Boolean) aValue;
        inputElem.setChecked(value);
        fireValueChange();
    }

    protected final Set<ValueChangeHandler> valueChangeHandlers = new HashSet<>();

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

    protected void fireValueChange() {
        ValueChangeEvent event = new ValueChangeEvent(this, !value, value);
        for (ValueChangeHandler h : valueChangeHandlers) {
            h.onValueChange(event);
        }
    }

    protected Set<ActionHandler> actionHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addActionHandler(ActionHandler handler) {
        actionHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                actionHandlers.remove(handler);
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
                group.remove((HasPublished) this);
            }
            group = aGroup;
            if (group != null) {
                group.add((HasPublished) this);
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
