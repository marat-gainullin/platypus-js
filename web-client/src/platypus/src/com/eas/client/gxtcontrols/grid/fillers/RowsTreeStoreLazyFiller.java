package com.eas.client.gxtcontrols.grid.fillers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.Callback;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.TreeStore;

public class RowsTreeStoreLazyFiller extends RowsTreeStoreFiller {

	protected Field paramSource;
	protected Parameter param2GetChildren;

	public RowsTreeStoreLazyFiller(TreeStore<Row> aStore, Entity aRowsetHost, Field aParentField, Parameter aParam2GetChildren, Field aParamSource) throws RowsetException {
		super(aStore, aRowsetHost, aParentField);
		param2GetChildren = aParam2GetChildren;
		paramSource = aParamSource;
	}

	@Override
	protected boolean hasRowChildren(Row parent) {
		return true;
	}

	@Override
	protected RpcProxy<Row, List<Row>> createDataProxy() {
		return new RpcProxy<Row, List<Row>>() {

			@Override
			public void load(final Row aParent, final AsyncCallback<List<Row>> callback) {
				try {
					if (aParent == null) {
						assert rootsLoadCallback == null;
						rootsLoadCallback = callback;
					} else {
						assert rowset != null;
						final List<Row> children = findChildren(aParent);
						if (!children.isEmpty()) {
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override
								public void execute() {
									try {
										List<Row> children = findChildren(aParent);
										if (!children.contains(aParent))
											callback.onSuccess(children);
										else
											throw new RowsetException("Parent in children found");
									} catch (RowsetException e) {
										callback.onFailure(e);
									}
								}
							});
						} else {
							rowsetHost.bindQueryParameters();
							Query query = rowsetHost.getQuery().copy();
							query.setClient(rowsetHost.getQuery().getClient());
							Parameter param2Init = query.getParameters().get(param2GetChildren.getName());
							param2Init.setValue(aParent.getColumnObject(rowset.getFields().find(paramSource.getName())));
							query.execute(new Callback<Rowset>() {

								@Override
								public void cancel() {
									callback.onFailure(new RowsetException("Rowset request has been cancelled"));
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
									callback.onSuccess(fetched);
								}

							}, new Callback<String>() {
								@Override
								public void run(String aResult) throws Exception {
									callback.onFailure(new RowsetException(aResult));
								}

								@Override
								public void cancel() {
								}
							});
						}
					}
				} catch (Exception e) {
					Logger.getLogger(RowsTreeStoreLazyFiller.class.getName()).log(Level.SEVERE, e.getMessage());
				}
			}
		};
	}
}
