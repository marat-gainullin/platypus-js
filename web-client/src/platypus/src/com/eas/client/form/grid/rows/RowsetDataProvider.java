package com.eas.client.form.grid.rows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.view.client.ListDataProvider;

public class RowsetDataProvider extends ListDataProvider<Row> implements IndexOfProvider<Row> {

	protected Map<Row, Integer> indicies;
	protected Rowset rowset;
	protected RowsetReflector rowsetReflector = new RowsetReflector();
	protected Runnable onResize;
	protected Runnable onLoadStart;
	protected Callback<Void, String> onError;

	public RowsetDataProvider(Rowset aRowset, Runnable aOnResize, Runnable aOnLoadStart, Callback<Void, String> aOnError) {
		super();
		setRowset(aRowset);
		onResize = aOnResize;
		onLoadStart = aOnLoadStart;
		onError = aOnError;
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

		public int pingCounter;
		
		protected void 				pingGWT(){
			Scheduler.get().scheduleDeferred(new ScheduledCommand(){

				@Override
                public void execute() {
					pingCounter = 0;
					pingCounter++;
                }
				
			});
		}
		
		@Override
		public void rowsetFiltered(RowsetFilterEvent event) {
			getList().clear();
			getList().addAll(rowset.getCurrent());
			invalidate();
			if (onResize != null)
				onResize.run();
			pingGWT();
		}

		@Override
		public void rowsetRequeried(RowsetRequeryEvent event) {
			getList().clear();
			getList().addAll(rowset.getCurrent());
			invalidate();
			if (onResize != null)
				onResize.run();
			pingGWT();
		}

		@Override
		public void rowsetSorted(RowsetSortEvent event) {
			getList().clear();
			getList().addAll(rowset.getCurrent());
			invalidate();
			if (onResize != null)
				onResize.run();
			pingGWT();
		}

		@Override
		public void rowsetRolledback(RowsetRollbackEvent event) {
			getList().clear();
			getList().addAll(rowset.getCurrent());
			invalidate();
			if (onResize != null)
				onResize.run();
			pingGWT();
		}

		@Override
		public void rowsetSaved(RowsetSaveEvent event) {
			// TODO: Somehow reset changed status of views (remove changes label
			// from cells).
			// Note! We need to aviod whole rerendering of views.
		}

		@Override
		public void rowsetScrolled(RowsetScrollEvent event) {
			/* rowset current cursor position change is handled by ModelGrid
			if(event.getOldRowIndex() >= 1 && event.getOldRowIndex() <= event.getRowset().size()){
				Row oldRow = event.getRowset().getRow(event.getOldRowIndex());
				validate();
				Integer index = indicies.get(oldRow);
				assert index != null;
				getList().set(index, oldRow);
			}
			if(event.getNewRowIndex() >= 1 && event.getNewRowIndex() <= event.getRowset().size()){
				Row newRow = event.getRowset().getRow(event.getNewRowIndex());
				validate();
				Integer index = indicies.get(newRow);
				assert index != null;
				getList().set(index, newRow);
				targetListCursor = index;
			}
			if(event.getRowset().isBeforeFirst() || event.getRowset().isAfterLast())
				targetListCursor = null;
				*/
			// avoid unnecessary updates of provider's list and therefore unnecessary rendering of displays
			if(event.getOldRowIndex() >= 1 && event.getOldRowIndex() <= event.getRowset().size()){
				Row oldRow = event.getRowset().getRow(event.getOldRowIndex());
				validate();
				Integer index = indicies.get(oldRow);
				assert index != null;
			}
			if(event.getNewRowIndex() >= 1 && event.getNewRowIndex() <= event.getRowset().size()){
				Row newRow = event.getRowset().getRow(event.getNewRowIndex());
				validate();
				Integer index = indicies.get(newRow);
				assert index != null;
				targetListCursor = index;
			}
			if(event.getRowset().isBeforeFirst() || event.getRowset().isAfterLast())
				targetListCursor = null;
		}

		protected Integer targetListCursor;

		@Override
		public void rowInserted(RowsetInsertEvent event) {
			List<Row> targetList = getList();
			if (targetListCursor != null) {
				if(targetListCursor >= 0 && targetListCursor <= targetList.size()){
					targetList.add(targetListCursor + 1, event.getRow());
					targetListCursor++;
				} else {
					targetList.add(event.getRow());
				}
			} else {
				targetList.add(event.getRow());
			}
			if (!event.isAjusting()) {
				invalidate();
				if (onResize != null)
					onResize.run();
				pingGWT();
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
				pingGWT();
			} else if (!getList().isEmpty()) {
				validate();
				Integer index = indicies.get(event.getChangedRow());
				assert index != null;
				getList().set(index, event.getChangedRow());
				pingGWT();
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
							if(targetList.get(i) == null){
								targetList.remove(i);
							}
						}
					}
					invalidate();
					if (onResize != null)
						onResize.run();
				}
				pingGWT();
			}
		}

		@Override
		public void beforeRequery(RowsetRequeryEvent event) {
			if(onLoadStart != null){
				onLoadStart.run();
			}
		}

		@Override
		public void rowsetNetError(RowsetNetErrorEvent event) {
			if(onError != null){
	            try {
	                onError.onFailure(event.getMessage());
                } catch (Exception e) {
                	Logger.getLogger(RowsetDataProvider.class.getName()).log(Level.SEVERE, null, e);
                }
			}
		}
	}
}
