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

import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public class SetParameterValueEdit<E extends Entity<?, ?, E>> extends FieldsEdit<E>{
    Object oldValue = null;
    Object newValue = null;
    
    public SetParameterValueEdit(E aEntity)
    {
        super(aEntity);
    }

    @Override
    protected void redoWork() {
        //entity.getDatamodel().fireEntityFieldChanged(entity, field);
    }

    @Override
    protected void undoWork() {
        //entity.getDatamodel().fireEntityFieldChanged(entity, field);
    }
    
}
