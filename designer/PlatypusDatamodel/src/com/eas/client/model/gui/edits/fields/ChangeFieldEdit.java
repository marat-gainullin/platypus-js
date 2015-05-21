/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits.fields;

import com.eas.client.metadata.Field;
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public class ChangeFieldEdit<E extends Entity<?, ?, E>> extends FieldsEdit<E> {

    private final Field beforeContent;
    private final Field afterContent;
    private final Field field;

    public ChangeFieldEdit(Field aBeforeContent, Field aAfterContent, Field aField, E aEntity) {
        super(aEntity);
        beforeContent = aBeforeContent;
        afterContent = aAfterContent;
        field = aField;
    }

    @Override
    protected void redoWork() {
        assert field != null;
        field.assignFrom(getAfterContent());
        entity.getFields().invalidateFieldsHash();
    }

    @Override
    protected void undoWork() {
        assert field != null;
        field.assignFrom(getBeforeContent());
        entity.getFields().invalidateFieldsHash();
    }

    /**
     * @return the beforeContent
     */
    public Field getBeforeContent() {
        return beforeContent;
    }

    /**
     * @return the afterContent
     */
    public Field getAfterContent() {
        return afterContent;
    }

    public E getEntity() {
        return entity;
    }
}
