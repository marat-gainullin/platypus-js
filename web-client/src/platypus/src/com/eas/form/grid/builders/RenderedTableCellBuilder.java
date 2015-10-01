package com.eas.form.grid.builders;

import com.eas.form.grid.GridSection;
import com.eas.form.grid.RenderedCellContext;
import com.eas.form.grid.ThemedCellTableBuilder;
import com.eas.form.published.PublishedCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.builder.shared.TableCellBuilder;

public class RenderedTableCellBuilder<T> extends ThemedCellTableBuilder<T> {

	public RenderedTableCellBuilder(GridSection<T> cellTable, String aDynamicTDClassName, String aDynamicCellClassName, String aDynamicOddRowsClassName, String aDynamicEvenRowsClassName) {
		super(cellTable, aDynamicTDClassName, aDynamicCellClassName, aDynamicOddRowsClassName, aDynamicEvenRowsClassName);
	}

	@Override
	protected Cell.Context createCellContext(int aIndex, int aColumn, Object aKey) {
		return new RenderedCellContext(aIndex, aColumn, aKey);
	}

	@Override
	protected void tdGenerated(TableCellBuilder aTd, Cell.Context aContext) {
		if (aContext instanceof RenderedCellContext) {
			PublishedCell pCell = ((RenderedCellContext) aContext).getPublishedCell();
			if (pCell != null && pCell.getBackground() != null) {
				aTd.style().trustedBackgroundColor(pCell.getBackground().toStyled());
			} else {
				super.tdGenerated(aTd, aContext);
			}
		} else {
			super.tdGenerated(aTd, aContext);
		}
	}
}
