/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import com.eas.script.ScriptFunction;
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
public class GridColumnsNode {

    protected Color background;
    protected Color foreground;
    protected Font font;
    protected GridColumnsNode parent;
    protected int minWidth = 0;
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
    protected TableColumn tableColumn;
    //
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

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
     * @see GridColumnsGroup
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
            } else if ("minWidth".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Integer) {
                minWidth = (Integer) evt.getNewValue();
            } else if ("maxWidth".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Integer) {
                maxWidth = (Integer) evt.getNewValue();
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

    public void assign(GridColumnsNode aSource) {
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
        GridColumnsNode colg = new GridColumnsNode();
        colg.lightAssign(this);
        return colg;
    }

    public GridColumnsNode copy() {
        GridColumnsNode colg = new GridColumnsNode();
        colg.assign(this);
        return colg;
    }

    @ScriptFunction
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

    @ScriptFunction
    public void setWidth(int aValue) {
        if (tableColumn != null) {
            tableColumn.setWidth(aValue);
        }
    }

    @ScriptFunction
    public int getPreferredWidth() {
        if (tableColumn != null) {
            return tableColumn.getPreferredWidth();
        } else {
            return 0;
        }
        /*
         if (children.isEmpty()) {
         if (tableColumn != null) {
         return tableColumn.getPreferredWidth();
         }else
         return 0;
         } else {
         int pWidth = 0;
         for (int i = 0; i < children.size(); i++) {
         pWidth += children.get(i).getPreferredWidth();
         }
         return pWidth;
         }
         */
    }

    @ScriptFunction
    public void setPreferredWidth(int aValue) {
        if (tableColumn != null) {
            tableColumn.setPreferredWidth(aValue);
        }
    }

    @ScriptFunction
    public int getMinWidth() {
        if (tableColumn != null) {
            return tableColumn.getMinWidth();
        } else {
            return minWidth;
        }
    }

    @ScriptFunction
    public void setMinWidth(int aValue) {
        if (tableColumn != null) {
            tableColumn.setMinWidth(aValue);
        } else {
            minWidth = aValue;
        }
    }

    @ScriptFunction
    public int getMaxWidth() {
        if (tableColumn != null) {
            return tableColumn.getMaxWidth();
        } else {
            return maxWidth;
        }
    }

    @ScriptFunction
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
            for (GridColumnsNode lcol : children) {
                if (lcol != null) {
                    lcol.setParent(this);
                }
            }
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

    @ScriptFunction
    public boolean isEditable() {
        return !readonly;
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        readonly = !aValue;
    }

    @ScriptFunction
    public boolean isEnabled() {
        return enabled;
    }

    @ScriptFunction
    public void setEnabled(boolean aValue) {
        enabled = aValue;
    }

    @ScriptFunction
    public boolean isVisible() {
        return visible;
    }

    @ScriptFunction
    public void setVisible(boolean aValue) {
        visible = aValue;
    }

    @ScriptFunction
    public boolean isMovable() {
        return movable;
    }

    @ScriptFunction
    public void setMovable(boolean aValue) {
        movable = aValue;
    }

    @ScriptFunction
    public boolean isResizable() {
        return resizable;
    }

    @ScriptFunction
    public void setResizable(boolean aValue) {
        resizable = aValue;
    }

    @ScriptFunction
    public boolean isSortable() {
        return sortable;
    }

    @ScriptFunction
    public void setSortable(boolean aValue) {
        sortable = aValue;
    }

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
    public Color getBackground() {
        return background;
    }

    @ScriptFunction
    public void setBackground(Color aValue) {
        Color oldValue = background;
        background = aValue;
        changeSupport.firePropertyChange("background", oldValue, aValue);
    }

    public void removeChild(GridColumnsNode aCol) {
        if (children != null) {
            children.remove(aCol);
        }
    }

    public void addChild(GridColumnsNode aGroup) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(aGroup)) {
            children.add(aGroup);
            aGroup.setParent(this);
        }
    }

    public void addChild(int atIndex, GridColumnsNode aGroup) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(aGroup)
                && atIndex >= 0 && atIndex <= children.size()) {
            children.add(atIndex, aGroup);
            aGroup.setParent(this);
        }
    }

    @ScriptFunction
    public Color getForeground() {
        return foreground;
    }

    @ScriptFunction
    public void setForeground(Color aValue) {
        Color oldValue = foreground;
        foreground = aValue;
        changeSupport.firePropertyChange("foreground", oldValue, aValue);
    }

    @ScriptFunction
    public Font getFont() {
        return font;
    }

    @ScriptFunction
    public void setFont(Font aValue) {
        Font oldValue = font;
        font = aValue;
        changeSupport.firePropertyChange("font", oldValue, aValue);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}
