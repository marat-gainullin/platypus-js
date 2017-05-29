package com.eas.widgets.boxes;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.CommonResources;
import com.eas.ui.HasEmptyText;
import com.eas.ui.HasJsValue;
import com.eas.ui.events.HasActionHandlers;
import com.eas.ui.Widget;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

public class TextField extends Widget implements HasActionHandlers, HasJsValue, HasValueChangeHandlers, HasEmptyText {

    protected String emptyText;
    protected String value;
    private boolean settingValue;

    public TextField() {
        this(Document.get().createTextInputElement());
    }
    
    protected TextField(Element aElement) {
        super(aElement);
        element.setClassName("form-control");
        CommonResources.INSTANCE.commons().ensureInjected();
        element.addClassName(CommonResources.INSTANCE.commons().borderSized());
        element.<XElement>cast().addEventListener(BrowserEvents.CHANGE, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                if (!settingValue) { // It is because of null to empty string conversion
                    setJsValue(element.<InputElement>cast().getValue());
                    fireActionPerformed(); // TODO: Check all widgets against value changes / action performed
                }
            }
        });
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
    public void setJsName(String aValue) {
        super.setJsName(aValue);
        element.setAttribute("name", aValue); // For HTML forms
    }

    @Override
    public Object getJsValue() {
        return value;
    }

    @Override
    public void setJsValue(Object aValue) {
        if (value != aValue) {
            settingValue = true;
            try {
                String oldValue = value;
                element.<InputElement>cast().setValue(aValue != null ? aValue + "" : ""); // TODO: Check all widgets againts null to empty strings conversion
                fireValueChange(oldValue);
            } finally {
                settingValue = false;
            }
        }
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

    protected void fireValueChange(Object oldValue) {
        com.eas.ui.events.ValueChangeEvent event = new com.eas.ui.events.ValueChangeEvent(this, oldValue, value);
        for (com.eas.ui.events.ValueChangeHandler h : valueChangeHandlers) {
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
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "value", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusTextField::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusTextField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
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
