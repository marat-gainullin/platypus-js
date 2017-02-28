/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableColumn;

/**
 *
 * @author mg
 */
public class GridColumnsNode implements ColumnNodesContainer {

    protected Color background;
    protected Color foreground;
    protected Font font;
    protected GridColumnsNode parent;
    protected int minWidth;
    protected int maxWidth = Integer.MAX_VALUE / 2;
    protected boolean movable = true;
    protected boolean resizable = true;
    protected boolean sortable = true;
    protected boolean readonly;
    protected boolean enabled = true;
    protected boolean visible = true;
    protected String title;
    protected boolean selectOnly;
    protected List<GridColumnsNode> children = new ArrayList<>();
    // events sources
    protected TableColumn tableColumn;
    protected GridColumnsNode styleSource;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    // because of nodes cloning
    protected PropertyChangeListener styleListener = (PropertyChangeEvent evt) -> {
        switch (evt.getPropertyName()) {
            case "font":
                font = styleSource.getFont();
                changeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                break;
            case "background":
                background = styleSource.getBackground();
                changeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                break;
            case "foreground":
                foreground = styleSource.getForeground();
                changeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                break;
            case "title":
                title = styleSource.getTitle();
                changeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                break;
        }
    };
    protected PropertyChangeListener childrenListener = (PropertyChangeEvent evt) -> {
        changeSupport.firePropertyChange(evt.getPropertyName(), null, children);
    };

    public GridColumnsNode() {
        super();
    }

    public GridColumnsNode(String aTitle) {
        this();
        title = aTitle;
    }

    /**
     * Constructor of grid columns group with parent specified.
     *
     * @param aParent Parent group, the new group is to be added to;
     */
    public GridColumnsNode(GridColumnsNode aParent) {
        this();
        setParent(aParent);
    }

    /**
     * Constructor of grid columns group based on table column information. It's
     * allowed only if this column group is a leaf group.
     *
     * @param aCol Table column instance to get information from.
     * @see TableColumn
     */
    public GridColumnsNode(TableColumn aCol) {
        this();
        setTableColumn(aCol);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public final void setParent(GridColumnsNode aParent) {
        parent = aParent;
    }

    public GridColumnsNode getStyleSource() {
        return styleSource;
    }

    public void setStyleSource(GridColumnsNode aValue) {
        if (styleSource != aValue) {
            if (styleSource != null) {
                styleSource.getChangeSupport().removePropertyChangeListener(styleListener);
            }
            styleSource = aValue;
            if (styleSource != null) {
                styleSource.getChangeSupport().addPropertyChangeListener(styleListener);
            }
        }
    }

    public boolean containsInChildren(GridColumnsNode aChild) {
        return children != null && children.indexOf(aChild) != -1;
    }

    public GridColumnsNode getParent() {
        return parent;
    }

    public TableColumn getTableColumn() {
        return tableColumn;
    }

    protected PropertyChangeListener columnListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("headerValue".equals(evt.getPropertyName())
                    && (evt.getNewValue() == null || evt.getNewValue() instanceof String)) {
                title = (String) evt.getNewValue();
            } else if ("width".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Integer) {
                changeSupport.firePropertyChange("width", evt.getOldValue(), evt.getNewValue());
            } else if ("preferredWidth".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Integer) {
                changeSupport.firePropertyChange("preferredWidth", evt.getOldValue(), evt.getNewValue());
            } else if ("minWidth".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Integer) {
                minWidth = (Integer) evt.getNewValue();
                changeSupport.firePropertyChange("minWidth", evt.getOldValue(), evt.getNewValue());
            } else if ("maxWidth".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Integer) {
                maxWidth = (Integer) evt.getNewValue();
                changeSupport.firePropertyChange("maxWidth", evt.getOldValue(), evt.getNewValue());
            } else if ("movable".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Boolean) {
                movable = (Boolean) evt.getNewValue();
            } else if ("resizable".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Boolean) {
                resizable = (Boolean) evt.getNewValue();
            } else if ("sortable".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Boolean) {
                sortable = (Boolean) evt.getNewValue();
            }
            changeSupport.firePropertyChange("headerValue".equals(evt.getPropertyName()) ? "title" : evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    };

    public final void setTableColumn(TableColumn aColumn) {
        if (tableColumn != null) {
            tableColumn.removePropertyChangeListener(columnListener);
        }
        if (aColumn == null && tableColumn != null) {
            minWidth = tableColumn.getMinWidth();
            maxWidth = tableColumn.getMaxWidth();
            if (tableColumn.getHeaderValue() instanceof String) {
                title = (String) tableColumn.getHeaderValue();
            }
        }
        tableColumn = aColumn;
        if (tableColumn != null) {
            tableColumn.addPropertyChangeListener(columnListener);
        }
    }

    public void lightAssign(GridColumnsNode aSource) {
        if (aSource != null) {
            enabled = aSource.isEnabled();
            minWidth = aSource.getMinWidth();
            maxWidth = aSource.getMaxWidth();
            readonly = aSource.isReadonly();
            visible = aSource.isVisible();
            selectOnly = aSource.isSelectOnly();
            movable = aSource.isMovable();
            resizable = aSource.isResizable();
            sortable = aSource.isSortable();
            title = aSource.getTitle();
            background = aSource.getBackground();
            foreground = aSource.getForeground();
            font = aSource.getFont();
        } else {
            readonly = false;
            enabled = true;
            visible = true;
            title = null;
            selectOnly = false;
            movable = true;
            resizable = true;
            sortable = true;
            background = null;
            foreground = null;
            font = null;
        }
    }

    public boolean lightEquals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GridColumnsNode other = (GridColumnsNode) obj;

        if (this.minWidth != other.minWidth) {
            return false;
        }
        if (this.maxWidth != other.maxWidth) {
            return false;
        }
        if (this.readonly != other.readonly) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.visible != other.visible) {
            return false;
        }
        if (this.selectOnly != other.selectOnly) {
            return false;
        }
        if (this.movable != other.movable) {
            return false;
        }
        if (this.resizable != other.resizable) {
            return false;
        }
        if (this.sortable != other.sortable) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.background == null) ? (other.background != null) : !this.background.equals(other.background)) {
            return false;
        }
        if ((this.foreground == null) ? (other.foreground != null) : !this.foreground.equals(other.foreground)) {
            return false;
        }
        if ((this.font == null) ? (other.font != null) : !this.font.equals(other.font)) {
            return false;
        }
        return true;
    }

    public boolean isEqual(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        if (!lightEquals(obj)) {
            return false;
        }
        final GridColumnsNode other = (GridColumnsNode) obj;
        if (this.children != other.children && (this.children == null || !isColumnsEquals(this.children, other.children))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.minWidth;
        hash = 37 * hash + this.maxWidth;
        hash = 37 * hash + (this.readonly ? 1 : 0);
        hash = 37 * hash + (this.enabled ? 1 : 0);
        hash = 37 * hash + (this.visible ? 1 : 0);
        hash = 37 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 37 * hash + (this.background != null ? this.background.hashCode() : 0);
        hash = 37 * hash + (this.foreground != null ? this.foreground.hashCode() : 0);
        hash = 37 * hash + (this.font != null ? this.font.hashCode() : 0);
        hash = 37 * hash + (this.selectOnly ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    public static boolean isColumnsEquals(List<GridColumnsNode> first, List<GridColumnsNode> second) {
        if (first != second) {
            if (first != null && second != null) {
                if (first.size() == second.size()) {
                    for (int i = 0; i < first.size(); i++) {
                        GridColumnsNode fColumn = first.get(i);
                        GridColumnsNode sColumn = second.get(i);
                        if (fColumn != sColumn) {
                            if (fColumn != null && sColumn != null) {
                                if (!fColumn.equals(sColumn)) {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public void assign(GridColumnsNode aSource) throws Exception {
        lightAssign(aSource);
        children = new ArrayList<>();
        if (aSource != null && aSource.getChildren() != null) {
            List<GridColumnsNode> lchildren = aSource.getChildren();
            for (int i = 0; i < lchildren.size(); i++) {
                if (lchildren.get(i) != null) {
                    GridColumnsNode newCol = lchildren.get(i).copy();
                    children.add(newCol);
                    newCol.setParent(this);
                } else {
                    children.add(null);
                }
            }
            assert children.size() == lchildren.size();
        }
    }

    public GridColumnsNode lightCopy() {
        GridColumnsNode copied = new GridColumnsNode();
        copied.lightAssign(this);
        return copied;
    }

    public GridColumnsNode copy() throws Exception {
        GridColumnsNode copied = new GridColumnsNode();
        copied.assign(this);
        return copied;
    }

    public int getWidth() {
        if (tableColumn != null) {
            return tableColumn.getWidth();
        } else {
            int pWidth = 0;
            for (int i = 0; i < children.size(); i++) {
                pWidth += children.get(i).getWidth();
            }
            return pWidth;
        }
    }

    public void setWidth(int aValue) {
        if (tableColumn != null) {
            tableColumn.setWidth(aValue);
        }
    }

    public int getPreferredWidth() {
        if (tableColumn != null) {
            return tableColumn.getPreferredWidth();
        } else {
            return 0;
        }
    }

    public void setPreferredWidth(int aValue) {
        if (tableColumn != null) {
            tableColumn.setPreferredWidth(aValue);
        }
    }

    public int getMinWidth() {
        if (tableColumn != null) {
            return tableColumn.getMinWidth();
        } else {
            return minWidth;
        }
    }

    public void setMinWidth(int aValue) {
        if (tableColumn != null) {
            tableColumn.setMinWidth(aValue);
        } else {
            minWidth = aValue;
        }
    }

    public int getMaxWidth() {
        if (tableColumn != null) {
            return tableColumn.getMaxWidth();
        } else {
            return maxWidth;
        }
    }

    public void setMaxWidth(int aValue) {
        if (tableColumn != null) {
            tableColumn.setMaxWidth(aValue);
        } else {
            maxWidth = aValue;
        }
    }

    public List<GridColumnsNode> getChildren() {
        return children;
    }

    public void setChildren(List<GridColumnsNode> aChildren) {
        children = aChildren;
        if (children != null) {
            children.stream().forEach((lcol) -> {
                lcol.setParent(this);
            });
        } else {
            children = new ArrayList<>();
        }
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public boolean isSelectOnly() {
        return selectOnly;
    }

    public void setSelectOnly(boolean aValue) {
        selectOnly = aValue;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean aValue) {
        readonly = aValue;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean aValue) {
        enabled = aValue;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean aValue) {
        visible = aValue;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean aValue) {
        movable = aValue;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean aValue) {
        resizable = aValue;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean aValue) {
        sortable = aValue;
    }

    public String getTitle() {
        if (tableColumn != null) {
            if (tableColumn.getHeaderValue() instanceof String) {
                return (String) tableColumn.getHeaderValue();
            } else {
                return title;
            }
        } else {
            return title;
        }
    }

    public void setTitle(String aTitle) {
        if (tableColumn != null) {
            tableColumn.setHeaderValue(aTitle);
        } else {
            if (title == null ? aTitle != null : !title.equals(aTitle)) {
                String oldTtile = title;
                title = aTitle;
                changeSupport.firePropertyChange("title", oldTtile, title);
            }
        }
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color aValue) {
        Color oldValue = background;
        background = aValue;
        changeSupport.firePropertyChange("background", oldValue, aValue);
    }

    @Override
    public void removeColumnNode(GridColumnsNode aNode) {
        if (children != null) {
            children.remove(aNode);
            aNode.getChangeSupport().removePropertyChangeListener("children", childrenListener);
            changeSupport.firePropertyChange("children", null, children);
        }
    }

    @Override
    public void addColumnNode(GridColumnsNode aNode) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(aNode)) {
            children.add(aNode);
            aNode.setParent(this);
            aNode.getChangeSupport().addPropertyChangeListener("children", childrenListener);
            changeSupport.firePropertyChange("children", null, children);
        }
    }

    @Override
    public void insertColumnNode(int atIndex, GridColumnsNode aNode) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(aNode)
                && atIndex >= 0 && atIndex <= children.size()) {
            children.add(atIndex, aNode);
            aNode.setParent(this);
            aNode.getChangeSupport().addPropertyChangeListener("children", childrenListener);
            changeSupport.firePropertyChange("children", null, children);
        }
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color aValue) {
        Color oldValue = foreground;
        foreground = aValue;
        changeSupport.firePropertyChange("foreground", oldValue, aValue);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font aValue) {
        Font oldValue = font;
        font = aValue;
        changeSupport.firePropertyChange("font", oldValue, aValue);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}
