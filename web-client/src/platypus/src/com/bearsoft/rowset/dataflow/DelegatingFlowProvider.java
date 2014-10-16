package com.bearsoft.rowset.dataflow;

import java.util.List;

import com.bearsoft.rowset.Cancellable;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.google.gwt.core.client.Callback;

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
	public Cancellable refresh(Parameters aParams, Callback<Rowset, String> aCallback) throws Exception {
		return delegate.refresh(aParams, aCallback);
	}

	@Override
	public List<Change> getChangeLog() {
		return delegate.getChangeLog();
	}
}
