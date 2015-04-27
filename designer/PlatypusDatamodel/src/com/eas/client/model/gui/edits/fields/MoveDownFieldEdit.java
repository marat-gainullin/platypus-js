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
public class MoveDownFieldEdit<E extends Entity<?, ?, E>> extends OrderFieldsEdit<E> {

    public MoveDownFieldEdit(E aEntity, Field aField) {
        super(aEntity, aField);
    }

    @Override
    protected void redoWork() {
        Fields fields = entity.getFields();
        if (fields != null) {
            int lfi = fieldIndex + 1;
            fields.remove(field);
            if (lfi >= 1 && lfi <= fields.getFieldsCount() + 1) {
                fields.add(lfi, field);
            } else {
                if (fieldIndex >= 1 && fieldIndex <= fields.getFieldsCount() + 1) {
                    fields.add(fieldIndex, field);
                } else {
                    fields.add(field);
                }
            }
        }
    }
}
