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
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public class NewFieldEdit<E extends Entity<?, ?, E>> extends DeleteFieldEdit<E> {

    public NewFieldEdit(E aEntity) {
        super(aEntity, (Field) null);
    }

    public NewFieldEdit(E aEntity, Field aField) {
        super(aEntity, aField);
    }

    @Override
    protected void redoWork() {
        super.undoWork();
    }

    @Override
    protected void undoWork() {
        super.redoWork();
    }

    public void setFieldIndex(int aIndex) {
        fieldIndex = aIndex;
    }
}
