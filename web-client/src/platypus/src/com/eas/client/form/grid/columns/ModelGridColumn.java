package com.eas.client.form.grid.columns;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.bearsoft.gwt.ui.widgets.grid.GridColumn;
import com.bearsoft.rowset.Row;
import com.eas.client.converters.RowValueConverter;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.eas.client.model.Entity;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.JavaScriptObject;

public abstract class ModelGridColumn<T> extends GridColumn<Row, T> implements FieldUpdater<Row, T>, ChangesHost, HasPublished, ModelGridColumnFacade {

	protected Entity rowsEntity;
	protected ModelElementRef columnModelRef;
	protected RowValueConverter<T> converter;
	protected DraggableHeader<T> header;
	protected PublishedDecoratorBox<T> editor;
	protected ModelGrid grid;
	protected String name;
	protected int designedWidth;
	protected boolean fixed;
	protected boolean resizable;
	protected boolean readonly;
	protected boolean visible;
	protected boolean sortable;
	protected JavaScriptObject published;
	protected JavaScriptObject onRender;
	protected JavaScriptObject onSelect;

	public ModelGridColumn(Cell<T> aCell, String aName, Entity aRowsEntity, ModelElementRef aColumnModelRef, RowValueConverter<T> aConverter) {
		super(aCell);
		rowsEntity = aRowsEntity;
		columnModelRef = aColumnModelRef;
		name = aName;
		converter = aConverter;
		setFieldUpdater(this);
	}

	public ModelGrid getGrid() {
		return grid;
	}

	public void setGrid(ModelGrid aValue) {
		grid = aValue;
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
		columnModelRef = aRef;
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
	}

	@Override
	public boolean isReadonly() {
		return readonly;
	}

	@Override
	public void setReadonly(boolean aValue) {
		readonly = aValue;
	}

	@Override
	public boolean isSortable() {
		return sortable;
	}

	@Override
	public void setSortable(boolean aValue) {
		sortable = aValue;
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
			}
		}
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
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnFacade::setResizable(Z)((false != aValue));
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
