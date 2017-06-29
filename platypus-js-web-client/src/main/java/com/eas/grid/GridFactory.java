package com.eas.grid;

import java.util.ArrayList;
import java.util.List;

import com.eas.core.Logger;
import com.eas.core.Utils;
import com.eas.grid.columns.header.CheckHeaderNode;
import com.eas.grid.columns.header.HeaderNode;
import com.eas.grid.columns.header.RadioHeaderNode;
import com.eas.grid.columns.header.ServiceHeaderNode;
import com.eas.ui.PublishedColor;
import com.eas.ui.PublishedFont;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.eas.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;

public class GridFactory implements UiWidgetReader {

    @Override
    public Grid readWidget(Element anElement, final UiReader aFactory) throws Exception {
        String type = anElement.getTagName();
        switch (type) {
            case "mg":
            case "ModelGrid": {
                Grid grid = new Grid();
                GridPublisher.publish(grid);
                aFactory.readGeneralProps(anElement, grid);
                int frozenColumns = Utils.getIntegerAttribute(anElement, "frc", "frozenColumns", 0);
                int frozenRows = Utils.getIntegerAttribute(anElement, "frr", "frozenRows", 0);
                boolean insertable = Utils.getBooleanAttribute(anElement, "ie", "insertable", Boolean.TRUE);
                boolean deletable = Utils.getBooleanAttribute(anElement, "de", "deletable", Boolean.TRUE);
                boolean editable = Utils.getBooleanAttribute(anElement, "e", "editable", Boolean.TRUE);
                boolean headerVisible = Utils.getBooleanAttribute(anElement, "hv", "headerVisible", Boolean.TRUE);
                boolean draggableRows = Utils.getBooleanAttribute(anElement, "dr", "draggableRows", Boolean.FALSE);
                boolean showHorizontalLines = Utils.getBooleanAttribute(anElement, "shl", "showHorizontalLines", Boolean.TRUE);
                boolean showVerticalLines = Utils.getBooleanAttribute(anElement, "svl", "showVerticalLines", Boolean.TRUE);
                boolean showOddRowsInOtherColor = Utils.getBooleanAttribute(anElement, "soc", "showOddRowsInOtherColor", Boolean.TRUE);
                int rowsHeight = Utils.getIntegerAttribute(anElement, "rh", "rowsHeight", 30);
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
                if (Utils.hasAttribute(anElement, "orc", "oddRowsColor")) {
                    String oddRowsColorDesc = Utils.getAttribute(anElement, "orc", "oddRowsColor", null);
                    grid.setOddRowsColor(PublishedColor.parse(oddRowsColorDesc));
                }
                if (Utils.hasAttribute(anElement, "gc", "gridColor")) {
                    String gridColorDesc = Utils.getAttribute(anElement, "gc", "gridColor", null);
                    grid.setGridColor(PublishedColor.parse(gridColorDesc));
                }
                if (Utils.hasAttribute(anElement, "pf", "parentField")) {
                    String parentFieldPath = Utils.getAttribute(anElement, "pf", "parentField", null);
                    grid.setParentField(parentFieldPath);
                }
                if (Utils.hasAttribute(anElement, "cf", "childrenField")) {
                    String childrenFieldPath = Utils.getAttribute(anElement, "cf", "childrenField", null);
                    grid.setChildrenField(childrenFieldPath);
                }
                List<HeaderNode> roots = readColumns(anElement, aFactory);
                grid.setHeader(roots);
                if (Utils.hasAttribute(anElement, "d", "data")) {
                    String entityName = Utils.getAttribute(anElement, "d", "data", null);
                    try {
                        grid.setData(aFactory.resolveEntity(entityName));
                    } catch (Exception ex) {
                        Logger.severe("While setting data to named model's property " + entityName + " to widget " + grid.getName() + " exception occured: " + ex.getMessage());
                    }
                }
                if (Utils.hasAttribute(anElement, "f", "field")) {
                    String dataPropertyPath = Utils.getAttribute(anElement, "f", "field", null);
                    grid.setField(dataPropertyPath);
                }
                return grid;
            }
            default:
                return null;
        }
    }

    private static List<HeaderNode> readColumns(Element aColumnsElement, UiReader aFactory) throws Exception {
        List<HeaderNode> nodes = new ArrayList<>();
        Node childNode = aColumnsElement.getFirstChild();
        while (childNode != null) {
            if (childNode instanceof Element) {
                Element childTag = (Element) childNode;
                String columnType = childTag.getTagName();
                switch (columnType) {
                    case "cgc":
                    case "CheckGridColumn": {
                        CheckHeaderNode column = new CheckHeaderNode();
                        GridPublisher.publish(column);
                        readColumnNode(column, childTag, aFactory);
                        nodes.add(column);
                        List<HeaderNode> children = readColumns(childTag, aFactory);
                        for (int i = 0; i < children.size(); i++) {
                            column.addColumnNode(children.get(i));
                        }
                        break;
                    }
                    case "rgc":
                    case "RadioGridColumn": {
                        RadioHeaderNode column = new RadioHeaderNode();
                        GridPublisher.publish(column);
                        readColumnNode(column, childTag, aFactory);
                        nodes.add(column);
                        List<HeaderNode> children = readColumns(childTag, aFactory);
                        for (int i = 0; i < children.size(); i++) {
                            column.addColumnNode(children.get(i));
                        }
                        break;
                    }
                    case "sgc":
                    case "ServiceGridColumn": {
                        ServiceHeaderNode column = new ServiceHeaderNode();
                        GridPublisher.publish(column);
                        readColumnNode(column, childTag, aFactory);
                        nodes.add(column);
                        List<HeaderNode> children = readColumns(childTag, aFactory);
                        for (int i = 0; i < children.size(); i++) {
                            column.addColumnNode(children.get(i));
                        }
                        break;
                    }
                    case "mgc":
                    case "ModelGridColumn": {
                        HeaderNode column = new HeaderNode();
                        GridPublisher.publish(column);
                        readColumnNode(column, childTag, aFactory);
                        if (Utils.hasAttribute(childTag, "f", "field")) {
                            column.setField(Utils.getAttribute(childTag, "f", "field", null));
                        }
                        if (Utils.hasAttribute(childTag, "sf", "sortField")) {
                            column.setSortField(Utils.getAttribute(childTag, "sf", "sortField", null));
                        }
                        Node _childNode = childTag.getFirstChild();
                        while (_childNode != null) {
                            if (_childNode instanceof Element) {
                                Element _childTag = (Element) _childNode;
                                Widget editorComp = aFactory.readWidget(_childTag);
                                column.setEditor(editorComp);
                                break;
                            }
                            _childNode = _childNode.getNextSibling();
                        }
                        nodes.add(column);
                        List<HeaderNode> children = readColumns(childTag, aFactory);
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

    private static void readColumnNode(HeaderNode aNode, Element anElement, UiReader aFactory) throws Exception {
        aNode.setName(Utils.getAttribute(anElement, "n", "name", null));
        if (Utils.hasAttribute(anElement, "tl", "title")) {
            aNode.setTitle(Utils.getAttribute(anElement, "tl", "title", null));
        }
        if (Utils.hasAttribute(anElement, "bg", "background")) {
            PublishedColor background = PublishedColor.parse(Utils.getAttribute(anElement, "bg", "background", null));
            aNode.setBackground(background);
        }
        if (Utils.hasAttribute(anElement, "fg", "foreground")) {
            PublishedColor foreground = PublishedColor.parse(Utils.getAttribute(anElement, "fg", "foreground", null));
            aNode.setForeground(foreground);
        }
        aNode.setReadonly(Utils.getBooleanAttribute(anElement, "ro", "readonly", Boolean.FALSE));
        // aNode.setEnabled(Utils.getBooleanAttribute(anElement, "en", "enabled",
        // Boolean.TRUE));
        PublishedFont font = aFactory.readFont(anElement);
        if (font != null) {
            aNode.setFont(font);
        }
        if (Utils.hasAttribute(anElement, "mw", "minWidth")) {
            String minWidth = Utils.getAttribute(anElement, "mw", "minWidth", null);
            if (minWidth.length() > 2 && minWidth.endsWith("px")) {
                aNode.setMinWidth(Integer.parseInt(minWidth.substring(0, minWidth.length() - 2)));
            }
        }
        if (Utils.hasAttribute(anElement, "mxw", "maxWidth")) {
            String maxWidth = Utils.getAttribute(anElement, "mxw", "maxWidth", null);
            if (maxWidth.length() > 2 && maxWidth.endsWith("px")) {
                aNode.setMaxWidth(Integer.parseInt(maxWidth.substring(0, maxWidth.length() - 2)));
            }
        }
        if (Utils.hasAttribute(anElement, "prw", "preferredWidth")) {
            String preferredWidth = Utils.getAttribute(anElement, "prw", "preferredWidth", null);
            if (preferredWidth.length() > 2 && preferredWidth.endsWith("px")) {
                aNode.setPreferredWidth(Integer.parseInt(preferredWidth.substring(0, preferredWidth.length() - 2)));
            }
        }
        aNode.setMoveable(Utils.getBooleanAttribute(anElement, "m", "movable", Boolean.TRUE));
        aNode.setResizable(Utils.getBooleanAttribute(anElement, "rs", "resizable", aNode instanceof CheckHeaderNode || aNode instanceof RadioHeaderNode || aNode instanceof ServiceHeaderNode ? Boolean.FALSE
                : Boolean.TRUE));
        // aNode.setSelectOnly(Utils.getBooleanAttribute(anElement, "so",
        // "selectOnly", Boolean.FALSE));
        aNode.setSortable(Utils.getBooleanAttribute(anElement, "s", "sortable", Boolean.TRUE));
        aNode.setVisible(Utils.getBooleanAttribute(anElement, "v", "visible", Boolean.TRUE));
    }
}
