/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.processing;

import com.google.gwt.event.shared.HandlerRegistration;
import java.util.List;

/**
 * Interface that serves as tree model. Unlike swing TreeModel it has a few
 * methods, informing library implementation about data structure.
 *
 * @author mg
 * @param <T>
 */
public interface Tree<T> {

    public interface ChangeHandler<T> {

        public void removed(T aSubject);

        public void added(T aSubject);
        
        public void changed(T aSubject);
        
        public void everythingChanged();
    }

    /**
     * Returns the parent of specified element
     *
     * @param anElement An element to return parent of.
     * @return Element of the data that is parent of passed element. Null is
     * allowed to be returned as top-level parent for forests.
     */
    public T getParentOf(T anElement);

    /**
     * Returns order preserved collection of element's children.
     *
     * @param anElement Element to return children of. It might be null for
     * forests. In this case top-level elements are to be returned.
     * @return A collection of element's children.
     */
    public List<T> getChildrenOf(T anElement);

    /**
     * Returns whether an element is leaf.
     *
     * @param anElement Element to return leaf status of.
     * @return True is the element has no any children, false otherwise.
     */
    public boolean isLeaf(T anElement);

    public void add(T aParent, T anElement);

    public void add(int aIndex, T aParent, T anElement);

    public void addAfter(T afterElement, T anElement);

    public void remove(T anElement);

    public HandlerRegistration addChangesHandler(ChangeHandler<T> aHandler);
    
}
