/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.models;

import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class TreedRowsRowsetListener {

    protected ArrayTreedModel model;
    protected List<JSObject> oldRows = new ArrayList<>();

    public TreedRowsRowsetListener(ArrayTreedModel aModel) {
        super();
        model = aModel;
    }

    /*
    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        if (!event.isAjusting()) {
            model.fireElementsRemoved(Collections.singletonList(event.getRow().getPublished()));
            oldRows = toJs(rowset.getCurrent());
        }
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        oldRows.add(event.getRow().getPublished());
        if (!event.isAjusting()) {
            model.fireElementsAdded(Collections.singletonList(event.getRow().getPublished()));
        }
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        model.fireElementsRemoved(oldRows);
        List<JSObject> newRows = toJs(rowset.getCurrent());
        oldRows = newRows;
        model.fireElementsAdded(newRows);
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        model.fireElementsRemoved(oldRows);
        List<JSObject> newRows = toJs(rowset.getCurrent());
        oldRows = newRows;
        model.fireElementsAdded(newRows);
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        model.fireElementsRemoved(oldRows);
        List<JSObject> newRows = toJs(rowset.getCurrent());
        oldRows = newRows;
        model.fireElementsAdded(newRows);
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        model.fireElementsRemoved(oldRows);
        List<JSObject> newRows = toJs(rowset.getCurrent());
        oldRows = newRows;
        model.fireElementsAdded(newRows);
    }

    private List<JSObject> toJs(List<Row> current) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */
}
