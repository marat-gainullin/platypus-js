package com.eas.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.bound.ModelDecoratorBox;
import com.eas.core.Utils;
import com.eas.grid.columns.ModelColumn;
import com.eas.grid.columns.header.CheckHeaderNode;
import com.eas.grid.columns.header.HeaderNode;
import com.eas.grid.columns.header.ModelHeaderNode;
import com.eas.grid.columns.header.RadioHeaderNode;
import com.eas.grid.columns.header.ServiceHeaderNode;
import com.eas.ui.PublishedColor;
import com.eas.ui.PublishedFont;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;

public class GridFactory implements UiWidgetReader{

	public UIObject readWidget(Element anElement, final UiReader aFactory) throws Exception {
		String type = anElement.getTagName();
		switch (type) {
		case "ModelGrid": {
			ModelGrid grid = new ModelGrid();
			GridPublisher.publish(grid);
			aFactory.readGeneralProps(anElement, grid);
			int frozenColumns = Utils.getIntegerAttribute(anElement, "frozenColumns", 0);
			int frozenRows = Utils.getIntegerAttribute(anElement, "frozenRows", 0);
			boolean insertable = Utils.getBooleanAttribute(anElement, "insertable", Boolean.TRUE);
			boolean deletable = Utils.getBooleanAttribute(anElement, "deletable", Boolean.TRUE);
			boolean editable = Utils.getBooleanAttribute(anElement, "editable", Boolean.TRUE);
			boolean headerVisible = Utils.getBooleanAttribute(anElement, "headerVisible", Boolean.TRUE);
			boolean draggableRows = Utils.getBooleanAttribute(anElement, "draggableRows", Boolean.FALSE);
			boolean showHorizontalLines = Utils.getBooleanAttribute(anElement, "showHorizontalLines", Boolean.TRUE);
			boolean showVerticalLines = Utils.getBooleanAttribute(anElement, "showVerticalLines", Boolean.TRUE);
			boolean showOddRowsInOtherColor = Utils.getBooleanAttribute(anElement, "showOddRowsInOtherColor", Boolean.TRUE);
			int rowsHeight = Utils.getIntegerAttribute(anElement, "rowsHeight", 20);
			grid.setHeaderVisible(headerVisible);
			grid.setDraggableRows(draggableRows);
			grid.setRowsHeight(rowsHeight);
			grid.setShowOddRowsInOtherColor(showOddRowsInOtherColor);
			grid.setShowVerticalLines(showVerticalLines);
			grid.setShowHorizontalLines(showHorizontalLines);
			grid.setEditable(editable);
			grid.setDeletable(deletable);
			grid.setInsertable(insertable);
			grid.setFrozenColumns(frozenColumns);
			grid.setFrozenRows(frozenRows);
			if (anElement.hasAttribute("oddRowsColor")) {
				String oddRowsColorDesc = anElement.getAttribute("oddRowsColor");
				grid.setOddRowsColor(PublishedColor.parse(oddRowsColorDesc));
			}
			if (anElement.hasAttribute("gridColor")) {
				String gridColorDesc = anElement.getAttribute("gridColor");
				grid.setGridColor(PublishedColor.parse(gridColorDesc));
			}
			if (anElement.hasAttribute("parentField")) {
				String parentFieldPath = anElement.getAttribute("parentField");
				grid.setParentField(parentFieldPath);
			}
			if (anElement.hasAttribute("childrenField")) {
				String childrenFieldPath = anElement.getAttribute("childrenField");
				grid.setChildrenField(childrenFieldPath);
			}
			List<HeaderNode<JavaScriptObject>> roots = readColumns(anElement, aFactory);
			grid.setHeader(roots);
			if (anElement.hasAttribute("data")) {
				String entityName = anElement.getAttribute("data");
				try {
					grid.setData(aFactory.resolveEntity(entityName));
				} catch (Exception ex) {
					Logger.getLogger(GridFactory.class.getName()).log(Level.SEVERE,
					        "While setting data to named model's property " + entityName + " to widget " + grid.getJsName() + " exception occured: " + ex.getMessage());
				}
			}
			if (anElement.hasAttribute("field")) {
				String dataPropertyPath = anElement.getAttribute("field");
				grid.setField(dataPropertyPath);
			}
			return grid;
		}
		default:
			return null;
		}
	}
	
	private static List<HeaderNode<JavaScriptObject>> readColumns(Element aColumnsElement, UiReader aFactory) throws Exception {
		List<HeaderNode<JavaScriptObject>> nodes = new ArrayList<>();
		Node childNode = aColumnsElement.getFirstChild();
		while (childNode != null) {
			if (childNode instanceof Element) {
				Element childTag = (Element) childNode;
				String columnType = childTag.getTagName();
				switch (columnType) {
				case "CheckGridColumn": {
					CheckHeaderNode column = new CheckHeaderNode();
					GridPublisher.publish(column);
					readColumnNode(column, childTag, aFactory);
					nodes.add(column);
					List<HeaderNode<JavaScriptObject>> children = readColumns(childTag, aFactory);
					for (int i = 0; i < children.size(); i++) {
						column.addColumnNode(children.get(i));
					}
					break;
				}
				case "RadioGridColumn": {
					RadioHeaderNode column = new RadioHeaderNode();
					GridPublisher.publish(column);
					readColumnNode(column, childTag, aFactory);
					nodes.add(column);
					List<HeaderNode<JavaScriptObject>> children = readColumns(childTag, aFactory);
					for (int i = 0; i < children.size(); i++) {
						column.addColumnNode(children.get(i));
					}
					break;
				}
				case "ServiceGridColumn": {
					ServiceHeaderNode column = new ServiceHeaderNode();
					GridPublisher.publish(column);
					readColumnNode(column, childTag, aFactory);
					nodes.add(column);
					List<HeaderNode<JavaScriptObject>> children = readColumns(childTag, aFactory);
					for (int i = 0; i < children.size(); i++) {
						column.addColumnNode(children.get(i));
					}
					break;
				}
				case "ModelGridColumn": {
					ModelHeaderNode column = new ModelHeaderNode();
					GridPublisher.publish(column);
					readColumnNode(column, childTag, aFactory);
					if (childTag.hasAttribute("field")) {
						column.setField(childTag.getAttribute("field"));
					}
					if (childTag.hasAttribute("sortField")) {
						column.setSortField(childTag.getAttribute("sortField"));
					}
					Node _childNode = childTag.getFirstChild();
					while (_childNode != null) {
						if (_childNode instanceof Element) {
							Element _childTag = (Element) _childNode;
							UIObject editorComp = aFactory.readWidget(_childTag);
							if (editorComp instanceof ModelDecoratorBox<?>) {
								ModelColumn col = (ModelColumn) column.getColumn();
								col.setEditor((ModelDecoratorBox<Object>) editorComp);
								// ModelWidget viewComp = (ModelWidget)
								// readWidget((Element) _childNode);
								// col.setView(viewComp);
								break;
							}
						}
						_childNode = _childNode.getNextSibling();
					}
					nodes.add(column);
					List<HeaderNode<JavaScriptObject>> children = readColumns(childTag, aFactory);
					for (int i = 0; i < children.size(); i++) {
						column.addColumnNode(children.get(i));
					}
					break;
				}
				}
			}
			childNode = childNode.getNextSibling();
		}
		return nodes;
	}

	private static void readColumnNode(ModelHeaderNode aNode, Element anElement, UiReader aFactory) throws Exception {
		aNode.setJsName(anElement.getAttribute("name"));
		if (anElement.hasAttribute("title")) {
			aNode.setTitle(anElement.getAttribute("title"));
		}
		if (anElement.hasAttribute("background")) {
			PublishedColor background = PublishedColor.parse(anElement.getAttribute("background"));
			aNode.setBackground(background);
		}
		if (anElement.hasAttribute("foreground")) {
			PublishedColor foreground = PublishedColor.parse(anElement.getAttribute("foreground"));
			aNode.setForeground(foreground);
		}
		aNode.setReadonly(Utils.getBooleanAttribute(anElement, "readonly", Boolean.FALSE));
		// aNode.setEnabled(Utils.getBooleanAttribute(anElement, "enabled",
		// Boolean.TRUE));
		PublishedFont font = aFactory.readFont(anElement);
		if (font != null) {
			aNode.setFont(font);
		}
		if (anElement.hasAttribute("minWidth")) {
			String minWidth = anElement.getAttribute("minWidth");
			if (minWidth.length() > 2 && minWidth.endsWith("px")) {
				aNode.setMinWidth(Integer.parseInt(minWidth.substring(0, minWidth.length() - 2)));
			}
		}
		if (anElement.hasAttribute("maxWidth")) {
			String maxWidth = anElement.getAttribute("maxWidth");
			if (maxWidth.length() > 2 && maxWidth.endsWith("px")) {
				aNode.setMaxWidth(Integer.parseInt(maxWidth.substring(0, maxWidth.length() - 2)));
			}
		}
		if (anElement.hasAttribute("preferredWidth")) {
			String preferredWidth = anElement.getAttribute("preferredWidth");
			if (preferredWidth.length() > 2 && preferredWidth.endsWith("px")) {
				aNode.setPreferredWidth(Integer.parseInt(preferredWidth.substring(0, preferredWidth.length() - 2)));
			}
		}
		aNode.setMoveable(Utils.getBooleanAttribute(anElement, "movable", Boolean.TRUE));
		aNode.setResizable(Utils.getBooleanAttribute(anElement, "resizable", aNode instanceof CheckHeaderNode || aNode instanceof RadioHeaderNode || aNode instanceof ServiceHeaderNode ? Boolean.FALSE
		        : Boolean.TRUE));
		// aNode.setSelectOnly(Utils.getBooleanAttribute(anElement,
		// "selectOnly", Boolean.FALSE));
		aNode.setSortable(Utils.getBooleanAttribute(anElement, "sortable", Boolean.TRUE));
		aNode.setVisible(Utils.getBooleanAttribute(anElement, "visible", Boolean.TRUE));
	}
}
