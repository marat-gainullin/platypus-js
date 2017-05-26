package com.eas.widgets.boxes;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.ButtonGroup;
import com.eas.ui.events.HasActionHandlers;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.eas.ui.HasButtonGroup;
import com.eas.ui.HasJsValue;
import com.eas.ui.Widget;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

public class CheckBox extends Widget implements HasActionHandlers, HasButtonGroup, HasJsValue, HasValueChangeHandlers {

    protected Element anchor = Document.get().createDivElement();
    protected InputElement input;
    protected Element label;
    protected Boolean value;

    protected ButtonGroup group;

    public CheckBox() {
        this(Document.get().createCheckInputElement(), Document.get().createLabelElement());
        input.setClassName("check-box");
        label.addClassName("check-label");
    }

    protected CheckBox(InputElement aInput, Element aLabel) {
        super();
        input = aInput;
        label = aLabel;
        label.appendChild(input);
        element.appendChild(label);
        anchor.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        anchor.getStyle().setPosition(Style.Position.RELATIVE);
        anchor.getStyle().setHeight(100, Style.Unit.PCT);
        anchor.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        element.appendChild(anchor);
        element.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                setJsValue(value != null ? !value : true);
                fireActionPerformed();
            }
        });
    }

    @Override
    public Object getJsValue() {
        return value;
    }

    @Override
    public void setJsValue(Object aValue) {
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

    protected void fireValueChange(Boolean oldValue) {
        ValueChangeEvent event = new ValueChangeEvent(this, oldValue, value);
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
