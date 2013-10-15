package com.eas.client.gxtcontrols.grid.fillers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNetErrorEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.locators.RowWrap;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;

public class RowsTreeStoreFiller extends RowsetAdapter {

	protected class RowsTreeLoader extends TreeLoader<Row> {
		public RowsTreeLoader(RpcProxy<Row, List<Row>> aProxy) {
			super(aProxy);
		}

		@Override
		public boolean hasChildren(Row parent) {
			return hasRowChildren(parent);
		}

		@Override
		public boolean load() {
			if (rootsLoadCallback == null) {
				boolean res = super.load();
				if (res) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							checkIfRootsLoaded();
							checkIfRootsWithError();
						}
					});
				}
				return res;
			} else
				return false;
		}
	}

	protected boolean inited;
	protected AsyncCallback<List<Row>> rootsLoadCallback;
	protected TreeLoader<Row> loader;
	protected TreeStore<Row> store;
	protected Entity rowsetHost;
	protected Rowset rowset;
	protected String rowsetError;
	protected Field parentField;
	protected Locator pkLocator;
	protected Locator parentLocator;
	protected List<Integer> pkColIndicies;
	protected int parentColIndex;

	public RowsTreeStoreFiller(TreeStore<Row> aStore, Entity aRowsetHost, Field aParentField) throws RowsetException {
		super();
		store = aStore;
		rowsetHost = aRowsetHost;
		parentField = aParentField;
		if (rowsetHost != null) {
			rowset = rowsetHost.getRowset();
			constructLocator();
			rowset.addRowsetListener(this);
		}
		loader = new RowsTreeLoader(createDataProxy());
		loader.addLoadHandler(new ChildTreeStoreBinding<Row>(store));
	}

	protected boolean hasRowChildren(Row parent) {
		try {
			List<Row> children = findChildren(parent);
			return children != null && !children.isEmpty();
		} catch (RowsetException ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}

	public TreeLoader<Row> getLoader() {
		return loader;
	}

	protected RpcProxy<Row, List<Row>> createDataProxy() {
		return new RpcProxy<Row, List<Row>>() {
			@Override
			public void load(final Row aParent, final AsyncCallback<List<Row>> callback) {
				if (aParent == null) {
					assert rootsLoadCallback == null;
					rootsLoadCallback = callback;
				} else {
					assert rowset != null;
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
				}
			}
		};
	}

	protected void constructLocator() {
		assert pkLocator == null;
		pkLocator = rowset.createLocator();
		pkLocator.beginConstrainting();
		try {
			pkColIndicies = rowset.getFields().getPrimaryKeysIndicies();
			for (Integer pkColIndex : pkColIndicies)
				pkLocator.addConstraint(pkColIndex);
		} finally {
			pkLocator.endConstrainting();
		}
		parentColIndex = rowset.getFields().find(parentField.getName());
		parentLocator = rowset.createParentLocator(parentColIndex, pkLocator);
		parentLocator.beginConstrainting();
		try {
			parentLocator.addConstraint(parentColIndex);
		} finally {
			parentLocator.endConstrainting();
		}
	}

	protected List<Row> findChildren(Row aParent) throws RowsetException {
		if (pkLocator.getFields().size() != rowset.getFields().getPrimaryKeysIndicies().size()) {
			rowset.removeLocator(pkLocator);
			pkLocator = null;
			rowset.removeLocator(parentLocator);
			parentLocator = null;
			constructLocator();
		}
		Object[] pkValues = new Object[pkLocator.getFields().size()];
		for (int i = 0; i < pkValues.length; i++) {
			if (aParent != null)
				pkValues[i] = aParent.getColumnObject(pkLocator.getFields().get(i));
			else
				pkValues[i] = null;
		}
		if (parentLocator.find(pkValues)) {
			List<Row> children = new ArrayList();
			for (RowWrap rw : parentLocator.getSubSet()) {
				children.add(rw.getRow());
			}
			return children;
		}
		return Collections.<Row> emptyList();
	}

	protected void checkIfRootsLoaded() {
		if (rootsLoadCallback != null && rowset != null && !rowset.isPending()) {
			try {
				store.clear();
				List<Row> children = findChildren(null);
				rootsLoadCallback.onSuccess(children);
				rootsLoadCallback = null;
				inited = true;
			} catch (Exception ex) {
				Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	protected void checkIfRootsWithError() {
		if (rowsetError != null && rootsLoadCallback != null) {
			rootsLoadCallback.onFailure(new RowsetException(rowsetError) {
				@Override
				public void printStackTrace() {
					// no op
				}
			});
			rootsLoadCallback = null;
			rowsetError = null;
		}
	}

	protected boolean isInitied() {
		return rowset != null && inited;
	}

	@Override
	public void rowsetFiltered(RowsetFilterEvent event) {
		try {
			if (isInitied()) {
				store.clear();
				store.add(rowset.getCurrent());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void beforeRequery(RowsetRequeryEvent event) {
		try {
			loader.load();
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowsetRequeried(RowsetRequeryEvent event) {
		try {
			assert rowset != null && !rowset.isPending();
			rowsetError = null;
			checkIfRootsLoaded();
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowsetNetError(RowsetNetErrorEvent event) {
		try {
			rowsetError = event.getMessage();
			assert rowsetError != null;
			checkIfRootsWithError();
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowsetRolledback(RowsetRollbackEvent event) {
		try {
			if (isInitied()) {
				store.clear();
				store.add(rowset.getCurrent());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowsetSaved(RowsetSaveEvent event) {
		try {
			if (isInitied()) {
				store.fireEvent(new StoreUpdateEvent<Row>(store.getAll()));
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowInserted(RowsetInsertEvent event) {
		try {
			if (isInitied()) {
				Object parentKey = event.getRow().getColumnObject(parentColIndex);
				Row parentRow = store.findModelWithKey(String.valueOf(parentKey));
				if (parentRow != null) {
					store.add(parentRow, event.getRow());
				} else {
					store.add(event.getRow());
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowChanged(RowChangeEvent event) {
		try {
			if (isInitied()) {
				if (event.getFieldIndex() == parentColIndex) {
					Row oldParentRow = store.findModelWithKey(String.valueOf(event.getOldValue()));
					Row newParentRow = store.findModelWithKey(String.valueOf(event.getNewValue()));
					if (oldParentRow != newParentRow) {
						store.remove(event.getChangedRow());
						if (newParentRow != null && newParentRow != event.getChangedRow())
							store.add(newParentRow, event.getChangedRow());
						else
							store.add(event.getChangedRow());
					}
				}
				store.update(event.getChangedRow());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowDeleted(RowsetDeleteEvent event) {
		try {
			if (isInitied()) {
				store.remove(event.getRow());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsTreeStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}
}
