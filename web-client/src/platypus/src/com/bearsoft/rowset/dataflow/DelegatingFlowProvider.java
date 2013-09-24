package com.bearsoft.rowset.dataflow;

import java.util.List;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.TransactionListener.Registration;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Callback;
import com.eas.client.Cancellable;

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
