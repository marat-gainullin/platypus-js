/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.threetier.Response;

/**
 *
 * @author pk, mg refactoring
 */
public class RowsetResponse extends Response {

    private Rowset rowset;
    private Fields expectedFields;
    private int updateCount;

    public RowsetResponse(long requestId, Rowset aRowset, int aUpdateCount) {
        super(requestId);
        rowset = aRowset;
        updateCount = aUpdateCount;
    }

    public RowsetResponse(long requestId, Rowset aRowset, int aUpdateCount, Fields aExpectedFields) {
        this(requestId, aRowset, aUpdateCount);
        expectedFields = aExpectedFields;
    }

    public Rowset getRowset() {
        return rowset;
    }

    public void setRowset(Rowset aValue) {
        rowset = aValue;
    }

    public Fields getExpectedFields() {
        return expectedFields;
    }

    public void setExpectedFields(Fields aFields) {
        expectedFields = aFields;
    }

    /**
     * @return the updateCount
     */
    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int aValue) {
        updateCount = aValue;
    }

    @Override
    public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }
}
