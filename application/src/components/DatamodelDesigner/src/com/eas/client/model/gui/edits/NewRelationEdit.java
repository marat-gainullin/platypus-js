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

import com.eas.client.model.Entity;
import com.eas.client.model.Relation;

/**
 *
 * @author mg
 */
public class NewRelationEdit<E extends Entity<?, ?, E>> extends DeleteRelationEdit<E>
{
    public NewRelationEdit()
    {
        super();
    }

    public NewRelationEdit(Relation<E> aRel)
    {
        this();
        setRelation(aRel);
    }

    @Override
    protected void redoWork()
    {
        super.undoWork();
    }

    @Override
    protected void undoWork()
    {
        super.redoWork();
    }
}