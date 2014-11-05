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
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class DeleteEntityEdit<E extends Entity<?, SqlQuery, E>, M extends Model<E, SqlQuery>> extends DatamodelEdit {

    private final M model;
    protected E entity;

    public DeleteEntityEdit(M aModel, E aEntity) {
        super();
        model = aModel;
        entity = aEntity;
    }

    public E getEntity() {
        return entity;
    }

    public void setEntity(E aValue) {
        entity = aValue;
    }

    @Override
    public boolean isNeedConnectors2Reroute() {
        return true;
    }

    @Override
    protected void redoWork() {
        if (entity != null && model != null) {
            model.removeEntity(entity);
        }
    }

    @Override
    protected void undoWork() {
        if (entity != null && model != null) {
            try {
                model.addEntity(entity);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
