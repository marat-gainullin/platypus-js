package com.eas.client.form.published.widgets.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.bearsoft.gwt.ui.widgets.grid.GridSelectionEventManager;
import com.bearsoft.gwt.ui.widgets.grid.builders.ThemedHeaderOrFooterBuilder;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderSplitter;
import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
import com.bearsoft.gwt.ui.widgets.grid.processing.ListMultiSortHandler;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeDataProvider;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeDataProvider.ExpandedCollapsedHandler;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeMultiSortHandler;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Utils.JsObject;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.grid.FindWindow;
import com.eas.client.form.grid.RowsetPositionSelectionHandler;
import com.eas.client.form.grid.cells.rowmarker.RowMarkerCell;
import com.eas.client.form.grid.columns.CheckServiceColumn;
import com.eas.client.form.grid.columns.ModelGridColumn;
import com.eas.client.form.grid.columns.ModelGridColumnFacade;
import com.eas.client.form.grid.columns.RadioServiceColumn;
import com.eas.client.form.grid.columns.UsualServiceColumn;
import com.eas.client.form.grid.rows.RowChildrenFetcher;
import com.eas.client.form.grid.rows.RowsetDataProvider;
import com.eas.client.form.grid.rows.RowsetTree;
import com.eas.client.form.grid.selection.CheckBoxesEventTranslator;
import com.eas.client.form.grid.selection.MultiRowSelectionModel;
import com.eas.client.form.grid.selection.SingleRowSelectionModel;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasOnRender;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SetSelectionModel;

/**
 * Class intended to wrap a grid or tree grid. It also contains grid API.
 * 
 * @author mg
 * 
 */
public class ModelGrid extends Grid<Row> implements HasJsFacade, HasOnRender, HasComponentPopupMenu, HasEventsExecutor, HasEnabled, HasShowHandlers, HasHideHandlers, HasResizeHandlers {

	public static final int ROWS_HEADER_TYPE_NONE = 0;
	public static final int ROWS_HEADER_TYPE_USUAL = 1;
	public static final int ROWS_HEADER_TYPE_CHECKBOX = 2;
	public static final int ROWS_HEADER_TYPE_RADIOBUTTON = 3;
	//
	public static final int ONE_FIELD_ONE_QUERY_TREE_KIND = 1;
	public static final int FIELD_2_PARAMETER_TREE_KIND = 2;
	public static final int SCRIPT_PARAMETERS_TREE_KIND = 3;

	public static final int SERVICE_COLUMN_WIDTH = 22;

	protected class RowMarkerRerenderer extends RowsetAdapter {

		@Override
		public void rowsetScrolled(RowsetScrollEvent event) {
			if (getDataColumnCount() > 0 && getDataColumn(0) instanceof UsualServiceColumn) {
				if (frozenColumns > 0) {
					frozenLeft.redrawAllRowsInColumn(0, ModelGrid.this.dataProvider);
					scrollableLeft.redrawAllRowsInColumn(0, ModelGrid.this.dataProvider);
				} else {
					frozenRight.redrawAllRowsInColumn(0, ModelGrid.this.dataProvider);
					scrollableRight.redrawAllRowsInColumn(0, ModelGrid.this.dataProvider);
				}
			}
		}
	}

	protected boolean enabled = true;
	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	//
	protected int treeKind = ONE_FIELD_ONE_QUERY_TREE_KIND;
	protected ModelElementRef unaryLinkField;
	protected ModelElementRef param2GetChildren;
	protected ModelElementRef paramSourceField;
	//
	protected Entity rowsSource;
	protected RowMarkerRerenderer markerRerenderer = new RowMarkerRerenderer();
	protected JavaScriptObject onRender;
	protected PublishedComponent published;
	protected FindWindow finder;
	protected String groupName = "group-name-" + Document.get().createUniqueId();
	protected int rowsHeaderType = -1;
	protected List<HeaderNode> header = new ArrayList<>();
	// runtime
	protected ListHandler<Row> sortHandler;
	protected HandlerRegistration sortHandlerReg;
	protected HandlerRegistration positionSelectionHandler;
	protected boolean editable;
	protected boolean deletable;
	protected boolean insertable;

	public ModelGrid() {
		super(new RowKeyProvider());
		addDomHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				try {
					if (event.getNativeKeyCode() == KeyCodes.KEY_DELETE && deletable) {
						if (getSelectionModel() instanceof SetSelectionModel<?>) {
							SetSelectionModel<Row> rowSelection = (SetSelectionModel<Row>) getSelectionModel();
							rowsSource.getRowset().delete(rowSelection.getSelectedSet());
						}
					} else if (event.getNativeKeyCode() == KeyCodes.KEY_INSERT && insertable) {
						rowsSource.getRowset().insert();
						Row inserted = rowsSource.getRowset().getCurrentRow();
						if (inserted != null && getSelectionModel() instanceof SetSelectionModel<?>) {
							SetSelectionModel<Row> rowSelection = (SetSelectionModel<Row>) getSelectionModel();
							rowSelection.clear();
							rowSelection.setSelected(inserted, true);
						}
					}
				} catch (RowsetException e) {
					Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, e);
				}
			}

		}, KeyUpEvent.getType());
		addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F) {
					event.stopPropagation();
					event.preventDefault();
					ModelGrid.this.find();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_F3) {
					event.stopPropagation();
					event.preventDefault();
					if (finder != null) {
						finder.findNext();
					}
				}
			}
		}, KeyDownEvent.getType());
	}

	public ModelElementRef getUnaryLinkField() {
		return unaryLinkField;
	}

	public void setUnaryLinkField(ModelElementRef aValue) {
		unaryLinkField = aValue;
	}

	public ModelElementRef getParam2GetChildren() {
		return param2GetChildren;
	}

	public void setParam2GetChildren(ModelElementRef aValue) {
		param2GetChildren = aValue;
	}

	public ModelElementRef getParamSourceField() {
		return paramSourceField;
	}

	public void setParamSourceField(ModelElementRef aValue) {
		paramSourceField = aValue;
	}

	public int getTreeKind() {
		return treeKind;
	}

	public void setTreeKind(int aValue) {
		treeKind = aValue;
	}

	public ListHandler<Row> getSortHandler() {
		return sortHandler;
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return addHandler(handler, HideEvent.getType());
	}

	@Override
	public HandlerRegistration addShowHandler(ShowHandler handler) {
		return addHandler(handler, ShowEvent.getType());
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldValue = isVisible();
		super.setVisible(visible);
		if (oldValue != visible) {
			if (visible) {
				ShowEvent.fire(this, this);
			} else {
				HideEvent.fire(this, this);
			}
		}
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
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		boolean oldValue = enabled;
		enabled = aValue;
		if (!oldValue && enabled) {
			getElement().<XElement> cast().unmask();
		} else if (oldValue && !enabled) {
			getElement().<XElement> cast().disabledMask();
		}
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

	/*
	 * Indicates that subsequent changes will take no effect in general columns
	 * collection and header. They will affect only underlying grid sections
	 */
	protected boolean columnsAjusting;

	public boolean isColumnsAjusting() {
		return columnsAjusting;
	}

	public void setColumnsAjusting(boolean aValue) {
		columnsAjusting = aValue;
	}

	@Override
	protected void refreshColumns() {
		columnsAjusting = true;
		try {
			super.refreshColumns();
		} finally {
			columnsAjusting = false;
		}
		applyHeader();
	}

	public int getRowsHeaderType() {
		return rowsHeaderType;
	}

	public void setRowsHeaderType(int aValue) {
		if (rowsHeaderType != aValue) {
			if (rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX || rowsHeaderType == ROWS_HEADER_TYPE_RADIOBUTTON || rowsHeaderType == ROWS_HEADER_TYPE_USUAL) {
				header.remove(0);
				super.removeColumn(0);
			}
			boolean needRefreshColumns = getDataColumnCount() > 0;
			rowsHeaderType = aValue;
			SelectionModel<Row> sm;
			if (rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX) {
				sm = new MultiRowSelectionModel(this);
				Header<String> colHeader = new TextHeader(" ");
				super.addColumn(true, 0, new CheckServiceColumn(sm), SERVICE_COLUMN_WIDTH + "px", colHeader, null, false);
				header.add(0, new HeaderNode(colHeader));
			} else if (rowsHeaderType == ROWS_HEADER_TYPE_RADIOBUTTON) {
				sm = new SingleRowSelectionModel(this);
				Header<String> colHeader = new TextHeader(" ");
				super.addColumn(true, 0, new RadioServiceColumn(groupName, sm), SERVICE_COLUMN_WIDTH + "px", colHeader, null, false);
				header.add(0, new HeaderNode(colHeader));
			} else if (rowsHeaderType == ROWS_HEADER_TYPE_USUAL) {
				sm = new MultiRowSelectionModel(this);
				Header<String> colHeader = new TextHeader(" ");
				UsualServiceColumn col = new UsualServiceColumn(new RowMarkerCell(rowsSource != null ? rowsSource.getRowset() : null));
				super.addColumn(true, 0, col, SERVICE_COLUMN_WIDTH + "px", colHeader, null, false);
				header.add(0, new HeaderNode(colHeader));
			} else {
				sm = new MultiRowSelectionModel(this);
			}
			setSelectionModel(sm);
			applyRowsHeaderTypeToSelectionModel();
			if (needRefreshColumns) {
				refreshColumns();
				applyHeader();
			}
		}
	}

	private boolean isLazyTreeConfigured() {
		return param2GetChildren.isCorrect() && param2GetChildren.field != null && paramSourceField.isCorrect() && paramSourceField.field != null;
	}

	private boolean isTreeConfigured() throws Exception {
		return rowsSource != null && unaryLinkField.isCorrect() && unaryLinkField.field != null && (treeKind == ONE_FIELD_ONE_QUERY_TREE_KIND || treeKind == FIELD_2_PARAMETER_TREE_KIND);
	}

	@Override
	public void addColumn(boolean forceRefreshColumns, int aIndex, Column<Row, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		Column<Row, ?> shifted = aIndex >= 0 && aIndex < getDataColumnCount() ? getDataColumn(aIndex) : null;
		if (shifted != null && !(shifted instanceof ModelGridColumn<?>)) {
			// won't shift service column
			aIndex++;
			shifted = getDataColumn(aIndex);
		}
		super.addColumn(forceRefreshColumns, aIndex, aColumn, aWidth, aHeader, aFooter, hidden);
		if (aColumn instanceof ModelGridColumn<?> && !columnsAjusting) {
			ModelGridColumn<?> mInserted = (ModelGridColumn<?>) aColumn;
			mInserted.setGrid(this);
			if (shifted != null) {
				assert shifted instanceof ModelGridColumn<?>;
				ModelGridColumn<?> mShifted = (ModelGridColumn<?>) shifted;
				HeaderNode hParent = mShifted.getHeaderNode().getParent();
				if (hParent != null) {
					int hIndex = hParent.getChildren().indexOf(mShifted.getHeaderNode());
					hParent.getChildren().add(hIndex, mInserted.getHeaderNode());
					mInserted.getHeaderNode().setParent(hParent);
				} else {
					int hIndex = header.indexOf(mShifted.getHeaderNode());
					header.add(hIndex, mInserted.getHeaderNode());
				}
			} else {
				header.add(mInserted.getHeaderNode());
			}
			applyHeader();
		}
	}

	public void addColumn(ModelGridColumn<?> aColumn) {
		addColumn(getDataColumnCount(), aColumn);
	}

	public void addColumn(int aIndex, ModelGridColumn<?> aColumn) {
		addColumn(aIndex, aColumn, aColumn.getWidth() + "px", aColumn.getHeaderNode().getHeader(), null, !aColumn.isVisible());
	}

	protected ModelGridColumn<?> treeIndicatorColumn;

	@Override
	public void addColumn(int aIndex, Column<Row, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		super.addColumn(aIndex, aColumn, aWidth, aHeader, aFooter, hidden);
		if (treeIndicatorColumn == null) {
			int treeIndicatorIndex = rowsHeaderType == ROWS_HEADER_TYPE_NONE ? 0 : 1;
			if (treeIndicatorIndex < getDataColumnCount()) {
				Column<Row, ?> indicatorColumn = getDataColumn(treeIndicatorIndex);
				if (indicatorColumn instanceof ModelGridColumn<?>) {
					treeIndicatorColumn = (ModelGridColumn<?>) indicatorColumn;
					if (dataProvider instanceof TreeDataProvider<?> && treeIndicatorColumn.getCell() instanceof TreeExpandableCell<?, ?>) {
						TreeExpandableCell<Row, ?> treeCell = (TreeExpandableCell<Row, ?>) treeIndicatorColumn.getCell();
						treeCell.setDataProvider((TreeDataProvider<Row>) dataProvider);
					}
				}
			}
		}
	}

	@Override
	public void removeColumn(int aIndex) {
		Column<Row, ?> toDel = getDataColumn(aIndex);
		if (toDel instanceof ModelGridColumn<?>) {
			ModelGridColumn<?> mCol = (ModelGridColumn<?>) toDel;
			if (mCol == treeIndicatorColumn) {
				TreeExpandableCell<Row, ?> treeCell = (TreeExpandableCell<Row, ?>) mCol.getCell();
				if (treeCell.getDataProvider() != null) {
					treeCell.setDataProvider(null);
				}
				treeIndicatorColumn = null;
			}
			super.removeColumn(aIndex);
			if (!columnsAjusting) {
				HeaderNode colNode = mCol.getHeaderNode();
				HeaderNode parent = mCol.getHeaderNode().getParent();
				while (parent != null) {
					parent.getChildren().remove(colNode);
					colNode.setParent(null);
					if (!parent.getChildren().isEmpty())
						break;
					colNode = parent;
					parent = parent.getParent();
				}
				if (parent == null)
					header.remove(colNode);
				mCol.setGrid(null);
				applyHeader();
			}
		} else {
			// won't remove service columns
		}
	}

	@Override
	public void setColumnWidthFromHeaderDrag(Column<Row, ?> aColumn, double aWidth, Unit aUnit) {
		super.setColumnWidth(aColumn, aWidth, aUnit);
		if (aColumn instanceof ModelGridColumn<?>) {
			ModelGridColumn<?> colFacade = (ModelGridColumn<?>) aColumn;
			colFacade.setWidth(aWidth);
		}
	}

	@Override
	public void setColumnWidth(Column<Row, ?> aColumn, double aWidth, Unit aUnit) {
		super.setColumnWidth(aColumn, aWidth, aUnit);
		if (aColumn instanceof ModelGridColumn<?>) {
			ModelGridColumn<?> colFacade = (ModelGridColumn<?>) aColumn;
			colFacade.updateWidth(aWidth);
		}
	}

	@Override
	public void showColumn(Column<Row, ?> aColumn) {
		super.showColumn(aColumn);
		if (aColumn instanceof ModelGridColumnFacade) {
			ModelGridColumn<?> colFacade = (ModelGridColumn<?>) aColumn;
			colFacade.updateVisible(true);
		} else if (aColumn instanceof UsualServiceColumn || aColumn instanceof CheckServiceColumn || aColumn instanceof RadioServiceColumn) {
			super.setColumnWidth(aColumn, SERVICE_COLUMN_WIDTH, Style.Unit.PX);
		}
	}

	public void hideColumn(Column<Row, ?> aColumn) {
		super.hideColumn(aColumn);
		if (aColumn instanceof ModelGridColumnFacade) {
			ModelGridColumn<?> colFacade = (ModelGridColumn<?>) aColumn;
			colFacade.updateVisible(false);
		}
	}

	protected boolean headerAjusting;

	public boolean isHeaderAjusting() {
		return headerAjusting;
	}

	public void setHeaderAjusting(boolean aValue) {
		headerAjusting = aValue;
	}

	public void applyHeader() {
		if (!headerAjusting) {
			ThemedHeaderOrFooterBuilder<Row> leftBuilder = (ThemedHeaderOrFooterBuilder<Row>) headerLeft.getHeaderBuilder();
			ThemedHeaderOrFooterBuilder<Row> rightBuilder = (ThemedHeaderOrFooterBuilder<Row>) headerRight.getHeaderBuilder();
			List<HeaderNode> leftHeader = HeaderSplitter.split(header, 0, frozenColumns);
			leftBuilder.setHeaderNodes(leftHeader);
			List<HeaderNode> rightHeader = HeaderSplitter.split(header, frozenColumns, getDataColumnCount());
			rightBuilder.setHeaderNodes(rightHeader);
			redrawHeaders();
		}
	}

	@Override
	public void setSelectionModel(SelectionModel<Row> aValue) {
		assert aValue != null : "Selection model can't be null.";
		SelectionModel<? super Row> oldValue = getSelectionModel();
		if (aValue != oldValue) {
			if (positionSelectionHandler != null)
				positionSelectionHandler.removeHandler();
			super.setSelectionModel(aValue);
			applyRowsHeaderTypeToSelectionModel();
			positionSelectionHandler = aValue.addSelectionChangeHandler(new RowsetPositionSelectionHandler(rowsSource, aValue));
		}
	}

	protected DefaultSelectionEventManager<Row> createSelectionEventManager(){
		return rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX ? GridSelectionEventManager.<Row>create(new CheckBoxesEventTranslator<>(getDataColumn(0)))
		        : GridSelectionEventManager.<Row>create();
	}
	
	protected void applyRowsHeaderTypeToSelectionModel() {
		frozenLeft.setSelectionModel(frozenLeft.getSelectionModel(), createSelectionEventManager());
		frozenRight.setSelectionModel(frozenRight.getSelectionModel(), createSelectionEventManager());
		scrollableLeft.setSelectionModel(scrollableLeft.getSelectionModel(), createSelectionEventManager());
		scrollableRight.setSelectionModel(scrollableRight.getSelectionModel(), createSelectionEventManager());
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

	public Entity getRowsSource() {
		return rowsSource;
	}

	/**
	 * Sets entity instance, that have to be used as rows source. Configures
	 * tree if needed.
	 * 
	 * @param aValue
	 * @throws Exception
	 */
	public void setRowsSource(Entity aValue) throws Exception {
		if (rowsSource != aValue) {
			if (sortHandlerReg != null)
				sortHandlerReg.removeHandler();
			if (rowsSource != null && rowsSource.getRowset() != null)
				rowsSource.getRowset().removeRowsetListener(markerRerenderer);
			rowsSource = aValue;
			if (rowsSource != null) {
				Runnable onResize = new Runnable() {
					@Override
					public void run() {
						ModelGrid.this.getElement().<XElement> cast().unmask();
						setupVisibleRanges();
					}

				};
				Runnable onSort = new Runnable() {
					@Override
					public void run() {
						if (dataProvider instanceof IndexOfProvider<?>)
							((IndexOfProvider<?>) dataProvider).rescan();
					}

				};
				Runnable onLoadingStart = new Runnable() {
					@Override
					public void run() {
						ModelGrid.this.getElement().<XElement> cast().unmask();
						ModelGrid.this.getElement().<XElement> cast().loadMask();
					}
				};
				Callback<Void, String> onError = new Callback<Void, String>() {
					@Override
					public void onSuccess(Void result) {
					}

					@Override
					public void onFailure(String reason) {
						ModelGrid.this.getElement().<XElement> cast().unmask();
						ModelGrid.this.getElement().<XElement> cast().errorMask(reason);
					}
				};
				if (isTreeConfigured()) {
					TreeDataProvider<Row> treeDataProvider;
					if (isLazyTreeConfigured()) {
						RowsetTree tree = new RowsetTree(rowsSource.getRowset(), unaryLinkField.field, true, onLoadingStart, onError);
						treeDataProvider = new TreeDataProvider<>(tree, onResize, new RowChildrenFetcher(rowsSource, (Parameter) param2GetChildren.field, paramSourceField.field));
					} else {
						RowsetTree tree = new RowsetTree(rowsSource.getRowset(), unaryLinkField.field, onLoadingStart, onError);
						treeDataProvider = new TreeDataProvider<>(tree, onResize);
					}
					setDataProvider(treeDataProvider);
					sortHandler = new TreeMultiSortHandler<>(treeDataProvider, onSort);
					treeDataProvider.addExpandedCollapsedHandler(new ExpandedCollapsedHandler<Row>() {

						@Override
						public void expanded(Row anElement) {
							ColumnSortEvent.fire(ModelGrid.this, sortList);
						}

						@Override
						public void collapsed(Row anElement) {
							ColumnSortEvent.fire(ModelGrid.this, sortList);
						}

					});
				} else {
					setDataProvider(new RowsetDataProvider(rowsSource.getRowset(), onResize, onLoadingStart, onError));
					sortHandler = new ListMultiSortHandler<>(dataProvider.getList(), onSort);
				}
				sortHandlerReg = addColumnSortHandler(sortHandler);
				if (rowsSource.getRowset() != null)
					rowsSource.getRowset().addRowsetListener(markerRerenderer);
			}
		}
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	public void setPublished(JavaScriptObject aValue) {
		published = aValue != null ? aValue.<PublishedComponent> cast() : null;
		if (published != null) {
			publish(this, published);
			for (int i = 0; i < getDataColumnCount(); i++) {
				Column<Row, ?> col = getDataColumn(i);
				if (col instanceof ModelGridColumnFacade) {
					ModelGridColumnFacade fCol = (ModelGridColumnFacade) col;
					if (fCol.getJsName() != null && !fCol.getJsName().isEmpty() && col instanceof HasPublished) {
						HasPublished pCol = (HasPublished) col;
						published.<JsObject> cast().inject(fCol.getJsName(), pCol.getPublished());
					}
				}
			}
		}
	}

	private native static void publish(ModelGrid aWidget, JavaScriptObject aPublished)/*-{
		aPublished.select = function(aRow) {
			if(aRow != null && aRow != undefined)
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::selectRow(Lcom/bearsoft/rowset/Row;)(aRow.unwrap());
		};
		aPublished.unselect = function(aRow) {
			if(aRow != null && aRow != undefined)
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::unselectRow(Lcom/bearsoft/rowset/Row;)(aRow.unwrap());
		};
		aPublished.clearSelection = function() {
			aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::clearSelection()();
		};
		aPublished.find = function(){
			aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::find()();
		};
		aPublished.findSomething = function() {
			aPublished.find();
		};
		aPublished.makeVisible = function(aRow, needToSelect) {
			var need2Select = true;
			if(needToSelect != undefined)
				need2Select = (false != needToSelect);
			if(aRow != null)
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::makeVisible(Lcom/bearsoft/rowset/Row;Z)(aRow.unwrap(), need2Select);
			else
				return false;
		};
			
		Object.defineProperty(aPublished, "rowsHeight", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getRowsHeight()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setRowsHeight(I)(aValue * 1);
			}
		});
		Object.defineProperty(aPublished, "showHorizontalLines", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isShowHorizontalLines()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setShowHorizontalLines(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "showVerticalLines", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isShowVerticalLines()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setShowVerticalLines(Z)(!!aValue);
			}
		});		
		Object.defineProperty(aPublished, "showOddRowsInOtherColor", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isShowOddRowsInOtherColor()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setShowOddRowsInOtherColor(Z)(!!aValue);
			}
		});		
		Object.defineProperty(aPublished, "gridColor", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getGridColor()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setGridColor(Lcom/eas/client/form/published/PublishedColor;)(aValue);
			}
		});		
		Object.defineProperty(aPublished, "oddRowsColor", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getOddRowsColor()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setOddRowsColor(Lcom/eas/client/form/published/PublishedColor;)(aValue);
			}
		});
		
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getOnRender()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "selected", {
			get : function() {
				var selectionList = aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getJsSelected()();
				var selectionArray = [];
				for(var i = 0; i < selectionList.@java.util.List::size()(); i++){
					selectionArray[selectionArray.length] = selectionList.@java.util.List::get(I)(i);
				}
				return selectionArray;
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isEditable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setEditable(Z)(aValue);
			}
		});
		Object.defineProperty(aPublished, "deletable", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isDeletable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setDeletable(Z)(aValue);
			}
		});
		Object.defineProperty(aPublished, "insertable", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isInsertable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setInsertable(Z)(aValue);
			}
		});
	}-*/;

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
		if (finder == null) {
			finder = new FindWindow(ModelGrid.this);
		}
		finder.show();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (finder != null) {
			finder.close();
		}
	}

	@Override
	public void onResize() {
		super.onResize();
		if (isAttached()) {
			double commonWidth = 0;
			double weightedWidth = 0;
			for (int i = 0; i < getDataColumnCount(); i++) {
				Column<Row, ?> column = getDataColumn(i);
				String factWidth = i < frozenColumns ? scrollableLeft.getColumnWidth(column, false) : scrollableRight.getColumnWidth(column, false);
				if (column instanceof ModelGridColumn<?>) {
					ModelGridColumn<?> mCol = (ModelGridColumn<?>) column;
					if (mCol.isVisible()) {
						double colWidth = mCol.getDesignedWidth();
						commonWidth += colWidth;
						if (!mCol.isFixed()) {
							weightedWidth += colWidth;
						}
					}
				} else {
					if (factWidth != null && factWidth.endsWith("px")) {
						double colWidth = Double.valueOf(factWidth.substring(0, factWidth.length() - 2));
						commonWidth += colWidth;
					}
				}
			}
			double delta = (scrollableLeftContainer.getElement().getClientWidth() + scrollableRightContainer.getElement().getClientWidth()) - commonWidth;
			if (delta < 0)
				delta = 0;
			for (int i = 0; i < getDataColumnCount(); i++) {
				Column<Row, ?> column = getDataColumn(i);
				if (column instanceof ModelGridColumn<?>) {
					ModelGridColumn<?> mCol = (ModelGridColumn<?>) column;
					if (mCol.isVisible() && !mCol.isFixed()) {
						double colWidth = mCol.getDesignedWidth();
						double newFloatWidth = colWidth + colWidth / weightedWidth * delta;
						setColumnWidth(mCol, newFloatWidth, Style.Unit.PX);
					}
				}
			}
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
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
}
