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
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ScriptableColumn implements HasPublished {

    private static JSObject publisher;
    //
    protected MultiLevelHeader header;
    protected GridColumnsGroup group;
    protected TableColumnModel viewModel;
    protected RowsetsModel rowsModel;
    // design
    protected DbGridColumn designColumn;
    // runtime
    protected ModelColumn modelColumn;
    // view
    protected TableColumn viewColumn;
    protected Object published;
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
    public JSObject getOnSelect() {
        return modelColumn.getSelectHandler();
    }

    @ScriptFunction
    public void setOnSelect(JSObject aHandler) throws Exception {
        modelColumn.setSelectHandler(aHandler);
    }

    @ScriptFunction(jsDoc = "On render column event.")
    public JSObject getOnRender() {
        return modelColumn.getCellsHandler();
    }

    @ScriptFunction
    public void setOnRender(JSObject aHandler) {
        modelColumn.setCellsHandler(aHandler);
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(Object jsColumn) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = jsColumn;
        if (viewColumn != null) {
            if (viewColumn.getCellEditor() instanceof ScalarDbControl) {
                ((ScalarDbControl) viewColumn.getCellEditor()).injectPublished(published);
            }
            if (viewColumn.getCellRenderer() instanceof ScalarDbControl) {
                ((ScalarDbControl) viewColumn.getCellRenderer()).injectPublished(published);
            }
        }
        if (modelColumn != null) {
            modelColumn.setPublished(published);
        }
    }
    
    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
