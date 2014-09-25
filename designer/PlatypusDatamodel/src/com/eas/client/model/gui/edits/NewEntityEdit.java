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

import com.eas.client.SqlQuery;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.queries.QueriesProxy;

/**
 *
 * @author mg
 */
public class NewEntityEdit<E extends Entity<?, SqlQuery, E>, M extends Model<E, ?, SqlQuery>> extends DeleteEntityEdit<E, M> {

    public NewEntityEdit(M aModel, E aEntity) {
        super(aModel, aEntity);
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
