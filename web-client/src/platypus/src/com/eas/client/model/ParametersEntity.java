/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;

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
	public String getQueryName() {
		return null;
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
	public void execute(Callback<Rowset, String> aCallback) {
		if (rowset == null) {
			rowset = new ParametersRowset((Parameters) model.getParameters());
			rowset.addRowsetListener(this);
		}
	}

	@Override
	public Rowset getRowset() {
		execute((Callback<Rowset, String>)null);
		return super.getRowset();
	}

	@Override
	protected void internalExecute(Callback<Rowset, String> aCallback) {
	}

	@Override
	protected void refreshRowset(Callback<Rowset, String> onFailure) throws Exception {
	}

	@Override
	public void validateQuery() throws Exception {
	}
	
	public static native void publishTopLevelFacade(JavaScriptObject published)/*-{
		Object.defineProperty(published, "length", {
			get : function(){
				return published.schema.length; 
			}
		});
		for(var i = 0; i < published.schema.length; i++){
			(function(){
				var pParameter = published.schema[i];
				var _i = i;
				var propDesc = {
					get : function(){
						return published.getObject(_i + 1);
					},
					set : function(aValue){
						published.updateObject(_i + 1, aValue);
					}
				};
				Object.defineProperty(published, pParameter.name, propDesc);
				Object.defineProperty(published, _i, propDesc);
			})();
		}
	}-*/;
}
