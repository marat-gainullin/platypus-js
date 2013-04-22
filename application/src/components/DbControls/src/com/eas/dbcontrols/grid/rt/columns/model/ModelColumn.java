/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.columns.model;

import com.bearsoft.rowset.Rowset;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.grid.rt.HasStyle;
import com.eas.gui.CascadedStyle;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * The base clas for table's model's column classes.
 *
 * @author mg
 */
public class ModelColumn implements HasStyle {

    protected Function cellsHandler;
    protected Function selectHandler;
    protected Rowset rowset;
    protected boolean readOnly;
    protected HasStyle styleHost;
    protected Scriptable eventsThis;
    protected ScalarDbControl view;
    protected ScalarDbControl editor;

    public ModelColumn(Rowset aRowset, Function aCellsHandler, Function aSelectHandler, boolean aReadOnly, HasStyle aStyleHost, ScalarDbControl aView, ScalarDbControl aEditor) {
        super();
        rowset = aRowset;
        cellsHandler = aCellsHandler;
        selectHandler = aSelectHandler;
        readOnly = aReadOnly;
        styleHost = aStyleHost;
        view = aView;
        editor = aEditor;
    }

    public ScalarDbControl getView() {
        return view;
    }

    public void setView(ScalarDbControl aView) {
        view = aView;
    }

    public ScalarDbControl getEditor() {
        return editor;
    }

    public void setEditor(ScalarDbControl aEditor) {
        editor = aEditor;
    }

    @Override
    public CascadedStyle getStyle() {
        return styleHost != null ? styleHost.getStyle() : null;
    }

    /**
     * Returns rowset to achieve data from for this column.
     *
     * @return Rowset, to achieve data from.
     */
    public Rowset getRowset() {
        return rowset;
    }

    /**
     * Returns script handler, used for calculate cell's data, display value and
     * style.
     *
     * @return
     */
    public Function getCellsHandler() {
        return cellsHandler;
    }

    public void setCellsHandler(Function aValue) {
        cellsHandler = aValue;
    }

    /**
     * Returns script handler, used for select a value of the cell.
     */
    public Function getSelectHandler() {
        return selectHandler;
    }

    public void setSelectHandler(Function aValue) throws Exception {
        selectHandler = aValue;
        if (editor != null) {
            editor.extraCellControls(selectHandler, editor.haveNullerAction());
        }
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean aValue) {
        readOnly = aValue;
    }

    public Scriptable getEventsThis() {
        return eventsThis;
    }

    public void setEventsThis(Scriptable aValue) {
        eventsThis = aValue;
    }
}
