/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits.fields;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public abstract class OrderFieldsEdit<E extends Entity<?, ?, E>> extends FieldsEdit<E> {

    protected Field field;
    protected int fieldIndex;

    public OrderFieldsEdit(E aEntity, Field aField) {
        super(aEntity);
        if (entity != null && aField != null) {
            field = aField;
            fieldIndex = entity.getFields().find(aField.getName());
        }
    }

    @Override
    protected abstract void redoWork();

    @Override
    protected void undoWork() {
        Fields fields = entity.getFields();
        if (fields != null) {
            fields.remove(field);
            if (fieldIndex >= 1 && fieldIndex <= fields.getFieldsCount() + 1) {
                fields.add(fieldIndex, field);
            } else {
                fields.add(field);
            }
        }
    }
}
