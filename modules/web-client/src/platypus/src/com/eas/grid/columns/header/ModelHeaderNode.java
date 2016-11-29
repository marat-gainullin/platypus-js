package com.eas.grid.columns.header;

import com.eas.bound.ModelDecoratorBox;
import com.eas.bound.ModelFormattedField;
import com.eas.core.HasPublished;
import com.eas.grid.DraggableHeader;
import com.eas.grid.columns.ModelColumn;
import com.eas.ui.HasJsName;
import com.eas.ui.PublishedColor;
import com.eas.ui.PublishedFont;
import com.google.gwt.core.client.JavaScriptObject;

public class ModelHeaderNode extends HeaderNode<JavaScriptObject> implements HasJsName, HasPublished {

	protected JavaScriptObject published;
	protected String name;

	public ModelHeaderNode() {
		super();
		column = new ModelColumn();
		((ModelColumn)column).setEditor(new ModelFormattedField());
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
	
	public JavaScriptObject getJsEditor(){
		return ((ModelColumn) column).getEditor().getPublished();
	}
	
	public void setEditor(ModelDecoratorBox<?> aEditor){
		((ModelColumn) column).setEditor(aEditor);
	}
	
	private static native void publish(ModelHeaderNode aColumn, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "editor", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getJsEditor()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setEditor(Lcom/eas/bound/ModelDecoratorBox;)(aValue && aValue.unwrap ? aValue.unwrap() : null);
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getField()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setField(Ljava/lang/String;)(aValue != null ? ''+aValue : '');
			}
		});
		Object.defineProperty(aPublished, "visible", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::isVisible()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setVisible(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "minWidth", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getMinWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setMinWidth(D)(aValue != null ? aValue : 0);
			}
		});
		Object.defineProperty(aPublished, "maxWidth", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getMaxWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setMaxWidth(D)(aValue != null ? aValue : 0);
			}
		});
		Object.defineProperty(aPublished, "preferredWidth", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getPreferredWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setPreferredWidth(D)(aValue != null ? aValue : 0);
			}
		});
		Object.defineProperty(aPublished, "width", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getWidth()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setWidth(D)(aValue != null ? aValue : 0);
			}
		});
		Object.defineProperty(aPublished, "title", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getTitle()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setTitle(Ljava/lang/String;)(aValue != null ? ''+aValue : '');
			}
		});
		Object.defineProperty(aPublished, "resizable", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::isResizable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setResizable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "movable", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::isMoveable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setMoveable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "readonly", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::isReadonly()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setReadonly(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "sortable", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::isSortable()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setSortable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "sortField", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getSortField()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setSortField(Ljava/lang/String;)(aValue != null ? ''+aValue : '');
			}
		});
		Object.defineProperty(aPublished, "foreground", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getForeground()()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setForeground(Lcom/eas/ui/PublishedColor;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "background", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getBackground()()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setBackground(Lcom/eas/ui/PublishedColor;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "font", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getFont()()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setFont(Lcom/eas/ui/PublishedFont;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getOnRender()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getOnSelect()();
			},
			set : function(aValue) {
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		aPublished.sort = function() {
			aColumn.@com.eas.grid.columns.header.ModelHeaderNode::sort()();
		};
		aPublished.sortDesc = function() {
			aColumn.@com.eas.grid.columns.header.ModelHeaderNode::sortDesc()();
		};
		aPublished.unsort = function() {
			aColumn.@com.eas.grid.columns.header.ModelHeaderNode::unsort()();
		};
		aPublished.removeColumnNode = function(aColumnFacade){
			if(aColumnFacade && aColumnFacade.unwrap)
				return aColumn.@com.eas.grid.columns.header.ModelHeaderNode::removeColumnNode(Lcom/eas/grid/columns/header/HeaderNode;)(aColumnFacade.unwrap());
			else
				return false;
		};
		aPublished.addColumnNode = function(aColumnFacade){
			if(aColumnFacade && aColumnFacade.unwrap)
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::addColumnNode(Lcom/eas/grid/columns/header/HeaderNode;)(aColumnFacade.unwrap());
		};
		aPublished.insertColumnNode = function(aIndex, aColumnFacade){
			if(aColumnFacade && aColumnFacade.unwrap)
				aColumn.@com.eas.grid.columns.header.ModelHeaderNode::insertColumnNode(ILcom/eas/grid/columns/header/HeaderNode;)(aIndex, aColumnFacade.unwrap());
		};
		aPublished.columnNodes = function(){
			var nChildren = aColumn.@com.eas.grid.columns.header.ModelHeaderNode::getChildren()();
			var nChildrenCount = nChildren.@java.util.List::size()();
			var res = [];
			for(var c = 0; c < nChildrenCount; c++){
				var nNode = nChildren.@java.util.List::get(I)(c);
				var jsNode = nNode.@com.eas.core.HasPublished::getPublished()();
				res.push(jsNode);
			}
			return res;
		};
	}-*/;
}
