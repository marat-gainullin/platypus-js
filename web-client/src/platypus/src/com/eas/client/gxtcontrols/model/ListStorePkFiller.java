package com.eas.client.gxtcontrols.model;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;

public class ListStorePkFiller extends RowsetAdapter {

	protected ListStore<Object> store;
	protected Rowset rowset;

	public ListStorePkFiller(ListStore<Object> aStore, Rowset aRowset) {
		super();
		store = aStore;
		rowset = aRowset;
		rowset.addRowsetListener(this);
		rowsetRequeried(null);
	}

	public ListStorePkFiller(ListStore<Object> aStore) {
		super();
		store = aStore;
	}

	public void setValuesRowset(Rowset aRowset) {
		if (rowset != null) {
			rowset.removeRowsetListener(this);
		}
		rowset = aRowset;
		if (rowset != null) {
			rowset.addRowsetListener(this);
			rowsetRequeried(null);
		}
	}

	protected List<Object> rowsToPks(List<Row> aRows) {
		List<Object> pks = new ArrayList<Object>();
		for (Row row : aRows) {
			Object[] pkValues = row.getPKValues();
			if (pkValues != null && pkValues.length > 0)
				pks.add(pkValues[0]);
			else
				pks.add(null);
		}
		return pks;
	}

	protected Object rowToPk(Row aRow) {
		Object[] pkValues = aRow.getPKValues();
		if (pkValues != null && pkValues.length > 0)
			return pkValues[0];
		else
			return null;
	}

	@Override
	public void rowsetFiltered(RowsetFilterEvent event) {
		store.clear();
		store.addAll(rowsToPks(rowset.getCurrent()));
	}

	@Override
	public void rowsetRequeried(RowsetRequeryEvent event) {
		store.clear();
		store.addAll(rowsToPks(rowset.getCurrent()));
	}

	@Override
	public void rowsetRolledback(RowsetRollbackEvent event) {
		store.clear();
		store.addAll(rowsToPks(rowset.getCurrent()));
	}

	@Override
	public void rowsetSaved(RowsetSaveEvent event) {
		store.fireEvent(new StoreUpdateEvent<Object>(store.getAll()));
	}

	@Override
	public void rowsetScrolled(RowsetScrollEvent event) {
	}

	@Override
	public void rowInserted(RowsetInsertEvent event) {
		if (rowset.getCurrentRow() == event.getRow())
			store.add(rowset.getCursorPos() - 1, rowToPk(event.getRow()));
		else
			store.add(rowToPk(event.getRow()));
	}

	@Override
	public void rowChanged(RowChangeEvent event) {
		store.update(rowToPk(event.getChangedRow()));
	}

	@Override
	public void rowDeleted(RowsetDeleteEvent event) {
		store.remove(rowToPk(event.getRow()));
	}
}
