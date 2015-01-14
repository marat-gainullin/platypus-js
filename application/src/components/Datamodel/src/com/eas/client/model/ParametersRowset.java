/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ParametersRowset extends Rowset {

    protected class ParametersRowWrapper extends Row {

        ParametersRowWrapper() {
            super("params", params);
        }

        @Override
        public Object getColumnObject(int colIndex) throws InvalidColIndexException {
            return params.get(colIndex).getValue();
        }

        @Override
        public boolean setColumnObject(int aColIndex, Object aColValue) throws RowsetException {
            if (!smartEquals(getColumnObject(aColIndex), aColValue)) {
                Object oldColValue = getColumnObject(aColIndex);
                //if (ParametersRowset.this.rowsetChangeSupport.fireWillChangeEvent(ParametersRowWrapper.this, aColIndex, oldColValue, aColValue)) {
                    params.get(aColIndex).setValue(aColValue);
                    //ParametersRowset.this.rowsetChangeSupport.fireRowChangedEvent(ParametersRowWrapper.this, aColIndex, oldColValue);
                    return true;
                //}
            }
            return false;
        }

        @Override
        public Object[] getOriginalValues() {
            Object[] vals = new Object[params.getParametersCount()];
            for (int i = 0; i < vals.length; i++) {
                vals[i] = params.get(i + 1).getValue();
            }
            return vals;
        }

        @Override
        public Object[] getCurrentValues() {
            return getOriginalValues();
        }
    }
    protected Parameters params = null;
    protected Row paramRow = null;

    public ParametersRowset(Parameters aParams) {
        super(aParams);
        params = aParams;
        paramRow = new ParametersRowWrapper();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public ParametersRowset createCopy() {
        return new ParametersRowset(params.copy());
    }

    @Override
    public Row getCurrentRow() {
        return paramRow;
    }

    @Override
    public Row getRow(int aRowNumber) {
        return getCurrentRow();
    }

    @Override
    public List<Row> getCurrent() {
        List<Row> rows = new ArrayList<>();
        rows.add(paramRow);
        return rows;
    }

    @Override
    public void setCurrent(List<Row> aCurrent) {
    }

    @Override
    public void insert(Row toInsert, boolean aAjusting) throws RowsetException {
    }

    @Override
    public void insert(Row toInsert, boolean aAjusting, Object... initingValues) throws RowsetException {
    }

    @Override
    public void delete(Set<Row> rows2Delete) throws RowsetException {
    }

    @Override
    public void deleteAll() throws RowsetException {
    }

    @Override
    protected void generateDelete(Row aRow) {
    }

    @Override
    protected void generateInsert(Row aRow) {
    }

}
