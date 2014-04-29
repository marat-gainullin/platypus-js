/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.columns.model;

import com.bearsoft.rowset.Rowset;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.grid.rt.HasStyle;
import com.eas.gui.CascadedStyle;
import com.eas.script.HasPublished;
import jdk.nashorn.api.scripting.JSObject;

/**
 * The base clas for table's model's column classes.
 *
 * @author mg
 */
public class ModelColumn implements HasStyle, HasPublished {

    protected JSObject onRender;
    protected JSObject onSelect;
    protected Rowset rowset;
    protected boolean readOnly;
    protected HasStyle styleHost;
    protected Object published;
    protected ScalarDbControl view;
    protected ScalarDbControl editor;

    public ModelColumn(Rowset aRowset, JSObject aOnRender, JSObject aOnSelect, boolean aReadOnly, HasStyle aStyleHost, ScalarDbControl aView, ScalarDbControl aEditor) {
        super();
        rowset = aRowset;
        onRender = aOnRender;
        onSelect = aOnSelect;
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
    public JSObject getCellsHandler() {
        return onRender;
    }

    public void setCellsHandler(JSObject aValue) {
        onRender = aValue;
    }

    /**
     * Returns script handler, used for select a value of the cell.
     */
    public JSObject getSelectHandler() {
        return onSelect;
    }

    public void setSelectHandler(JSObject aValue) throws Exception {
        onSelect = aValue;
        if (editor != null) {
            editor.extraCellControls(onSelect, editor.haveNullerAction());
        }
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean aValue) {
        readOnly = aValue;
    }

    @Override
    public Object getPublished() {
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        published = aValue;
    }
}
