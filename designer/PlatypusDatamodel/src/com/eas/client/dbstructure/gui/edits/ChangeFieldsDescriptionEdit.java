/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.edits.fields.FieldsEdit;

/**
 * The diagram edit. It's located in structure designer because of it's work is
 * specific for table description editing.
 *
 * @author mg
 */
public class ChangeFieldsDescriptionEdit extends FieldsEdit<FieldsEntity> {

    protected String oldValue;
    protected String newValue;

    public ChangeFieldsDescriptionEdit(FieldsEntity aEntity, String aOldValue, String aNewValue) {
        super(aEntity);
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    protected void redoWork() {
        entity.setTitle(newValue);
        entity.clearFields();
    }

    @Override
    protected void undoWork() {
        entity.setTitle(oldValue);
        entity.clearFields();
    }
}
