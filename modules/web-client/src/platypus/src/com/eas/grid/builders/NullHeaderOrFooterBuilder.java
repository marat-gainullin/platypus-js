/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.grid.builders;

import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;

/**
 *
 * @author mg
 * @param <T>
 */
public class NullHeaderOrFooterBuilder<T> extends AbstractHeaderOrFooterBuilder<T> {

    public NullHeaderOrFooterBuilder(AbstractCellTable<T> table, boolean isFooter) {
        super(table, isFooter);
    }

    @Override
    protected boolean buildHeaderOrFooterImpl() {
        return false;
    }
    
}
