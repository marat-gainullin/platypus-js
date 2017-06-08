package com.eas.widgets.boxes;

import com.eas.core.Logger;
import com.eas.core.Utils;
import com.eas.core.XElement;
import com.eas.ui.BlurEvent;
import com.eas.ui.CommonResources;
import com.eas.ui.FocusEvent;
import com.eas.ui.Focusable;
import com.eas.ui.HasBinding;
import com.eas.ui.HasCustomEditing;
import com.eas.ui.HasEmptyText;
import com.eas.ui.HasJsValue;
import com.eas.ui.HasOnRender;
import com.eas.ui.HasOnSelect;
import com.eas.ui.HasReadonly;
import com.eas.ui.PublishedCell;
import com.eas.ui.Widget;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.BlurHandler;
import com.eas.ui.events.FocusHandler;
import com.eas.ui.events.HasActionHandlers;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.HasFocusHandlers;
import com.eas.ui.events.HasBlurHandlers;
import com.eas.ui.events.HasKeyDownHandlers;
import com.eas.ui.events.HasKeyPressHandlers;
import com.eas.ui.events.HasKeyUpHandlers;
import com.eas.ui.events.KeyDownEvent;
import com.eas.ui.events.KeyDownHandler;
import com.eas.ui.events.KeyPressEvent;
import com.eas.ui.events.KeyPressHandler;
import com.eas.ui.events.KeyUpEvent;
import com.eas.ui.events.KeyUpHandler;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.eas.widgets.WidgetsUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasText;

/**
 *
 * @author mg
 */
public abstract class ValueDecoratorField extends Widget implements HasJsValue, Focusable, HasText,
        HasFocusHandlers, HasBlurHandlers, HasActionHandlers,
        HasKeyDownHandlers, HasKeyUpHandlers, HasKeyPressHandlers,
        HasValueChangeHandlers, HasBinding, HasEmptyText,
        HasOnRender, HasOnSelect, HasCustomEditing, HasReadonly {

    protected Widget decorated;
    protected boolean nullable = true;
    protected boolean editable = true;
    protected Element btnSelect = Document.get().createDivElement();
    protected Element btnClear = Document.get().createDivElement();
    protected JavaScriptObject onRender;
    protected JavaScriptObject onSelect;
    protected boolean selectOnly;
    // binding
    protected JavaScriptObject data;
    protected String field;
    protected PublishedCell cellToRender;

    public ValueDecoratorField(Widget aDecorated) {
        super();
        // TODO: Check all widgets against element.setClassName() in derived classes
        element.setClassName("form-control");
        element.addClassName("decorator");
        assert decorated instanceof HasJsValue;
        assert decorated instanceof HasValueChangeHandlers;
        decorated = aDecorated;
        CommonResources.INSTANCE.commons().ensureInjected();
        decorated.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
        ((HasValueChangeHandlers) decorated).addValueChangeHandler(new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                setClearButtonVisible(nullable && event.getNewValue() != null);
            }
        });

        btnSelect.setClassName("decorator-select");
        btnSelect.getStyle().setDisplay(Style.Display.NONE);
        btnSelect.getStyle().setHeight(100, Style.Unit.PCT);
        btnSelect.getStyle().setPosition(Style.Position.RELATIVE);
        btnSelect.getStyle().setZIndex(1); // FireFox hides this in ModelCombo without such setting
        btnClear.setClassName("decorator-clear");
        btnClear.getStyle().setDisplay(Style.Display.NONE);
        btnClear.getStyle().setHeight(100, Style.Unit.PCT);
        btnClear.getStyle().setPosition(Style.Position.RELATIVE);
        btnClear.getStyle().setZIndex(1); // FireFox hides this in ModelCombo without such setting

        // TODO: Ensure action event occurs when user clicks on select or on clear button.
        btnSelect.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                selectValue();
            }
        });
        btnClear.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                clearValue();
                setFocus(true);
            }
        });
    }

    public Widget getDecorated() {
        return decorated;
    }

    @Override
    public String getEmptyText() {
        return decorated.getElement().getAttribute("placeholder");
    }

    @Override
    public void setEmptyText(String aValue) {
        decorated.getElement().setAttribute("placeholder", aValue);
    }

    @Override
    public String getText() {
        if (decorated instanceof HasText) {
            return ((HasText) decorated).getText();
        } else {
            return null;
        }
    }

    @Override
    public void setText(String text) {
        if (decorated instanceof HasText) {
            ((HasText) decorated).setText(text);
        }
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        if (decorated instanceof HasKeyDownHandlers) {
            return ((HasKeyDownHandlers) decorated).addKeyDownHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    event.setSource(ValueDecoratorField.this);
                    handler.onKeyDown(event);
                }

            });
        } else {
            return new EmptyHandlerRegistration();
        }
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        if (decorated instanceof HasKeyPressHandlers) {
            return ((HasKeyPressHandlers) decorated).addKeyPressHandler(new KeyPressHandler() {
                @Override
                public void onKeyPress(KeyPressEvent event) {
                    event.setSource(ValueDecoratorField.this);
                    handler.onKeyPress(event);
                }

            });
        } else {
            return new EmptyHandlerRegistration();
        }
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        if (decorated instanceof HasKeyUpHandlers) {
            return ((HasKeyUpHandlers) decorated).addKeyUpHandler(new KeyUpHandler() {
                @Override
                public void onKeyUp(KeyUpEvent event) {
                    event.setSource(ValueDecoratorField.this);
                    handler.onKeyUp(event);
                }

            });
        } else {
            return new EmptyHandlerRegistration();
        }
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean aValue) {
        if (nullable != aValue) {
            nullable = aValue;
            setClearButtonVisible(nullable && getJsValue() != null);
        }
    }

    @Override
    public void setFocus(boolean focused) {
        if (decorated instanceof Focusable) {
            ((Focusable) decorated).setFocus(focused);
        }
    }

    @Override
    public int getTabIndex() {
        if (decorated instanceof Focusable) {
            return ((Focusable) decorated).getTabIndex();
        } else {
            return -1;
        }
    }

    @Override
    public void setTabIndex(int index) {
        if (decorated instanceof Focusable) {
            ((Focusable) decorated).setTabIndex(index);
        }
    }

    protected boolean isSelectButtonVisible() {
        return !Style.Display.NONE.getCssName().equalsIgnoreCase(btnSelect.getStyle().getDisplay());
    }

    protected void setSelectButtonVisible(boolean aValue) {
        if (isSelectButtonVisible() != aValue) {
            if (aValue) {
                btnSelect.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            } else {
                btnSelect.getStyle().setDisplay(Style.Display.NONE);
            }
        }
    }

    protected boolean isClearButtonVisible() {
        return !Style.Display.NONE.getCssName().equalsIgnoreCase(btnClear.getStyle().getDisplay());
    }

    protected void setClearButtonVisible(boolean aValue) {
        if (isClearButtonVisible() != aValue) {
            if (aValue) {
                btnClear.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            } else {
                btnClear.getStyle().setDisplay(Style.Display.NONE);
            }
        }
    }

    @Override
    public Object getJsValue() {
        return ((HasJsValue) decorated).getJsValue();
    }

    @Override
    public void setJsValue(Object value) {
        ((HasJsValue) decorated).setJsValue(value);
        setClearButtonVisible(nullable && ((HasJsValue) decorated).getJsValue() != null);
        try {
            if (onRender != null && data != null && field != null && !field.isEmpty()) {
                cellToRender = WidgetsUtils.calcStandalonePublishedCell(published, onRender, data, field, getText(), cellToRender);
            }
            if (cellToRender != null) {
                if (cellToRender.getDisplayCallback() == null) {
                    cellToRender.setDisplayCallback(new Runnable() {
                        @Override
                        public void run() {
                            cellToRender.styleToElement(getElement());
                        }
                    });
                }
                cellToRender.styleToElement(getElement());
            }
        } catch (Exception ex) {
            Logger.severe(ex);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler handler) {
        return ((HasValueChangeHandlers) decorated).addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                setClearButtonVisible(nullable && ((HasJsValue) decorated).getJsValue() != null);
                event.setSource(ValueDecoratorField.this);
                handler.onValueChange(event);
            }

        });
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        if (decorated instanceof HasFocusHandlers) {
            return ((HasFocusHandlers) decorated).addFocusHandler(new FocusHandler() {
                @Override
                public void onFocus(FocusEvent event) {
                    event.setSource(ValueDecoratorField.this);
                    handler.onFocus(event);
                }
            });
        } else {
            return new EmptyHandlerRegistration();
        }
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        if (decorated instanceof HasBlurHandlers) {
            return ((HasBlurHandlers) decorated).addBlurHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    event.setSource(ValueDecoratorField.this);
                    handler.onBlur(event);
                }
            });
        } else {
            return new EmptyHandlerRegistration();
        }
    }

    @Override
    public HandlerRegistration addActionHandler(ActionHandler handler) {
        if (decorated instanceof HasActionHandlers) {
            return ((HasActionHandlers) decorated).addActionHandler(new ActionHandler() {
                @Override
                public void onAction(ActionEvent event) {
                    event.setSource(ValueDecoratorField.this);
                    handler.onAction(event);
                }
            });
        } else {
            return new EmptyHandlerRegistration();
        }
    }

    @Override
    public void setReadonly(boolean aValue) {
        decorated.getElement().setPropertyBoolean("readOnly", aValue);
    }

    @Override
    public boolean isReadonly() {
        return decorated.getElement().getPropertyBoolean("readOnly");
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(boolean aValue) {
        if (editable != aValue) {
            editable = aValue;
            setReadonly(!editable || selectOnly);
        }
    }

    @Override
    public boolean isSelectOnly() {
        return selectOnly;
    }

    @Override
    public void setSelectOnly(boolean aValue) {
        if (selectOnly != aValue) {
            selectOnly = aValue;
            setReadonly(!editable || selectOnly);
        }
    }

    @Override
    public JavaScriptObject getOnSelect() {
        return onSelect;
    }

    @Override
    public void setOnSelect(JavaScriptObject aValue) {
        if (onSelect != aValue) {
            onSelect = aValue;
            setSelectButtonVisible(onSelect != null);
        }
    }

    @Override
    public JavaScriptObject getOnRender() {
        return onRender;
    }

    @Override
    public void setOnRender(JavaScriptObject aValue) {
        if (aValue != onRender) {
            onRender = aValue;
        }
    }

    protected void clearValue() {
        setJsValue(null);
    }

    protected void selectValue() {
        try {
            Utils.executeScriptEventVoid(published, onSelect, published);
        } catch (Exception ex) {
            Logger.severe(ex);
        }
    }

    protected HandlerRegistration boundToData;
    protected HandlerRegistration boundToValue;
    protected boolean settingValueFromJs;
    protected boolean settingValueToJs;

    protected void bind() {
        if (data != null && field != null && !field.isEmpty()) {
            boundToData = Utils.listenPath(data, field, new Utils.OnChangeHandler() {

                @Override
                public void onChange(JavaScriptObject anEvent) {
                    rebind();
                }
            });
            Object oData = Utils.getPathData(data, field);
            try {
                setJsValue(oData);
            } catch (Exception ex) {
                Logger.severe(ex);
            }
            ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {

                @Override
                public void onValueChange(ValueChangeEvent event) {
                    if (!settingValueFromJs) {
                        settingValueToJs = true;
                        try {
                            Utils.setPathData(data, field, Utils.toJs(event.getNewValue()));
                        } finally {
                            settingValueToJs = false;
                        }
                    }
                }

            };
            boundToValue = addValueChangeHandler(valueChangeHandler);
        } else {
            try {
                setJsValue(null);
            } catch (Exception ex) {
                Logger.severe(ex);
            }
        }
    }

    protected void unbind() {
        if (boundToData != null) {
            boundToData.removeHandler();
            boundToData = null;
        }
        if (boundToValue != null) {
            boundToValue.removeHandler();
            boundToValue = null;
        }
    }

    protected void rebind() {
        if (!settingValueToJs) {
            settingValueFromJs = true;
            try {
                try {
                    Object pathData = Utils.getPathData(data, field);
                    setJsValue(pathData);
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
            } finally {
                settingValueFromJs = false;
            }
        }
    }

    @Override
    public JavaScriptObject getData() {
        return data;
    }

    @Override
    public void setData(JavaScriptObject aValue) {
        if (data != aValue) {
            unbind();
            data = aValue;
            bind();
        }
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setField(String aValue) {
        if (field == null ? aValue != null : !field.equals(aValue)) {
            unbind();
            field = aValue;
            bind();
        }
    }

    // TODO: Check all widgets against publish method polymophism
    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private static native void publish(Widget aWidget, JavaScriptObject aPublished)/*-{
        Object.defineProperty(aPublished, "onSelect", {
            get : function() {
                return aWidget.@com.eas.ui.HasOnSelect::getOnSelect()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasOnSelect::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "onRender", {
            get : function() {
                return aWidget.@com.eas.ui.HasOnRender::getOnRender()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasOnRender::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "editable", {
            get : function() {
                return aWidget.@com.eas.ui.HasCustomEditing::isEditable()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasCustomEditing::setEditable(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "nullable", {
            get : function() {
                return aWidget.@com.eas.widgets.boxes.DecoratorBox::isNullable()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.boxes.DecoratorBox::setNullable(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "selectOnly", {
            get : function() {
                return aWidget.@com.eas.ui.HasCustomEditing::isSelectOnly()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasCustomEditing::setSelectOnly(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "data", {
            get : function() {
                return aWidget.@com.eas.ui.HasBinding::getData()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasBinding::setData(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "field", {
            get : function() {
                return aWidget.@com.eas.ui.HasBinding::getField()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasBinding::setField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
    }-*/;

}
