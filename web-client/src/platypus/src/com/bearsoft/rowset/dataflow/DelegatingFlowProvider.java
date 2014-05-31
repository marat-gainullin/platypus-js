package com.bearsoft.rowset.dataflow;

import java.util.List;

import com.bearsoft.rowset.Callback;
import com.bearsoft.rowset.Cancellable;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.TransactionListener.Registration;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;

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
	public Fields getExpectedFields() {
		return delegate.getExpectedFields();
	}

	@Override
	public Cancellable refresh(Parameters aParams, Callback<Rowset> onSuccess, Callback<String> onFailure) throws Exception {
		return delegate.refresh(aParams, onSuccess, onFailure);
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
