/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.application;

import java.util.ArrayList;
import java.util.List;

import com.eas.client.AppClient;
import com.eas.client.changes.Change;
import com.eas.client.dataflow.FlowProvider;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameters;
import com.eas.core.Cancellable;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author mg
 */
public class WebFlowProvider implements FlowProvider {

	protected String entityId;
	protected Fields expectedFields;
	protected AppClient client;
	protected boolean procedure;
	protected List<Change> changeLog = new ArrayList<Change>();

	public WebFlowProvider(AppClient aClient, String aEntityId, Fields aExpectedFields) {
		client = aClient;
		entityId = aEntityId;
		expectedFields = aExpectedFields;
	}

	@Override
	public String getEntityName() {
		return entityId;
	}

	@Override
	public Fields getExpectedFields() {
		return expectedFields;
	}

	@Override
	public Cancellable refresh(Parameters aParams, Callback<JavaScriptObject, String> aCallback) throws Exception {
		return client.requestData(entityId, aParams, expectedFields, aCallback);
	}

	@Override
	public List<Change> getChangeLog() {
		return changeLog;
	}
}
