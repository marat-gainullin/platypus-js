package com.eas.grid.columns.header;

import com.eas.grid.HeaderView;
import java.util.ArrayList;
import java.util.List;

import com.eas.grid.columns.Column;
import com.eas.ui.HasJsName;
import com.eas.ui.PublishedColor;
import com.eas.ui.PublishedFont;
import com.eas.ui.Widget;
import com.eas.widgets.boxes.FormattedDecoratorField;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * If this header node is not a leaf, its column is reserved for further
 * structure changes, wich may make the node a leaf.
 *
 * @author mgainullin
 */
public class HeaderNode implements HasJsName {

    protected String name;
    protected Column column;
    protected HeaderView header;
    protected HeaderNode parent;
    protected List<HeaderNode> children = new ArrayList<>();

    private int leavesCount;
    private int depthRemainder;

    public HeaderNode() {
        super();
        column = new Column();
        column.setEditor(new FormattedDecoratorField());
        header = new HeaderView("", this);
    }

    public HeaderNode lightCopy() {
        HeaderNode copied = new HeaderNode();
        copied.setColumn(column);
        copied.setHeader(header);
        return copied;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column aValue) {
        column = aValue;
    }

    public HeaderNode getParent() {
        return parent;
    }

    public void setParent(HeaderNode aParent) {
        parent = aParent;
    }

    public List<HeaderNode> getChildren() {
        return children;
    }

    public HeaderView getHeader() {
        return header;
    }

    public void setHeader(HeaderView aHeader) {
        header = aHeader;
    }

    public boolean removeColumnNode(HeaderNode aNode) {
        if (children != null) {
            return children.remove(aNode);
        } else {
            return false;
        }
    }

    public void addColumnNode(HeaderNode aNode) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(aNode)) {
            children.add(aNode);
            aNode.setParent(this);
        }
    }

    public void insertColumnNode(int atIndex, HeaderNode aNode) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(aNode) && atIndex >= 0 && atIndex <= children.size()) {
            children.add(atIndex, aNode);
            aNode.setParent(this);
        }
    }

    public HeaderNode[] getColumnNodes() {
        return children.toArray(new HeaderNode[]{});
    }

    public int getDepthRemainder() {
        return depthRemainder;
    }

    public int getLeavesCount() {
        return leavesCount;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public String getJsName() {
        return name;
    }

    @Override
    public void setJsName(String aValue) {
        name = aValue;
    }

    public PublishedColor getBackground() {
        return header.getBackground();
    }

    public void setBackground(PublishedColor aValue) {
        header.setBackground(aValue);
    }

    public PublishedColor getForeground() {
        return header.getForeground();
    }

    public void setForeground(PublishedColor aValue) {
        header.setForeground(aValue);
    }

    public PublishedFont getFont() {
        return header.getFont();
    }

    public void setFont(PublishedFont aValue) {
        header.setFont(aValue);
    }

    public double getMinWidth() {
        return column.getMinWidth();
    }

    public void setMinWidth(double aValue) {
        column.setMinWidth(aValue);
    }

    public double getMaxWidth() {
        return column.getMaxWidth();
    }

    public void setMaxWidth(double aValue) {
        column.setMaxWidth(aValue);
    }

    public double getPreferredWidth() {
        return column.getDesignedWidth();
    }

    public void setPreferredWidth(double aValue) {
        column.setWidth(aValue);
    }

    public String getField() {
        return column.getField();
    }

    public void setField(String aValue) {
        column.setField(aValue);
    }

    public String getTitle() {
        return header.getText();
    }

    public void setTitle(String aValue) {
        header.setText(aValue);
    }

    public boolean isResizable() {
        return header.isResizable();
    }

    public void setResizable(boolean aValue) {
        header.setResizable(aValue);
    }

    public boolean isMoveable() {
        return header.isMoveable();
    }

    public void setMoveable(boolean aValue) {
        header.setMoveable(aValue);
    }

    public boolean isVisible() {
        return column.isVisible();
    }

    public void setVisible(boolean aValue) {
        column.setVisible(aValue);
    }

    public double getWidth() {
        return column.getWidth();
    }

    public void setWidth(double aValue) {
        column.setWidth(aValue);
    }

    public boolean isReadonly() {
        return column.isReadonly();
    }

    public void setReadonly(boolean aValue) {
        column.setReadonly(aValue);
    }

    public boolean isSortable() {
        return column.isSortable();
    }

    public String getSortField() {
        return column.getSortField();
    }

    public void setSortField(String aValue) {
        column.setSortField(aValue);
    }

    public void setSortable(boolean aValue) {
        column.setSortable(aValue);
    }

    public JavaScriptObject getOnRender() {
        return column.getOnRender();
    }

    public void setOnRender(JavaScriptObject aValue) {
        column.setOnRender(aValue);
    }

    public JavaScriptObject getOnSelect() {
        return column.getOnSelect();
    }

    public void setOnSelect(JavaScriptObject aValue) {
        column.setOnSelect(aValue);
    }

    public void sort() {
        column.sort();
    }

    public void sortDesc() {
        column.sortDesc();
    }

    public void unsort() {
        column.unsort();
    }

    public Widget getEditor() {
        return column != null ? column.getEditor() : null;
    }

    public void setEditor(Widget aWidget) {
        if (column != null) {
            column.setEditor(aWidget);
        }
    }

    public void setDepthRemainder(int aValue) {
        depthRemainder = aValue;
        header.getElement().setAttribute("rowspan", (aValue + 1) + "");
    }

    public void setLeavesCount(int aValue) {
        leavesCount = aValue;
        header.getElement().setAttribute("colspan", aValue + "");
    }
}
