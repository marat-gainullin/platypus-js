package com.eas.client.gxtcontrols.grid;

import com.google.gwt.core.client.JavaScriptObject;


public class ModelGridSpinColumn extends ModelGridColumn<Double> {

	public ModelGridSpinColumn(String aName) {
		super(aName);
	}

	public boolean isVisible() {
		return super.isVisible();
	}

	public void setVisible(boolean aValue) {
		super.setVisible(aValue);
	}

	public int getWidth() {
		return super.getWidth();
	}

	public void setWidth(int aValue) {
		super.setWidth(aValue);
	}

	public String getTitle() {
		return super.getTitle();
	}

	public void setTitle(String aValue) {
		super.setTitle(aValue);
	}

	public boolean isResizable() {
		return super.isResizable();
	}

	public void setResizable(boolean aValue) {
		super.setResizable(aValue);
	}

	public boolean isReadonly() {
		return super.isReadonly();
	}

	public void setReadonly(boolean aValue) {
		super.setReadonly(aValue);
	}

	public boolean isSortable() {
		return super.isSortable();
	}

	public void setSortable(boolean aValue) {
		super.setSortable(aValue);
	}

	public JavaScriptObject getCellFunction() {
		return super.getCellFunction();
	}

	public void setCellFunction(JavaScriptObject aValue) {
		super.setCellFunction(aValue);
	}

	public JavaScriptObject getSelectFunction() {
		return super.getSelectFunction();
	}

	public void setSelectFunction(JavaScriptObject aValue) {
		super.setSelectFunction(aValue);
	}

	@Override
	public void publish(JavaScriptObject aInjectionTarget) {
		if (name != null && !name.isEmpty())
			jsPublish(this, name, aInjectionTarget);
	}

	private static native void jsPublish(ModelGridSpinColumn aColumn, String aName, JavaScriptObject aInjectionTarget)/*-{
		var published = {};
		Object.defineProperty(aInjectionTarget, aName, {
			get : function() {
				return published;
			}
		});
		Object.defineProperty(published, "visible", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "width", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::getWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setWidth(I)(aValue != null ?aValue:0);
			}
		});
		Object.defineProperty(published, "title", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::getTitle()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setTitle(Ljava/lang/String;)(aValue != null ?''+aValue:'');
			}
		});
		Object.defineProperty(published, "resizable", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::isResizable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setResizable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "readonly", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::isReadonly()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setReadonly(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "sortable", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setSortable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "onRender", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::getCellFunction()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setCellFunction(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onSelect", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::getSelectFunction()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setSelectFunction(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		aColumn.@com.eas.client.gxtcontrols.grid.ModelGridSpinColumn::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
	}-*/;
}
