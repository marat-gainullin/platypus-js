/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.columns.model;

import com.eas.client.forms.api.components.model.ModelWidget;
import com.eas.dbcontrols.grid.rt.HasStyle;
import com.eas.gui.CascadedStyle;
import com.eas.script.HasPublished;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Model's column, bound with some additional information about how to achieve
 * model data. It holds a reference to corresponding view's column. It is used
 * when model's columns are added or removed. In such case, view's column's
 * model index become invalid and view columns need to be reindexed.
 *
 * @author mg
 */
public class ModelColumn implements HasStyle, HasPublished {

    protected String field;

    protected JSObject onRender;
    protected JSObject onSelect;
    protected boolean readOnly;
    protected HasStyle styleHost;
    protected JSObject published;
    protected ModelWidget view;
    protected ModelWidget editor;

    /**
     * Constructs model column, bounded with view's column.
     *
     * @param aOnRender
     * @param aOnSelect
     * @param aReadOnly
     * @param aStyleHost
     * @param aView
     * @param aEditor
     */
    public ModelColumn(JSObject aOnRender, JSObject aOnSelect, boolean aReadOnly, HasStyle aStyleHost, ModelWidget aView, ModelWidget aEditor) {
        super();
        onRender = aOnRender;
        onSelect = aOnSelect;
        readOnly = aReadOnly;
        styleHost = aStyleHost;
        view = aView;
        editor = aEditor;
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
        view = aView;
    }

    public ModelWidget getEditor() {
        return editor;
    }

    public void setEditor(ModelWidget aEditor) {
        if (editor != aEditor) {
            editor = aEditor;
            editor.setOnSelect(onSelect);
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
    public JSObject getOnRender() {
        return onRender;
    }

    public void setOnRender(JSObject aValue) {
        onRender = aValue;
    }

    /**
     * Returns script handler, used for select a value of the cell.
     *
     * @return
     */
    public JSObject getOnSelect() {
        return onSelect;
    }

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

    @Override
    public JSObject getPublished() {
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        published = aValue;
    }
}
