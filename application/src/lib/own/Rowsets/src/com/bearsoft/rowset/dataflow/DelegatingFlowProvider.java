/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Parameters;
import java.util.List;
import java.util.function.Consumer;

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

    public FlowProvider getDelegate() {
        return delegate;
    }

    @Override
    public String getEntityId() {
        return delegate.getEntityId();
    }

    @Override
    public Rowset refresh(Parameters aParams, Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return delegate.refresh(aParams, onSuccess, onFailure);
    }

    @Override
    public Rowset nextPage(Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return delegate.nextPage(onSuccess, onFailure);
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

}
