/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes;

import com.bearsoft.rowset.metadata.Field;

/**
 *
 * @author mg
 */
public interface EntitiesHost {

    public Field resolveField(String aEntityId, String aFieldName) throws Exception;
}
