package com.eas.client.gxtcontrols.grid;

import com.eas.client.gxtcontrols.grid.wrappers.PlatypusTreeGridView;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.form.FieldCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class FieldCellsColumnResizePropagator<M, T> implements ColumnWidthChangeHandler {

	private Grid<M> grid;

	public FieldCellsColumnResizePropagator(Grid<M> aGrid) {
		grid = aGrid;
	}

	@Override
	public void onColumnWidthChange(ColumnWidthChangeEvent event) {
		ColumnConfig<M, T> column = (ColumnConfig<M, T>) event.getColumnConfig();
		if (column.getCell() instanceof FieldCell<?>) {
			FieldCell<T> cell = (FieldCell<T>) column.getCell();
			int w = event.getColumnConfig().getWidth();
			int rows = grid.getStore().size();

			int col = event.getIndex();

			cell.setWidth(w);

			Store<M> store = grid.getStore();

			for (int i = 0; i < rows; i++) {
				M p = grid.getStore().get(i);

				// option 1 could be better for force fit where all columns are
				// resized
				// would need to run deferred using DelayedTask to ensure only
				// run
				// once
				// grid.getStore().update(p);

				// option 2
				final TableCellElement parent = (TableCellElement) grid.getView().getCell(i, col);
				if (parent != null) {
					// detemine where to apply rendered html 
					XElement cellInnerElement = null;
					if (grid.getView() instanceof PlatypusTreeGridView) {
						PlatypusTreeGridView pTreeView = (PlatypusTreeGridView) grid.getView();
						String cellInnerClass = pTreeView.getFirstCellTextSelector();
						// The cellInnerClass is already with dot prefix
						cellInnerElement = parent.<XElement> cast().selectNode(cellInnerClass);
						if (cellInnerElement == null) {
							cellInnerClass = grid.getView().getAppearance().styles().cellInner();
							cellInnerElement = parent.<XElement> cast().selectNode("." + cellInnerClass);
						}
					} else {
						String cellInnerClass = grid.getView().getAppearance().styles().cellInner();
						cellInnerElement = parent.<XElement> cast().selectNode("." + cellInnerClass);
					}
					
					SafeHtmlBuilder sb = new SafeHtmlBuilder();
					cell.render(new Context(i, col, store.getKeyProvider().getKey(p)), column.getValueProvider().getValue(p), sb);
					cellInnerElement.setInnerSafeHtml(sb.toSafeHtml());
				}

			}
		}
	}

}