package com.eas.client.gxtcontrols.grid.wrappers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.grid.valueproviders.ChangesHost;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnData;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.RowExpander;
import com.sencha.gxt.widget.core.client.menu.Menu;

public class PlatypusGridView extends GridView<Row> {

	public PlatypusGridView() {
		super();
	}

	@Override
	protected void initColumnHeader() {
		// super.initColumnHeader();
		// This is dirty hack, but we have no any other outgo from Sencha's bug
		// when hiding
		// a column in multi-row header
		header = new ColumnHeader<Row>(grid, cm) {

			@Override
			protected Menu getContextMenu(int column) {
				return createContextMenu(column);
			}

			@Override
			protected void onColumnSplitterMoved(int colIndex, int width) {
				super.onColumnSplitterMoved(colIndex, width);
				PlatypusGridView.this.onColumnSplitterMoved(colIndex, width);
			}

			@Override
			protected void onHeaderClick(Event ce, int column) {
				super.onHeaderClick(ce, column);
				PlatypusGridView.this.onHeaderClick(column);
			}

			@Override
			protected void onKeyDown(Event ce, int index) {
				ce.stopPropagation();
				// auto select on key down
				if (grid.getSelectionModel() instanceof CellSelectionModel<?>) {
					CellSelectionModel<?> csm = (CellSelectionModel<?>) grid.getSelectionModel();
					csm.selectCell(0, index);
				} else {
					grid.getSelectionModel().select(0, false);
				}
			}

			@Override
			public void updateColumnHidden(int index, boolean hidden) {
				try {
					super.updateColumnHidden(index, hidden);
				} catch (Exception ex) {
					Logger.getLogger(PlatypusGridView.class.getName()).log(Level.SEVERE, "Sencha's bug while hiding columns in multi row grid header: " + ex.getMessage());
				}
			}

		};
		header.setSplitterWidth(splitterWidth);
		header.setMinColumnWidth(grid.getMinColumnWidth());
	}

	/**
	 * Renders the grid view into safe HTML.
	 * 
	 * @param cs
	 *            the column attributes required for rendering
	 * @param rows
	 *            the data models for the rows to be rendered
	 * @param startRow
	 *            the index of the first row in <code>rows</code>
	 */
	protected SafeHtml doRender(List<ColumnData> cs, List<Row> rows, int startRow) {
		int colCount = cm.getColumnCount();
		int last = colCount - 1;

		int[] columnWidths = getColumnWidths();

		// root builder
		SafeHtmlBuilder buf = new SafeHtmlBuilder();

		SafeStyles rowStyles = SafeStylesUtils.fromTrustedString("width: " + getTotalWidth() + "px;");

		String unselectableClass = " " + unselectable;
		String rowAltClass = " " + styles.rowAlt();
		String rowDirtyClass = " " + styles.rowDirty();

		String cellClass = styles.cell();
		String cellInnerClass = styles.cellInner();
		String cellFirstClass = " x-grid-cell-first";
		String cellLastClass = " x-grid-cell-last";
		String cellDirty = " " + styles.cellDirty();

		String rowWrap = styles.rowWrap();
		String rowBody = styles.rowBody();
		String rowBodyRow = styles.rowBodyRow();

		// loop over all rows
		for (int j = 0; j < rows.size(); j++) {
			Row model = rows.get(j);

			ListStore<Row>.Record r = ds.hasRecord(model) ? ds.getRecord(model) : null;

			int rowBodyColSpanCount = colCount;
			if (enableRowBody) {
				for (ColumnConfig<Row, ?> c : cm.getColumns()) {
					if (c instanceof RowExpander) {
						rowBodyColSpanCount--;
					}
				}
			}

			int rowIndex = (j + startRow);

			String rowClasses = styles.row();

			if (!selectable) {
				rowClasses += unselectableClass;
			}
			if (isStripeRows() && ((rowIndex + 1) % 2 == 0)) {
				rowClasses += rowAltClass;
			}

			if (super.isShowDirtyCells() && model.isUpdated()) {
				rowClasses += rowDirtyClass;
			}

			if (viewConfig != null) {
				rowClasses += " " + viewConfig.getRowStyle(model, rowIndex);
			}

			SafeHtmlBuilder trBuilder = new SafeHtmlBuilder();

			// loop each cell per row
			for (int i = 0; i < colCount; i++) {
				SafeHtml rv = getRenderedValue(rowIndex, i, model, r);
				ColumnConfig<Row, ?> columnConfig = cm.getColumn(i);
				ColumnData columnData = cs.get(i);

				String cellClasses = cellClass;
				cellClasses += (i == 0 ? cellFirstClass : (i == last ? cellLastClass : ""));

				if (columnConfig.getColumnTextClassName() != null) {
					cellInnerClass += " " + columnConfig.getColumnTextClassName();
				}

				String id = columnConfig.getColumnClassSuffix();

				if (columnData.getClassNames() != null) {
					cellClasses += " " + columnData.getClassNames();
				}

				if (id != null && !id.equals("")) {
					cellClasses += " x-grid-td-" + id;
				}
				if (columnConfig instanceof PlatypusColumnConfig) {
					assert ((PlatypusColumnConfig) columnConfig).getValueProvider() instanceof ChangesHost;
					ChangesHost ch = (ChangesHost) ((PlatypusColumnConfig) columnConfig).getValueProvider();
					if (super.isShowDirtyCells() && columnConfig instanceof PlatypusColumnConfig && ch.isChanged(model)) {
						cellClasses += cellDirty;
					}
				}

				if (viewConfig != null) {
					cellClasses += " " + viewConfig.getColStyle(model, cm.getValueProvider(i), rowIndex, i);
				}

				SafeStylesBuilder cellStyles = new SafeStylesBuilder();
				cellStyles.append(columnData.getStyles());

				SafeHtml tdContent = null;
				if (enableRowBody && i == 0) {
					tdContent = tpls.tdRowSpan(i, cellClasses, cellStyles.toSafeStyles(), super.getRowBodyRowSpan(), rv);
				} else {
					tdContent = tpls.td(i, cellClasses, cellStyles.toSafeStyles(), cellInnerClass, columnConfig.getColumnTextStyle(), rv);
				}
				trBuilder.append(tdContent);
			}

			if (enableRowBody) {
				String cls = styles.dataTable() + " x-grid-resizer";

				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				sb.append(tpls.tr("", trBuilder.toSafeHtml()));
				sb.appendHtmlConstant("<tr class=" + rowBodyRow + "><td colspan=" + rowBodyColSpanCount + "><div class=" + rowBody + "></div></td></tr>");

				buf.append(tpls.tr(rowClasses, tpls.tdWrap(colCount, "", rowWrap, tpls.table(cls, rowStyles, sb.toSafeHtml(), renderHiddenHeaders(columnWidths)))));

			} else {
				buf.append(tpls.tr(rowClasses, trBuilder.toSafeHtml()));
			}

		}
		// end row loop
		return buf.toSafeHtml();
	}

	public static void fitColumnsToSpace(ColumnModel<Row> cm, int aSpaceWidth) {
		int colsWidth = cm.getTotalWidth();
		int freeSpace = aSpaceWidth - colsWidth;
		int fixedSpace = 0;
		List<ColumnConfig<Row, ?>> columns = cm.getColumns();
		for (ColumnConfig<Row, ?> cc : columns) {
			if (cc.isFixed()) {
				fixedSpace += cc.getWidth();
			}
		}
		colsWidth -= fixedSpace;
		for (int i = 0; i < columns.size(); i++) {
			ColumnConfig<Row, ?> cc = columns.get(i);
			if (!cc.isFixed()) {
				float coef = (float) cc.getWidth() / (float) colsWidth;
				int targetWidth = cc.getWidth() + (int) (coef * (float) freeSpace);
				if (cc instanceof PlatypusColumnConfig<?, ?>) {
					PlatypusColumnConfig<?, ?> pcc = (PlatypusColumnConfig<?, ?>) cc;
					if (targetWidth < pcc.getDesignedWidth())
						targetWidth = pcc.getDesignedWidth();
				}
				cc.setWidth(targetWidth);
			}
		}
	}

	public void fitColumnsToSpace(int aSpaceWidth) {
		fitColumnsToSpace(cm, aSpaceWidth);
		updateAllColumnWidths();
		header.refresh();
	}
}
