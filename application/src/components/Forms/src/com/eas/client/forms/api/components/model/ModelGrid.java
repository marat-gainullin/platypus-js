/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.bearsoft.gui.grid.editing.InsettedEditor;
import com.bearsoft.rowset.Row;
import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.ControlsWrapper;
import com.eas.dbcontrols.CellRenderEvent;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.rt.columns.ScriptableColumn;
import com.eas.script.EventMethod;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.table.TableCellEditor;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelGrid extends Component<DbGrid> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that shows a data grid.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelGrid() {
        super();
        setDelegate(new DbGrid());
    }

    protected ModelGrid(DbGrid aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    @Override
    protected void setDelegate(DbGrid aDelegate) {
        super.setDelegate(aDelegate);
        if (delegate != null) {
            try {
                delegate.configure();
            } catch (Exception ex) {
                Logger.getLogger(ScalarModelComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static final String SHOW_HORIZONTAL_LINES_JSDOC = ""
            + "/**\n"
            + "* Determines if grid shows horizontal lines.\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_HORIZONTAL_LINES_JSDOC)
    public boolean getShowHorizontalLines() {
        return delegate.isShowHorizontalLines();
    }

    @ScriptFunction
    public void setShowHorizontalLines(boolean aValue) {
        delegate.setShowHorizontalLines(aValue);
    }
    private static final String SHOW_VERTICAL_LINES_JSDOC = ""
            + "/**\n"
            + "* Determines if grid shows vertical lines.\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_VERTICAL_LINES_JSDOC)
    public boolean getShowVerticalLines() {
        return delegate.isShowVerticalLines();
    }

    @ScriptFunction
    public void setShowVerticalLines(boolean aValue) {
        delegate.setShowVerticalLines(aValue);
    }
    private static final String ODD_ROW_COLOR_JSDOC = ""
            + "/**\n"
            + "* Odd rows color.\n"
            + "*/";

    @ScriptFunction(jsDoc = ODD_ROW_COLOR_JSDOC)
    public Color getOddRowsColor() {
        return delegate.getOddRowsColor();
    }

    @ScriptFunction
    public void setOddRowsColor(Color aValue) {
        delegate.setOddRowsColor(aValue);
    }
    private static final String SHOW_ODD_ROWS_IN_OTHER_COLOR_JSDOC = ""
            + "/**\n"
            + "* Determines if grid shows odd rows if other color.\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_ODD_ROWS_IN_OTHER_COLOR_JSDOC)
    public boolean getShowOddRowsInOtherColor() {
        return delegate.isShowOddRowsInOtherColor();
    }

    @ScriptFunction
    public void setShowOddRowsInOtherColor(boolean aValue) {
        delegate.setShowOddRowsInOtherColor(aValue);
    }
    private static final String GRID_COLOR_JSDOC = ""
            + "/**\n"
            + "* The color of the grid.\n"
            + "*/";

    @ScriptFunction(jsDoc = GRID_COLOR_JSDOC)
    public Color getGridColor() {
        return delegate.getGridColor();
    }

    @ScriptFunction
    public void setGridColor(Color aValue) {
        delegate.setGridColor(aValue);
    }
    private static final String ROWS_HEIGHT_JSDOC = ""
            + "/**\n"
            + "* The height of grid's rows.\n"
            + "*/";

    @ScriptFunction(jsDoc = ROWS_HEIGHT_JSDOC)
    public int getRowsHeight() {
        return delegate.getRowsHeight();
    }

    @ScriptFunction
    public void setRowsHeight(int aValue) {
        delegate.setRowsHeight(aValue);
    }
    private static final String ON_RENDER_JSDOC = ""
            + "/**\n"
            + " * General render event handler.\n"
            + " * This hanler be called on each cell's rendering in the case when no render handler is provided for the conrete column.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_RENDER_JSDOC)
    @EventMethod(eventClass = CellRenderEvent.class)
    public JSObject getOnRender() {
        return delegate.getGeneralOnRender();
    }

    @ScriptFunction
    public void setOnRender(JSObject aValue) {
        delegate.setGeneralOnRender(aValue);
    }
    private static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + "* Determines if gris cells are editable.\n"
            + "*/";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }

    @ScriptFunction
    public boolean getEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public boolean getInsertable() {
        return delegate.isInsertable();
    }
    private static final String INSERTABLE_JSDOC = ""
            + "/**\n"
            + "* Determines if grid allows row insertion.\n"
            + "*/";

    @ScriptFunction(jsDoc = INSERTABLE_JSDOC)
    public void setInsertable(boolean aValue) {
        delegate.setInsertable(aValue);
    }

    @ScriptFunction
    public boolean getDeletable() {
        return delegate.isDeletable();
    }
    private static final String DELETABLE_JSDOC = ""
            + "/**\n"
            + "* Determines if grid allows to delete rows.\n"
            + "*/";

    @ScriptFunction(jsDoc = DELETABLE_JSDOC)
    public void setDeletable(boolean aValue) {
        delegate.setDeletable(aValue);
    }
    private static final String SELECTED_JSDOC = ""
            + "/**\n"
            + "*  Gets the array of selected rows.\n"
            + "*/";

    @ScriptFunction(jsDoc = SELECTED_JSDOC)
    public List<Row> getSelected() throws Exception {
        return delegate.getSelected();
    }
    private static final String SELECT_JSDOC = ""
            + "/**\n"
            + " *  Gets the array of selected rows.\n"
            + " * @param instance Entity's instance to be selected.\n"
            + " */";

    @ScriptFunction(jsDoc = SELECT_JSDOC, params = {"instance"})
    public void select(Row aRow) throws Exception {
        delegate.select(aRow);
    }
    private static final String UNSELECT_JSDOC = ""
            + "/**\n"
            + " * Unselects the specified instance.\n"
            + " * @param instance Entity's instance to be unselected\n"
            + " */";

    @ScriptFunction(jsDoc = UNSELECT_JSDOC, params = {"instance"})
    public void unselect(Row aRow) throws Exception {
        delegate.unselect(aRow);
    }
    private static final String FIND_SOMETHING_JSDOC = ""
            + "/**\n"
            + "* Shows find dialog.\n"
            + "* @deprecated Use find() instead. \n"
            + "*/";

    @ScriptFunction(jsDoc = FIND_SOMETHING_JSDOC)
    public void findSomething() {
        delegate.findSomething();
    }
    private static final String FIND_JSDOC = ""
            + "/**\n"
            + "* Shows find dialog.\n"
            + "*/";

    @ScriptFunction(jsDoc = FIND_JSDOC)
    public void find() {
        delegate.findSomething();
    }
    private static final String CLEAR_SELECTION_JSDOC = ""
            + "/**\n"
            + "* Clears current selection.\n"
            + "*/";

    @ScriptFunction(jsDoc = CLEAR_SELECTION_JSDOC)
    public void clearSelection() {
        delegate.clearSelection();
    }

    private static final String MAKE_VISIBLE_JSDOC = ""
            + "/**\n"
            + "* Makes specified instance visible.\n"
            + "* @param instance Entity's instance to make visible.\n"
            + "* @param need2select true to select the instance (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = MAKE_VISIBLE_JSDOC, params = {"instance", "need2select"})
    public boolean makeVisible(Row aRow, Boolean need2Select) throws Exception {
        return delegate.makeVisible(aRow, need2Select != null ? need2Select : false);
    }
    private static final String CELLS_JSDOC = ""
            + "/**\n"
            + "* Gets all grid cells as an array.\n"
            + "* <b>WARNING!!! All cells will be copied.</b>\n"
            + "*/";

    @ScriptFunction(jsDoc = CELLS_JSDOC)
    public Object[] getCells() throws Exception {
        return delegate.getCells();
    }
    private static final String SELECTED_CELLS_JSDOC = ""
            + "/**\n"
            + "* Gets all grid selected cells as an array.\n"
            + "* <b>WARNING!!! All selected cells will be copied.</b>\n"
            + "*/";

    @ScriptFunction(jsDoc = SELECTED_CELLS_JSDOC)
    public Object[] getSelectedCells() throws Exception {
        return delegate.getSelectedCells();
    }
    private static final String COLUMNS_CELLS_JSDOC = ""
            + "/**\n"
            + "* Gets grid columns as an array.\n"
            + "*/";

    @ScriptFunction(jsDoc = COLUMNS_CELLS_JSDOC)
    public List<Object> getColumns() throws Exception {
        List<Object> columns = new ArrayList<>();
        // we have to preserve order of columns
        delegate.getScriptableColumns().forEach((scrCol) -> {
            columns.add(scrCol.getPublished());
        });
        return columns;
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
            JSObject jsPublished = (JSObject) published;
            delegate.getScriptableColumns().stream().forEach((ScriptableColumn aColumn) -> {
                if (aColumn.getDesignColumn() != null && aColumn.getDesignColumn().getControlInfo() != null) {
                    TableCellEditor cellEditor = aColumn.getViewColumn().getCellEditor();
                    if(cellEditor instanceof InsettedEditor){
                        cellEditor = ((InsettedEditor)cellEditor).unwrap();
                    }
                    if (cellEditor instanceof ScalarDbControl
                            && cellEditor instanceof JComponent) {
                        ControlsWrapper apiWrapper = new ControlsWrapper((JComponent)cellEditor);
                        aColumn.getDesignColumn().getControlInfo().accept(apiWrapper);
                        ScalarDbControl editorControl = (ScalarDbControl) cellEditor;
                        editorControl.injectPublished(apiWrapper.getResult().getPublished());
                    }
                }
                jsPublished.setMember(aColumn.getName(), aColumn.getPublished());
            });
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
