/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import com.bearsoft.gui.grid.header.BindedColumn;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.table.TableColumn;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Model's column, bound with some additional information about how to achieve
 * model data. It holds a reference to corresponding view's column. It is used
 * when model's columns are added or removed. In such case, view's column's
 * model index become invalid and view columns need to be reindexed.
 *
 * @author mg
 */
public class ModelColumn extends TableColumn implements HasPublished, BindedColumn {

    private static JSObject publisher;
    //
    protected String name;
    protected MultiLevelHeader header;
    //
    protected boolean moveable = true;
    protected boolean readonly;
    protected boolean sortable = true;
    protected String field;
    //
    protected JSObject onRender;
    protected JSObject onSelect;
    protected boolean readOnly;
    protected JSObject published;
    protected ModelWidget view;
    protected ModelWidget editor;
    protected boolean visible = true;
    //
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    // Temporarily stored values involved in columns show/hide process.
    protected int tempWidth;
    protected int tempMinWidth;
    protected int tempMaxWidth;
    protected boolean tempResizable = true;
    protected boolean tempMoveable = true;

    /**
     * Constructs model column, bounded with view's column.
     *
     * @param aName
     * @param aOnRender
     * @param aOnSelect
     * @param aReadOnly
     * @param aView
     * @param aEditor
     */
    public ModelColumn(String aName, JSObject aOnRender, JSObject aOnSelect, boolean aReadOnly, ModelWidget aView, ModelWidget aEditor) {
        super();
        name = aName;
        onRender = aOnRender;
        onSelect = aOnSelect;
        readOnly = aReadOnly;
        view = aView;
        super.setCellRenderer(aView);
        editor = aEditor;
        super.setCellEditor(editor);
        super.setWidth(50);

        tempWidth = super.getWidth();
        tempMinWidth = super.getMinWidth();
        tempMaxWidth = super.getMaxWidth();
        tempResizable = super.getResizable();
        tempMoveable = moveable;

        super.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            changeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        });
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return changeSupport.getPropertyChangeListeners();
    }

    public ModelColumn() {
        this("column", null, null, false, null, null);
    }

    /**
     *
     * @return 
     */
    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setField(String aValue) {
        if (field == null ? aValue != null : !field.equals(aValue)) {
            field = aValue;
        }
    }

    public ModelWidget getView() {
        return view;
    }

    public void setView(ModelWidget aView) {
        if (view != aView) {
            view = aView;
            super.setCellRenderer(aView);
        }
    }

    public ModelWidget getEditor() {
        return editor;
    }

    public void setEditor(ModelWidget aEditor) {
        if (editor != aEditor) {
            editor = aEditor;
            editor.setOnSelect(onSelect);
            super.setCellEditor(editor);
        }
    }

    /**
     * Returns script handler, used for calculate cell's data, display value and
     * style.
     *
     * @return
     */
    @ScriptFunction(jsDoc = "On render column event.")
    public JSObject getOnRender() {
        return onRender;
    }

    @ScriptFunction
    public void setOnRender(JSObject aValue) {
        onRender = aValue;
    }

    /**
     * Returns script handler, used for select a value of the cell.
     *
     * @return
     */
    @ScriptFunction(jsDoc = "On select column event.")
    public JSObject getOnSelect() {
        return onSelect;
    }

    @ScriptFunction
    public void setOnSelect(JSObject aValue) throws Exception {
        if (onSelect != aValue) {
            onSelect = aValue;
            if (editor != null) {
                editor.setOnSelect(aValue);
            }
        }
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean aValue) {
        readOnly = aValue;
    }

    @ScriptFunction(jsDoc = "Determines if column is visible.")
    public boolean isVisible() {
        return visible;
    }

    @ScriptFunction
    public void setVisible(boolean aValue) {
        if (visible != aValue) {
            visible = aValue;
            if (visible) {
                showColumn();
            } else {
                hideColumn();
            }
        }
    }

    private void hideColumn() {
        tempWidth = super.getWidth();
        tempMinWidth = super.getMinWidth();
        tempMaxWidth = super.getMaxWidth();
        tempResizable = super.getResizable();
        tempMoveable = moveable;

        setResizeable(false);
        setMoveable(false);
        setMinWidth(0);
        setWidth(0);
        setMaxWidth(0);
    }

    private void showColumn() {
        //int oldWidth = getWidth();
        setMaxWidth(tempMaxWidth);
        setMinWidth(tempMinWidth);
        setResizeable(tempResizable);
        setMoveable(tempMoveable);
        setWidth(tempWidth);
    }

    @ScriptFunction(jsDoc = "Width of the column.")
    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @ScriptFunction
    @Override
    public void setWidth(int aValue) {
        if (isResizeable()) {
            super.setWidth(aValue);
            /*
             if (header != null) {
             List<ModelColumnsGroup> leaves = new ArrayList<>();
             MultiLevelHeader.achieveLeaves(group, leaves);
             header.setResizingColGroup(group);
             try {
             header.setPreferredWidth2LeafColGroups(leaves, oldValue, aValue);
             } finally {
             header.setResizingColGroup(null);
             }
             }
             */
        }
    }

    @ScriptFunction(jsDoc = "The title of the column.")
    public String getTitle() {
        return getHeaderValue() != null && getHeaderValue() instanceof String ? (String) getHeaderValue() : null;
    }

    @ScriptFunction
    public void setTitle(String aTitle) {
        setHeaderValue(aTitle);
    }

    public boolean isMoveable() {
        return moveable;
    }

    public void setMoveable(boolean aValue) {
        if (moveable != aValue) {
            boolean oldValue = moveable;
            moveable = aValue;
            changeSupport.firePropertyChange("moveable", oldValue, moveable);
        }
    }

    @ScriptFunction(jsDoc = "Determines if column is resizeable.")
    public boolean isResizeable() {
        return super.isResizable;
    }

    @ScriptFunction
    public void setResizeable(boolean aValue) {
        if (super.getResizable() != aValue) {
            boolean oldValue = super.getResizable();
            super.isResizable = aValue;
            changeSupport.firePropertyChange("resizable", oldValue, aValue);
        }
    }

    @ScriptFunction(jsDoc = "Determines if column is readonly.")
    public boolean isReadonly() {
        return readonly;
    }

    @ScriptFunction
    public void setReadonly(boolean aValue) {
        if (readonly != aValue) {
            boolean oldValue = readonly;
            readonly = aValue;
            changeSupport.firePropertyChange("readonly", oldValue, readonly);
        }
    }

    @ScriptFunction(jsDoc = "Determines if column is sortable.")
    public boolean isSortable() {
        return sortable;
    }

    @ScriptFunction
    public void setSortable(boolean aValue) {
        if (sortable != aValue) {
            boolean oldValue = sortable;
            sortable = aValue;
            changeSupport.firePropertyChange("sortable", oldValue, sortable);
        }
    }

    @ScriptFunction(jsDoc = "The name of the column.")
    public String getName() {
        return name;
    }

    public void setName(String aValue) {
        name = aValue;
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject jsColumn) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = jsColumn;
        /*
         if(view != null)
         view.injectPublished(published);
         if(editor != null)
         editor.injectPublished(published);
         */
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
