package com.eas.client.gxtcontrols.grid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell.CheckBoxCellOptions;
import com.sencha.gxt.cell.core.client.form.RadioCell.RadioAppearance;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

public class RadioBoxSelectionModel<M> extends GridSelectionModel<M> {

	protected ColumnConfig<M, M> column;

	private final RadioAppearance appearance = GWT
			.<RadioAppearance> create(RadioAppearance.class);
	private final CheckBoxCellOptions radioOptions = new CheckBoxCellOptions();

	public RadioBoxSelectionModel(IdentityValueProvider<M> valueProvider) {
		super();
		selectionMode = SelectionMode.SINGLE;
		column = newColumnConfig(valueProvider);
		column.setColumnClassSuffix("checker");
		column.setWidth(25);
		column.setSortable(false);
		column.setResizable(false);
		column.setFixed(true);
		column.setMenuDisabled(true);

		column.setCell(new AbstractCell<M>() {
			@Override
			public void render(Context context, M item, SafeHtmlBuilder sb) {
				appearance.render(sb, getSelectedItem() == item, radioOptions);
			}
		});
	}

	/**
	 * Returns the column config.
	 * 
	 * @return the column config
	 */
	public ColumnConfig<M, M> getColumn() {
		return column;
	}

	@Override
	protected void handleRowMouseDown(RowMouseDownEvent event) {
		boolean left = event.getEvent().getButton() == Event.BUTTON_LEFT;
		if (left) {
			M newSelection = listStore.get(event.getRowIndex());
			select(newSelection, false);
		}
	}

	protected ColumnConfig<M, M> newColumnConfig(
			IdentityValueProvider<M> valueProvider) {
		return new ColumnConfig<M, M>(valueProvider);
	}

	protected boolean updating;
	protected boolean ifSelectWhileUpdating;

	protected void onUpdate(M item) {
		if (!updating)
			super.onUpdate(item);
		else {
			updating = false;
			super.onSelectChange(item, getSelectedItem() == item);
		}
	};

	@Override
	protected void onSelectChange(M item, boolean select) {
		ifSelectWhileUpdating = select;
		Store<M> store = null;
		if (grid instanceof TreeGrid) {
			store = ((TreeGrid<M>) grid).getTreeStore();
		} else
			store = grid.getStore();
		updating = true;
		store.update(item);
	}
}
