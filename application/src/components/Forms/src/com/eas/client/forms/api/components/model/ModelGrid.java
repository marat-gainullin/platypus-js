/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.client.forms.api.Component;
import com.eas.client.model.script.RowHostObject;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.script.ScriptFunction;
import java.awt.Color;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ModelGrid extends Component<DbGrid> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A model component that shows a data grid. \n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelGrid() {
        super();
        setDelegate(new DbGrid());
    }

    protected ModelGrid(DbGrid aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    private static final String SHOW_HORIZONTAL_LINES_JSDOC = "/**\n"
            + "* Determines if grid shows horizontal lines. \n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_HORIZONTAL_LINES_JSDOC)
    public boolean getShowHorizontalLines() {
        return delegate.isShowHorizontalLines();
    }

    @ScriptFunction
    public void setShowHorizontalLines(boolean aValue) {
        delegate.setShowHorizontalLines(aValue);
    }
    private static final String SHOW_VERTICAL_LINES_JSDOC = "/**\n"
            + "* Determines if grid shows vertical lines. \n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_VERTICAL_LINES_JSDOC)
    public boolean getShowVerticalLines() {
        return delegate.isShowVerticalLines();
    }

    @ScriptFunction
    public void setShowVerticalLines(boolean aValue) {
        delegate.setShowVerticalLines(aValue);
    }
    private static final String ODD_ROW_COLOR_JSDOC = "/**\n"
            + "* Odd rows color. \n"
            + "*/";

    @ScriptFunction(jsDoc = ODD_ROW_COLOR_JSDOC)
    public Color getOddRowsColor() {
        return delegate.getOddRowsColor();
    }

    @ScriptFunction
    public void setOddRowsColor(Color aValue) {
        delegate.setOddRowsColor(aValue);
    }
    private static final String SHOW_ODD_ROWS_IN_OTHER_COLOR_JSDOC = "/**\n"
            + "* Determines if grid shows odd rows if other color. \n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_ODD_ROWS_IN_OTHER_COLOR_JSDOC)
    public boolean getShowOddRowsInOtherColor() {
        return delegate.isShowOddRowsInOtherColor();
    }

    @ScriptFunction
    public void setShowOddRowsInOtherColor(boolean aValue) {
        delegate.setShowOddRowsInOtherColor(aValue);
    }
    private static final String GRID_COLOR_JSDOC = "/**\n"
            + "* The color of the grid. \n"
            + "*/";

    @ScriptFunction(jsDoc = GRID_COLOR_JSDOC)
    public Color getGridColor() {
        return delegate.getGridColor();
    }

    @ScriptFunction
    public void setGridColor(Color aValue) {
        delegate.setGridColor(aValue);
    }
    private static final String ROWS_HEIGHT_JSDOC = "/**\n"
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
    private static final String ON_RENDER_JSDOC = "/**\n"
            + "* General render event handler.\n"
            + " This hanler be called on each cell's rendering in the case when no render handler is provided for the conrete column.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_RENDER_JSDOC)
    public Function getOnRender() {
        return delegate.getGeneralRowFunction();
    }

    @ScriptFunction
    public void setOnRender(Function aValue) {
        delegate.setGeneralRowFunction(aValue);
    }
    private static final String EDITABLE_JSDOC = "/**\n"
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
    private static final String INSERTABLE_JSDOC = "/**\n"
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
    private static final String DELETABLE_JSDOC = "/**\n"
            + "* Determines if grid allows to delete rows.\n"
            + "*/";

    @ScriptFunction(jsDoc = DELETABLE_JSDOC)
    public void setDeletable(boolean aValue) {
        delegate.setDeletable(aValue);
    }
    private static final String SELECTED_JSDOC = "/**\n"
            + "*  Gets the array of selected rows.\n"
            + "*/";

    @ScriptFunction(jsDoc = SELECTED_JSDOC)
    public Scriptable getSelected() throws Exception {
        return delegate.getSelected();
    }
    private static final String SELECT_JSDOC = "/**\n"
            + "*  Gets the array of selected rows.\n"
            + "*/";

    @ScriptFunction(jsDoc = SELECT_JSDOC)
    public void select(RowHostObject aRow) throws Exception {
        delegate.select(aRow);
    }
    private static final String UNSELECT_JSDOC = "/**\n"
            + "* Unselects the specified row.\n"
            + "* @param row the row to be unselected\n"
            + "*/";

    @ScriptFunction(jsDoc = UNSELECT_JSDOC, params = {"row"})
    public void unselect(RowHostObject aRow) throws Exception {
        delegate.unselect(aRow);
    }
    private static final String FIND_SOMETHING_JSDOC = "/**\n"
            + "* Shows find dialog.\n"
            + "* @deprecated Use find() instead. \n"
            + "*/";

    @ScriptFunction(jsDoc = FIND_SOMETHING_JSDOC)
    public void findSomething() {
        delegate.findSomething();
    }
    private static final String FIND_JSDOC = "/**\n"
            + "* Shows find dialog.\n"
            + "*/";

    @ScriptFunction(jsDoc = FIND_JSDOC)
    public void find() {
        delegate.findSomething();
    }
    private static final String CLEAR_SELECTION_JSDOC = "/**\n"
            + "* Clears current selection.\n"
            + "*/";

    @ScriptFunction(jsDoc = CLEAR_SELECTION_JSDOC)
    public void clearSelection() {
        delegate.clearSelection();
    }

    @ScriptFunction(jsDoc = MAKE_VISIBLE_JSDOC)
    public boolean makeVisible(RowHostObject aRow) throws Exception {
        return delegate.makeVisible(aRow);
    }
    private static final String MAKE_VISIBLE_JSDOC = "/**\n"
            + "*  Makes specified row visible.\n"
            + "* @param row the row to make visible\n"
            + "* @param need2select true to select the row (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = MAKE_VISIBLE_JSDOC)
    public boolean makeVisible(RowHostObject aRow, boolean need2Select) throws Exception {
        return delegate.makeVisible(aRow, need2Select);
    }
    private static final String CELLS_JSDOC = "/**\n"
            + "* Gets all grid cells as an array.\n"
            + "* <b>WARNING!!! All cells will be copied.</b>\n"
            + "*/";

    @ScriptFunction(jsDoc = CELLS_JSDOC)
    public Scriptable getCells() throws Exception {
        return delegate.getCells();
    }
    private static final String SELECTED_CELLS_JSDOC = "/**\n"
            + "* Gets all grid selected cells as an array.\n"
            + "* <b>WARNING!!! All selected cells will be copied.</b>\n"
            + "*/";

    @ScriptFunction(jsDoc = SELECTED_CELLS_JSDOC)
    public Scriptable getSelectedCells() throws Exception {
        return delegate.getSelectedCells();
    }
    private static final String COLUMNS_CELLS_JSDOC = "/**\n"
            + "* Gets grid columns as an array.\n"
            + "*/";

    @ScriptFunction(jsDoc = COLUMNS_CELLS_JSDOC)
    public Scriptable getColumns() throws Exception {
        return delegate.getColumnsScriptView();
    }
}
