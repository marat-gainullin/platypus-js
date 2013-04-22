/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.selectors;

import com.eas.client.metadata.TableRef;

/**
 *
 * @author mg
 */
public interface TablesSelectorCallback {

    public TableRef[] selectTableRef(TableRef oldValue) throws Exception;
    
}
