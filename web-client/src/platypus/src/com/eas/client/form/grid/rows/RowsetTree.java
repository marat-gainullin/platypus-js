package com.eas.client.form.grid.rows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.processing.TreeAdapter;
import com.bearsoft.rowset.Callback;
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
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.locators.RowWrap;
import com.bearsoft.rowset.metadata.Field;

public class RowsetTree extends TreeAdapter<Row> {

	protected Rowset rowset;
	protected Field parentField;
	protected Locator pkLocator;
	protected Locator parentLocator;
	protected List<Integer> pkColIndicies;
	protected int parentColIndex;
	protected boolean lazy;
	protected RowsetReflector rowsetReflector = new RowsetReflector();
	protected Runnable onLoadStart;
	protected Callback<String> onError;

	public RowsetTree(Rowset aRowset, Field aParentField, Runnable aOnLoadStart, Callback<String> aOnError) {
		this(aRowset, aParentField, false, aOnLoadStart, aOnError);
	}

	public RowsetTree(Rowset aRowset, Field aParentField, boolean aLazy, Runnable aOnLoadStart, Callback<String> aOnError) {
		super();
		parentField = aParentField;
		lazy = aLazy;
		onLoadStart = aOnLoadStart;
		onError = aOnError;
		setRowset(aRowset);
	}

	public void setRowset(Rowset aRowset) {
		if (rowset != aRowset) {
			if (rowset != null) {
				rowset.removeRowsetListener(rowsetReflector);
			}
			rowset = aRowset;
			if (rowset != null) {
				constructLocator();
				rowset.addRowsetListener(rowsetReflector);
				rowsetReflector.rowsetRequeried(null);
			}
		}
	}

	protected boolean hasRowChildren(Row parent) {
		try {
			List<Row> children = findChildren(parent);
			return children != null && !children.isEmpty();
		} catch (RowsetException ex) {
			Logger.getLogger(RowsetTree.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
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
			List<Row> children = new ArrayList<>();
			for (RowWrap rw : parentLocator.getSubSet()) {
				children.add(rw.getRow());
			}
			return children;
		}
		return Collections.<Row> emptyList();
	}

	// Tree structure of a rowset
	@Override
	public Row getParentOf(Row anElement) {
		if (anElement != null) {
			try {
				Object parentKey = anElement.getColumnObject(parentColIndex);
				if (parentKey != null && pkLocator.find(parentKey)) {
					return pkLocator.getRow(0);
				}
			} catch (RowsetException e) {
				Logger.getLogger(RowsetTree.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		return null;
	}

	@Override
	public List<Row> getChildrenOf(Row anElement) {
		try {
			return findChildren(anElement);
		} catch (RowsetException e) {
			Logger.getLogger(RowsetTree.class.getName()).log(Level.SEVERE, null, e);
			return Collections.emptyList();
		}
	}

	@Override
	public boolean isLeaf(Row anElement) {
		return !lazy && !hasRowChildren(anElement);
	}

	// Tree mutation methods
	@Override
	public void add(Row aParent, Row anElement) {
	}

	@Override
	public void add(int aIndex, Row aParent, Row anElement) {
	}

	@Override
	public void addAfter(Row afterElement, Row anElement) {
	}

	@Override
	public void remove(Row anElement) {
		try {
			rowset.deleteRow(anElement);
		} catch (RowsetException e) {
			Logger.getLogger(RowsetTree.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	// Events reflection
	protected class RowsetReflector extends RowsetAdapter {
		@Override
		public void rowChanged(RowChangeEvent event) {
			if (event.getOldRowCount() == event.getNewRowCount())
				changed(event.getChangedRow());
			else
				everythingChanged();
		}

		@Override
		public void rowDeleted(RowsetDeleteEvent event) {
			removed(event.getRow(), getParentOf(event.getRow()));
		}

		@Override
		public void rowInserted(RowsetInsertEvent event) {
			added(event.getRow());
		}

		@Override
		public void rowsetFiltered(RowsetFilterEvent event) {
			everythingChanged();
		}

		@Override
		public void rowsetRequeried(RowsetRequeryEvent event) {
			everythingChanged();
		}

		@Override
		public void rowsetRolledback(RowsetRollbackEvent event) {
			everythingChanged();
		}

		@Override
		public void rowsetSorted(RowsetSortEvent event) {
			everythingChanged();
		}

		@Override
		public void rowsetSaved(RowsetSaveEvent event) {
			// TODO: Somehow reset changed status of views (remove changes label
			// from cells in grid).
			// Note! We need to avoid whole re-rendering of views.
		}

		@Override
		public void rowsetScrolled(RowsetScrollEvent event) {
			/* rowset current cursor position change is handled by ModelGrid
			if (event.getOldRowIndex() >= 1 && event.getOldRowIndex() <= event.getRowset().size()) {
				Row oldRow = event.getRowset().getRow(event.getOldRowIndex());
				changed(oldRow);
			}
			if (event.getNewRowIndex() >= 1 && event.getNewRowIndex() <= event.getRowset().size()) {
				Row newRow = event.getRowset().getRow(event.getNewRowIndex());
				changed(newRow);
			}
			*/
		}

		@Override
		public void beforeRequery(RowsetRequeryEvent event) {
			if (onLoadStart != null) {
				onLoadStart.run();
			}
		}

		@Override
		public void rowsetNetError(RowsetNetErrorEvent event) {
			if (onError != null) {
				try {
					onError.run(event.getMessage());
				} catch (Exception e) {
					Logger.getLogger(RowsetTree.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		}
	}
}
