package com.eas.client.gxtcontrols.grid;

import com.google.gwt.core.client.JavaScriptObject;

public class ModelGridCheckColumn extends ModelGridColumn<Boolean> {

	public ModelGridCheckColumn(String aName) {
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

	private static native void jsPublish(ModelGridCheckColumn aColumn, String aName, JavaScriptObject aInjectionTarget)/*-{
		var published = {};
		Object.defineProperty(aInjectionTarget, aName, {
			get : function() {
				return published;
			}
		});
		Object.defineProperty(published, "visible", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "width", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::getWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setWidth(I)(aValue != null ?aValue:0);
			}
		});
		Object.defineProperty(published, "title", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::getTitle()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setTitle(Ljava/lang/String;)(aValue != null ?''+aValue:'');
			}
		});
		Object.defineProperty(published, "resizable", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::isResizable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setResizable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "readonly", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::isReadonly()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setReadonly(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "sortable", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setSortable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "onRender", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::getCellFunction()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setCellFunction(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onSelect", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::getSelectFunction()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setSelectFunction(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		aColumn.@com.eas.client.gxtcontrols.grid.ModelGridCheckColumn::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
	}-*/;
}
