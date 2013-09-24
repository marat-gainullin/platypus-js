package com.eas.client.gxtcontrols.grid.rowmarker;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.eas.client.beans.PropertyChangeEvent;
import com.eas.client.beans.PropertyChangeListener;
import com.eas.client.model.Entity;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ComponentPlugin;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;

public class RowMarker extends ColumnConfig<Row, Row> implements ComponentPlugin<Grid<Row>> {

	public interface RowMarkerAppearance {

	}

	private class Handler extends RowsetAdapter {

		@Override
		public void rowsetScrolled(RowsetScrollEvent event) {
			RowMarker.this.doUpdate();
		}

	}

	protected Entity rowsSource;
	protected Grid<Row> grid;

	private Handler handler = new Handler();

	/**
	 * Creates a row numberer. To use the row numberer, add it to a column
	 * model, create a grid with the column model and then invoke
	 * {@link RowNumberer#initPlugin(Grid)} on the grid.
	 * 
	 * @param valueProvider
	 *            an identity value provider (e.g. new
	 *            IdentityValueProvider<M>()).
	 */
	public RowMarker(Entity aRowsSource, IdentityValueProvider<Row> valueProvider) {
		super(valueProvider);
		rowsSource = aRowsSource;
		if (rowsSource.getRowset() == null) {
			rowsSource.getChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if ("rowset".equals(evt.getPropertyName()) && evt.getNewValue() != null && evt.getOldValue() == null) {
						assert rowsSource.getRowset() != null;
						rowsSource.getRowset().addRowsetListener(handler);
						final PropertyChangeListener toRemove = this;
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {

							@Override
							public void execute() {
								rowsSource.getChangeSupport().removePropertyChangeListener(toRemove);
							}

						});
					}
				}
			});
		} else
			rowsSource.getRowset().addRowsetListener(handler);

		setHeader("");
		setWidth(23);
		setColumnClassSuffix("numberer");
		// setColumnStyle();
		setSortable(false);
		setResizable(false);
		setFixed(true);
		setHideable(false);
		setMenuDisabled(true);

		setCell(new AbstractCell<Row>() {
			@Override
			public void render(Context context, Row value, SafeHtmlBuilder sb) {
				RowMarkerResources.INSTANCE.style().ensureInjected();
				StringBuilder bl = new StringBuilder();
				bl.append(RowMarkerResources.INSTANCE.style().rowMarkerLeft());
				StringBuilder br = new StringBuilder();
				br.append(RowMarkerResources.INSTANCE.style().rowMarkerRight());
				boolean currentRow = rowsSource.getRowset() != null && rowsSource.getRowset().getCurrentRow() == value;
				if (currentRow)
					br.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerCurrent());
				if (value.isInserted())
					bl.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerNew());
				else if (value.isUpdated())
					bl.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerEdited());
				sb.appendHtmlConstant("<div class=\"" + bl.toString() + "\">&nbsp;</div><div class=\"" + br.toString() + "\">&nbsp;</div>");
			}
		});
	}

	@Override
	public void initPlugin(Grid<Row> component) {
		this.grid = component;
	}

	@SuppressWarnings("unchecked")
	protected void doUpdate() {
		int col = grid.getColumnModel().indexOf(this);
		ModelKeyProvider<Row> kp = (ModelKeyProvider<Row>) grid.getStore().getKeyProvider();
		for (int i = 0, len = grid.getStore().size(); i < len; i++) {
			Element cell = grid.getView().getCell(i, col);
			if (cell != null) {
				//SafeHtmlBuilder sb = new SafeHtmlBuilder();
				Row toRender = grid.getStore().get(i);
				boolean currentRow = rowsSource.getRowset() != null && rowsSource.getRowset().getCurrentRow() == toRender;
				XElement xCell = cell.getFirstChildElement().<XElement> cast();
				XElement left = xCell.selectNode("."+RowMarkerResources.INSTANCE.style().rowMarkerLeft());
				XElement right = xCell.selectNode("."+RowMarkerResources.INSTANCE.style().rowMarkerRight());
				right.removeClassName(RowMarkerResources.INSTANCE.style().rowMarkerEdited());
				right.removeClassName(RowMarkerResources.INSTANCE.style().rowMarkerNew());
				if (toRender.isInserted())
					left.addClassName(RowMarkerResources.INSTANCE.style().rowMarkerNew());
				else if (toRender.isUpdated())
					left.addClassName(RowMarkerResources.INSTANCE.style().rowMarkerEdited());

				if (currentRow)
					right.addClassName(RowMarkerResources.INSTANCE.style().rowMarkerCurrent());
				else
					right.removeClassName(RowMarkerResources.INSTANCE.style().rowMarkerCurrent());
				/*
				 * getCell().render(new Context(i, col, kp.getKey(toRender)),
				 * toRender, sb);
				 * cell.getFirstChildElement().setInnerHTML(sb.toSafeHtml
				 * ().asString());
				 */
			}
		}
	}
}
