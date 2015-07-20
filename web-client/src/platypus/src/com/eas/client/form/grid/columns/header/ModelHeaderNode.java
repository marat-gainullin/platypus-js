package com.eas.client.form.grid.columns.header;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.eas.client.form.grid.columns.ModelColumn;
import com.eas.client.form.published.HasJsName;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedColor;
import com.eas.client.form.published.PublishedFont;
import com.google.gwt.core.client.JavaScriptObject;

public class ModelHeaderNode extends HeaderNode<JavaScriptObject> implements HasJsName, HasPublished {

	protected JavaScriptObject published;
	protected String name;

	public ModelHeaderNode() {
		super();
		column = new ModelColumn();
		header = new DraggableHeader<JavaScriptObject>("", null, column, this);
	}

	@Override
	public ModelHeaderNode lightCopy(){
		ModelHeaderNode copied = new ModelHeaderNode();
		copied.setColumn(column);
		copied.setHeader(header);
		return copied;
	}
	
	public PublishedColor getBackground() {
		return ((DraggableHeader<?>)header).getBackground();
	}

	public void setBackground(PublishedColor aValue) {
		((DraggableHeader<?>)header).setBackground(aValue);
	}

	public PublishedColor getForeground() {
		return ((DraggableHeader<?>)header).getForeground();
	}

	public void setForeground(PublishedColor aValue) {
		((DraggableHeader<?>)header).setForeground(aValue);
	}

	public PublishedFont getFont() {
		return ((DraggableHeader<?>)header).getFont();
	}

	public void setFont(PublishedFont aValue) {
		((DraggableHeader<?>)header).setFont(aValue);
	}

	public double getMinWidth() {
		return ((ModelColumn) column).getMinWidth();
	}

	public void setMinWidth(double aValue) {
		((ModelColumn) column).setMinWidth(aValue);
	}

	public double getMaxWidth() {
		return ((ModelColumn) column).getMaxWidth();
	}

	public void setMaxWidth(double aValue) {
		((ModelColumn) column).setMaxWidth(aValue);
	}

	public double getPreferredWidth() {
		return ((ModelColumn) column).getDesignedWidth();
	}

	public void setPreferredWidth(double aValue) {
		((ModelColumn) column).setWidth(aValue);
	}

	public String getField() {
		return ((ModelColumn) column).getField();
	}

	public void setField(String aValue) {
		((ModelColumn) column).setField(aValue);
	}

	public String getTitle() {
		return ((DraggableHeader<JavaScriptObject>) header).getTitle();
	}

	public void setTitle(String aValue) {
		((DraggableHeader<JavaScriptObject>) header).setTitle(aValue);
	}

	public boolean isResizable() {
		return ((DraggableHeader<JavaScriptObject>) header).isResizable();
	}

	public void setResizable(boolean aValue) {
		((DraggableHeader<JavaScriptObject>) header).setResizable(aValue);
	}

	public boolean isMoveable() {
		return ((DraggableHeader<JavaScriptObject>) header).isMoveable();
	}

	public void setMoveable(boolean aValue) {
		((DraggableHeader<JavaScriptObject>) header).setMoveable(aValue);
	}

	public boolean isVisible() {
		return ((ModelColumn) column).isVisible();
	}

	public void setVisible(boolean aValue) {
		((ModelColumn) column).setVisible(aValue);
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

	public String getSortField() {
		return ((ModelColumn) column).getSortField();
	}

	public void setSortField(String aValue) {
		((ModelColumn) column).setSortField(aValue);
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
			((ModelColumn) column).setPublished(published);
			if (published != null) {
				publish(this, published);				
			}
		}
	}

	public void sort(){
		((ModelColumn) column).sort();
	}
	public void sortDesc(){
		((ModelColumn) column).sortDesc();
	}
	public void unsort(){
		((ModelColumn) column).unsort();
	}
	
	
	private static native void publish(ModelHeaderNode aColumn, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getField()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setField(Ljava/lang/String;)(aValue != null ? ''+aValue : '');
			}
		});
		Object.defineProperty(aPublished, "visible", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "minWidth", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getMinWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setMinWidth(D)(aValue != null ? aValue : 0);
			}
		});
		Object.defineProperty(aPublished, "maxWidth", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getMaxWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setMaxWidth(D)(aValue != null ? aValue : 0);
			}
		});
		Object.defineProperty(aPublished, "preferredWidth", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getPreferredWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setPreferredWidth(D)(aValue != null ? aValue : 0);
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
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setReadonly(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "sortable", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setSortable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "sortField", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getSortField()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setSortField(Ljava/lang/String;)(aValue != null ? ''+aValue : '');
			}
		});
		Object.defineProperty(aPublished, "foreground", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getForeground()()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setForeground(Lcom/eas/client/form/published/PublishedColor;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "background", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getBackground()()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setBackground(Lcom/eas/client/form/published/PublishedColor;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "font", {
			get : function() {
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::getFont()()();
			},
			set : function(aValue) {
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::setFont(Lcom/eas/client/form/published/PublishedFont;)(aValue);
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
		aPublished.sort = function() {
			aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::sort()();
		};
		aPublished.sortDesc = function() {
			aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::sortDesc()();
		};
		aPublished.unsort = function() {
			aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::unsort()();
		};
		aPublished.removeColumnNode = function(aColumnFacade){
			if(aColumnFacade && aColumnFacade.unwrap)
				return aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::removeColumnNode(Lcom/bearsoft/gwt/ui/widgets/grid/header/HeaderNode;)(aColumnFacade.unwrap());
			else
				return false;
		};
		aPublished.addColumnNode = function(aColumnFacade){
			if(aColumnFacade && aColumnFacade.unwrap)
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::addColumnNode(Lcom/bearsoft/gwt/ui/widgets/grid/header/HeaderNode;)(aColumnFacade.unwrap());
		};
		aPublished.insertColumnNode = function(aIndex, aColumnFacade){
			if(aColumnFacade && aColumnFacade.unwrap)
				aColumn.@com.eas.client.form.grid.columns.header.ModelHeaderNode::insertColumnNode(ILcom/bearsoft/gwt/ui/widgets/grid/header/HeaderNode;)(aIndex, aColumnFacade.unwrap());
		};
	}-*/;
}
