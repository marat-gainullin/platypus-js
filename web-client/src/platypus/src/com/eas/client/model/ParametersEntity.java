/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Callback;
import com.eas.client.Cancellable;
import com.eas.client.CancellableCallback;
import com.eas.client.queries.Query;

/**
 * 
 * @author mg
 */
public class ParametersEntity extends Entity {

	public static final String PARAMETERS_ENTITY_ID = "-1";

	public ParametersEntity() {
		super();
		entityId = PARAMETERS_ENTITY_ID;
	}

	@Override
	public boolean isRowsetPresent() {
		return true;
	}

	@Override
	public Fields getFields() {
		model.getParameters().setOwner(this);
		return model.getParameters();
	}

	@Override
	protected boolean isTagValid(String aTagName) {
		return true;
	}

	@Override
	public Entity copy() throws Exception {
		ParametersEntity copied = new ParametersEntity();
		assign(copied);
		return copied;
	}

	@Override
	public void accept(ModelVisitor visitor) {
		if (visitor != null) {
			visitor.visit(this);
		}
	}

	@Override
	public String getQueryId() {
		return PARAMETERS_ENTITY_ID;
	}

	@Override
	public Query getQuery() {
		return null;
	}

	@Override
	public String getEntityId() {
		return PARAMETERS_ENTITY_ID;
	}

	@Override
	public void execute(CancellableCallback onSuccess, Callback<String> onFailure) {
		if (rowset == null) {
			rowset = new ParametersRowset((Parameters) model.getParameters());
			rowset.addRowsetListener(this);
		}
	}

	@Override
	public Rowset getRowset() {
		execute(null, null);
		return super.getRowset();
	}

	@Override
	protected void internalExecute(CancellableCallback onSuccess, Callback<String> onFailure) {
		if (onSuccess != null) {
			try {
				onSuccess.run();
			} catch (Exception e) {
				Logger.getLogger(ParametersEntity.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	@Override
	protected Cancellable achieveOrRefreshRowset(CancellableCallback onSuccess, Callback<String> onFailure) throws Exception {
		return null;
	}

	@Override
	public void validateQuery() throws Exception {
	}
}
