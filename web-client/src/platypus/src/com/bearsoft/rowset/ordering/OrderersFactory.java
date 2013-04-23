/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.filters.Filter;
import com.bearsoft.rowset.locators.Locator;

/**
 * Iterface used by rowset to create it's rows processors objects
 * @author mg
 */
public interface OrderersFactory {

    /**
     * Creates a filter object
     * @see Filter
     */
    public Filter createFilter();

    /**
     * Creates a locator object
     * @see Locator
     */
    public Locator createLocator();

}
