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

public class HeaderNode implements HasJsName {

    protected String name;
    protected Column column; // assigned only if this header node is a leaf
    protected HeaderView header; // visual part of this header node
    protected HeaderNode parent;
    protected List<HeaderNode> children = new ArrayList<>();

    protected int leavesCount;
    protected int depthRemainder;

    public HeaderNode() {
        super();
        column = new Column();
        ((Column) column).setEditor(new FormattedDecoratorField());
        header = new HeaderView("", null, this);
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

    public HeaderNode[] getColumnNodes(){
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
        return ((HeaderView) header).getBackground();
    }

    public void setBackground(PublishedColor aValue) {
        ((HeaderView) header).setBackground(aValue);
    }

    public PublishedColor getForeground() {
        return ((HeaderView) header).getForeground();
    }

    public void setForeground(PublishedColor aValue) {
        ((HeaderView) header).setForeground(aValue);
    }

    public PublishedFont getFont() {
        return ((HeaderView) header).getFont();
    }

    public void setFont(PublishedFont aValue) {
        ((HeaderView) header).setFont(aValue);
    }

    public double getMinWidth() {
        return ((Column) column).getMinWidth();
    }

    public void setMinWidth(double aValue) {
        ((Column) column).setMinWidth(aValue);
    }

    public double getMaxWidth() {
        return ((Column) column).getMaxWidth();
    }

    public void setMaxWidth(double aValue) {
        ((Column) column).setMaxWidth(aValue);
    }

    public double getPreferredWidth() {
        return ((Column) column).getDesignedWidth();
    }

    public void setPreferredWidth(double aValue) {
        ((Column) column).setWidth(aValue);
    }

    public String getField() {
        return ((Column) column).getField();
    }

    public void setField(String aValue) {
        ((Column) column).setField(aValue);
    }

    public String getTitle() {
        return ((HeaderView) header).getTitle();
    }

    public void setTitle(String aValue) {
        ((HeaderView) header).setTitle(aValue);
    }

    public boolean isResizable() {
        return ((HeaderView) header).isResizable();
    }

    public void setResizable(boolean aValue) {
        ((HeaderView) header).setResizable(aValue);
    }

    public boolean isMoveable() {
        return ((HeaderView) header).isMoveable();
    }

    public void setMoveable(boolean aValue) {
        ((HeaderView) header).setMoveable(aValue);
    }

    public boolean isVisible() {
        return ((Column) column).isVisible();
    }

    public void setVisible(boolean aValue) {
        ((Column) column).setVisible(aValue);
    }

    public double getWidth() {
        return ((Column) column).getWidth();
    }

    public void setWidth(double aValue) {
        ((Column) column).setWidth(aValue);
    }

    public boolean isReadonly() {
        return ((Column) column).isReadonly();
    }

    public void setReadonly(boolean aValue) {
        ((Column) column).setReadonly(aValue);
    }

    public boolean isSortable() {
        return ((Column) column).isSortable();
    }

    public String getSortField() {
        return ((Column) column).getSortField();
    }

    public void setSortField(String aValue) {
        ((Column) column).setSortField(aValue);
    }

    public void setSortable(boolean aValue) {
        ((Column) column).setSortable(aValue);
    }

    public JavaScriptObject getOnRender() {
        return ((Column) column).getOnRender();
    }

    public void setOnRender(JavaScriptObject aValue) {
        ((Column) column).setOnRender(aValue);
    }

    public JavaScriptObject getOnSelect() {
        return ((Column) column).getOnSelect();
    }

    public void setOnSelect(JavaScriptObject aValue) {
        ((Column) column).setOnSelect(aValue);
    }

    public void sort() {
        ((Column) column).sort();
    }

    public void sortDesc() {
        ((Column) column).sortDesc();
    }

    public void unsort() {
        ((Column) column).unsort();
    }
    
    public Widget getEditor(){
        return column != null ? column.getEditor() : null;
    }
    
    public void setEditor(Widget aWidget){
        if(column != null)
            column.setEditor(aWidget);
    }
}
