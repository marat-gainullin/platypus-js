package com.eas.client.gxtcontrols.grid;

import com.google.gwt.core.client.JavaScriptObject;

public class ModelGridTextColumn extends ModelGridColumn<String> {

	public ModelGridTextColumn(String aName) {
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

	private static native void jsPublish(ModelGridTextColumn aColumn, String aName, JavaScriptObject aInjectionTarget)/*-{
		var published = {};
		Object.defineProperty(aInjectionTarget, aName, {
			get : function() {
				return published;
			}
		});
		Object.defineProperty(published, "visible", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "width", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::getWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setWidth(I)(aValue != null ?aValue:0);
			}
		});
		Object.defineProperty(published, "title", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::getTitle()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setTitle(Ljava/lang/String;)(aValue != null ?''+aValue:'');
			}
		});
		Object.defineProperty(published, "resizable", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::isResizable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setResizable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "readonly", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::isReadonly()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setReadonly(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "sortable", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setSortable(Z)((false != aValue));
			}
		});
		Object.defineProperty(published, "onRender", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::getCellFunction()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setCellFunction(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onSelect", {
			get : function() {
				return aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::getSelectFunction()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setSelectFunction(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		aColumn.@com.eas.client.gxtcontrols.grid.ModelGridTextColumn::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
	}-*/;
}
