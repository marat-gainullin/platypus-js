package com.eas.client.form.published.widgets.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.bearsoft.gwt.ui.widgets.grid.GridSection;
import com.bearsoft.gwt.ui.widgets.grid.GridSelectionEventManager;
import com.bearsoft.gwt.ui.widgets.grid.builders.ThemedHeaderOrFooterBuilder;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderAnalyzer;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderSplitter;
import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
import com.bearsoft.gwt.ui.widgets.grid.processing.ListMultiSortHandler;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeDataProvider;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeMultiSortHandler;
import com.eas.client.Utils;
import com.eas.client.Utils.JsObject;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.JavaScriptObjectKeyProvider;
import com.eas.client.form.events.CollapsedHandler;
import com.eas.client.form.events.ExpandedHandler;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.grid.FindWindow;
import com.eas.client.form.grid.RenderedTableCellBuilder;
import com.eas.client.form.grid.CursorPropertySelectionReflector;
import com.eas.client.form.grid.columns.CheckServiceColumn;
import com.eas.client.form.grid.columns.ModelColumn;
import com.eas.client.form.grid.columns.RadioServiceColumn;
import com.eas.client.form.grid.columns.UsualServiceColumn;
import com.eas.client.form.grid.rows.JsArrayListDataProvider;
import com.eas.client.form.grid.rows.JsArrayTreeDataProvider;
import com.eas.client.form.grid.rows.JsDataContainer;
import com.eas.client.form.grid.selection.CheckBoxesEventTranslator;
import com.eas.client.form.grid.selection.HasSelectionLead;
import com.eas.client.form.grid.selection.MultiJavaScriptObjectSelectionModel;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.HasBinding;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasJsName;
import com.eas.client.form.published.HasOnRender;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SetSelectionModel;

/**
 * Class intended to wrap a grid or tree grid. It also contains grid API.
 * 
 * @author mg
 * 
 */
public class ModelGrid extends Grid<JavaScriptObject> implements HasJsFacade, HasOnRender, HasComponentPopupMenu, HasEventsExecutor, HasEnabled, HasShowHandlers, HasHideHandlers, HasResizeHandlers,
        HasBinding, HasSelectionHandlers<JavaScriptObject> {

	protected boolean enabled = true;
	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	//
	protected String parentField;
	protected String childrenField;
	//
	protected JavaScriptObject data;
	protected String field;
	protected HandlerRegistration boundToData;
	protected HandlerRegistration boundToCursor;
	protected String cursorProperty = "cursor";
	protected JavaScriptObject onRender;
	protected JavaScriptObject onExpand;
	protected JavaScriptObject onCollapse;
	protected PublishedComponent published;
	protected FindWindow finder;
	protected String groupName = "group-name-" + Document.get().createUniqueId();
	protected List<HeaderNode<JavaScriptObject>> header = new ArrayList<>();
	// runtime
	protected Widget activeEditor;
	protected ListHandler<JavaScriptObject> sortHandler;
	protected HandlerRegistration sortHandlerReg;
	protected HandlerRegistration positionSelectionHandler;
	protected HandlerRegistration onSelectEventSelectionHandler;
	protected boolean editable;
	protected boolean deletable;
	protected boolean insertable;

	public ModelGrid() {
		super(new JavaScriptObjectKeyProvider());
		addDomHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				Object oData = data != null && field != null && !field.isEmpty() ? Utils.getPathData(data, field) : data;
				JsObject jsData = oData instanceof JavaScriptObject ? ((JavaScriptObject) oData).<JsObject> cast() : null;
				if (jsData != null) {
					if (activeEditor == null && getSelectionModel() instanceof SetSelectionModel<?>) {
						final SetSelectionModel<JavaScriptObject> rowsSelection = (SetSelectionModel<JavaScriptObject>) getSelectionModel();
						if (event.getNativeKeyCode() == KeyCodes.KEY_DELETE && deletable) {
							final List<JavaScriptObject> viewElements = dataProvider.getList();
							if (!viewElements.isEmpty() && rowsSelection.getSelectedSet() != null && !rowsSelection.getSelectedSet().isEmpty()) {
								// calculate some view sugar
								int lastSelectedViewIndex = -1;
								for (int i = viewElements.size() - 1; i >= 0; i--) {
									JavaScriptObject element = viewElements.get(i);
									if (rowsSelection.isSelected(element)) {
										lastSelectedViewIndex = i;
										break;
									}
								}
								// actually delete selected elements
								int deletedAt = -1;
								for (int i = jsData.length() - 1; i >= 0; i--) {
									JavaScriptObject element = jsData.getSlot(i);
									if (rowsSelection.isSelected(element)) {
										jsData.splice(i, 1);
										deletedAt = i;
									}
								}
								final int viewIndexToSelect = lastSelectedViewIndex;
								Scheduler.get().scheduleDeferred(new ScheduledCommand() {

									@Override
									public void execute() {
										Scheduler.get().scheduleDeferred(new ScheduledCommand() {

											@Override
											public void execute() {
												int vIndex = viewIndexToSelect;
												if (vIndex >= 0 && !viewElements.isEmpty()) {
													if (vIndex >= viewElements.size())
														vIndex = viewElements.size() - 1;
													JavaScriptObject toSelect = viewElements.get(vIndex);
													makeVisible(toSelect, true);
												}
											}
										});
									}

								});
							}
						} else if (event.getNativeKeyCode() == KeyCodes.KEY_INSERT && insertable) {
							int insertAt = -1;
							if (rowsSelection instanceof HasSelectionLead<?> && dataProvider instanceof IndexOfProvider<?>) {
								JavaScriptObject lead = ((HasSelectionLead<JavaScriptObject>) rowsSelection).getLead();
								insertAt = ((IndexOfProvider<JavaScriptObject>) dataProvider).indexOf(lead);
								insertAt++;
								JavaScriptObject oElementClass = jsData.getJs("elementClass");
								JsObject elementClass = oElementClass != null ? oElementClass.<JsObject> cast() : null;
								final JavaScriptObject inserted = elementClass != null ? elementClass.newObject() : JavaScriptObject.createObject();
								jsData.splice(insertAt, 0, inserted);
								Scheduler.get().scheduleDeferred(new ScheduledCommand() {

									@Override
									public void execute() {
										Scheduler.get().scheduleDeferred(new ScheduledCommand() {

											@Override
											public void execute() {
												makeVisible(inserted, true);
											}
										});
									}

								});
							}
						}
					}
				}
			}

		}, KeyUpEvent.getType());
		addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if ((event.isMetaKeyDown() || event.isControlKeyDown()) && event.getNativeKeyCode() == KeyCodes.KEY_F) {
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
		applyRows();
	}
	
	public Widget getActiveEditor(){
		return activeEditor;
	}
	
	public void setActiveEditor(Widget aWidget) {
	    activeEditor = aWidget;
    }
	
	public String getGroupName() {
		return groupName;
	}

	protected void installCellBuilders() {
		for (GridSection<?> section : new GridSection<?>[] { frozenLeft, frozenRight, scrollableLeft, scrollableRight }) {
			GridSection<JavaScriptObject> gSection = (GridSection<JavaScriptObject>) section;
			gSection.setTableBuilder(new RenderedTableCellBuilder<>(gSection, dynamicTDClassName, dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName));
		}
	}

	protected boolean serviceColumnsRedrawQueued;

	protected void enqueueServiceColumnsRedraw() {
		serviceColumnsRedrawQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (serviceColumnsRedrawQueued) {
					serviceColumnsRedrawQueued = false;
					if(getDataColumnCount() > 0){
						for (int i = 0; i < getDataColumnCount(); i++) {
							ModelColumn col = (ModelColumn) getDataColumn(i);
							if (col instanceof UsualServiceColumn) {
								if (i < frozenColumns) {
									frozenLeft.redrawAllRowsInColumn(i, dataProvider);
									scrollableLeft.redrawAllRowsInColumn(i, dataProvider);
								} else {
									frozenRight.redrawAllRowsInColumn(i - frozenColumns, dataProvider);
									scrollableRight.redrawAllRowsInColumn(i - frozenColumns, dataProvider);
								}
							}
						}
					}
				}
			}
		});
	}

	protected boolean redrawQueued;

	private void enqueueRedraw() {
		redrawQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (redrawQueued) {
					redrawQueued = false;
					redraw();
				}
			}
		});
	}

	protected void applyRows() {
		unbindCursor();
		if (sortHandlerReg != null)
			sortHandlerReg.removeHandler();
		Runnable onResize = new Runnable() {
			@Override
			public void run() {
				setupVisibleRanges();
				if (dataProvider instanceof IndexOfProvider<?>)
					((IndexOfProvider<?>) dataProvider).rescan();
				sortHandler.setList(dataProvider != null ? dataProvider.getList() : new ArrayList<JavaScriptObject>());
				if (sortList.size() > 0) {
					sortList.clear();
					redrawHeaders();
				}
			}

		};
		Runnable onChange = new Runnable() {
			@Override
			public void run() {
				ModelGrid.this.redraw();
			}

		};
		Runnable onSort = new Runnable() {
			@Override
			public void run() {
				if (dataProvider instanceof IndexOfProvider<?>)
					((IndexOfProvider<?>) dataProvider).rescan();
			}

		};
		Object oData = data != null && field != null && !field.isEmpty() ? Utils.getPathData(data, field) : data;
		JavaScriptObject jsData = oData instanceof JavaScriptObject ? (JavaScriptObject) oData : null;
		if (jsData != null) {
			if (isTreeConfigured()) {
				JsArrayTreeDataProvider treeDataProvider = new JsArrayTreeDataProvider(parentField, childrenField, onResize);
				setDataProvider(treeDataProvider);
				sortHandler = new TreeMultiSortHandler<>(treeDataProvider, onSort);
				treeDataProvider.addExpandedHandler(new ExpandedHandler<JavaScriptObject>() {
					@Override
					public void expanded(JavaScriptObject anElement) {
						ColumnSortEvent.fire(ModelGrid.this, sortList);
						if (onExpand != null) {
							JavaScriptObject jsEvent = JsEvents.publishItemEvent(ModelGrid.this.published, anElement);
							try {
								Utils.executeScriptEventVoid(ModelGrid.this.published, onExpand, jsEvent);
							} catch (Exception e) {
								Logger.getLogger(EventsExecutor.class.getName()).log(Level.SEVERE, null, e);
							}
						}
					}

				});
				treeDataProvider.addCollapsedHandler(new CollapsedHandler<JavaScriptObject>() {
					@Override
					public void collapsed(JavaScriptObject anElement) {
						ColumnSortEvent.fire(ModelGrid.this, sortList);
						if (onCollapse != null) {
							JavaScriptObject jsEvent = JsEvents.publishItemEvent(ModelGrid.this.published, anElement);
							try {
								Utils.executeScriptEventVoid(ModelGrid.this.published, onCollapse, jsEvent);
							} catch (Exception e) {
								Logger.getLogger(EventsExecutor.class.getName()).log(Level.SEVERE, null, e);
							}
						}
					}

				});
			} else {
				setDataProvider(new JsArrayListDataProvider(onResize, onChange, null));
				sortHandler = new ListMultiSortHandler<>(dataProvider.getList(), onSort);
			}
			for (int colIndex = 0; colIndex < getDataColumnCount(); colIndex++) {
				ModelColumn modelCol = (ModelColumn) getDataColumn(colIndex);
				sortHandler.setComparator(modelCol, modelCol.getComparator());
			}
			sortHandlerReg = addColumnSortHandler(sortHandler);
			((JsDataContainer) getDataProvider()).setData(jsData);
			boundToCursor = Utils.listenPath(jsData, cursorProperty, new Utils.OnChangeHandler() {

				@Override
				public void onChange(JavaScriptObject anEvent) {
					enqueueServiceColumnsRedraw();
				}

			});
		} else {
			setDataProvider(null);
		}
	}

	@Override
	public void setDataProvider(ListDataProvider<JavaScriptObject> aDataProvider) {
		if (getDataProvider() instanceof JsDataContainer)
			((JsDataContainer) getDataProvider()).setData(null);
		super.setDataProvider(aDataProvider);
		checkTreeIndicatorColumnDataProvider();
	}

	public void redraw() {
		headerLeft.redraw();
		headerRight.redraw();
		frozenLeft.redraw();
		frozenRight.redraw();
		scrollableLeft.redraw();
		scrollableRight.redraw();
		pingGWTFinallyCommands();
	}

	private void pingGWTFinallyCommands(){
		// Dirty hack of GWT Scgeduler.get().scheduleFinally();
		// Finally commands ocasionally not been executed without user interaction for while.
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Logger.getLogger(ModelGrid.class.getName()).log(Level.FINE, "ping GWT Finally commands");
			}
		});
	}
	
	protected void bind() {
		if (data != null) {
			applyRows();
			setSelectionModel(new MultiJavaScriptObjectSelectionModel(this));
			if (field != null && !field.isEmpty()) {
				boundToData = Utils.listenPath(data, field, new Utils.OnChangeHandler() {
					
					@Override
					public void onChange(JavaScriptObject anEvent) {
						applyRows();
						setSelectionModel(new MultiJavaScriptObjectSelectionModel(ModelGrid.this));
					}
				});
			}
		} else {
			applyRows();
			setSelectionModel(null);
		}
	}

	protected void unbind() {
		if (boundToData != null) {
			boundToData.removeHandler();
			boundToData = null;
		}
		unbindCursor();
	}

	protected void unbindCursor() {
		if (boundToCursor != null) {
			boundToCursor.removeHandler();
			boundToCursor = null;
		}
	}

	@Override
	public JavaScriptObject getData() {
		return data;
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		if (data != aValue) {
			unbind();
			data = aValue;
			bind();
		}
	}

	@Override
	public String getField() {
		return field;
	}

	@Override
	public void setField(String aValue) {
		if (field == null ? aValue != null : !field.equals(aValue)) {
			unbind();
			field = aValue;
			bind();
		}
	}

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String aValue) {
		if (parentField == null ? aValue != null : !parentField.equals(aValue)) {
			boolean wasTree = isTreeConfigured();
			parentField = aValue;
			boolean isTree = isTreeConfigured();
			if (wasTree != isTree) {
				applyRows();
			}
		}
	}

	public String getChildrenField() {
		return childrenField;
	}

	public void setChildrenField(String aValue) {
		if (childrenField == null ? aValue != null : !childrenField.equals(aValue)) {
			boolean wasTree = isTreeConfigured();
			childrenField = aValue;
			boolean isTree = isTreeConfigured();
			if (wasTree != isTree) {
				applyRows();
			}
		}
	}

	public final boolean isTreeConfigured() {
		return parentField != null && !parentField.isEmpty() && childrenField != null && !childrenField.isEmpty();
	}

	public ListHandler<JavaScriptObject> getSortHandler() {
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

	public HandlerRegistration addSelectionHandler(SelectionHandler<JavaScriptObject> handler) {
		return addHandler(handler, SelectionEvent.getType());
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

	public List<HeaderNode<JavaScriptObject>> getHeader() {
		return header;
	}

	public void setHeader(List<HeaderNode<JavaScriptObject>> aHeader) {
		if (header != aHeader) {
			unpublishColumnNodes(header);
			header = aHeader;
			if (autoRefreshHeader) {
				applyColumns();
			}
			publishColumnNodes(header);
		}
	}

	@Override
	public void moveColumnNode(HeaderNode<JavaScriptObject> aSubject, HeaderNode<JavaScriptObject> aInsertBefore) {
		if (aSubject != null && aInsertBefore != null && aSubject.getParent() == aInsertBefore.getParent()) {
			List<HeaderNode<JavaScriptObject>> neighbours = aSubject.getParent() != null ? aSubject.getParent().getChildren() : header;
			boolean removed = neighbours.remove(aSubject);
			assert removed;
			int insertAt = neighbours.indexOf(aInsertBefore);
			neighbours.add(insertAt, aSubject);
			applyColumns();
			onResize();
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
		// no op since hierarchical header processing
	}

	@Override
	public void moveColumn(int aFromIndex, int aToIndex) {
		if (aFromIndex < frozenColumns && aToIndex < frozenColumns || aFromIndex >= frozenColumns && aToIndex >= frozenColumns) {
			Column<JavaScriptObject, ?> movedColumn = getColumn(aFromIndex);
		}
	}

	protected ModelColumn treeIndicatorColumn;

	@Override
	public void addColumn(int aIndex, Column<JavaScriptObject, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		((ModelColumn) aColumn).setGrid(this);
		super.addColumn(aIndex, aColumn, aWidth, aHeader, aFooter, hidden);
	}

	private void checkTreeIndicatorColumnDataProvider() {
		if (dataProvider instanceof TreeDataProvider<?>) {
			if (treeIndicatorColumn == null) {
				int treeIndicatorIndex = 0;
				while (treeIndicatorIndex < getDataColumnCount()) {
					Column<JavaScriptObject, ?> indicatorColumn = getDataColumn(treeIndicatorIndex);
					if (indicatorColumn instanceof UsualServiceColumn || indicatorColumn instanceof RadioServiceColumn || indicatorColumn instanceof CheckServiceColumn) {
						treeIndicatorIndex++;
					} else if (indicatorColumn instanceof ModelColumn) {
						treeIndicatorColumn = (ModelColumn) indicatorColumn;
						break;
					}
				}
			}
			if (treeIndicatorColumn != null && treeIndicatorColumn.getCell() instanceof TreeExpandableCell<?, ?>) {
				TreeExpandableCell<JavaScriptObject, ?> treeCell = (TreeExpandableCell<JavaScriptObject, ?>) treeIndicatorColumn.getCell();
				treeCell.setDataProvider((TreeDataProvider<JavaScriptObject>) dataProvider);
			}
		} else {
			if (treeIndicatorColumn != null && treeIndicatorColumn.getCell() instanceof TreeExpandableCell<?, ?>) {
				TreeExpandableCell<JavaScriptObject, ?> treeCell = (TreeExpandableCell<JavaScriptObject, ?>) treeIndicatorColumn.getCell();
				treeCell.setDataProvider(null);
			}
			treeIndicatorColumn = null;
		}
	}

	@Override
	public void removeColumn(int aIndex) {
		Column<JavaScriptObject, ?> toDel = getDataColumn(aIndex);
		ModelColumn mCol = (ModelColumn) toDel;
		if (mCol == treeIndicatorColumn) {
			TreeExpandableCell<JavaScriptObject, ?> treeCell = (TreeExpandableCell<JavaScriptObject, ?>) mCol.getCell();
			treeCell.setDataProvider(null);
			treeIndicatorColumn = null;
		}
		super.removeColumn(aIndex);
		mCol.setGrid(null);
	}

	@Override
	public void setColumnWidthFromHeaderDrag(Column<JavaScriptObject, ?> aColumn, double aWidth, Unit aUnit) {
		ModelColumn modelCol = (ModelColumn) aColumn;
		if (aWidth <= modelCol.getMinWidth())
			aWidth = modelCol.getMinWidth();
		if (aWidth >= modelCol.getMaxWidth())
			aWidth = modelCol.getMaxWidth();
		super.setColumnWidth(aColumn, aWidth, aUnit);
		modelCol.setWidth(aWidth);
		propagateHeightButScrollable();
	}

	@Override
	public void setColumnWidth(Column<JavaScriptObject, ?> aColumn, double aWidth, Unit aUnit) {
		ModelColumn modelCol = (ModelColumn) aColumn;
		if (aWidth <= modelCol.getMinWidth())
			aWidth = modelCol.getMinWidth();
		if (aWidth >= modelCol.getMaxWidth())
			aWidth = modelCol.getMaxWidth();
		super.setColumnWidth(aColumn, aWidth, aUnit);
		modelCol.updateWidth(aWidth);
	}

	@Override
	public void showColumn(Column<JavaScriptObject, ?> aColumn) {
		super.showColumn(aColumn);
		ModelColumn colFacade = (ModelColumn) aColumn;
		colFacade.updateVisible(true);
		enqueueRedraw(); // because of AbstractCellTable.isInteractive crazy
		                 // updating while rendering instead of updating it
		                 // while show/hide columns
	}

	public void hideColumn(Column<JavaScriptObject, ?> aColumn) {
		super.hideColumn(aColumn);
		ModelColumn colFacade = (ModelColumn) aColumn;
		colFacade.updateVisible(false);
	}

	@Override
	public void setFrozenColumns(int aValue) {
		if (aValue >= 0 && frozenColumns != aValue) {
			if (aValue >= 0) {
				frozenColumns = aValue;
				if (autoRefreshHeader && getDataColumnCount() > 0 && aValue <= getDataColumnCount()) {
					applyColumns();
				}
			}
		}
	}

	protected boolean autoRefreshHeader = true;

	public boolean isAutoRefreshHeader() {
		return autoRefreshHeader;
	}

	public void setAutoRefreshHeader(boolean aValue) {
		autoRefreshHeader = aValue;
	}

	public void applyColumns() {
		for (int i = getDataColumnCount() - 1; i >= 0; i--) {
			removeColumn(i);
		}
		List<HeaderNode<JavaScriptObject>> leaves = new ArrayList<>();
		HeaderAnalyzer.achieveLeaves(header, leaves);
		for (HeaderNode<JavaScriptObject> leaf : leaves) {
			Header<String> header = leaf.getHeader();
			ModelColumn column = (ModelColumn) leaf.getColumn();
			column.setGrid(this);
			addColumn(column, column.getWidth() + "px", header, null, !column.isVisible());
		}
		ThemedHeaderOrFooterBuilder<JavaScriptObject> leftBuilder = (ThemedHeaderOrFooterBuilder<JavaScriptObject>) headerLeft.getHeaderBuilder();
		ThemedHeaderOrFooterBuilder<JavaScriptObject> rightBuilder = (ThemedHeaderOrFooterBuilder<JavaScriptObject>) headerRight.getHeaderBuilder();
		List<HeaderNode<JavaScriptObject>> leftHeader = HeaderSplitter.split(header, 0, frozenColumns);
		leftBuilder.setHeaderNodes(leftHeader);
		List<HeaderNode<JavaScriptObject>> rightHeader = HeaderSplitter.split(header, frozenColumns, getDataColumnCount());
		rightBuilder.setHeaderNodes(rightHeader);
		checkTreeIndicatorColumnDataProvider();
		redrawHeaders();
	}

	@Override
	public void setSelectionModel(final SelectionModel<JavaScriptObject> aValue) {
		SelectionModel<? super JavaScriptObject> oldValue = getSelectionModel();
		if (aValue != oldValue) {
			if (positionSelectionHandler != null)
				positionSelectionHandler.removeHandler();
			if (onSelectEventSelectionHandler != null)
				onSelectEventSelectionHandler.removeHandler();
			CellPreviewEvent.Handler<JavaScriptObject> eventsManager = GridSelectionEventManager.<JavaScriptObject> create(new CheckBoxesEventTranslator<JavaScriptObject>());
			headerLeft.setSelectionModel(aValue, eventsManager);
			headerRight.setSelectionModel(aValue, eventsManager);
			frozenLeft.setSelectionModel(aValue, eventsManager);
			frozenRight.setSelectionModel(aValue, eventsManager);
			scrollableLeft.setSelectionModel(aValue, eventsManager);
			scrollableRight.setSelectionModel(aValue, eventsManager);
			if (aValue != null) {
				Object oData = field != null && !field.isEmpty() ? Utils.getPathData(data, field) : data;
				if (oData instanceof JavaScriptObject) {
					positionSelectionHandler = aValue.addSelectionChangeHandler(new CursorPropertySelectionReflector((JavaScriptObject) oData, aValue));
					onSelectEventSelectionHandler = aValue.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
						@Override
						public void onSelectionChange(SelectionChangeEvent event) {
							if (aValue instanceof HasSelectionLead<?>) {
								try {
									JavaScriptObject lead = ((HasSelectionLead<JavaScriptObject>) aValue).getLead();
									SelectionEvent.fire(ModelGrid.this, lead);
								} catch (Exception e) {
									Logger.getLogger(CursorPropertySelectionReflector.class.getName()).log(Level.SEVERE, e.getMessage());
								}
							}
						}
					});
				}
			}
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

	public JavaScriptObject getPublished() {
		return published;
	}

	public void setPublished(JavaScriptObject aValue) {
		published = aValue != null ? aValue.<PublishedComponent> cast() : null;
		if (published != null) {
			publish(this, published);
			publishColumnNodes(header);
		}
	}

	protected void unpublishColumnNodes(List<HeaderNode<JavaScriptObject>> aNodes) {
		for (HeaderNode<JavaScriptObject> node : aNodes) {
			String jsName = ((HasJsName) node).getJsName();
			if (jsName != null && !jsName.isEmpty()) {
				published.<JsObject> cast().deleteProperty(jsName);
			}
			unpublishColumnNodes(node.getChildren());
		}
	}

	protected void publishColumnNodes(List<HeaderNode<JavaScriptObject>> aNodes) {
		for (HeaderNode<JavaScriptObject> node : aNodes) {
			String jsName = ((HasJsName) node).getJsName();
			if (jsName != null && !jsName.isEmpty()) {
				HasPublished pCol = (HasPublished) node;
				published.<JsObject> cast().inject(jsName, pCol.getPublished(), true);
			}
			publishColumnNodes(node.getChildren());
		}
	}

	private native static void publish(ModelGrid aWidget, JavaScriptObject aPublished)/*-{
		aPublished.select = function(aRow) {
			if (aRow != null && aRow != undefined)
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::selectElement(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
		};
		aPublished.unselect = function(aRow) {
			if (aRow != null && aRow != undefined)
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::unselectElement(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
		};
		aPublished.clearSelection = function() {
			aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::clearSelection()();
		};
		aPublished.find = function() {
			aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::find()();
		};
		aPublished.findSomething = function() {
			aPublished.find();
		};
		aPublished.makeVisible = function(aRow, needToSelect) {
			var need2Select = arguments.length > 1 ? !!needToSelect : false;
			if (aRow != null)
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::makeVisible(Lcom/google/gwt/core/client/JavaScriptObject;Z)(aRow, need2Select);
			else
				return false;
		};
		aPublished.redraw = function() {
			aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::redraw()();
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
		Object.defineProperty(aPublished, "cursorProperty", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getCursorProperty()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setCursorProperty(Ljava/lang/String;)(aValue ? '' + aValue : null);
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
		Object.defineProperty(aPublished, "onExpand", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getOnExpand()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setOnExpand(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onCollapse", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getOnCollapse()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setOnCollapse(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "selected", {
			get : function() {
				var selectionList = aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getJsSelected()();
				var selectionArray = [];
				for ( var i = 0; i < selectionList.@java.util.List::size()(); i++) {
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
		Object.defineProperty(aPublished, "data", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasBinding::getData()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasBinding::setData(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasBinding::getField()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasBinding::setField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(aPublished, "parentField", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getParentField();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setParentField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(aPublished, "childrenField", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getChildrenField();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setChildrenField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});

		aPublished.unsort = function() {
			aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::unsort()();
		};

	}-*/;

	public JavaScriptObject getOnRender() {
		return onRender;
	}

	public void setOnRender(JavaScriptObject aValue) {
		onRender = aValue;
	}

	public JavaScriptObject getOnExpand() {
		return onExpand;
	}

	public void setOnExpand(JavaScriptObject aValue) {
		onExpand = aValue;
	}

	public JavaScriptObject getOnCollapse() {
		return onCollapse;
	}

	public void setOnCollapse(JavaScriptObject aValue) {
		onCollapse = aValue;
	}

	public String getCursorProperty() {
		return cursorProperty;
	}

	public void setCursorProperty(String aValue) {
		if (aValue != null && !cursorProperty.equals(aValue)) {
			unbind();
			cursorProperty = aValue;
			bind();
		}
	}

	public void selectElement(JavaScriptObject aElement) {
		getSelectionModel().setSelected(aElement, true);
		pingGWTFinallyCommands();
	}

	public void unselectElement(JavaScriptObject anElement) {
		getSelectionModel().setSelected(anElement, false);
		pingGWTFinallyCommands();
	}

	public List<JavaScriptObject> getJsSelected() throws Exception {
		List<JavaScriptObject> result = new ArrayList<>();
		for (JavaScriptObject row : dataProvider.getList()) {
			if (getSelectionModel().isSelected(row))
				result.add(row);
		}
		return result;
	}

	public void clearSelection() {
		SelectionModel<? super JavaScriptObject> sm = getSelectionModel();
		for (JavaScriptObject row : dataProvider.getList()) {
			if (getSelectionModel().isSelected(row)) {
				sm.setSelected(row, false);
			}
		}
		pingGWTFinallyCommands();
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

	public boolean makeVisible(JavaScriptObject anElement, boolean needToSelect) {
		IndexOfProvider<JavaScriptObject> indexOfProvider = (IndexOfProvider<JavaScriptObject>) dataProvider;
		int index = indexOfProvider.indexOf(anElement);
		if (index > -1) {
			if (index >= 0 && index < frozenRows) {
				TableCellElement leftCell = frozenLeft.getCell(index, 0);
				if (leftCell != null) {
					leftCell.scrollIntoView();
				} else {
					TableCellElement rightCell = frozenRight.getCell(index, 0);
					if (rightCell != null)
						rightCell.scrollIntoView();
				}
			} else {
				TableCellElement leftCell = scrollableLeft.getCell(index, 0);
				if (leftCell != null) {
					leftCell.scrollIntoView();
				} else {
					TableCellElement rightCell = scrollableRight.getCell(index, 0);
					if (rightCell != null)
						rightCell.scrollIntoView();
				}
			}
			if (needToSelect) {
				clearSelection();
				selectElement(anElement);
				if (index >= 0 && index < frozenRows) {
					frozenLeft.setKeyboardSelectedRow(index, true);
					frozenRight.setKeyboardSelectedRow(index, true);
				} else {
					scrollableLeft.setKeyboardSelectedRow(index - frozenRows, true);
					scrollableRight.setKeyboardSelectedRow(index - frozenRows, true);
				}
			}
			pingGWTFinallyCommands();
			return true;
		} else
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
	protected void onColumnsResize() {
		if (isAttached()) {
			double prevError = 0;
			double error = distributeColumnsWidths();
			while (Math.abs(error) > 0.8 && Math.abs(error - prevError) > 2) {
				prevError = error;
				error = distributeColumnsWidths();
			}
		}
	}

	protected double distributeColumnsWidths() {
		List<ModelColumn> availableColumns = new ArrayList<>();
		double commonWidth = 0;
		for (int i = 0; i < getDataColumnCount(); i++) {
			Column<JavaScriptObject, ?> column = getDataColumn(i);
			ModelColumn mCol = (ModelColumn) column;
			if (mCol.isVisible()) {
				commonWidth += mCol.getWidth();
				availableColumns.add(mCol);
			}
		}
		double widthError = 0;
		double delta = (scrollableLeftContainer.getElement().getOffsetWidth() + scrollableRightContainer.getElement().getOffsetWidth()) - 1 - commonWidth;
		for (ModelColumn mCol : availableColumns) {
			double coef = mCol.getWidth() / commonWidth;
			double newWidth = mCol.getWidth() + delta * coef;
			setColumnWidth(mCol, newWidth, Style.Unit.PX);
			widthError += Math.abs(mCol.getWidth() - newWidth);
		}
		return widthError;
	}

	@Override
	public void onResize() {
		super.onResize();
		if (isAttached()) {
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
	}

	public void complementPublishedStyle(PublishedCell aComplemented) {
		if (published.isBackgroundSet()) {
			aComplemented.setBackground(published.getBackground());
		}
		if (published.isForegroundSet()) {
			aComplemented.setForeground(published.getForeground());
		}
		if (published.isFontSet()) {
			aComplemented.setFont(published.getFont());
		}
	}
}
