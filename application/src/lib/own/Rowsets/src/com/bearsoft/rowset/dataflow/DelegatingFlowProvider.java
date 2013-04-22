/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.TransactionListener.Registration;
import com.bearsoft.rowset.metadata.Parameters;
import java.util.List;

/**
 *
 * @author mg
 */
public class DelegatingFlowProvider implements FlowProvider {

    protected FlowProvider delegate;

    public DelegatingFlowProvider(FlowProvider aDelegate) {
        super();
        delegate = aDelegate;
    }

    @Override
    public String getEntityId() {
        return delegate.getEntityId();
    }

    @Override
    public Rowset refresh(Parameters aParams) throws Exception {
        return delegate.refresh(aParams);
    }

    @Override
    public Rowset nextPage() throws Exception {
        return delegate.nextPage();
    }

    @Override
    public int getPageSize() {
        return delegate.getPageSize();
    }

    @Override
    public void setPageSize(int aPageSize) {
        delegate.setPageSize(aPageSize);
    }

    @Override
    public boolean isProcedure() {
        return delegate.isProcedure();
    }

    @Override
    public void setProcedure(boolean aProcedure) {
        delegate.setProcedure(aProcedure);
    }

    @Override
    public List<Change> getChangeLog() {
        return delegate.getChangeLog();
    }

    @Override
    public Registration addTransactionListener(TransactionListener aListener) {
        return delegate.addTransactionListener(aListener);
    }
}
