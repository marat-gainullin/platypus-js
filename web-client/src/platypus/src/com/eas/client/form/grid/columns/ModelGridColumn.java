package com.eas.client.form.grid.columns;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.bearsoft.gwt.ui.widgets.grid.GridColumn;
import com.bearsoft.gwt.ui.widgets.grid.GridSection;
import com.bearsoft.gwt.ui.widgets.grid.cells.CellHasReadonly;
import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedEditorCell;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.eas.client.converters.RowValueConverter;
import com.eas.client.form.Publisher;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.eas.client.model.Entity;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public abstract class ModelGridColumn<T> extends GridColumn<Row, T> implements FieldUpdater<Row, T>, ChangesHost, HasPublished, ModelGridColumnFacade {

	protected Entity rowsEntity;
	protected ModelElementRef columnModelRef;
	protected RowValueConverter<T> converter;
	// Original header (before splitting and analyzing) node.
	protected HeaderNode headerNode;
	protected PublishedDecoratorBox<T> editor;
	protected RowsComparator comparator;
	protected ModelGrid grid;
	protected String name;
	protected double designedWidth;
	protected double widthDelta;
	protected boolean fixed;
	protected boolean resizable = true;
	protected boolean moveable = true;
	protected boolean readonly;
	protected boolean visible = true;
	protected boolean selectOnly;
	protected JavaScriptObject published;
	protected JavaScriptObject onRender;
	protected JavaScriptObject onSelect;

	public ModelGridColumn(Cell<T> aCell, String aName, Entity aRowsEntity, ModelElementRef aColumnModelRef, RowValueConverter<T> aConverter) {
		super(aCell);
		if (getTargetCell() instanceof RenderedEditorCell<?>) {
			((RenderedEditorCell<T>) getTargetCell()).setReadonly(new CellHasReadonly() {

				@Override
				public boolean isReadonly() {
					return readonly || !grid.isEditable();
				}

			});
			((RenderedEditorCell<T>) getTargetCell()).setOnEditorClose(new RenderedEditorCell.EditorCloser() {
				@Override
				public void closed(Element aTable) {
					final GridSection<?> toFocus = GridSection.getInstance(aTable);
					if (toFocus != null) {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {

							@Override
							public void execute() {
								toFocus.setFocus(true);
							}
						});
					}
				}
			});
		}
		headerNode = new HeaderNode(new DraggableHeader<Row>("", null, this));
		rowsEntity = aRowsEntity;
		setColumnModelRef(aColumnModelRef);
		name = aName;
		converter = aConverter;
		setFieldUpdater(this);
		setDefaultSortAscending(true);
		setPublished(JavaScriptObject.createObject());
	}

	protected Cell<T> getTargetCell() {
		return ((TreeExpandableCell<Row, T>) getCell()).getCell();
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public ModelGrid getGrid() {
		return grid;
	}

	public void setGrid(ModelGrid aValue) {
		if (grid != aValue) {
			if (grid != null) {
				grid.getSortHandler().setComparator(this, null);
			}
			grid = aValue;
			if (grid != null) {
				if (grid.getSortHandler() != null) {
					if (isSortable()) {
						grid.getSortHandler().setComparator(this, comparator);
					} else {
						grid.getSortHandler().setComparator(this, null);
					}
				}
				grid.setColumnWidth(this, getWidth(), Style.Unit.PX);
				if (visible)
					grid.showColumn(this);
				else
					grid.hideColumn(this);
			} else {
				if (headerNode.getHeader() instanceof DraggableHeader<?>) {
					((DraggableHeader<?>) headerNode.getHeader()).setTable(null);
				}
			}
		}
	}

	public HeaderNode getHeaderNode() {
		return headerNode;
	}

	public Entity getRowsEntity() {
		return rowsEntity;
	}

	public void setRowsEntity(Entity aEntity) {
		rowsEntity = aEntity;
	}

	public ModelElementRef getColumnModelRef() {
		return columnModelRef;
	}

	public void setColumnModelRef(ModelElementRef aRef) {
		if (columnModelRef != aRef) {
			columnModelRef = aRef;
			if (editor != null) {
				editor.setClearButtonVisible(columnModelRef != null && columnModelRef.field != null ? columnModelRef.field.isNullable() : true);
			}
			if (columnModelRef != null && columnModelRef.entity != null && columnModelRef.field != null) {
				comparator = new RowsComparator(new SortingCriterion(columnModelRef.getColIndex(), true));
			}
		}
	}

	@Override
	public boolean isChanged(Row aRow) {
		if (aRow != null && columnModelRef != null && columnModelRef.getColIndex() > 0) {
			try {
				return aRow.isColumnUpdated(columnModelRef.getColIndex());
			} catch (Exception e) {
				Logger.getLogger(ModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
		return false;
	}

	@Override
	public T getValue(Row aRow) {
		if (aRow != null && columnModelRef != null && columnModelRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnModelRef.entity) {
					return converter.convert(aRow.getColumnObject(columnModelRef.getColIndex()));
				}
			} catch (Exception e) {
				Logger.getLogger(ModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
		return null;
	}

	@Override
	public void update(int aIndex, Row aRow, T value) {
		if (aRow != null && columnModelRef != null && columnModelRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnModelRef.entity) {
					aRow.setColumnObject(columnModelRef.getColIndex(), value);
				}
			} catch (Exception e) {
				Logger.getLogger(ModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean aValue) {
		if (visible != aValue) {
			visible = aValue;
			if (grid != null) {
				if (visible){
					grid.showColumn(this);
				}else{
					grid.hideColumn(this);
				}
			}
		}
	}

	public void updateVisible(boolean aValue) {
		if (visible != aValue) {
			visible = aValue;
		}
	}

	public double getDesignedWidth() {
		return designedWidth;
	}

	@Override
	public double getWidth() {
		return designedWidth + widthDelta;
	}

	@Override
	public void setWidth(double aValue) {
		if (getWidth() != aValue) {
			designedWidth = aValue;
			widthDelta = 0;
			if (grid != null) {
				grid.setColumnWidth(this, getWidth(), Style.Unit.PX);
			}
		}
	}

	public void updateWidth(double aValue) {
		if (getWidth() != aValue) {
			widthDelta = Math.max(0, aValue - designedWidth);
		}
	}

	@Override
	public String getTitle() {
		return headerNode != null && headerNode.getHeader() instanceof DraggableHeader<?> ? ((DraggableHeader<T>) headerNode.getHeader()).getTitle() : null;
	}

	@Override
	public void setTitle(String aValue) {
		if (headerNode != null && headerNode.getHeader() instanceof DraggableHeader<?>){
			((DraggableHeader<T>) headerNode.getHeader()).setTitle(aValue);
		}
	}

	@Override
	public boolean isResizable() {
		return resizable;
	}

	@Override
	public void setResizable(boolean aValue) {
		resizable = aValue;
		if (headerNode != null && headerNode.getHeader() instanceof DraggableHeader<?>){
			((DraggableHeader<T>) headerNode.getHeader()).setResizable(resizable && !fixed);
		}
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean aValue) {
		moveable = aValue;
		if (headerNode != null && headerNode.getHeader() instanceof DraggableHeader<?>){
			((DraggableHeader<T>) headerNode.getHeader()).setMoveable(moveable && !fixed);
		}
	}

	@Override
	public boolean isReadonly() {
		return readonly;
	}

	@Override
	public void setReadonly(boolean aValue) {
		readonly = aValue;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean aValue) {
		fixed = aValue;
		if (headerNode != null && headerNode.getHeader() instanceof DraggableHeader<?>) {
			((DraggableHeader<T>) headerNode.getHeader()).setResizable(resizable && !fixed);
			((DraggableHeader<T>) headerNode.getHeader()).setMoveable(moveable && !fixed);
		}
	}

	@Override
	public boolean isSortable() {
		return super.isSortable();
	}

	@Override
	public void setSortable(boolean aValue) {
		super.setSortable(aValue);
		if (grid != null) {
			if (aValue) {
				grid.getSortHandler().setComparator(this, comparator);
			} else {
				grid.getSortHandler().setComparator(this, null);
			}
		}
	}

	public boolean isSelectOnly() {
		return selectOnly;
	}

	public void setSelectOnly(boolean aValue) {
		selectOnly = aValue;
	}

	@Override
	public JavaScriptObject getOnRender() {
		return onRender != null ? onRender : grid.getOnRender();
	}

	@Override
	public void setOnRender(JavaScriptObject aValue) {
		onRender = aValue;
	}

	@Override
	public JavaScriptObject getOnSelect() {
		return onSelect;
	}

	@Override
	public void setOnSelect(JavaScriptObject aValue) {
		onSelect = aValue;
		if (editor != null){
			editor.setOnSelect(onSelect);
		}
	}

	public String getName() {
		return name;
	}

	public PublishedDecoratorBox<T> getEditor() {
		return editor;
	}

	public void setEditor(PublishedDecoratorBox<T> aEditor) {
		if (editor != aEditor) {
			if (editor != null) {
				editor.setOnSelect(null);
			}
			editor = aEditor;
			if (editor != null) {
				editor.setOnSelect(onSelect);
				editor.setClearButtonVisible(columnModelRef != null && columnModelRef.field != null ? columnModelRef.field.isNullable() : true);
				editor.setPublished(JavaScriptObject.createObject());
			}
		}
	}

	public static PublishedCell calcContextPublishedCell(JavaScriptObject aThis, JavaScriptObject aOnRender, com.google.gwt.cell.client.Cell.Context context, ModelElementRef aColModelElement,
	        String aDisplay, Entity aRowsEntity) throws Exception {
		if (aOnRender != null) {
			Row renderedRow = renderedRow(context, aRowsEntity);
			if (renderedRow != null) {
				Object data = aColModelElement != null ? Utils.toJs(renderedRow.getColumnObject(aColModelElement.getColIndex())) : null;
				PublishedCell cell = Publisher.publishCell(data, aDisplay);
				Object[] rowIds = renderedRow.getPKValues();
				if (rowIds != null) {
					for (int i = 0; i < rowIds.length; i++)
						rowIds[i] = Utils.toJs(rowIds[i]);
				}
				Utils.executeScriptEventVoid(
				        aThis,
				        aOnRender,
				        JsEvents.publishOnRenderEvent(aThis, rowIds != null && rowIds.length > 0 ? (rowIds.length > 1 ? Utils.toJsArray(rowIds) : rowIds[0]) : null, null,
				                Entity.publishRowFacade(renderedRow, aRowsEntity, null), cell));
				return cell;
			}
		}
		return null;
	}

	protected static Row renderedRow(com.google.gwt.cell.client.Cell.Context context, Entity aRowsEntity) throws Exception {
		Object key = context.getKey();
		Row renderedRow = aRowsEntity.findRowById(key);
		return renderedRow;
	}

	protected void bindGridDisplayCallback(final String aTargetElementId, final PublishedCell aCell) {
		aCell.setDisplayCallback(new Runnable() {
			@Override
			public void run() {
				Element padded = Document.get().getElementById(aTargetElementId);
				if (padded != null) {
					aCell.styleToElementBackgroundToTd(padded);
					int paddingLeft = RenderedEditorCell.CELL_PADDING;
					ImageResource icon = aCell.getStyle().getIcon();
					if (icon != null) {
						paddingLeft += icon.getWidth();
					}
					padded.getStyle().setPaddingLeft(paddingLeft, Style.Unit.PX);
					String toRender = aCell.getDisplay();
					if (toRender == null)
						toRender = "&#160;";
					padded.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString(toRender));
				}
			}
		});
	}

	protected static void bindIconCallback(final PublishedStyle aStyle, final String aTargetElementId) {
		aStyle.setIconCallback(new Runnable() {

			@Override
			public void run() {
				Element padded = Document.get().getElementById(aTargetElementId);
				if (padded != null) {
					int paddingLeft = RenderedEditorCell.CELL_PADDING + (aStyle.getIcon()!= null ? aStyle.getIcon().getWidth() : 0);
					padded.getStyle().setPaddingLeft(paddingLeft, Style.Unit.PX);
				}
			}

		});
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null){
				publish(this, published);
			}
		}
	}

	private static native void publish(ModelGridColumnFacade aColumn, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "visible", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "width", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::getWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setWidth(D)(aValue != null ? aValue : 0);
			}
		});
		Object.defineProperty(aPublished, "title", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::getTitle()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setTitle(Ljava/lang/String;)(aValue != null ? ''+aValue : '');
			}
		});
		Object.defineProperty(aPublished, "resizable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isResizable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setResizable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "movable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isMoveable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setMoveable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "readonly", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isReadonly()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setReadonly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "sortable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setSortable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::getOnRender()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::getOnSelect()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
	}-*/;
}
