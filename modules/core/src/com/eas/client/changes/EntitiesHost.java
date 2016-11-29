/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;


/**
 *
 * @author mg
 */
public interface EntitiesHost {

    public Field resolveField(String aEntityName, String aFieldName) throws Exception;
    
    public Parameter resolveParameter(String aEntityName, String aParamName) throws Exception;
}
