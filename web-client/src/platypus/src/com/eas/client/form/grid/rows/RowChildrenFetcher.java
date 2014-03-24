package com.eas.client.form.grid.rows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.processing.ChildrenFetcher;
import com.bearsoft.rowset.Callback;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.queries.Query;

public class RowChildrenFetcher implements ChildrenFetcher<Row> {

	protected Entity targetEntity;
	protected Rowset rowset;
	protected Field paramSource;
	protected Parameter param2GetChildren;

	public RowChildrenFetcher(Entity aTargetEntity, Parameter aParam2GetChildren, Field aParamSource) throws RowsetException {
		super();
		targetEntity = aTargetEntity;
		rowset = targetEntity.getRowset();
		param2GetChildren = aParam2GetChildren;
		paramSource = aParamSource;
	}

	@Override
	public void fetch(Row aParent, final Runnable aFetchCompleter) {
		try {
			targetEntity.bindQueryParameters();
			Query query = targetEntity.getQuery().copy();
			query.setClient(targetEntity.getQuery().getClient());
			Parameter param2Init = query.getParameters().get(param2GetChildren.getName());
			param2Init.setValue(aParent.getColumnObject(rowset.getFields().find(paramSource.getName())));
			query.execute(new Callback<Rowset>() {

				@Override
				public void cancel() {
					// callback.onFailure(new
					// RowsetException("Rowset request has been cancelled"));
				}

				@Override
				public void run(Rowset aRowset) throws Exception {
					List<Row> fetched = aRowset.getCurrent();
					if (!fetched.isEmpty()) {
						Set<RowsetListener> listeners = rowset.getRowsetChangeSupport().getRowsetListeners();
						rowset.getRowsetChangeSupport().setRowsetListeners(null);
						FlowProvider rowsetFlow = rowset.getFlowProvider();
						rowset.setFlowProvider(null);
						try {
							int preservedCursorPos = rowset.getCursorPos();
							boolean oldModified = rowset.isModified();
							try {
								for (Row row : fetched) {
									rowset.insert(row, true);
									row.clearInserted();
								}
							} finally {
								rowset.setModified(oldModified);
							}
							aRowset.setCurrent(new ArrayList<Row>());
							aRowset.currentToOriginal();
							rowset.absolute(preservedCursorPos);
						} finally {
							rowset.setFlowProvider(rowsetFlow);
							rowset.getRowsetChangeSupport().setRowsetListeners(listeners);
							// rowset.getRowsetChangeSupport().fireFilteredEvent();
							// TODO: Check if Java SE client
							// fires
							// same
							// event and avoid processing of
							// such
							// event
							// by TreeStoreLoader code.
						}
					}
					aFetchCompleter.run();
				}

			}, new Callback<String>() {
				@Override
				public void run(String aResult) throws Exception {
				}

				@Override
				public void cancel() {
				}
			});
		} catch (Exception e) {
			Logger.getLogger(RowChildrenFetcher.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
