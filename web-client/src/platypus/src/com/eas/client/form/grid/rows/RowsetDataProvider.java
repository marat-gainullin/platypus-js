package com.eas.client.form.grid.rows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
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
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.google.gwt.view.client.ListDataProvider;

public class RowsetDataProvider extends ListDataProvider<Row> implements IndexOfProvider<Row> {

	protected Map<Row, Integer> indicies;
	protected Rowset rowset;
	protected RowsetReflector rowsetReflector = new RowsetReflector();
	protected Runnable onResize;

	public RowsetDataProvider(Rowset aRowset, Runnable aOnResize) {
		super();
		setRowset(aRowset);
		onResize = aOnResize;
	}

	public RowsetReflector getRowsetReflector() {
		return rowsetReflector;
	}

	public void setRowset(Rowset aRowset) {
		if (rowset != aRowset) {
			invalidate();
			if (rowset != null) {
				rowset.removeRowsetListener(rowsetReflector);
			}
			rowset = aRowset;
			if (rowset != null) {
				rowset.addRowsetListener(rowsetReflector);
				rowsetReflector.rowsetRequeried(null);
			}
		}
	}

	protected void invalidate() {
		indicies = null;
	}

	protected void validate() {
		if (indicies == null) {
			indicies = new HashMap<>();
			List<Row> targetList = getList();
			for (int i = 0; i < targetList.size(); i++) {
				indicies.put(targetList.get(i), i);
			}
		}
	}

	@Override
	public int indexOf(Row aItem) {
		validate();
		Integer idx = indicies.get(aItem);
		return idx != null ? idx.intValue() : -1;
	}

	@Override
	public void rescan() {
		invalidate();
		validate();
	}
	
	/*
	 * protected List<Object> rowsToPks(List<Row> aRows) { List<Object> pks =
	 * new ArrayList<Object>(); for (Row row : aRows) { Object[] pkValues =
	 * row.getPKValues(); if (pkValues != null && pkValues.length > 0)
	 * pks.add(pkValues[0]); else pks.add(null); } return pks; }
	 */
	/*
	 * protected Object rowToPk(Row aRow) { Object[] pkValues =
	 * aRow.getPKValues(); if (pkValues != null && pkValues.length > 0) return
	 * pkValues[0]; else return null; }
	 */

	protected class RowsetReflector extends RowsetAdapter {
		@Override
		public void rowsetFiltered(RowsetFilterEvent event) {
			getList().clear();
			getList().addAll(rowset.getCurrent());
			if (onResize != null)
				onResize.run();
			invalidate();
		}

		@Override
		public void rowsetRequeried(RowsetRequeryEvent event) {
			getList().clear();
			getList().addAll(rowset.getCurrent());
			if (onResize != null)
				onResize.run();
			invalidate();
		}

		@Override
		public void rowsetRolledback(RowsetRollbackEvent event) {
			getList().clear();
			getList().addAll(rowset.getCurrent());
			if (onResize != null)
				onResize.run();
			invalidate();
		}

		@Override
		public void rowsetSaved(RowsetSaveEvent event) {
			// TODO: Somehow reset changed status of views (remove changes label
			// from cells).
			// Note! We need to aviod whole rerendering of views.
		}

		@Override
		public void rowsetScrolled(RowsetScrollEvent event) {
		}

		protected Integer targetListCursor;

		@Override
		public void rowInserted(RowsetInsertEvent event) {
			if (targetListCursor != null) {
				getList().add(targetListCursor + 1, event.getRow());
				targetListCursor++;
			} else {
				getList().add(event.getRow());
			}
			if (!event.isAjusting()) {
				invalidate();
				if (onResize != null)
					onResize.run();
			}
		}

		@Override
		public void rowChanged(RowChangeEvent event) {
			if (event.getOldRowCount() != event.getNewRowCount()) {
				getList().clear();
				getList().addAll(rowset.getCurrent());
				invalidate();
				if (onResize != null)
					onResize.run();
			} else if (!getList().isEmpty()) {
				validate();
				Integer index = indicies.get(event.getChangedRow());
				assert index != null;
				getList().set(index, event.getChangedRow());
			}
		}

		protected boolean deferredDeletions;

		@Override
		public void rowDeleted(RowsetDeleteEvent event) {
			if (!getList().isEmpty()) {
				validate();
				Integer index = indicies.get(event.getRow());
				if (event.isAjusting()) {
					getList().set(index.intValue(), null);
					deferredDeletions = true;
				} else {
					getList().remove(index.intValue());
					if (deferredDeletions) {
						deferredDeletions = false;
						List<Row> targetList = getList();
						for (int i = getList().size() - 1; i >= 0; i--) {
							targetList.remove(i);
						}
					}
					invalidate();
					if (onResize != null)
						onResize.run();
				}
			}
		}

		@Override
		public void beforeRequery(RowsetRequeryEvent event) {
		}

		@Override
		public void rowsetNetError(RowsetNetErrorEvent event) {
		}
	}
}
