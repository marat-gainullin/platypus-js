/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.columns;

import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.rt.columns.model.ModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsModel;
import com.eas.gui.CascadedStyle;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ScriptableColumn {

    protected MultiLevelHeader header;
    protected GridColumnsGroup group;
    protected TableColumnModel viewModel;
    protected RowsetsModel rowsModel;
    protected DbGridColumn designColumn;
    protected ModelColumn modelColumn;
    protected TableColumn viewColumn;
    protected Scriptable published;
    protected int viewIndex = -1;
    protected boolean visible;
    // Temporarily stored values involved in columns show/hide process.
    protected int tempWidth;
    protected int tempMinWidth;
    protected int tempMaxWidth;
    protected boolean tempResizable = true;
    protected boolean tempMoveable = true;

    public ScriptableColumn(DbGridColumn aDesignColumn, ModelColumn aModelColumn, TableColumn aViewColumn, int aViewIndex, TableColumnModel aViewModel, RowsetsModel aRowsModel, Map<TableColumn, GridColumnsGroup> aLeaves2Groups) {
        super();
        designColumn = aDesignColumn;
        modelColumn = aModelColumn;
        viewColumn = aViewColumn;
        viewIndex = aViewIndex;
        viewModel = aViewModel;
        rowsModel = aRowsModel;
        visible = designColumn == null ? true : designColumn.isVisible();
        group = aLeaves2Groups != null ? aLeaves2Groups.get(viewColumn) : null;

        if (group != null) {
            tempWidth = group.getWidth();
            tempMinWidth = group.getMinWidth();
            tempMaxWidth = group.getMaxWidth();
            tempResizable = group.isResizeable();
            tempMoveable = group.isMoveable();
        }
        if (!visible) {
            hideColumn();
        }
    }

    public void setHeader(MultiLevelHeader aValue) {
        header = aValue;
    }

    @ScriptFunction(jsDoc = "Determines if column is visible.")
    public boolean isVisible() {
        return visible;
    }

    @ScriptFunction
    public void setVisible(boolean aValue) {
        if (visible != aValue) {
            visible = aValue;
            if (visible)// true, was false
            {
                showColumn();
            } else // false, was true
            {
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

    public TableColumn getViewColumn() {
        return viewColumn;
    }

    @ScriptFunction(jsDoc = "Width of the column.")
    public int getWidth() {
        return group.getWidth();
    }

    @ScriptFunction
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
        if (viewColumn != null && viewColumn.getHeaderValue() != null && viewColumn.getHeaderValue() instanceof String) {
            return (String) viewColumn.getHeaderValue();
        }
        return null;
    }

    @ScriptFunction
    public void setTitle(String aTitle) {
        if (viewColumn != null) {
            viewColumn.setHeaderValue(aTitle);
        }
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
        modelColumn.setReadOnly(aValue);
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
        if (designColumn != null) {
            return designColumn.getName();
        }
        return null;
    }

    //@ScriptFunction(jsDoc = "Column's header style.")
    public CascadedStyle getHeaderStyle() {
        return group.getStyle();
    }

    @ScriptFunction(jsDoc = "On select column event.")
    public Function getOnSelect() {
        return modelColumn.getSelectHandler();
    }

    @ScriptFunction
    public void setOnSelect(Function aHandler) throws Exception {
        modelColumn.setSelectHandler(aHandler);
    }

    @ScriptFunction(jsDoc = "On render column event.")
    public Function getOnRender() {
        return modelColumn.getCellsHandler();
    }

    @ScriptFunction
    public void setOnRender(Function aHandler) {
        modelColumn.setCellsHandler(aHandler);
    }

    public Scriptable getPublished() {
        return published;
    }

    public void setPublished(Scriptable jsColumn) {
        published = jsColumn;
        if (viewColumn != null) {
            if (viewColumn.getCellEditor() instanceof ScalarDbControl) {
                ((ScalarDbControl) viewColumn.getCellEditor()).setEventsThis(published);
            }
            if (viewColumn.getCellRenderer() instanceof ScalarDbControl) {
                ((ScalarDbControl) viewColumn.getCellRenderer()).setEventsThis(published);
            }
        }
        if (modelColumn != null) {
            modelColumn.setEventsThis(published);
        }
    }
}
