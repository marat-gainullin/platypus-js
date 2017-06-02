package com.eas.widgets.boxes;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.ButtonGroup;
import com.eas.ui.HasButtonGroup;
import com.eas.ui.HasJsValue;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ImageToggleButton extends ImageButton implements HasJsValue, HasValueChangeHandlers, HasButtonGroup {

    protected Boolean value;

    protected ButtonGroup group;
    
    public ImageToggleButton() {
        this("", false);
    }
    
    public ImageToggleButton(String aTitle, boolean asHtml) {
        this(aTitle, asHtml, null);
    }

    public ImageToggleButton(String aTitle, boolean asHtml, String aImage) {
        super(aTitle, asHtml, aImage);
        element.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                setJsValue(value != null ? !value : true);
            }
        });
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
        Boolean oldValue = value;
        value = (Boolean) aValue;
        if (value) {
            element.setClassName("btn btn-active");
        } else {
            element.setClassName("btn btn-default");
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
        published.opaque = true;

        Object.defineProperty(published, "text", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusToggleButton::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusToggleButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
        Object.defineProperty(published, "icon", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusToggleButton::getImageResource()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusToggleButton::setImageResource(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
            }
        });
        Object.defineProperty(published, "iconTextGap", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusToggleButton::getIconTextGap()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusToggleButton::setIconTextGap(I)(aValue);
            }
        });
        Object.defineProperty(published, "horizontalTextPosition", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusToggleButton::getHorizontalTextPosition()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusToggleButton::setHorizontalTextPosition(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalTextPosition", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusToggleButton::getVerticalTextPosition()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusToggleButton::setVerticalTextPosition(I)(+aValue);
            }
        });

        Object.defineProperty(published, "horizontalAlignment", {
            get : function() {
                    return aWidget.@com.eas.widgets.PlatypusToggleButton::getHorizontalAlignment()();
            },
            set : function(aValue) {
                    aWidget.@com.eas.widgets.PlatypusToggleButton::setHorizontalAlignment(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalAlignment", {
            get : function() {
                    return aWidget.@com.eas.widgets.PlatypusToggleButton::getVerticalAlignment()();
            },
            set : function(aValue) {
                    aWidget.@com.eas.widgets.PlatypusToggleButton::setVerticalAlignment(I)(+aValue);
            }
        });
        Object.defineProperty(published, "selected", {
            get : function() {
                    return aWidget.@com.eas.widgets.PlatypusToggleButton::getPlainValue()();
            },
            set : function(aValue) {
                    aWidget.@com.eas.widgets.PlatypusToggleButton::setPlainValue(Z)(aValue!=null?aValue:false);
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
