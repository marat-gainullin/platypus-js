package com.eas.client.gxtcontrols.grid.fillers;

import java.util.List;
import java.util.Set;
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
import com.eas.client.model.Entity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;

public class RowsListStoreFiller extends RowsetAdapter implements ListStoreFiller<Row> {

	protected class ObservingProxy implements DataProxy<ListLoadConfig, ListLoadResult<Row>> {

		@Override
		public void load(ListLoadConfig loadConfig, final Callback<ListLoadResult<Row>, Throwable> callback) {
			if (loadCallback == null) {
				loadCallback = callback;
			}
		}
	}

	protected class RowsListLoader extends ListLoader<ListLoadConfig, ListLoadResult<Row>> {
		public RowsListLoader(DataProxy<ListLoadConfig, ListLoadResult<Row>> aProxy) {
			super(aProxy);
		}

		@Override
		public boolean load() {
			if (loadCallback == null) {
				boolean res = super.load();
				if (res)
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							checkIfDataLoaded();
							checkIfDataError();
						}
					});

				return res;
			} else
				return false;
		}
	}

	protected boolean inited;
	protected ListLoader<ListLoadConfig, ListLoadResult<Row>> loader;
	protected Callback<ListLoadResult<Row>, Throwable> loadCallback;
	protected ListStore<Row> store;
	protected Entity rowsetHost;
	protected Rowset rowset;
	protected String rowsetError;

	public RowsListStoreFiller(ListStore<Row> aStore, Entity aRowsetHost) {
		super();
		store = aStore;
		rowsetHost = aRowsetHost;
		if (rowsetHost != null) {
			rowset = rowsetHost.getRowset();
			rowset.addRowsetListener(this);
		}
		loader = new RowsListLoader(new ObservingProxy());
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, Row, ListLoadResult<Row>>(store));
	}

	public ListLoader<ListLoadConfig, ListLoadResult<Row>> getLoader() {
		return loader;
	}

	@Override
	public ListStore<Row> getStore() {
		return store;
	}

	@SuppressWarnings("serial")
	protected void checkIfDataLoaded() {
		if (loadCallback != null && rowset != null && !rowset.isPending()) {
			store.clear();
			loadCallback.onSuccess(new ListLoadResult<Row>() {
				@Override
				public List<Row> getData() {
					return rowset.getCurrent();
				}
			});
			loadCallback = null;
			inited = true;
		}
	}

	protected void checkIfDataError() {
		if (rowsetError != null && loadCallback != null) {
			loadCallback.onFailure(new RowsetException(rowsetError) {
				@Override
				public void printStackTrace() {
					// no op
				}
			});
			loadCallback = null;
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
				store.replaceAll(rowset.getCurrent());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void beforeRequery(RowsetRequeryEvent event) {
		try {
			loader.load();
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowsetRequeried(RowsetRequeryEvent event) {
		try {
			assert rowset != null && !rowset.isPending();
			rowsetError = null;
			checkIfDataLoaded();
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	public void loaded() {
		// no-op here because of rowset's and self generated events about
		// rowset's data completeness.
	}

	@Override
	public void rowsetNetError(RowsetNetErrorEvent event) {
		try {
			rowsetError = event.getMessage();
			assert rowsetError != null;
			checkIfDataError();
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowsetRolledback(RowsetRollbackEvent event) {
		try {
			if (isInitied()) {
				store.replaceAll(rowset.getCurrent());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowsetSaved(RowsetSaveEvent event) {
		try {
			if (isInitied()) {
				store.fireEvent(new StoreUpdateEvent<Row>(store.getAll()));
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowInserted(RowsetInsertEvent event) {
		try {
			if (isInitied()) {
				if (rowset.getCurrentRow() == event.getRow())
					store.add(rowset.getCursorPos() - 1, event.getRow());
				else
					store.add(event.getRow());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowChanged(RowChangeEvent event) {
		try {
			if (isInitied()) {
				store.update(event.getChangedRow());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public void rowDeleted(RowsetDeleteEvent event) {
		try {
			if (isInitied()) {
				store.remove(event.getRow());
			}
		} catch (Exception ex) {
			Logger.getLogger(RowsListStoreFiller.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}
}
