package com.eas.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.core.Utils;
import com.eas.core.Utils.JsObject;
import com.eas.grid.columns.Column;
import com.eas.grid.columns.UsualServiceColumn;
import com.eas.grid.processing.IndexOfProvider;
import com.eas.grid.processing.ListMultiSortHandler;
import com.eas.grid.processing.TreeMultiSortHandler;
import com.eas.grid.rows.JsArrayListDataProvider;
import com.eas.grid.rows.JsArrayTreeDataProvider;
import com.eas.grid.rows.JsDataContainer;
import com.eas.grid.selection.CheckBoxesEventTranslator;
import com.eas.grid.selection.HasSelectionLead;
import com.eas.grid.selection.MultiJavaScriptObjectSelectionModel;
import com.eas.ui.HasBinding;
import com.eas.ui.HasOnRender;
import com.eas.ui.PublishedCell;
import com.eas.ui.PublishedComponent;
import com.eas.ui.EventsPublisher;
import com.eas.ui.events.CollapsedHandler;
import com.eas.ui.events.ExpandedHandler;
import com.eas.ui.events.HasFocusHandlers;
import com.eas.ui.events.HasBlurHandlers;
import com.eas.ui.events.HasKeyPressHandlers;
import com.eas.ui.events.HasKeyUpHandlers;
import com.eas.ui.events.HasKeyDownHandlers;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Focusable;
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
public class ModelGrid extends Grid
        implements HasOnRender, HasBinding, HasSelectionHandlers<JavaScriptObject>, HasFocusHandlers,
        HasBlurHandlers, Focusable, HasKeyDownHandlers, HasKeyPressHandlers, HasKeyUpHandlers, JsDataContainer {

    public ModelGrid() {
        super();
    }

    protected boolean serviceColumnsRedrawQueued;

    protected void enqueueServiceColumnsRedraw() {
        serviceColumnsRedrawQueued = true;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                if (serviceColumnsRedrawQueued) {
                    serviceColumnsRedrawQueued = false;
                    if (getColumnCount() > 0) {
                        for (int i = 0; i < getColumnCount(); i++) {
                            Column col = (Column) getDataColumn(i);
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

    protected ScheduledCommand redrawQueued;

    private void enqueueRedraw() {
        redrawQueued = new ScheduledCommand() {

            @Override
            public void execute() {
                if (redrawQueued == this) {
                    redrawQueued = null;
                    redraw();
                }
            }
        };
        Scheduler.get().scheduleDeferred(redrawQueued);
    }

    @Override
    public void setDataProvider(ListDataProvider<JavaScriptObject> aDataProvider) {
        if (getDataProvider() != null) {
            ((JsDataContainer) getDataProvider()).setData(null);
        }
        super.setDataProvider(aDataProvider);
        checkTreeIndicatorColumnDataProvider();
    }

    @Override
    public void changedItems(JavaScriptObject anItems) {
        ((JsDataContainer) getDataProvider()).changedItems(anItems);
    }

    @Override
    public void addedItems(JavaScriptObject anItems) {
        ((JsDataContainer) getDataProvider()).addedItems(anItems);
    }

    @Override
    public void removedItems(JavaScriptObject anItems) {
        ((JsDataContainer) getDataProvider()).removedItems(anItems);
    }

    private List<Runnable> onAfterRenderTasks = new ArrayList<>();

    protected void afterRender(Runnable aTask) {
        onAfterRenderTasks.add(aTask);
    }

    @Override
    protected void renderingCompleted() {
        super.renderingCompleted();
        for (final Runnable task : onAfterRenderTasks) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    task.run();
                }
            });
        }
        onAfterRenderTasks.clear();
        if (onAfterRender != null) {
            final Utils.JsObject event = JavaScriptObject.createObject().cast();
            event.setJs("source", published);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    if (onAfterRender != null) {
                        onAfterRender.<Utils.JsObject>cast().call(published, event);
                    }
                }
            });
        }
    }

    public ListHandler<JavaScriptObject> getSortHandler() {
        return sortHandler;
    }

    public HandlerRegistration addSelectionHandler(SelectionHandler<JavaScriptObject> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    @Override
    public void setSelectionModel(final SelectionModel<JavaScriptObject> aValue) {
        SelectionModel<? super JavaScriptObject> oldValue = getSelectionModel();
        if (aValue != oldValue) {
            if (positionSelectionHandler != null) {
                positionSelectionHandler.removeHandler();
            }
            if (onSelectEventSelectionHandler != null) {
                onSelectEventSelectionHandler.removeHandler();
            }
            CellPreviewEvent.Handler<JavaScriptObject> eventsManager = GridSelectionEventManager
                    .<JavaScriptObject>create(new CheckBoxesEventTranslator<JavaScriptObject>());
            headerLeft.setSelectionModel(aValue, eventsManager);
            headerRight.setSelectionModel(aValue, eventsManager);
            frozenLeft.setSelectionModel(aValue, eventsManager);
            frozenRight.setSelectionModel(aValue, eventsManager);
            scrollableLeft.setSelectionModel(aValue, eventsManager);
            scrollableRight.setSelectionModel(aValue, eventsManager);
            if (aValue != null) {
                Object oData = field != null && !field.isEmpty() ? Utils.getPathData(data, field) : data;
                if (oData instanceof JavaScriptObject) {
                    positionSelectionHandler = aValue.addSelectionChangeHandler(
                            new CursorPropertySelectionReflector((JavaScriptObject) oData, aValue));
                    onSelectEventSelectionHandler = aValue
                            .addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                                @Override
                                public void onSelectionChange(SelectionChangeEvent event) {
                                    if (aValue instanceof HasSelectionLead<?>) {
                                        try {
                                            JavaScriptObject lead = ((HasSelectionLead<JavaScriptObject>) aValue)
                                                    .getLead();
                                            SelectionEvent.fire(ModelGrid.this, lead);
                                        } catch (Exception e) {
                                            Logger.getLogger(CursorPropertySelectionReflector.class.getName())
                                                    .log(Level.SEVERE, e.getMessage());
                                        }
                                    }
                                }
                            });
                }
            }
        }
    }

    public JavaScriptObject getPublished() {
        return published;
    }

    public void setPublished(JavaScriptObject aValue) {
        published = aValue != null ? aValue.<PublishedComponent>cast() : null;
        if (published != null) {
            publish(this, published);
            publishColumnNodes(header);
        }
    }

    private native static void publish(ModelGrid aWidget, JavaScriptObject aPublished)/*-{
        aPublished.select = function(aRow) {
            if (aRow != null && aRow != undefined)
                aWidget.@com.eas.grid.ModelGrid::selectElement(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
            };
        aPublished.unselect = function(aRow) {
            if (aRow != null && aRow != undefined)
                aWidget.@com.eas.grid.ModelGrid::unselectElement(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
        };
        aPublished.clearSelection = function() {
            aWidget.@com.eas.grid.ModelGrid::clearSelection()();
        };
        aPublished.find = function() {
            aWidget.@com.eas.grid.ModelGrid::find()();
        };
        aPublished.findSomething = function() {
            aPublished.find();
        };
        aPublished.makeVisible = function(aInstance, needToSelect) {
            var need2Select = arguments.length > 1 ? !!needToSelect : false;
            if (aInstance != null){
            	// We have to use willBeVisible() instead of makeVisible(), because of
            	// asynchronous nature of grid's cells rendering.
            	// Imagine, that someone requested cells re-rendering already.
            	// In such situation, results of makeVisible call is a zombie.
                aWidget.@com.eas.grid.ModelGrid::willBeVisible(Lcom/google/gwt/core/client/JavaScriptObject;Z)(aInstance, need2Select);
            }
        };
          
        aPublished.expanded = function(aInstance) {
            return aWidget.@com.eas.grid.ModelGrid::expanded(Lcom/google/gwt/core/client/JavaScriptObject;)(aInstance);
        };
          
        aPublished.expand = function(aInstance) {
            aWidget.@com.eas.grid.ModelGrid::expand(Lcom/google/gwt/core/client/JavaScriptObject;)(aInstance);
        };
          
        aPublished.collapse = function(aInstance) {
            aWidget.@com.eas.grid.ModelGrid::collapse(Lcom/google/gwt/core/client/JavaScriptObject;)(aInstance);
        };
          
        aPublished.toggle = function(aInstance) {
            aWidget.@com.eas.grid.ModelGrid::toggle(Lcom/google/gwt/core/client/JavaScriptObject;)(aInstance);
        };
          
        aPublished.unsort = function() {
            aWidget.@com.eas.grid.ModelGrid::unsort()();
        };
          
        aPublished.redraw = function() {
            // ModelGrid.redraw() is used as a rebinder in applications.
            // Because applications don't care about grid's dataProviders' internal
            // data structures, such as JsArrayDataProvider and its internal list of elements.
          	// So, redraw grid through rebinding of its data.
            aWidget.@com.eas.grid.ModelGrid::rebind()();
        };
        aPublished.changed = function(aItems){
            if(!$wnd.Array.isArray(aItems))
                aItems = [aItems];
            aWidget.@com.eas.grid.ModelGrid::changedItems(Lcom/google/gwt/core/client/JavaScriptObject;)(aItems);
        };
        aPublished.added = function(aItems){
            if(!$wnd.Array.isArray(aItems))
                aItems = [aItems];
            aWidget.@com.eas.grid.ModelGrid::addedItems(Lcom/google/gwt/core/client/JavaScriptObject;)(aItems);
        };
        aPublished.removed = function(aItems){
            if(!$wnd.Array.isArray(aItems))
                aItems = [aItems];
            aWidget.@com.eas.grid.ModelGrid::removedItems(Lcom/google/gwt/core/client/JavaScriptObject;)(aItems);
        };
          
        aPublished.removeColumnNode = function(aColumnFacade){
            if(aColumnFacade && aColumnFacade.unwrap)
                return aWidget.@com.eas.grid.ModelGrid::removeColumnNode(Lcom/eas/grid/columns/header/HeaderNode;)(aColumnFacade.unwrap());
            else
              return false;
        };
        aPublished.addColumnNode = function(aColumnFacade){
            if(aColumnFacade && aColumnFacade.unwrap)
                aWidget.@com.eas.grid.ModelGrid::addColumnNode(Lcom/eas/grid/columns/header/HeaderNode;)(aColumnFacade.unwrap());
        };
        aPublished.insertColumnNode = function(aIndex, aColumnFacade){
            if(aColumnFacade && aColumnFacade.unwrap)
                aWidget.@com.eas.grid.ModelGrid::insertColumnNode(ILcom/eas/grid/columns/header/HeaderNode;)(aIndex, aColumnFacade.unwrap());
        };
        aPublished.columnNodes = function(){
            var headerRoots = aWidget.@com.eas.grid.ModelGrid::getHeader()();
            var rootsCount = headerRoots.@java.util.List::size()();
            var res = [];
            for(var r = 0; r < rootsCount; r++){
                var nNode = headerRoots.@java.util.List::get(I)(r);
                var jsNode = nNode.@com.eas.core.HasPublished::getPublished()();
                res.push(jsNode);
            }
            return res;
        };
        Object.defineProperty(aPublished, "headerVisible", {
            get : function() {
              return aWidget.@com.eas.grid.ModelGrid::isHeaderVisible()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setHeaderVisible(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "draggableRows", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isDraggableRows()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setDraggableRows(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "frozenRows", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getFrozenRows()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setFrozenRows(I)(+aValue);
            }
        });
        Object.defineProperty(aPublished, "frozenColumns", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getFrozenColumns()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setFrozenColumns(I)(+aValue);
            }
        });
        Object.defineProperty(aPublished, "rowsHeight", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getRowsHeight()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setRowsHeight(I)(aValue * 1);
            }
        });
        Object.defineProperty(aPublished, "showHorizontalLines", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isShowHorizontalLines()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setShowHorizontalLines(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "showVerticalLines", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isShowVerticalLines()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setShowVerticalLines(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "showOddRowsInOtherColor", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isShowOddRowsInOtherColor()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setShowOddRowsInOtherColor(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "gridColor", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getGridColor()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setGridColor(Lcom/eas/ui/PublishedColor;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "oddRowsColor", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOddRowsColor()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOddRowsColor(Lcom/eas/ui/PublishedColor;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "cursorProperty", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getCursorProperty()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setCursorProperty(Ljava/lang/String;)(aValue ? '' + aValue : null);
            }
        });
        
        Object.defineProperty(aPublished, "onRender", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOnRender()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "onAfterRender", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOnAfterRender()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOnAfterRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "onExpand", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOnExpand()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOnExpand(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "onCollapse", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOnCollapse()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOnCollapse(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
          });
        Object.defineProperty(aPublished, "selected", {
            get : function() {
                var selectionList = aWidget.@com.eas.grid.ModelGrid::getJsSelected()();
                var selectionArray = [];
                for ( var i = 0; i < selectionList.@java.util.List::size()(); i++) {
                    selectionArray[selectionArray.length] = selectionList.@java.util.List::get(I)(i);
                }
                return selectionArray;
            }
        });
        Object.defineProperty(aPublished, "editable", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isEditable()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setEditable(Z)(aValue);
            }
        });
        Object.defineProperty(aPublished, "deletable", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isDeletable()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setDeletable(Z)(aValue);
            }
        });
        Object.defineProperty(aPublished, "insertable", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isInsertable()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setInsertable(Z)(aValue);
            }
        });
        Object.defineProperty(aPublished, "data", {
            get : function() {
                return aWidget.@com.eas.ui.HasBinding::getData()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasBinding::setData(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "field", {
            get : function() {
                return aWidget.@com.eas.ui.HasBinding::getField()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasBinding::setField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(aPublished, "parentField", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getParentField();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setParentField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
		Object.defineProperty(aPublished, "childrenField", {
			get : function() {
				return aWidget.@com.eas.grid.ModelGrid::getChildrenField();
			},
			set : function(aValue) {
				aWidget.@com.eas.grid.ModelGrid::setChildrenField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
    }-*/;

    public JavaScriptObject getOnRender() {
        return onRender;
    }

    public void setOnRender(JavaScriptObject aValue) {
        onRender = aValue;
    }

    public JavaScriptObject getOnAfterRender() {
        return onAfterRender;
    }

    public void setOnAfterRender(JavaScriptObject aValue) {
        onAfterRender = aValue;
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
        SetSelectionModel<? super JavaScriptObject> sm = getSelectionModel();
        return new ArrayList<JavaScriptObject>((Collection<JavaScriptObject>) sm.getSelectedSet());
    }

    public void clearSelection() {
        SetSelectionModel<? super JavaScriptObject> sm = getSelectionModel();
        sm.clear();
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

    public boolean expanded(JavaScriptObject anElement) {
        if (getDataProvider() instanceof JsArrayTreeDataProvider) {
            JsArrayTreeDataProvider treeDataProvider = (JsArrayTreeDataProvider) getDataProvider();
            return treeDataProvider.isExpanded(anElement);
        } else {
            return false;
        }
    }

    public void expand(JavaScriptObject anElement) {
        if (getDataProvider() instanceof JsArrayTreeDataProvider) {
            JsArrayTreeDataProvider treeDataProvider = (JsArrayTreeDataProvider) getDataProvider();
            treeDataProvider.expand(anElement);
        }
    }

    public void collapse(JavaScriptObject anElement) {
        if (getDataProvider() instanceof JsArrayTreeDataProvider) {
            JsArrayTreeDataProvider treeDataProvider = (JsArrayTreeDataProvider) getDataProvider();
            treeDataProvider.collapse(anElement);
        }
    }

    public void toggle(JavaScriptObject anElement) {
        if (getDataProvider() instanceof JsArrayTreeDataProvider) {
            JsArrayTreeDataProvider treeDataProvider = (JsArrayTreeDataProvider) getDataProvider();
            if (treeDataProvider.isExpanded(anElement)) {
                treeDataProvider.collapse(anElement);
            } else {
                treeDataProvider.expand(anElement);
            }
        }
    }

    public void willBeVisible(final JavaScriptObject anElement, final boolean aNeedToSelect) {
        redraw(); // force rendering request to avoid makeVisible() deferring on an undefined moment in the future. 
        afterRender(new Runnable() {

            @Override
            public void run() {
                makeVisible(anElement, aNeedToSelect);
            }

        });
    }

    public boolean makeVisible(JavaScriptObject anElement, boolean aNeedToSelect) {
        IndexOfProvider<JavaScriptObject> indexOfProvider = (IndexOfProvider<JavaScriptObject>) dataProvider;
        int index = indexOfProvider.indexOf(anElement);
        if (index > -1) {
            if (index >= 0 && index < frozenRows) {
                TableCellElement leftCell = frozenLeft.getViewCell(index, 0);
                if (leftCell != null) {
                    leftCell.scrollIntoView();
                } else {
                    TableCellElement rightCell = frozenRight.getViewCell(index, 0);
                    if (rightCell != null) {
                        rightCell.scrollIntoView();
                    }
                }
            } else {
                TableCellElement leftCell = scrollableLeft.getViewCell(index, 0);
                if (leftCell != null) {
                    leftCell.scrollIntoView();
                } else {
                    TableCellElement rightCell = scrollableRight.getViewCell(index, 0);
                    if (rightCell != null) {
                        rightCell.scrollIntoView();
                    }
                }
            }
            if (aNeedToSelect) {
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
        } else {
            return false;
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
