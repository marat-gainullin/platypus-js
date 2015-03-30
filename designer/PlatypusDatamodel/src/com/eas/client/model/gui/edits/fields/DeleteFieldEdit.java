/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.edits.fields;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public class DeleteFieldEdit<E extends Entity<?, ?, E>> extends FieldsEdit<E> {

    protected Field field;
    protected int fieldIndex = 0; // inoperable field index

    public DeleteFieldEdit(E aEntity, Field aField) {
        super(aEntity);
        field = aField;
        if (field != null) {
            fieldIndex = entity.getFields().find(field.getName());
        }
        createFieldIfNeeded();
    }

    public Field getField() {
        return field;
    }

    public static Field createField(Entity anEntity) {
        Fields fields = anEntity.getFields();
        Field lfield = fields.createNewField();
        lfield.setSize(100);
        return lfield;
    }

    @Override
    protected void redoWork() {
        Fields fields = entity.getFields();
        fields.remove(field);
    }

    @Override
    protected void undoWork() {
        createFieldIfNeeded();
        Fields fields = entity.getFields();
        if (field != null) {
            if (fieldIndex >= 1 && fieldIndex <= fields.getFieldsCount() + 1) {
                fields.add(fieldIndex, field);
            } else {
                fields.add(field);
            }
        }
    }

    private void createFieldIfNeeded() {
        if (field == null) {
            field = createField(entity);
        }
    }
}
