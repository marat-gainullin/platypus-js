/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits.fields;

import com.eas.client.model.gui.edits.DatamodelEdit;
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public abstract class FieldsEdit<E extends Entity<?, ?, E>> extends DatamodelEdit {

    protected E entity;

    public FieldsEdit(E aEntity) {
        super();
        entity = aEntity;
    }
}
