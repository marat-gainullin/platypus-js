/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.edits;

import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ReferenceRelation;

/**
 *
 * @author mg
 */
public class NewReferenceRelationEdit<E extends ApplicationEntity<?, ?, E>> extends DeleteReferenceRelationEdit<E> {

    public NewReferenceRelationEdit(ReferenceRelation<E> aRel) {
        super(aRel);
    }

    @Override
    protected void redoWork() {
        super.undoWork();
    }

    @Override
    protected void undoWork() {
        super.redoWork();
    }
}