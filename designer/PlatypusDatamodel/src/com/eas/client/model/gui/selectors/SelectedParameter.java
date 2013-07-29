/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public class SelectedParameter<E extends Entity<?, ?, E>> {
    public E entity;
    public Parameter parameter;
    
    public SelectedParameter(E aEntity, Parameter aParameter)
    {
        super();
        entity = aEntity;
        parameter = aParameter;
    }
}
