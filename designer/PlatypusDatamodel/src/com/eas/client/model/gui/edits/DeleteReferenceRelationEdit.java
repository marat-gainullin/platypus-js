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

import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ReferenceRelation;

/**
 *
 * @author mg
 */
public class DeleteReferenceRelationEdit<E extends ApplicationEntity<?, ?, E>> extends DatamodelEdit {

    private ReferenceRelation<E> relation;

    public DeleteReferenceRelationEdit(ReferenceRelation<E> aRel) {
        super();
        relation = aRel;
    }

    @Override
    public boolean isNeedConnectors2Reroute() {
        return true;
    }

    @Override
    protected void redoWork() {
        ApplicationModel<E, ?> model = relation.getLeftEntity().getModel();
        if (model != null) {
            model.removeReferenceRelation(relation);
        }
    }

    @Override
    protected void undoWork() {
        ApplicationModel<E, ?> model = relation.getLeftEntity().getModel();
        if (model != null) {
            model.addReferenceRelation(relation);
        }
    }

    /**
     * @return the relation
     */
    public Relation<E> getRelation() {
        return relation;
    }

    /**
     * @param aValue the relation to set
     */
    public void setRelation(ReferenceRelation<E> aValue) {
        relation = aValue;
    }
}