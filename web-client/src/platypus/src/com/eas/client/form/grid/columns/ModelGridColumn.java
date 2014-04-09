package com.eas.client.form.grid.columns;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.bearsoft.gwt.ui.widgets.grid.GridColumn;
import com.bearsoft.gwt.ui.widgets.grid.cells.CellHasReadonly;
import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedPopupEditorCell;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.eas.client.converters.RowValueConverter;
import com.eas.client.form.ControlsUtils;
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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.dom.client.Element;

public abstract class ModelGridColumn<T> extends GridColumn<Row, T> implements FieldUpdater<Row, T>, ChangesHost, HasPublished, ModelGridColumnFacade {

	protected Entity rowsEntity;
	protected ModelElementRef columnModelRef;
	protected RowValueConverter<T> converter;
	protected DraggableHeader<T> header;
	protected PublishedDecoratorBox<T> editor;
	protected RowsComparator comparator;
	protected ModelGrid grid;
	protected String name;
	protected int designedWidth;
	protected boolean fixed;
	protected boolean resizable;
	protected boolean moveable;
	protected boolean readonly;
	protected boolean visible;
	protected boolean selectOnly;
	protected JavaScriptObject published;
	protected JavaScriptObject onRender;
	protected JavaScriptObject onSelect;

	public ModelGridColumn(Cell<T> aCell, String aName, Entity aRowsEntity, ModelElementRef aColumnModelRef, RowValueConverter<T> aConverter) {
		super(aCell);
		if (aCell instanceof RenderedPopupEditorCell<?>) {
			((RenderedPopupEditorCell<T>) aCell).setReadonly(new CellHasReadonly() {

				@Override
				public boolean isReadonly() {
					return readonly || !grid.isEditable();
				}

			});
		}
		rowsEntity = aRowsEntity;
		setColumnModelRef(aColumnModelRef);
		name = aName;
		converter = aConverter;
		setFieldUpdater(this);
		setDefaultSortAscending(true);
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
				if (isSortable()) {
					grid.getSortHandler().setComparator(this, comparator);
				} else {
					grid.getSortHandler().setComparator(this, null);
				}
			}
		}
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
			if(columnModelRef != null && columnModelRef.entity != null && columnModelRef.field != null){
				comparator = new RowsComparator(new SortingCriterion(columnModelRef.getColIndex(), true));
			}
		}
	}

	@Override
	public boolean isChanged(Row aRow) {
		if (aRow != null && columnModelRef != null && columnModelRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnModelRef.entity) {
					return aRow.isColumnUpdated(columnModelRef.getColIndex());
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						return columnModelRef.entity.getRowset().getCurrentRow().isColumnUpdated(columnModelRef.getColIndex());
					}
				}
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
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						Object value = columnModelRef.entity.getRowset().getObject(columnModelRef.getColIndex());
						return converter.convert(value);
					}
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
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						columnModelRef.entity.getRowset().updateObject(columnModelRef.getColIndex(), value);
					}
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
		visible = aValue;
	}

	@Override
	public int getWidth() {
		return designedWidth;
	}

	@Override
	public void setWidth(int aValue) {
		designedWidth = aValue;
	}

	@Override
	public String getTitle() {
		return header != null ? header.getTitle() : null;
	}

	@Override
	public void setTitle(String aValue) {
		if (header != null)
			header.setTitle(aValue);
	}

	@Override
	public boolean isResizable() {
		return resizable;
	}

	@Override
	public void setResizable(boolean aValue) {
		resizable = aValue;
		if (header != null)
			header.setResizable(resizable && !fixed);
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean aValue) {
		moveable = aValue;
		if (header != null)
			header.setMoveable(moveable && !fixed);
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
		if (header != null) {
			header.setResizable(resizable && !fixed);
			header.setMoveable(moveable && !fixed);
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
		if (editor != null)
			editor.setOnSelect(onSelect);
	}

	public String getName() {
		return name;
	}

	public DraggableHeader<T> getHeader() {
		return header;
	}

	public void setHeader(DraggableHeader<T> aValue) {
		header = aValue;
		if (header != null) {
			header.setResizable(resizable && !fixed);
			header.setMoveable(moveable && !fixed);
		}
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
			}
		}
	}

	public static PublishedCell calcContextPublishedCell(JavaScriptObject aThis, JavaScriptObject cellFunction, com.google.gwt.cell.client.Cell.Context context, ModelElementRef aColModelElement,
	        String aDisplay, Entity aRowsEntity) throws Exception {
		if (cellFunction != null) {
			Row renderedRow = renderedRow(context, aRowsEntity);
			if (renderedRow != null) {
				Object data = aColModelElement != null ? Utils.toJs(renderedRow.getColumnObject(aColModelElement.getColIndex())) : null;
				PublishedCell cell = Publisher.publishCell(data, aDisplay);
				Object[] rowIds = renderedRow.getPKValues();
				if (rowIds != null) {
					for (int i = 0; i < rowIds.length; i++)
						rowIds[i] = Utils.toJs(rowIds[i]);
				}
				Boolean res = Utils.executeScriptEventBoolean(
				        aThis,
				        cellFunction,
				        JsEvents.publishOnRenderEvent(aThis, rowIds != null && rowIds.length > 0 ? (rowIds.length > 1 ? Utils.toJsArray(rowIds) : rowIds[0]) : null, null,
				                Entity.publishRowFacade(renderedRow, aRowsEntity), cell));
				if (res != null && res) {
					return cell;
				}
			}
		}
		return null;
	}

	protected static Row renderedRow(com.google.gwt.cell.client.Cell.Context context, Entity aRowsEntity) throws Exception {
		Object key = context.getKey();
		Row renderedRow = aRowsEntity.findRowById(key);
		return renderedRow;
	}

	protected void bindContextCallback(final com.google.gwt.cell.client.Cell.Context context, final PublishedCell cellToRender) {
		cellToRender.setDisplayCallback(new Runnable() {
			@Override
			public void run() {
				Element td = grid.getViewCell(context.getIndex(), context.getColumn());
				if (td != null) {
					// rendering
					SafeHtmlBuilder sb = new SafeHtmlBuilder();
					SafeHtmlBuilder lsb = new SafeHtmlBuilder();
					String toRender1 = cellToRender.getDisplay();
					if (toRender1 == null)
						lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
					else
						lsb.append(SafeHtmlUtils.fromString(toRender1));
					PublishedStyle styleToRender = grid.complementPublishedStyle(cellToRender.getStyle());
					ControlsUtils.renderDecorated(lsb, styleToRender, sb);
					td.setInnerSafeHtml(sb.toSafeHtml());
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
			if (published != null)
				publish(this, published);
		}
	}

	private static native void publish(ModelGridColumnFacade aColumn, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aInjectionTarget, aName, {
			get : function() {
				return published;
			}
		});
		Object.defineProperty(published, "visible", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "width", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::getWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setWidth(I)(aValue != null ?aValue:0);
			}
		});
		Object.defineProperty(published, "title", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::getTitle()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setTitle(Ljava/lang/String;)(aValue != null ?''+aValue:'');
			}
		});
		Object.defineProperty(published, "resizable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isResizable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setResizable(Z)(!!aValue);
			}
		});
		Object.defineProperty(published, "movable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isMoveable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setMoveable(Z)(!!aValue);
			}
		});
		Object.defineProperty(published, "readonly", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isReadonly()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setReadonly(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "sortable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setSortable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "onRender", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::getOnRender()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onSelect", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::getOnSelect()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
	}-*/;
}
