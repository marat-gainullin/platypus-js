package com.eas.client.gxtcontrols.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.widget.core.client.event.CellMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.CellMouseDownEvent.CellMouseDownHandler;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.selection.CellSelection;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent.CellSelectionChangedHandler;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent.HasCellSelectionChangedHandlers;

public class PlatypusCellSelectionModel<M> extends GridSelectionModel<M> implements HasCellSelectionChangedHandlers<M> {

	@Override
	public HandlerRegistration addCellSelectionChangedHandler(CellSelectionChangedHandler<M> handler) {
		return ensureHandlers().addHandler(CellSelectionChangedEvent.getType(), handler);
	}

	private class Handler implements ViewReadyHandler, CellMouseDownHandler {

		@Override
		public void onCellMouseDown(CellMouseDownEvent event) {
			handleMouseDown(event);
		}

		@Override
		public void onViewReady(ViewReadyEvent event) {
			if (selection != null) {
				for (CellSelection<M> cell : selection.values()) {
					// index may change with tree grid on expand / collapse
					// ask store for current row index
					int row = listStore.indexOf(cell.getModel());
					cellSelected(row, cell.getCell(), true);
				}
				fireCellsSelectionChanged();
			}
		}
	}

	//private SelectionModelCallback callback = new SelectionModelCallback(this);
	private Handler handler = new Handler();
	protected Map<String, CellSelection<M>> selection = new HashMap<String, CellSelection<M>>();
	protected CellSelection<M> lastSelectedCell;// lead
	protected int anchorRow;
	protected int anchorCell;
	protected boolean updateAnchor = true;
	protected ColumnConfig<M, M> column;

	public PlatypusCellSelectionModel(ColumnConfig<M, M> aFieldColumn) {
		super();
		column = aFieldColumn;
	}

	public ColumnConfig<M, M> getColumn() {
		return column;
	}

	@Override
	public void bindGrid(Grid<M> grid) {
		super.bindGrid(grid);
		if (grid != null) {
			handlerRegistration.add(grid.addViewReadyHandler(handler));
			handlerRegistration.add(grid.addCellMouseDownHandler(handler));
		}
	}

	@Override
	protected void handleRowMouseDown(RowMouseDownEvent event) {
	}

	protected void handleMouseDown(CellMouseDownEvent event) {
		XEvent e = event.getEvent().<XEvent> cast();
		if (e.getButton() == Event.BUTTON_LEFT && !isLocked()) {
			int row = event.getRowIndex();
			int cell = event.getCellIndex();

			ColumnConfig<M, ?> c = grid.getColumnModel().getColumn(cell);
			if (c == column) {
				final M model = listStore.get(row);
				if (model != null) {
					select(model, false);
				}
			} else {

				if (e.getCtrlOrMetaKey()) {
					if (isCellSelected(row, cell)) {
						deselectCell(row, cell);
					} else {
						selectCell(row, cell);
					}
				} else {
					deselectAll();
					if (e.getShiftKey()) {
						int rowStart = Math.min(row, anchorRow);
						int rowEnd = Math.max(row, anchorRow);
						int cellStart = Math.min(cell, anchorCell);
						int cellEnd = Math.max(cell, anchorCell);
						updateAnchor = false;
						try {
							for (int i = rowStart; i <= rowEnd; i++) {
								for (int j = cellStart; j <= cellEnd; j++) {
									selectCell(i, j);
								}
							}
						} finally {
							updateAnchor = true;
						}
					} else
						selectCell(row, cell);
				}
				if (!e.getShiftKey()) {
					anchorRow = row;
					anchorCell = cell;
				}
				fireCellsSelectionChanged();
			}
		}
	}

	protected boolean isCellSelected(int aRow, int aCell) {
		String key = aRow + "_" + aCell;
		return selection.containsKey(key);
	}

	protected void selectCell(int aRow, int aCell) {
		selectCell(aRow, aCell, false);
	}

	public void selectCell(int aRow, int aCell, boolean fireEvent) {
		M m = listStore.get(aRow);
		if (m != null) {
			String key = aRow + "_" + aCell;
			lastSelectedCell = new CellSelection<M>(m, aRow, aCell);
			lastSelected = m;
			if (!selection.containsKey(key)) {
				selection.put(key, lastSelectedCell);
			}
			cellSelected(aRow, aCell, fireEvent);
			if (fireEvent) {
				if (updateAnchor) {
					anchorRow = aRow;
					anchorCell = aCell;
				}
				fireCellsSelectionChanged();
			}
		}
	}

	protected void deselectCell(int aRow, int aCell) {
		String key = aRow + "_" + aCell;
		CellSelection<M> toRemove = selection.get(key);
		if (toRemove != null) {
			selection.remove(key);
			if (toRemove == lastSelectedCell) {
				lastSelectedCell = null;
				lastSelected = null;
			}
			cellDeselected(aRow, aCell);
		}
	}

	protected void cellSelected(int aRow, int aCell, boolean hscroll) {
		if (grid.isViewReady()) {
			Element cell = grid.getView().getCell(aRow, aCell);
			if (cell != null)
				grid.getView().getAppearance().onCellSelect(cell, true);
			grid.getView().focusCell(aRow, aCell, hscroll);
		}
	}

	protected void cellDeselected(int aRow, int aCell) {
		if (grid.isViewReady()) {
			Element cell = grid.getView().getCell(aRow, aCell);
			if (cell != null)
				grid.getView().getAppearance().onCellSelect(cell, false);
		}
	}

	protected void clear() {
		for (CellSelection<M> cell : selection.values()) {
			int row = listStore.indexOf(cell.getModel());
			// index may change with tree grid on expand / collapse
			// ask store for current row index
			if (row == -1)// clear is invoked after a model has been remmoved
			              // from the store
			{
				cellDeselected(cell.getRow(), cell.getCell());
			} else
				cellDeselected(row, cell.getCell());
		}
		selection.clear();
	}

	/*
	 * public boolean isSelected(int aRow, int aCell) { return
	 * selection.containsKey(aRow + "_" + aCell); }
	 */
	protected void onKeyLeft(com.google.gwt.dom.client.NativeEvent e) {
		if (Element.is(e.getEventTarget()) && !grid.getView().isSelectableTarget(Element.as(e.getEventTarget()))) {
			return;
		}
		if (lastSelectedCell != null) {
			int row = lastSelectedCell.getRow();
			int cell = lastSelectedCell.getCell();
			if (cell >= 1) {
				boolean wasSelected = isCellSelected(row, cell - 1);
				selectPreviousColumn(e.getShiftKey());
				if (e.getShiftKey() && wasSelected)
					deselectCell(row, cell);
			}
		}
		e.preventDefault();
	}

	protected void onKeyUp(com.google.gwt.dom.client.NativeEvent e) {
		if (Element.is(e.getEventTarget()) && !grid.getView().isSelectableTarget(Element.as(e.getEventTarget()))) {
			return;
		}
		if (lastSelectedCell != null) {
			int row = lastSelectedCell.getRow();
			int cell = lastSelectedCell.getCell();
			if (row >= 1) {
				boolean wasSelected = isCellSelected(row - 1, cell);
				selectPrevious(e.getShiftKey());
				if (e.getShiftKey() && wasSelected)
					deselectCell(row, cell);
			}
		}
		e.preventDefault();
	}

	protected void onKeyRight(NativeEvent e) {
		if (Element.is(e.getEventTarget()) && !grid.getView().isSelectableTarget(Element.as(e.getEventTarget()))) {
			return;
		}
		if (lastSelectedCell != null) {
			int row = lastSelectedCell.getRow();
			int idx = lastSelectedCell.getCell();
			if (idx >= 0 && idx + 1 < grid.getColumnModel().getColumnCount()) {
				boolean wasSelected = isCellSelected(row, idx + 1);
				selectNextColumn(e.getShiftKey());
				if (e.getShiftKey() && wasSelected)
					deselectCell(row, idx);
			}
		}
		e.preventDefault();
	}

	protected void onKeyDown(NativeEvent e) {
		if (Element.is(e.getEventTarget()) && !grid.getView().isSelectableTarget(Element.as(e.getEventTarget()))) {
			return;
		}
		if (lastSelectedCell != null) {
			int row = lastSelectedCell.getRow();
			int cell = lastSelectedCell.getCell();
			if (row >= 0 && row + 1 < listStore.size()) {
				boolean wasSelected = isCellSelected(row + 1, cell);
				selectNext(e.getShiftKey());
				if (e.getShiftKey() && wasSelected)
					deselectCell(row, cell);
			}
		}
		e.preventDefault();
	}

	/**
	 * Returns true if there is an item in the list store following the most
	 * recently selected item (i.e. the selected item is not the last item in
	 * the list store).
	 * 
	 * @return true if there is an item following the most recently selected
	 *         item
	 */
	protected boolean hasNext() {
		return lastSelectedCell != null && lastSelectedCell.getRow() < listStore.size() - 1;
	}

	/**
	 * Returns true if there is an item in the column model following the most
	 * recently selected item (i.e. the selected item is not the last item in
	 * the column model).
	 * 
	 * @return true if there is an item following the most recently selected
	 *         item
	 */
	protected boolean hasNextColumn() {
		return lastSelectedCell != null && lastSelectedCell.getCell() < grid.getColumnModel().getColumnCount() - 1;
	}

	/**
	 * Returns true if there is an item in the list store preceding the most
	 * recently selected item (i.e. the selected item is not the first item in
	 * the list store).
	 * 
	 * @return true if there is an item preceding the most recently selected
	 *         item
	 */
	protected boolean hasPrevious() {
		return lastSelectedCell != null && lastSelectedCell.getRow() > 0;
	}

	/**
	 * Returns true if there is an item in the column model preceding the most
	 * recently selected item (i.e. the selected item is not the first item in
	 * the column model).
	 * 
	 * @return true if there is an item preceding the most recently selected
	 *         item
	 */
	protected boolean hasPreviousColumn() {
		return lastSelectedCell != null && lastSelectedCell.getCell() > 0;
	}

	/**
	 * Selects the next row.
	 * 
	 * @param keepexisting
	 *            true to keep existing selections
	 */
	public void selectNext(boolean keepexisting) {
		if (hasNext()) {
			int idx = lastSelectedCell.getRow() + 1;
			int cell = lastSelectedCell.getCell();
			if (!keepexisting)
				deselectAll();
			selectCell(idx, cell, true);
			grid.getView().focusCell(idx, cell, true);
		}
	}

	/**
	 * Selects the next column.
	 * 
	 * @param keepexisting
	 *            true to keep existing selections
	 */
	public void selectNextColumn(boolean keepexisting) {
		if (hasNextColumn()) {
			int idx = lastSelectedCell.getRow();
			int cell = lastSelectedCell.getCell() + 1;
			if (!keepexisting)
				deselectAll();
			selectCell(idx, cell, true);
			grid.getView().focusCell(idx, cell, true);
		}
	}

	/**
	 * Selects the previous row.
	 * 
	 * @param keepexisting
	 *            true to keep existing selections
	 */
	public void selectPrevious(boolean keepexisting) {
		if (hasPrevious()) {
			int idx = lastSelectedCell.getRow() - 1;
			int cell = lastSelectedCell.getCell();
			if (!keepexisting)
				deselectAll();
			selectCell(idx, cell, true);
			grid.getView().focusCell(idx, cell, true);
		}
	}

	/**
	 * Selects the previous row.
	 * 
	 * @param keepexisting
	 *            true to keep existing selections
	 */
	public void selectPreviousColumn(boolean keepexisting) {
		if (hasPreviousColumn()) {
			int idx = lastSelectedCell.getRow();
			int cell = lastSelectedCell.getCell() - 1;
			if (!keepexisting)
				deselectAll();
			selectCell(idx, cell, true);
			grid.getView().focusCell(idx, cell, true);
		}
	}

	public CellSelection<M> getLastSelectedCell() {
		return lastSelectedCell;
	}

	@Override
	protected M getLastFocused() {
		return lastSelected;
	}

	@Override
	public M getSelectedItem() {
		return lastSelectedCell != null ? lastSelectedCell.getModel() : null;
	}

	@Override
	public List<M> getSelectedItems() {
		Set<M> selected = new HashSet<M>();
		for (CellSelection<M> ss : selection.values()) {
			selected.add(ss.getModel());
		}
		return new ArrayList<M>(selected);
	}

	protected void fireCellsSelectionChanged() {
		List<CellSelection<M>> selected = new ArrayList<CellSelection<M>>();
		selected.addAll(selection.values());
		fireEvent(new CellSelectionChangedEvent<M>(selected));
		SelectionEvent.fire(this, getSelectedItem());
	}

	@Override
	protected void onAdd(List<? extends M> models) {
	}

	protected void onRemove(M model) {
		if (lastSelectedCell != null) {
			final int lastRow = lastSelectedCell.getRow();
			final int lastCell = lastSelectedCell.getCell();
			for (CellSelection<M> ss : selection.values()) {
				if (ss.getModel() == model) {
					deselectCell(ss.getRow(), ss.getCell());
				}
			}
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					if (lastRow >= 0 && listStore.size() > 0) {
						if (lastRow == listStore.size())
							selectCell(lastRow - 1, lastCell, true);
						else if (lastRow < listStore.size())
							selectCell(lastRow, lastCell, true);
					}
				}
			});
		} else {
			for (CellSelection<M> ss : selection.values()) {
				if (ss.getModel() == model) {
					deselectCell(ss.getRow(), ss.getCell());
				}
			}
		}
	}

	@Override
	protected void onClear(StoreClearEvent<M> event) {
		deselectAll();
	}

	@Override
	public void refresh() {
		deselectAll();
	}

	@Override
	public void deselectAll() {
		clear();
		selected.clear();
		lastSelected = null;
		lastSelectedCell = null;
		fireCellsSelectionChanged();
	}

	@Override
	protected void doDeselect(List<M> models, boolean suppressEvent) {
		int columnCount = grid.getColumnModel().getColumnCount();
		for (M m : models) {
			int row = listStore.indexOf(m);
			for (int cell = 0; cell < columnCount; cell++)
				deselectCell(row, cell);
		}
		if (!suppressEvent)
			fireCellsSelectionChanged();
	}

	@Override
	protected void doMultiSelect(List<M> models, boolean keepExisting, boolean suppressEvent) {
		if (!keepExisting)
			clear();
		int columnCount = grid.getColumnModel().getColumnCount();
		for (M m : models) {
			int row = listStore.indexOf(m);
			for (int cell = 0; cell < columnCount; cell++)
				selectCell(row, cell);
		}
		if (!suppressEvent)
			fireCellsSelectionChanged();
	}

	@Override
	protected void doSelect(List<M> models, boolean keepExisting, boolean suppressEvent) {
		if (!keepExisting)
			clear();
		int columnCount = grid.getColumnModel().getColumnCount();
		for (M m : models) {
			int row = listStore.indexOf(m);
			for (int cell = 0; cell < columnCount; cell++)
				selectCell(row, cell);
		}
		if (!suppressEvent)
			fireCellsSelectionChanged();
	}

	protected void doSingleSelect(M model, boolean suppressEvent) {
		int columnCount = grid.getColumnModel().getColumnCount();
		int row = listStore.indexOf(model);
		for (int cell = 0; cell < columnCount; cell++)
			selectCell(row, cell);
		if (!suppressEvent)
			fireCellsSelectionChanged();
	}
}
