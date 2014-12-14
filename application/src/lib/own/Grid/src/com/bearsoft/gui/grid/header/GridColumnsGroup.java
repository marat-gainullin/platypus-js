/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import com.eas.gui.CascadedStyle;
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
public class GridColumnsGroup {

    protected GridColumnsGroup parent;
    protected int minWidth = 0;
    protected int maxWidth = Integer.MAX_VALUE / 2;
    protected boolean movable = true;
    protected boolean resizable = true;
    protected boolean sortable = true;
    protected boolean readonly;
    protected boolean enabled = true;
    protected boolean visible = true;
    protected String title;
    protected String name;
    protected boolean selectOnly;
    protected CascadedStyle style = new CascadedStyle();
    protected List<GridColumnsGroup> children = new ArrayList<>();
    protected TableColumn tableColumn;
    //
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    // style listener
    protected PropertyChangeListener styleListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            changeSupport.firePropertyChange("style", null, style);
        }
    };

    public GridColumnsGroup() {
        super();
        style.getChangeSupport().addPropertyChangeListener(styleListener);
    }

    public GridColumnsGroup(String aTitle) {
        this();
        title = aTitle;
    }

    /**
     * Constructor of grid columns group with parent specified.
     *
     * @param aParent Parent group, the new group is to be added to;
     */
    public GridColumnsGroup(GridColumnsGroup aParent) {
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
    public GridColumnsGroup(TableColumn aCol) {
        this();
        setTableColumn(aCol);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public final void setParent(GridColumnsGroup aParent) {
        parent = aParent;
        if (parent != null && style != null) {
            style.setParent(parent.getStyle());
        }
    }

    public CascadedStyle getStyle() {
        return style;
    }

    public void setStyle(CascadedStyle aValue) {
        if (style != aValue) {
            CascadedStyle oldValue = style;
            if (style != null) {
                style.getChangeSupport().removePropertyChangeListener(styleListener);
            }
            style = aValue;
            if (style != null) {
                style.getChangeSupport().addPropertyChangeListener(styleListener);
            }
            changeSupport.firePropertyChange("style", oldValue, style);
        }
    }

    public boolean containsInChildren(GridColumnsGroup aChild) {
        return children != null && children.indexOf(aChild) != -1;
    }

    public GridColumnsGroup getParent() {
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

    public void lightAssign(GridColumnsGroup aSource) {
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
            //name = aSource.getName();
            if (aSource.getStyle() != null) {
                style = aSource.getStyle().copy();
            } else {
                style = null;
            }
        } else {
            readonly = false;
            enabled = true;
            visible = true;
            title = null;
            name = null;
            selectOnly = false;
            style = null;
            movable = true;
            resizable = true;
            sortable = true;
        }
    }

    public boolean lightEquals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GridColumnsGroup other = (GridColumnsGroup) obj;

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
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.style != other.style && (this.style == null || !this.style.isEqual(other.style))) {
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
        final GridColumnsGroup other = (GridColumnsGroup) obj;
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
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.selectOnly ? 1 : 0);
        hash = 37 * hash + (this.style != null ? this.style.hashCode() : 0);
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

    public static boolean isColumnsEquals(List<GridColumnsGroup> first, List<GridColumnsGroup> second) {
        if (first != second) {
            if (first != null && second != null) {
                if (first.size() == second.size()) {
                    for (int i = 0; i < first.size(); i++) {
                        GridColumnsGroup fColumn = first.get(i);
                        GridColumnsGroup sColumn = second.get(i);
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

    public void assign(GridColumnsGroup aSource) {
        lightAssign(aSource);
        children = new ArrayList<>();
        if (aSource != null && aSource.getChildren() != null) {
            List<GridColumnsGroup> lchildren = aSource.getChildren();
            for (int i = 0; i < lchildren.size(); i++) {
                if (lchildren.get(i) != null) {
                    GridColumnsGroup newCol = lchildren.get(i).copy();
                    children.add(newCol);
                    newCol.setParent(this);
                } else {
                    children.add(null);
                }
            }
            assert children.size() == lchildren.size();
        }
    }

    public GridColumnsGroup lightCopy() {
        GridColumnsGroup colg = new GridColumnsGroup();
        colg.lightAssign(this);
        return colg;
    }

    public GridColumnsGroup copy() {
        GridColumnsGroup colg = new GridColumnsGroup();
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
    public int getPreferredWidth() {
        if (tableColumn != null) {
            return tableColumn.getPreferredWidth();
        } else {
            int pWidth = 0;
            for (int i = 0; i < children.size(); i++) {
                pWidth += children.get(i).getPreferredWidth();
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

    public List<GridColumnsGroup> getChildren() {
        return children;
    }

    public void setChildren(List<GridColumnsGroup> aChildren) {
        children = aChildren;
        if (children != null) {
            for (GridColumnsGroup lcol : children) {
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
    /*
     public String getName() {
     return name;
     }

     public void setName(String aName) {
     name = aName;
     }
     */

    @ScriptFunction
    public Color getBackground() {
        return style.getBackground();
    }

    @ScriptFunction
    public void setBackground(Color background) {
        style.setBackground(background);
    }

    public void removeChild(GridColumnsGroup aCol) {
        if (children != null) {
            children.remove(aCol);
        }
    }

    public void addChild(GridColumnsGroup aGroup) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(aGroup)) {
            children.add(aGroup);
            aGroup.setParent(this);
        }
    }

    public void addChild(int atIndex, GridColumnsGroup aGroup) {
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
        return style.getForeground();
    }

    @ScriptFunction
    public void setForeground(Color aColor) {
        style.setForeground(aColor);
    }

    @ScriptFunction
    public Font getFont() {
        return style.getFont();
    }

    @ScriptFunction
    public void setFont(Font aValue) {
        style.setFont(aValue);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}
