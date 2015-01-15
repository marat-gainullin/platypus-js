package com.eas.client.form.grid.columns.header;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.eas.client.form.grid.columns.ModelColumn;
import com.eas.client.form.published.HasJsName;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class ModelHeaderNode extends HeaderNode<JavaScriptObject, Object> implements HasJsName, HasPublished {

	protected JavaScriptObject published;
	protected String name;
	protected boolean resizable = true;
	protected boolean moveable = true;

	public ModelHeaderNode() {
		super();
		column = new ModelColumn();
		header = new DraggableHeader<JavaScriptObject>("", null, column);
	}

	public String getField() {
		return ((ModelColumn) column).getField();
	}

	public void setField(String aValue) {
		((ModelColumn) column).setField(aValue);
	}

	public String getTitle() {
		return header.getTitle();
	}

	public void setTitle(String aValue) {
		header.setTitle(aValue);
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean aValue) {
		resizable = aValue;
		header.setResizable(resizable);
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean aValue) {
		moveable = aValue;
		header.setMoveable(moveable);
	}

	public boolean isVisible() {
		return ((ModelColumn) column).isVisible();
	}

	public void setVisible(boolean aValue) {
		if (isVisible() != aValue) {
			((ModelColumn) column).setVisible(aValue);
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

	public double getWidth() {
		return ((ModelColumn) column).getWidth();
	}

	public void setWidth(double aValue) {
		((ModelColumn) column).setWidth(aValue);
	}

	public boolean isReadonly() {
		return ((ModelColumn) column).isReadonly();
	}

	public void setReadonly(boolean aValue) {
		((ModelColumn) column).setReadonly(aValue);
	}

	public boolean isSortable() {
		return ((ModelColumn) column).isSortable();
	}

	public void setSortable(boolean aValue) {
		((ModelColumn) column).setSortable(aValue);
	}

	public JavaScriptObject getOnRender() {
		return ((ModelColumn) column).getOnRender();
	}

	public void setOnRender(JavaScriptObject aValue) {
		((ModelColumn) column).setOnRender(aValue);
	}

	public JavaScriptObject getOnSelect() {
		return ((ModelColumn) column).getOnSelect();
	}

	public void setOnSelect(JavaScriptObject aValue) {
		((ModelColumn) column).setOnSelect(aValue);
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, published);
			}
		}
	}

	private static native void publish(ModelHeaderNode aColumn, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "visible", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "width", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setWidth(D)(aValue != null ? aValue : 0);
			}
		});
		Object.defineProperty(aPublished, "title", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getTitle()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setTitle(Ljava/lang/String;)(aValue != null ? ''+aValue : '');
			}
		});
		Object.defineProperty(aPublished, "resizable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::isResizable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setResizable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "movable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::isMoveable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setMoveable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "readonly", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::isReadonly()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setReadonly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "sortable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setSortable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getOnRender()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getOnSelect()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
	}-*/;
}
