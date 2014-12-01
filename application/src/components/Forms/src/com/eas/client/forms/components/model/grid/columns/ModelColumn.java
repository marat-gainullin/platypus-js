/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.HasStyle;
import com.eas.gui.CascadedStyle;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
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
public class ModelColumn extends TableColumn implements HasStyle, HasPublished {

    private static JSObject publisher;
    //
    protected String name;
    //
    protected MultiLevelHeader header;
    protected GridColumnsGroup group;
    //
    protected String field;

    protected JSObject onRender;
    protected JSObject onSelect;
    protected boolean readOnly;
    protected HasStyle styleHost;
    protected JSObject published;
    protected ModelWidget view;
    protected ModelWidget editor;
    protected boolean visible = true;
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
     * @param aStyleHost
     * @param aView
     * @param aEditor
     * @param aGroup
     */
    public ModelColumn(String aName, JSObject aOnRender, JSObject aOnSelect, boolean aReadOnly, HasStyle aStyleHost, ModelWidget aView, ModelWidget aEditor, GridColumnsGroup aGroup) {
        super();
        name = aName;
        onRender = aOnRender;
        onSelect = aOnSelect;
        readOnly = aReadOnly;
        styleHost = aStyleHost;
        view = aView;
        super.setCellRenderer(aView);
        editor = aEditor;
        super.setCellEditor(editor);

        group = aGroup;
        tempWidth = group.getWidth();
        tempMinWidth = group.getMinWidth();
        tempMaxWidth = group.getMaxWidth();
        tempResizable = group.isResizeable();
        tempMoveable = group.isMoveable();
    }

    public ModelColumn() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns a column number, bounded to this model column.
     *
     * @return Rowset's column number. 1-based.
     */
    public String getField() {
        return field;
    }

    public void setField(String aValue) {
        field = aValue;
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

    @Override
    public CascadedStyle getStyle() {
        return styleHost != null ? styleHost.getStyle() : null;
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
        tempWidth = group.getWidth();
        tempMinWidth = group.getMinWidth();
        tempMaxWidth = group.getMaxWidth();
        tempResizable = group.isResizeable();
        tempMoveable = group.isMoveable();

        group.setResizeable(false);
        group.setMoveable(false);
        group.setMinWidth(0);
        group.setWidth(0);
        group.setMaxWidth(0);
    }

    private void showColumn() {
        //int oldWidth = getWidth();
        group.setMaxWidth(tempMaxWidth);
        group.setMinWidth(tempMinWidth);
        group.setResizeable(tempResizable);
        group.setMoveable(tempMoveable);
        setWidth(tempWidth);
    }

    @ScriptFunction(jsDoc = "Width of the column.")
    @Override
    public int getWidth() {
        return group.getWidth();
    }

    @ScriptFunction
    @Override
    public void setWidth(int aValue) {
        if (group.isResizeable()) {
            int oldValue = getWidth();
            if (header != null) {
                List<GridColumnsGroup> leaves = new ArrayList<>();
                MultiLevelHeader.achieveLeaves(group, leaves);
                header.setResizingColGroup(group);
                try {
                    header.setPreferredWidth2LeafColGroups(leaves, oldValue, aValue);
                } finally {
                    header.setResizingColGroup(null);
                }
            }
        }
    }

    @ScriptFunction(jsDoc = "The title of the column.")
    public String getTitle() {
        if (getHeaderValue() != null && getHeaderValue() instanceof String) {
            return (String) getHeaderValue();
        }
        return null;
    }

    @ScriptFunction
    public void setTitle(String aTitle) {
        setHeaderValue(aTitle);
    }

    @ScriptFunction(jsDoc = "Determines if column is resizeable.")
    public boolean isResizeable() {
        return group.isResizeable();
    }

    @ScriptFunction
    public void setResizeable(boolean aValue) {
        group.setResizeable(aValue);
    }

    @ScriptFunction(jsDoc = "Determines if column is readonly.")
    public boolean isReadonly() {
        return group.isReadonly();
    }

    @ScriptFunction
    public void setReadonly(boolean aValue) {
        group.setReadonly(aValue);
    }

    @ScriptFunction(jsDoc = "Determines if column is sortable.")
    public boolean isSortable() {
        return group.isSortable();
    }

    @ScriptFunction
    public void setSortable(boolean aValue) {
        group.setSortable(aValue);
    }

    @ScriptFunction(jsDoc = "The name of the column.")
    public String getName() {
        return name;
    }

    //@ScriptFunction(jsDoc = "Column's header style.")
    public CascadedStyle getHeaderStyle() {
        return group.getStyle();
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
