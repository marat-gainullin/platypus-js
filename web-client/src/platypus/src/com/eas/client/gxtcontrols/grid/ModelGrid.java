package com.eas.client.gxtcontrols.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.CrossUpdater;
import com.eas.client.gxtcontrols.RowKeyProvider;
import com.eas.client.gxtcontrols.grid.wrappers.PlatypusGridInlineRowEditing;
import com.eas.client.gxtcontrols.grid.wrappers.PlatypusGridView;
import com.eas.client.gxtcontrols.grid.wrappers.PlatypusTreeGridView;
import com.eas.client.gxtcontrols.published.PublishedComponent;
import com.eas.client.gxtcontrols.published.PublishedStyle;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;

/**
 * Class intended to wrap a grid or tree grid. It also contains grid API.
 * 
 * @author mg
 * 
 */
public class ModelGrid extends ContentPanel {

	protected Grid<Row> grid;
	protected PlatypusGridInlineRowEditing editing;
	protected Entity rowsSource;
	protected JavaScriptObject generalCellFunction;
	protected PublishedComponent published;
	protected List<ModelGridColumn<?>> publishedColumns = new ArrayList<ModelGridColumn<?>>();
	protected Runnable crossUpdaterAction;
	protected CrossUpdater crossUpdater;
	protected FindWindow finder;

	/**
	 * Standalone constructor, assuming further configuration.
	 */
	public ModelGrid() {
		super();
		ListStore<Row> store = new ListStore<Row>(new RowKeyProvider());
		store.setAutoCommit(true);
		List<ColumnConfig<Row, ?>> columns = new ArrayList<ColumnConfig<Row, ?>>();
		ColumnModel<Row> cm = new ColumnModel<Row>(columns);
		grid = new Grid<Row>((ListStore<Row>) store, cm, new PlatypusGridView());
		finder = new FindWindow(grid);
		editing = new PlatypusGridInlineRowEditing(grid, null);
		grid.getView().setTrackMouseOver(true);
		grid.getView().setShowDirtyCells(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(true);
		grid.getSelectionModel().addSelectionHandler(new RowsetPositionSelectionHandler(rowsSource));
		crossUpdaterAction = new GridCrossUpdaterAction(grid);
		crossUpdater = new CrossUpdater(crossUpdaterAction);
		grid.addViewReadyHandler(new ViewReadyHandler() {
			@Override
			public void onViewReady(ViewReadyEvent event) {
				applyColorsFontCursor();
			}
		});
	}

	/**
	 * Factory constructor, using external grid and editing.
	 * 
	 * @param aGrid
	 * @param aEditing
	 */
	public ModelGrid(Grid<Row> aGrid, PlatypusGridInlineRowEditing aEditing) {
		super();
		grid = aGrid;
		finder = new FindWindow(grid);
		editing = aEditing;
		rowsSource = editing.getRowsSource();
		crossUpdaterAction = new GridCrossUpdaterAction(grid);
		crossUpdater = new CrossUpdater(crossUpdaterAction);
		setCollapsible(false);
		setHeaderVisible(false);
		setWidget(grid);
		grid.addViewReadyHandler(new ViewReadyHandler() {
			@Override
			public void onViewReady(ViewReadyEvent event) {
				applyColorsFontCursor();
			}
		});
	}

	protected void applyColorsFontCursor() {
		if (published.isBackgroundSet())
			ControlsUtils.applyBackground(grid, published.getBackground());
		if (published.isForegroundSet())
			ControlsUtils.applyForeground(grid, published.getForeground());
		if (published.isFontSet())
			ControlsUtils.applyFont(grid, published.getFont());
		if (published.isCursorSet())
			ControlsUtils.applyCursor(grid, published.getCursor());
	}

	public Runnable getCrossUpdaterAction() {
		return crossUpdaterAction;
	}

	public void addUpdatingTriggerEntity(Entity aTrigger) {
		crossUpdater.add(aTrigger);
	}

	public Entity getRowsSource() {
		return rowsSource;
	}

	public void setRowsSource(Entity aValue) {
		if (rowsSource != aValue) {
			rowsSource = aValue;
			editing.setRowsSource(rowsSource);
		}
	}

	public PublishedComponent getPublished() {
		return published;
	}

	public void setPublished(PublishedComponent aValue) {
		published = aValue;
		if (published != null) {
			for (ModelGridColumn<?> column : publishedColumns) {
				if (column.getName() != null && !column.getName().isEmpty()) {
					column.publish(published);
				}
			}
		}
	}

	public JavaScriptObject getGeneralCellFunction() {
		return generalCellFunction;
	}

	public void setGeneralCellFunction(JavaScriptObject aValue) {
		generalCellFunction = aValue;
	}

	public void selectRow(Row aRow) {
		if (grid != null)
			grid.getSelectionModel().select(aRow, true);
	}

	public void unselectRow(Row aRow) {
		if (grid != null)
			grid.getSelectionModel().deselect(aRow);
	}

	public List<JavaScriptObject> getJsSelected() throws Exception {
		List<JavaScriptObject> selected = new ArrayList<JavaScriptObject>();
		if (grid != null) {
			for (Row row : grid.getSelectionModel().getSelection())
				selected.add(Entity.publishRowFacade(row, rowsSource));
			return selected;
		} else
			return Collections.<JavaScriptObject> emptyList();
	}

	public void clearSelection() {
		if (grid != null)
			grid.getSelectionModel().deselectAll();
	}

	public boolean isEditable() {
		if (editing != null)
			return editing.isEditable();
		else
			return false;
	}

	public void setEditable(boolean aValue) {
		if (editing != null)
			editing.setEditable(aValue);
	}

	public boolean isDeletable() {
		if (editing != null)
			return editing.isDeletable();
		else
			return false;
	}

	public void setDeletable(boolean aValue) {
		if (editing != null)
			editing.setDeletable(aValue);
	}

	public boolean isInsertable() {
		if (editing != null)
			return editing.isInsertable();
		else
			return false;
	}

	public void setInsertable(boolean aValue) {
		if (editing != null)
			editing.setInsertable(aValue);
	}

	public boolean makeVisible(Row aRow, boolean needToSelect) {
		if (grid != null) {
			int row = grid.getStore().indexOf(aRow);
			if (row > -1) {
				int col = 0;
				Point pt = grid.getView().ensureVisible(row, col, true);
				if (needToSelect) {
					grid.getSelectionModel().select(aRow, true);
				}
				return pt != null;
			} else
				return false;
		} else
			return false;
	}

	public void find() {
		finder.show();
		finder.toFront();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		finder.hide();
	}

	public void addPublishedColumn(ModelGridColumn<?> aColumn) {
		aColumn.setGrid(this);
		publishedColumns.add(aColumn);
	}

	public void load() {
		if (grid instanceof TreeGrid<?>) {
			TreeLoader<?> loader =((TreeGrid<?>) grid).getTreeLoader();
			if(loader != null)
				loader.load();
		} else{
			ListLoader<?, ?> loader = grid.getLoader();
			if(loader != null)
				loader.load();
		}
	}

	public PublishedStyle complementPublishedStyle(PublishedStyle aStyle) {
		PublishedStyle complemented = aStyle;
		if (published.isBackgroundSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setBackground(published.getBackground());
		}
		if (published.isForegroundSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setForeground(published.getForeground());
		}
		if (published.isFontSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setFont(published.getFont());
		}
		return complemented;
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		if (isAttached()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					if (grid instanceof TreeGrid<?>) {
						TreeGridView<?> tgv = ((TreeGrid<?>) grid).getTreeView();
						if (tgv instanceof PlatypusTreeGridView) {
							PlatypusTreeGridView ptgv = (PlatypusTreeGridView) tgv;
							ptgv.fitColumnsToSpace(grid.getElement().getScrollWidth());
						}
					} else {
						GridView<?> gv = grid.getView();
						if (gv instanceof PlatypusGridView) {
							PlatypusGridView pgv = (PlatypusGridView) gv;
							pgv.fitColumnsToSpace(grid.getElement().getScrollWidth());
						}
					}
				}
			});
		}
	}

}
