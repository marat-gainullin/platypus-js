/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

import com.bearsoft.gui.grid.events.data.TreedModelListener;
import java.util.List;

/**
 * Interface that serves as tree model.
 * Unlike swing TreeModel it has a few methods, informing library implementation about
 * tree structure of processing data.
 * @author mg
 */
public interface TreedModel<T> {

    /**
     * Returns the parent of specified element
     * @param anElement An element to return parent of.
     * @return Element of the data that is parent of passed element.
     * Null is allowed to be returned as top-level parent for forests.
     */
    public T getParentOf(T anElement);

    /**
     * Returns order preserved collection of element's children.
     * @param anElement Element to return children of. It might be null for forests.
     * In this case top-level elements are to be returned.
     * @return A collection of element's children.
     */
    public List<T> getChildrenOf(T anElement);

    /**
     * Returns whether an element is leaf.
     * @param anElement Element to return leaf status of.
     * @return True is the element has no any children, false otherwise.
     */
    public boolean isLeaf(T anElement);
    
    /**
     * Returns columns count as in swing table model
     * @return Columns count.
     */
    public int getColumnCount();

    /**
     * Returns class of the particular column.
     * @param aColIndex Index of the interesed column.
     * @return Class of the column.
     */
    public Class<?> getColumnClass(int aColIndex);

    /**
     * Returns name of the particular column.
     * @param aColIndex Index of the interesed column.
     * @return Name of the column.
     */
    public String getColumnName(int aColIndex);

    /**
     * Returns a value of the specified indexed attribute of specified element.
     * @param anElement Element of data to take an attribute from.
     * @param aColIndex Index of attribute to return.
     * @return Value of the specified indexed attribute of specified element
     */
    public Object getValue(T anElement, int aColIndex);

    /**
     * Sets value of the specified indexed attribute of specified element to specified value.
     * @param anElement Element of data to set an attribute in.
     * @param aColIndex Index of attribute to set.
     * @param aValue Value to set as the  specified indexed attribute.
     */
    public void setValue(T anElement, int aColIndex, Object aValue);

    /**
     * Adds TreedModelListener instance to listeners of data chnaged events.
     * @param aListener TreedModelListener instance all data changed events are to be send to.
     */
    public void addTreedModelListener(TreedModelListener<T> aListener);

    /**
     * Removes a listener from model's listeners list.
     * @param aListener TreedModelListener instance to be removed.
     */
    public void removeTreedModelListener(TreedModelListener<T> aListener);
}
