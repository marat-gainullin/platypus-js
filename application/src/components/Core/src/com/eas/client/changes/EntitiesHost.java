/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

import com.eas.client.metadata.Field;


/**
 *
 * @author mg
 */
public interface EntitiesHost {

    public Field resolveField(String aEntityId, String aFieldName) throws Exception;
}
