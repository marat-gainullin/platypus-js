/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import java.util.List;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.dataflow.TransactionListener.Registration;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Callback;
import com.eas.client.Cancellable;

/**
 * 
 * @author mg
 */
public class WebFlowProvider implements FlowProvider {

	protected String entityId;
	protected AppClient client;
	protected boolean procedure;

	public WebFlowProvider(AppClient aClient, String aEntityId) {
		client = aClient;
		entityId = aEntityId;
	}

	@Override
	public String getEntityId() {
		return entityId;
	}

	@Override
	public Cancellable refresh(Parameters aParams, Callback<Rowset> onSuccess, Callback<String> onFailure) throws Exception {
		return client.pollData(entityId, aParams, onSuccess, onFailure);
	}

	@Override
	public List<Change> getChangeLog() {
		return client.getChangeLog();
	}

	@Override
	public Registration addTransactionListener(TransactionListener aListener) {
		return client.addTransactionListener(aListener);
	}
}
