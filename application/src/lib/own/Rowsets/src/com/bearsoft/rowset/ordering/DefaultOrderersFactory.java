/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.filters.Filter;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.locators.Locator;

/**
 * This class is the default implementation of <code>OrderersFactory</code> interface.
 * @see OrderersFactory
 * @author mg
 */
public class DefaultOrderersFactory implements OrderersFactory {

    protected Rowset rowset;

    public DefaultOrderersFactory(Rowset aRowset) {
        super();
        rowset = aRowset;
    }

    public Filter createFilter() {
        return new Filter(rowset);
    }

    public Locator createLocator() {
        return new Locator(rowset);
    }
}
