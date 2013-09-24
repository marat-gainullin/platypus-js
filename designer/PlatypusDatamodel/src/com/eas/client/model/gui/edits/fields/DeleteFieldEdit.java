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

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;

/**
 *
 * @author mg
 */
public class DeleteFieldEdit<E extends Entity<?, ?, E>> extends FieldsEdit<E> {

    protected Field field = null;
    protected E fieldsEntity = null;
    protected int fieldIndex = 0; // inoperable field index

    public DeleteFieldEdit(E aEntity, Field aField) {
        super(aEntity);
        field = aField;
        fieldsEntity = aEntity;
        if (field != null) {
            fieldIndex = fieldsEntity.getFields().find(field.getName());
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
        Model<E, ?, ?, ?> model = fieldsEntity.getModel();
        Fields fields = model.getParameters();
        if (fields == null && fieldsEntity != null) {
            fields = fieldsEntity.getFields();
        }
        fields.remove(field);
    }

    @Override
    protected void undoWork() {
        createFieldIfNeeded();
        Model<E, ?, ?, ?> model = fieldsEntity.getModel();
        Fields fields = model.getParameters();
        if (fields == null && fieldsEntity != null) {
            fields = fieldsEntity.getFields();
        }
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
            field = createField(fieldsEntity);
        }
    }
}
