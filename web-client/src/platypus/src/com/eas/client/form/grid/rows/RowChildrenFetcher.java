package com.eas.client.form.grid.rows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.processing.ChildrenFetcher;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.Callback;

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
			query.prepareRowset().refresh(new Callback<Rowset, String>() {

				@Override
				public void onSuccess(Rowset aRowset) {
					try {
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
							}
						}
						aFetchCompleter.run();
					} catch (Exception ex) {
						Logger.getLogger(RowChildrenFetcher.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				@Override
				public void onFailure(String reason) {
				}
			});
		} catch (Exception e) {
			Logger.getLogger(RowChildrenFetcher.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
