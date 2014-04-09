package com.eas.client.form.published.widgets.model;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.bearsoft.gwt.ui.widgets.grid.builders.ThemedHeaderOrFooterBuilder;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderSplitter;
import com.bearsoft.gwt.ui.widgets.grid.processing.ListMultiSortHandler;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.events.RowsetEvent;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.CrossUpdater;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.grid.FindWindow;
import com.eas.client.form.grid.GridCrossUpdaterAction;
import com.eas.client.form.grid.RowsetPositionSelectionHandler;
import com.eas.client.form.grid.cells.rowmarker.RowMarkerCell;
import com.eas.client.form.grid.columns.CheckServiceColumn;
import com.eas.client.form.grid.columns.RadioServiceColumn;
import com.eas.client.form.grid.rows.RowsetDataProvider;
import com.eas.client.form.grid.selection.MultiRowSelectionModel;
import com.eas.client.form.grid.selection.SingleRowSelectionModel;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasOnRender;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.view.client.SelectionModel;

/**
 * Class intended to wrap a grid or tree grid. It also contains grid API.
 * 
 * @author mg
 * 
 */
public class ModelGrid extends Grid<Row> implements HasJsFacade, HasOnRender, HasComponentPopupMenu, HasEventsExecutor {

	public static final int ROWS_HEADER_TYPE_NONE = 0;
	public static final int ROWS_HEADER_TYPE_USUAL = 1;
	public static final int ROWS_HEADER_TYPE_CHECKBOX = 2;
	public static final int ROWS_HEADER_TYPE_RADIOBUTTON = 3;

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	//
	protected Entity rowsSource;
	protected JavaScriptObject onRender;
	protected PublishedComponent published;
	protected Callback<RowsetEvent, RowsetEvent> crossUpdaterAction;
	protected CrossUpdater crossUpdater;
	protected FindWindow finder;
	protected int rowsHeaderType;
	protected List<HeaderNode> header = new ArrayList<>();
	// runtime
	protected ListHandler<Row> sortHandler;
	protected HandlerRegistration positionSelectionHandler;
	protected boolean editable;
	protected boolean deletable;
	protected boolean insertable;

	public ModelGrid() {
		super(new RowKeyProvider());
		finder = new FindWindow(this);
		crossUpdaterAction = new GridCrossUpdaterAction(this);
		crossUpdater = new CrossUpdater(crossUpdaterAction);
		setDataProvider(new RowsetDataProvider(null, new Runnable() {
			@Override
			public void run() {
				setupVisibleRanges();
			}

		}));
		sortHandler = new ListMultiSortHandler<>(dataProvider.getList());
		addColumnSortHandler(sortHandler);
	}

	public ListHandler<Row> getSortHandler() {
		return sortHandler;
	}

	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
	}

	@Override
	public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu;
	}

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {

					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
			}
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public List<HeaderNode> getHeader() {
		return header;
	}

	public void setHeader(List<HeaderNode> aHeader) {
		if (header != aHeader) {
			header = aHeader;
			applyHeader();
		}
	}

	@Override
	public void setFrozenColumns(int aValue) {
		if (frozenColumns != aValue) {
			super.setFrozenColumns(aValue);
			applyHeader();
		}
	}

	public int getRowsHeaderType() {
		return rowsHeaderType;
	}

	public void setRowsHeaderType(int aValue) {
		if (rowsHeaderType != aValue) {
			if (rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX || rowsHeaderType == ROWS_HEADER_TYPE_RADIOBUTTON || rowsHeaderType == ROWS_HEADER_TYPE_USUAL) {
				removeColumn(0);
			}
			rowsHeaderType = aValue;
			SelectionModel<Row> sm;
			if (rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX) {
				sm = new MultiRowSelectionModel();
				insertColumn(0, new CheckServiceColumn(sm), "\\", null);
			} else if (rowsHeaderType == ROWS_HEADER_TYPE_RADIOBUTTON) {
				sm = new SingleRowSelectionModel();
				insertColumn(0, new RadioServiceColumn(sm), "\\", null);
			} else if (rowsHeaderType == ROWS_HEADER_TYPE_USUAL) {
				sm = new MultiRowSelectionModel();
				IdentityColumn<Row> col = new IdentityColumn<>(new RowMarkerCell(rowsSource.getRowset()));
				insertColumn(0, col, "\\", null);
				Header<?> identityHeader = getColumnHeader(0);
				((DraggableHeader<?>) identityHeader).setMoveable(false);
				((DraggableHeader<?>) identityHeader).setResizable(false);
				setColumnWidth(col, 20, Style.Unit.PX);
			} else {
				sm = new MultiRowSelectionModel();
			}
			setSelectionModel(sm);
		}
	}

	protected void applyHeader() {
		ThemedHeaderOrFooterBuilder<Row> leftBuilder = (ThemedHeaderOrFooterBuilder<Row>) headerLeft.getHeaderBuilder();
		ThemedHeaderOrFooterBuilder<Row> rightBuilder = (ThemedHeaderOrFooterBuilder<Row>) headerRight.getHeaderBuilder();
		List<HeaderNode> leftHeader = HeaderSplitter.split(header, 0, frozenColumns);
		leftBuilder.setHeaderNodes(leftHeader);
		List<HeaderNode> rightHeader = HeaderSplitter.split(header, frozenColumns, getDataColumnCount());
		rightBuilder.setHeaderNodes(rightHeader);
		redrawHeaders();
	}

	@Override
	public void setSelectionModel(SelectionModel<Row> aValue) {
		assert aValue != null : "Selection model can't be null.";
		SelectionModel<? super Row> oldValue = getSelectionModel();
		if (aValue != oldValue) {
			if (positionSelectionHandler != null)
				positionSelectionHandler.removeHandler();
			super.setSelectionModel(aValue);
			positionSelectionHandler = aValue.addSelectionChangeHandler(new RowsetPositionSelectionHandler(rowsSource, aValue));
		}
	}

	protected void applyColorsFontCursor() {
		if (published.isBackgroundSet() && published.isOpaque())
			ControlsUtils.applyBackground(this, published.getBackground());
		if (published.isForegroundSet())
			ControlsUtils.applyForeground(this, published.getForeground());
		if (published.isFontSet())
			ControlsUtils.applyFont(this, published.getFont());
		if (published.isCursorSet())
			ControlsUtils.applyCursor(this, published.getCursor());
	}

	public Callback<RowsetEvent, RowsetEvent> getCrossUpdaterAction() {
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
			((RowsetDataProvider) dataProvider).setRowset(rowsSource.getRowset());
		}
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	public void setPublished(JavaScriptObject aValue) {
		published = aValue != null ? aValue.<PublishedComponent> cast() : null;
		if (published != null) {
			// Here were a cycle setting published to each column and publish
			// call
		}
	}

	public JavaScriptObject getOnRender() {
		return onRender;
	}

	public void setOnRender(JavaScriptObject aValue) {
		onRender = aValue;
	}

	public void selectRow(Row aRow) {
		getSelectionModel().setSelected(aRow, true);
	}

	public void unselectRow(Row aRow) {
		getSelectionModel().setSelected(aRow, false);
	}

	public List<JavaScriptObject> getJsSelected() throws Exception {
		List<JavaScriptObject> result = new ArrayList<>();
		for (Row row : dataProvider.getList()) {
			if (getSelectionModel().isSelected(row))
				result.add(Entity.publishRowFacade(row, rowsSource));
		}
		return result;
	}

	public void clearSelection() {
		SelectionModel<? super Row> sm = getSelectionModel();
		for (Row row : dataProvider.getList()) {
			if (getSelectionModel().isSelected(row)) {
				sm.setSelected(row, false);
			}
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean aValue) {
		editable = aValue;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean aValue) {
		deletable = aValue;
	}

	public boolean isInsertable() {
		return insertable;
	}

	public void setInsertable(boolean aValue) {
		insertable = aValue;
	}

	public boolean makeVisible(Row aRow, boolean needToSelect) {
		return false;
	}

	public void find() {
		finder.show();
		finder.toFront();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		finder.close();
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
}
