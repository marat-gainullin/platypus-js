package com.eas.widgets.boxes;

import com.eas.bound.JsArrayList;
import java.util.List;

import com.eas.client.IdGenerator;
import com.eas.client.converters.StringValueConverter;
import com.eas.core.Logger;
import com.eas.core.Utils;
import com.eas.ui.CommonResources;
import com.eas.ui.JavaScriptObjectKeyProvider;
import com.eas.ui.PublishedCell;
import com.eas.widgets.WidgetsUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;

public class DropDownListDecoratorField extends ValueDecoratorField {

    protected static final String CUSTOM_DROPDOWN_CLASS = "combo-field-custom-dropdown";
    protected JavaScriptObjectKeyProvider rowKeyProvider = new JavaScriptObjectKeyProvider();
    protected String keyForNullValue = String.valueOf(IdGenerator.genId());
    protected String emptyText;
    protected JavaScriptObject displayList;
    protected String displayField;
    protected HandlerRegistration boundToList;
    protected HandlerRegistration boundToListElements;
    protected Runnable onRedraw;
    protected InputElement nonListMask = Document.get().createTextInputElement();
    protected OptionElement nullOption;

    protected boolean list = true;

    public DropDownListDecoratorField() {
        super(new DropDownList());
        DropDownList box = (DropDownList) decorated;
        box.addItem("...", keyForNullValue, null, "");
        nullOption = box.getItem(0);
        decorated.getElement().addClassName(CUSTOM_DROPDOWN_CLASS);
        decorated.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        CommonResources.INSTANCE.commons().ensureInjected();
        box.getElement().addClassName(CommonResources.INSTANCE.commons().withoutDropdown());
        nonListMask.setReadOnly(true);
        nonListMask.addClassName(CommonResources.INSTANCE.commons().borderSized());
        nonListMask.getStyle().setPosition(Style.Position.ABSOLUTE);
        nonListMask.getStyle().setDisplay(Style.Display.NONE);
        nonListMask.getStyle().setTop(0, Style.Unit.PX);
        nonListMask.getStyle().setLeft(0, Style.Unit.PX);
        nonListMask.getStyle().setWidth(100, Style.Unit.PCT);
        nonListMask.getStyle().setHeight(100, Style.Unit.PCT);
        nonListMask.getStyle().setFloat(Style.Float.RIGHT); // Same as decorated within decorator

        getElement().insertFirst(nonListMask);

        selectButton.addClassName("decorator-select-combo");
        clearButton.addClassName("decorator-clear-combo");
    }

    @Override
    public void setFocus(boolean focused) {
        if (list) {
            super.setFocus(focused);
        } else {
            nonListMask.focus();
        }
    }

    public Runnable getOnRedraw() {
        return onRedraw;
    }

    public void setOnRedraw(Runnable aValue) {
        onRedraw = aValue;
    }

    @Override
    public void setJsValue(Object value) {
        Object oldValue = getJsValue();
        super.setJsValue(value);
        if (oldValue != value) {
            nonListMask.setValue(calcLabel((JavaScriptObject) value));
        }
    }

    @Override
    protected void rebind() {
        super.rebind();
        rebindList();
    }

    protected void rebindList() {
        try {
            DropDownList listBox = (DropDownList) decorated;
            listBox.setSelectedIndex(-1);
            listBox.clear();
            listBox.addItem(calcLabel(null), keyForNullValue, null, "");
            listBox.setSelectedIndex(0);
            if (list) {
                boolean valueMet = false;
                if (displayList != null) {
                    JavaScriptObject value = (JavaScriptObject) getJsValue();
                    List<JavaScriptObject> jsoList = new JsArrayList(displayList);
                    for (int i = 0; i < jsoList.size(); i++) {
                        JavaScriptObject item = jsoList.get(i);
                        if (item != null) {
                            String itemLabel = calcLabel(item);
                            listBox.addItem(itemLabel, item.hashCode() + "", item, "");
                            if (value == item) {
                                valueMet = true;
                                listBox.setSelectedIndex(listBox.getCount() - 1);
                            }
                        }
                    }
                }
                if (!valueMet) {
                    clearValue();
                }
            }
            nonListMask.setValue(calcLabel((JavaScriptObject) getJsValue()));
            if (onRedraw != null) {
                onRedraw.run();
            }
        } catch (Exception ex) {
            Logger.severe(ex);
        }
    }

    public String calcLabel(JavaScriptObject aValue) {
        String nullText = emptyText != null && !emptyText.isEmpty() ? emptyText : "...";
        String labelText = aValue != null
                ? new StringValueConverter().convert(Utils.getPathData(aValue, displayField))
                : nullText;
        PublishedCell cell = WidgetsUtils.calcValuedPublishedCell(published, onRender, aValue,
                labelText != null ? labelText : "", null);
        if (cell != null && cell.getDisplay() != null && !cell.getDisplay().isEmpty()) {
            labelText = cell.getDisplay();
        }
        return labelText;
    }

    public String getText() {
        if (list) {
            int selectedOption = ((DropDownList) decorated).getSelectedIndex();
            return ((DropDownList) decorated).getItem(selectedOption).getInnerText();
        } else {
            return nonListMask.getValue()/* value in nonListMask is exactly text */;
        }
    }

    @Override
    public void setText(String text) {
    }

    @Override
    public String getEmptyText() {
        return emptyText;
    }

    @Override
    public void setEmptyText(String aValue) {
        emptyText = aValue;
        nullOption.setInnerText(aValue);
        WidgetsUtils.applyEmptyText(getElement(), emptyText);
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean aValue) {
        if (list != aValue) {
            list = aValue;
            DropDownList listBox = (DropDownList) decorated;
            if (list) {
                listBox.getElement().addClassName(CUSTOM_DROPDOWN_CLASS);
                listBox.getElement().getStyle().clearVisibility();
                nonListMask.getStyle().setDisplay(Style.Display.NONE);
                selectButton.addClassName("decorator-select-combo");
                clearButton.addClassName("decorator-clear-combo");
                nonListMask.removeClassName("form-control");
            } else {
                listBox.getElement().removeClassName(CUSTOM_DROPDOWN_CLASS);
                listBox.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
                nonListMask.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
                selectButton.removeClassName("decorator-select-combo");
                clearButton.removeClassName("decorator-clear-combo");
                nonListMask.addClassName("form-control");
            }
            rebindList();
        }
    }

    public JavaScriptObject getDisplayList() {
        return displayList;
    }

    protected Scheduler.ScheduledCommand changesQueued;

    protected void enqueueListChanges() {
        changesQueued = new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                if (changesQueued == this) {
                    changesQueued = null;
                    rebindList();
                }
            }
        };
        Scheduler.get().scheduleDeferred(changesQueued);
    }

    protected boolean readdQueued;

    private void enqueueListReadd() {
        readdQueued = true;
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                if (readdQueued) {
                    readdQueued = false;
                    if (boundToListElements != null) {
                        boundToListElements.removeHandler();
                        boundToListElements = null;
                    }
                    if (displayList != null) {
                        boundToListElements = Utils.listenElements(displayList, new Utils.OnChangeHandler() {

                            @Override
                            public void onChange(JavaScriptObject anEvent) {
                                enqueueListChanges();
                            }
                        });
                    }
                    rebindList();
                }
            }
        });
    }

    protected void bindList() {
        if (displayList != null) {
            boundToList = Utils.listenPath(displayList, "length", new Utils.OnChangeHandler() {

                @Override
                public void onChange(JavaScriptObject anEvent) {
                    enqueueListReadd();
                }
            });
            enqueueListReadd();
        }
    }

    protected void unbindList() {
        if (boundToList != null) {
            boundToList.removeHandler();
            boundToList = null;
            enqueueListReadd();
        }
    }

    public void setDisplayList(JavaScriptObject aValue) {
        if (displayList != aValue) {
            unbindList();
            displayList = aValue;
            bindList();
        }
    }

    public String getDisplayField() {
        return displayField;
    }

    public void setDisplayField(String aValue) {
        if (displayField != null ? !displayField.equals(aValue) : aValue != null) {
            unbindList();
            displayField = aValue;
            bindList();
        }
    }

    @Override
    public void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(DropDownListDecoratorField aWidget, JavaScriptObject aPublished);
}
