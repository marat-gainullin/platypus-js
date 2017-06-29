package com.eas.grid.columns;

import com.eas.core.HasPublished;
import com.eas.core.Utils;
import com.eas.grid.PathComparator;
import com.eas.ui.HasJsValue;
import com.eas.ui.PublishedCell;
import com.eas.ui.Widget;
import com.eas.widgets.WidgetsUtils;
import com.eas.widgets.boxes.BooleanDecoratorField;
import com.eas.widgets.boxes.CheckBox;
import com.eas.widgets.boxes.RadioButton;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableColElement;
import com.google.gwt.user.client.ui.HasText;
import com.eas.core.Logger;
import com.eas.core.XElement;
import com.eas.grid.Grid;
import com.eas.grid.HeaderView;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import java.util.List;

// TODO: Check tree expandable cell decorations like left paddnig according to deepness and plus / minus icon and open / closed folder icons
public class Column implements HasPublished {

    private TableColElement element = Document.get().createColElement();
    private String radioGroup = "group-" + Document.get().createUniqueId();
    private StyleElement columnRule = Document.get().createStyleElement();
    protected String field;
    protected String sortField;
    protected Widget editor;
    /**
     * Minimum column width while resizing by a user.
     */
    protected double minWidth = 15;
    /**
     * Maximum column width while resizing by a user.
     */
    protected double maxWidth = Integer.MAX_VALUE;
    protected double width = 75;
    protected boolean readonly;
    protected boolean visible = true;
    protected boolean selectOnly;
    protected boolean sortable;
    protected int indent = 24;
    protected PathComparator comparator;
    private HeaderView header;
    protected JavaScriptObject published;
    protected JavaScriptObject onRender;
    protected JavaScriptObject onSelect;
    protected Grid grid;

    public Column() {
        super();
    }

    public TableColElement getElement() {
        return element;
    }

    public HeaderView getHeader() {
        return header;
    }

    public void setHeader(HeaderView header) {
        this.header = header;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public StyleElement getColumnRule() {
        return columnRule;
    }

    public PathComparator getComparator() {
        return comparator;
    }

    public void sort() {
        sort(true);
    }

    public void sort(boolean fireEvent) {
        comparator = new PathComparator(sortField != null && !sortField.isEmpty() ? sortField : field, true);
        if (fireEvent) {
            grid.sort();
        }
    }

    public void sortDesc() {
        sortDesc(true);
    }

    public void sortDesc(boolean fireEvent) {
        comparator = new PathComparator(sortField != null && !sortField.isEmpty() ? sortField : field, false);
        if (fireEvent) {
            grid.sort();
        }
    }

    public void unsort() {
        unsort(true);
    }

    public void unsort(boolean fireEvent) {
        comparator = null;
        if (fireEvent) {
            grid.sort();
        }
    }

    public String getField() {
        return field;
    }

    public void setField(String aValue) {
        if (field == null ? aValue != null : !field.equals(aValue)) {
            field = aValue;
        }
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String aValue) {
        if (sortField == null ? aValue != null : !sortField.equals(aValue)) {
            sortField = aValue;
        }
    }

    public double getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(double aValue) {
        minWidth = aValue;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(double aValue) {
        maxWidth = aValue;
    }

    public Object getValue(JavaScriptObject anElement) {
        if (anElement != null && field != null && !field.isEmpty()) {
            return Utils.getPathData(anElement, field);
        } else {
            return null;
        }
    }

    public void update(JavaScriptObject anElement, JavaScriptObject value) {
        if (anElement != null && field != null && !field.isEmpty() && !readonly) {
            Utils.setPathData(anElement, field, value);
        }
    }

    public void render(int viewIndex, JavaScriptObject dataRow, TableCellElement viewCell) {
        List<JavaScriptObject> path = grid.buildPathTo(dataRow);
        int padding = indent * path.size();
        Object value = getValue(dataRow);
        String text;
        if (value == null) {
            text = null; // No native rendering for null values
        } else {
            text = String.valueOf(value);
            if (editor != null) {
                ((HasJsValue) editor).setJsValue(value);
                if (editor instanceof BooleanDecoratorField || editor instanceof CheckBox) {
                    InputElement checkbox = Document.get().createCheckInputElement();
                    if (Boolean.TRUE.equals(value)) {
                        checkbox.setChecked(true);
                    } else {
                        checkbox.setChecked(false);
                        text = "";
                    }
                    viewCell.appendChild(checkbox);
                    checkbox.<XElement>cast().addEventListener(BrowserEvents.CHANGE, new XElement.NativeHandler() {
                        @Override
                        public void on(NativeEvent evt) {
                            update(dataRow, !Boolean.TRUE.equals(value)); //  !!!value
                        }

                    });
                } else if (editor instanceof RadioButton) {
                    InputElement radiobutton = Document.get().createRadioInputElement(radioGroup);
                    if (Boolean.TRUE.equals(value)) {
                        radiobutton.setChecked(true);
                    } else {
                        radiobutton.setChecked(false);
                        text = "";
                    }
                    viewCell.appendChild(radiobutton);
                    radiobutton.<XElement>cast().addEventListener(BrowserEvents.CHANGE, new XElement.NativeHandler() {
                        @Override
                        public void on(NativeEvent evt) {
                            if (Boolean.FALSE.equals(value)) {
                                update(dataRow, true);
                            }
                        }

                    });
                } else {
                    text = ((HasText) editor).getText();
                    viewCell.setInnerText(text);
                }
            }
        }
        if (onRender != null || grid.getOnRender() != null) { // User's rendering for all values, including null
            try {
                // TODO: Check if abstract 'cell' object is needed at all  
                PublishedCell cellToRender = WidgetsUtils.calcGridPublishedCell(published, onRender != null ? onRender : grid.getOnRender(), dataRow, field, text, viewCell, viewIndex, null);
                if (cellToRender != null) {
                    if (cellToRender.getDisplayCallback() == null) {
                        cellToRender.setDisplayCallback(new Runnable() {
                            @Override
                            public void run() {
                                cellToRender.styleToElement(viewCell);
                                viewCell.setInnerText(cellToRender.getDisplay());
                            }
                        });
                    }
                    cellToRender.styleToElement(viewCell);
                    viewCell.setInnerText(cellToRender.getDisplay());
                }
            } catch (Exception ex) {
                Logger.severe(ex);
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean aValue) {
        if (visible != aValue) {
            visible = aValue;
            if (visible) {
                columnRule.setInnerHTML("");
            } else {
                columnRule.setInnerHTML("display: none");
            }
        }
    }

    public double getDesignedWidth() {
        return width;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double aValue) {
        width = aValue;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean aValue) {
        readonly = aValue;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean aValue) {
        sortable = aValue;
    }

    public boolean isSelectOnly() {
        return selectOnly;
    }

    public void setSelectOnly(boolean aValue) {
        selectOnly = aValue;
    }

    public JavaScriptObject getOnRender() {
        return onRender;
    }

    public void setOnRender(JavaScriptObject aValue) {
        if (onRender != aValue) {
            onRender = aValue;
        }
    }

    public JavaScriptObject getOnSelect() {
        return onSelect;
    }

    public void setOnSelect(JavaScriptObject aValue) {
        if (onSelect != aValue) {
            onSelect = aValue;
        }
    }

    public Widget getEditor() {
        return editor;
    }

    public void setEditor(Widget aEditor) {
        if (editor != aEditor) {
            editor = aEditor;
        }
    }

    /*
    public void setEditor(ValueDecoratorField aEditor) {
        if (editor != aEditor) {
            if (editor != null) {
                editor.setOnRender(null);
                editor.setOnSelect(null);
                if (editor instanceof DropDownListDecoratorField) {
                    ((DropDownListDecoratorField) editor).setOnRedraw(null);
                }
            }
            editor = aEditor;
            if (editor != null) {
                editor.setOnRender(onRender);
                editor.setOnSelect(onSelect);
                if (editor instanceof DropDownListDecoratorField) {
                    ((DropDownListDecoratorField) editor).setOnRedraw(new Runnable() {

                        @Override
                        public void run() {
                            enqueueGridRedraw();
                        }

                    });
                }
            }
            if (editor instanceof BooleanDecoratorField) {
                ((TreeExpandableCell<JavaScriptObject, Object>) getCell()).setCell(new CheckBoxCell() {
                    @Override
                    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Object value, NativeEvent event, ValueUpdater<Object> valueUpdater) {
                        String type = event.getType();
                        if (BrowserEvents.CHANGE.equals(type) && (readonly || !grid.isEditable())) {
                            InputElement input = parent.<XElement>cast().firstChildByTagName("input").cast();
                            boolean checked = input.isChecked();
                            input.setChecked(!checked);
                        } else {
                            super.onBrowserEvent(context, parent, value, event, valueUpdater);
                        }
                    }

                    @Override
                    public void render(com.google.gwt.cell.client.Cell.Context context, Object aValue, SafeHtmlBuilder sb) {
                        try {
                            ValueDecoratorField modelEditor = getEditor();
                            aValue = modelEditor.convert(Utils.toJava(aValue));
                            super.render(context, aValue, sb);
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }

                });
            } else {
                ((TreeExpandableCell<JavaScriptObject, Object>) getCell()).setCell(new RenderedEditorCell<Object>(editor) {
                    @Override
                    protected void renderCell(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
                        try {
                            ValueDecoratorField modelEditor = getEditor();
                            value = modelEditor.convert(Utils.toJava(value));
                            super.renderCell(context, value, sb);
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }

                    @Override
                    public void startEditing(com.google.gwt.cell.client.Cell.Context context, Element aBoxPositionTemplate, Element aBoxParent, Object value, ValueUpdater<Object> valueUpdater,
                            Runnable onEditorClose) {
                        try {
                            ValueDecoratorField modelEditor = getEditor();
                            value = modelEditor.convert(Utils.toJava(value));
                            grid.setActiveEditor(getEditor());
                            super.startEditing(context, aBoxPositionTemplate, aBoxParent, value, valueUpdater, onEditorClose);
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }
                });
                RenderedEditorCell<Object> cell = (RenderedEditorCell<Object>) getTargetCell();
                cell.setReadonly(new CellHasReadonly() {

                    @Override
                    public boolean isReadonly() {
                        return readonly || !grid.isEditable();
                    }

                });
                cell.setOnEditorClose(new RenderedEditorCell.EditorCloser() {
                    @Override
                    public void closed(Element aTable) {
                        grid.setActiveEditor(null);
                        final GridSection<?> toFocus = GridSection.getInstance(aTable);
                        if (toFocus != null) {
                            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                                @Override
                                public void execute() {
                                    toFocus.setFocus(true);
                                }
                            });
                        }
                    }
                });
                cell.setRenderer(new CellRenderer<Object>() {

                    @Override
                    public boolean render(Context context, String aId, Object value, SafeHtmlBuilder sb) {
                        JavaScriptObject onRender = Column.this.getOnRender() != null ? Column.this.getOnRender() : Column.this.getGrid().getOnRender();
                        if (onRender != null) {
                            ValueDecoratorField modelEditor = getEditor();
                            value = modelEditor.convert(Utils.toJava(value));
                            String display = null;
                            Object oldValue = ((HasValue<Object>) editor).getValue();
                            ((HasValue<Object>) editor).setValue(value);
                            try {
                                display = ((HasText) editor).getText();
                            } finally {
                                // We have to take care about old value because
                                // of edited cell while grid rendering.
                                ((HasValue<Object>) editor).setValue(oldValue);
                            }
                            SafeHtmlBuilder lsb = new SafeHtmlBuilder();
                            PublishedCell cellToRender = calcContextPublishedCell(Column.this.getPublished(), onRender, context, Column.this.getField(), display);
                            if (cellToRender != null) {
                                if (cellToRender.getDisplay() != null) {
                                    display = cellToRender.getDisplay();
                                }
                            }

                            if (display == null) {
                                lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
                            } else {
                                lsb.append(SafeHtmlUtils.fromString(display));
                            }
                            grid.complementPublishedStyle(cellToRender);
                            String decorId = renderDecorated(lsb, aId, cellToRender, sb);
                            if (cellToRender != null) {
                                if (context instanceof RenderedCellContext) {
                                    ((RenderedCellContext) context).setPublishedCell(cellToRender);
                                }
                                Column.bindDisplayCallback(cellToRender, decorId);
                                Column.bindIconCallback(cellToRender, decorId);
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }

                });
            }
        }
    }

    public static PublishedCell calcContextPublishedCell(JavaScriptObject aThis, JavaScriptObject aOnRender, com.google.gwt.cell.client.Cell.Context context, String aField, String aDisplay) {
        if (aOnRender != null) {
            Object key = context.getKey();
            JavaScriptObject renderedElement = key instanceof JavaScriptObject ? (JavaScriptObject) key : null;
            if (renderedElement != null) {
                Object data = aField != null && !aField.isEmpty() ? Utils.getPathData(renderedElement, aField) : null;
                PublishedCell cell = WidgetsUtils.publishCell(data, aDisplay);
                JsArrayMixed args = JavaScriptObject.createArray().cast();
                args.push(EventsPublisher.publishOnRenderEvent(aThis, null, null, renderedElement, cell));
                aOnRender.<JsObject>cast().apply(aThis, args);
                return cell;
            }
        }
        return null;
    }

    protected static void bindDisplayCallback(final PublishedCell aCell, final String aTargetElementId) {
        aCell.setDisplayCallback(new Runnable() {
            @Override
            public void run() {
                Element identifiedTextSection = Document.get().getElementById(aTargetElementId);
                if (identifiedTextSection != null) {
                    aCell.styleToElementBackgroundToTd(identifiedTextSection);
                    String toRender = aCell.getDisplay();
                    if (toRender == null) {
                        toRender = "&#160;";
                    }
                    identifiedTextSection.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString(toRender));
                }
            }
        });
    }

    protected static void bindIconCallback(final PublishedCell aCell, final String aTargetElementId) {
        aCell.setIconCallback(new Runnable() {

            @Override
            public void run() {
                Element identifiedTextSection = Document.get().getElementById(aTargetElementId);
                if (identifiedTextSection != null) {
                    ImageElement iconSection = (ImageElement) identifiedTextSection.getPreviousSiblingElement();
                    if (iconSection != null) {
                        if (aCell.getIcon() != null) {
                            iconSection.setSrc(aCell.getIcon().getSafeUri().asString());
                        } else {
                            iconSection.setSrc(null);
                        }
                    }
                }
            }

        });
    }
     */
    @Override
    public JavaScriptObject getPublished() {
        return published;
    }

    @Override
    public void setPublished(JavaScriptObject aValue) {
        if (published != aValue) {
            published = aValue;
        }
    }

}
