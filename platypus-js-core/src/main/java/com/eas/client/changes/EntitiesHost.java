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
