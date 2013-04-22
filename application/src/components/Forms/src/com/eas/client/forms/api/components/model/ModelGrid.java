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

    protected ModelGrid(DbGrid aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelGrid() {
        super();
        setDelegate(new DbGrid());
    }

    @ScriptFunction(jsDoc = "/**\n * Determines if grid shows horizontal lines.\n */")
    public boolean isShowHorizontalLines() {
        return delegate.isShowHorizontalLines();
    }
    
    @ScriptFunction
    public void setShowHorizontalLines(boolean aValue) {
        delegate.setShowHorizontalLines(aValue);
    }

    @ScriptFunction(jsDoc = "/**\n * Determines if grid shows vertical lines.\n */")
    public boolean isShowVerticalLines() {
        return delegate.isShowVerticalLines();
    }
    
    @ScriptFunction
    public void setShowVerticalLines(boolean aValue) {
        delegate.setShowVerticalLines(aValue);
    }

    @ScriptFunction(jsDoc = "/**\n * Odd rows color.\n */")
    public Color getOddRowsColor() {
        return delegate.getOddRowsColor();
    }

    @ScriptFunction
    public void setOddRowsColor(Color aValue) {
        delegate.setOddRowsColor(aValue);
    }

    @ScriptFunction(jsDoc = "/**\n * Determines if grid shows odd rows if other color.\n */")
    public boolean isShowOddRowsInOtherColor() {
        return isShowOddRowsInOtherColor();
    }

    @ScriptFunction
    public void setShowOddRowsInOtherColor(boolean aValue) {
        delegate.setShowOddRowsInOtherColor(aValue);
    }

    @ScriptFunction(jsDoc = "/**\n * The color of the grid.\n */")
    public Color getGridColor() {
        return delegate.getGridColor();
    }

    @ScriptFunction
    public void setGridColor(Color aValue) {
        delegate.setGridColor(aValue);
    }

    @ScriptFunction(jsDoc = "/**\n * The height of grid's rows.\n */")
    public int getRowsHeight() {
        return delegate.getRowsHeight();
    }

    @ScriptFunction
    public void setRowsHeight(int aValue) {
        delegate.setRowsHeight(aValue);
    }

    @ScriptFunction
    public Function getOnRender()
    {
        return delegate.getGeneralRowFunction();
    }
    
    @ScriptFunction(jsDoc = "/**\n * General onRender event handler. Will be used with any column, which have no such handler. Return success status.\n */")
    public void setOnRender(Function aValue)
    {
        delegate.setGeneralRowFunction(aValue);
    }
    
    @ScriptFunction(jsDoc = "/**\n * Determines if gris cells are editable.\n */")
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }

    @ScriptFunction
    public boolean isEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public boolean isInsertable() {
        return delegate.isInsertable();
    }

    @ScriptFunction(jsDoc = "/**\n * Determines if grid allows row insertion.\n */")
    public void setInsertable(boolean aValue) {
        delegate.setInsertable(aValue);
    }

    @ScriptFunction
    public boolean isDeletable() {
        return delegate.isDeletable();
    }
    
    @ScriptFunction(jsDoc = "vDetermines if grid allows to delete rows.\n */")
    public void setDeletable(boolean aValue) {
        delegate.setDeletable(aValue);
    }
    
    @ScriptFunction(jsDoc = "/**\n * Gets array of selected rows.\n */")
    public Scriptable getSelected() throws Exception {
        return delegate.getSelected();
    }

    @ScriptFunction(jsDoc = "/**\n * Selects specified row.\n */")
    public void select(RowHostObject aRow) throws Exception {
        delegate.select(aRow);
    }

    @ScriptFunction(jsDoc = "/**\n * Unselects specified row.\n */")
    public void unselect(RowHostObject aRow) throws Exception {
        delegate.unselect(aRow);
    }

    @ScriptFunction(jsDoc = "/**\n * Shows find dialog.\n */")
    public void findSomething() {
        delegate.findSomething();
    }

    @ScriptFunction(jsDoc = "/**\n * Clears selection.\n */")
    public void clearSelection() {
        delegate.clearSelection();
    }

    @ScriptFunction(jsDoc = "/**\n * Makes specified row visible.\n */")
    public boolean makeVisible(RowHostObject aRow) throws Exception {
        return delegate.makeVisible(aRow);
    }

    @ScriptFunction(jsDoc = "/**\n * Makes specified row visible and selects it if second papameter is set to true.\n */")
    public boolean makeVisible(RowHostObject aRow, boolean need2Select) throws Exception {
        return delegate.makeVisible(aRow, need2Select);
    }
    
    @ScriptFunction(jsDoc = "/**\n * \n * Gets all grid cells as an array.\n * <b>WARNING!!! All cells will be copied.</b>\n */")
    public Scriptable getCells() throws Exception {
        return delegate.getCells();
    }
    
    @ScriptFunction(jsDoc = "/**\n * \n * Gets selected grid cells as an array.\n * <b>WARNING!!! All cells will be copied.</b>\n */")
    public Scriptable getSelectedCells() throws Exception {
        return delegate.getSelectedCells();
    }
    
    @ScriptFunction(jsDoc = "/**\n * \n * Gets grid columns as an array.\n */")
    public Scriptable getColumns() throws Exception {
        return delegate.getColumnsScriptView();
    }    
}
