package com.eas.client.form.grid.columns;

import com.eas.client.form.grid.ModelGrid;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public abstract class ModelGridColumnBase implements HasPublished {

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

	public ModelGridColumnBase(String aName) {
		super();
		name = aName;
	}

	public ModelGrid getGrid() {
		return grid;
	}

	public void setGrid(ModelGrid aValue) {
		grid = aValue;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean aValue) {
		visible = aValue;
	}

	public int getWidth() {
		return designedWidth;
	}

	public void setWidth(int aValue) {
		designedWidth = aValue;
	}

	public abstract String getTitle();
	
	public abstract void setTitle(String aValue);
	
	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean aValue) {
		resizable = aValue;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean aValue) {
		readonly = aValue;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean aValue) {
		sortable = aValue;
	}

	public JavaScriptObject getOnRender() {
		return onRender != null ? onRender : grid.getGeneralCellFunction();
	}

	public void setOnRender(JavaScriptObject aValue) {
		onRender = aValue;
	}

	public JavaScriptObject getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(JavaScriptObject aValue) {
		onSelect = aValue;
	}

	public String getName() {
		return name;
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if(published != aValue){
			published = aValue;
			if(published != null)
				publish(this, published);
		}
	}
	
	private static native void publish(ModelGridColumnBase aColumn, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aInjectionTarget, aName, {
			get : function() {
				return published;
			}
		});
		Object.defineProperty(published, "visible", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "width", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::getWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::setWidth(I)(aValue != null ?aValue:0);
			}
		});
		Object.defineProperty(published, "title", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::getTitle()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::setTitle(Ljava/lang/String;)(aValue != null ?''+aValue:'');
			}
		});
		Object.defineProperty(published, "resizable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::isResizable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::setResizable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "readonly", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::isReadonly()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::setReadonly(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "sortable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::setSortable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "onRender", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::getOnRender()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onSelect", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::getOnSelect()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.ModelGridColumnBase::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
	}-*/;
}
