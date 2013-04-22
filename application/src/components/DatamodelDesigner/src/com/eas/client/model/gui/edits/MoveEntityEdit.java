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
import java.awt.Point;

/**
 *
 * @author mg
 */
public class MoveEntityEdit<E extends Entity<?, ?, E>> extends DatamodelEdit {

    protected Point beforeLocation = null;
    protected Point afterLocation = null;
    protected E entity = null;

    public MoveEntityEdit(E aEntity, Point aBeforeLocation, Point aAfterLocation) {
        super();
        entity = aEntity;
        beforeLocation = new Point(aBeforeLocation);
        afterLocation = new Point(aAfterLocation);
    }

    public void setBeforeLocation(Point loc) {
        beforeLocation = new Point(loc);
    }

    public void setAfterLocation(Point loc) {
        afterLocation = new Point(loc);
    }

    @Override
    protected void redoWork() {
        entity.setX(afterLocation.x);
        entity.setY(afterLocation.y);
    }

    @Override
    protected void undoWork() {
        entity.setX(beforeLocation.x);
        entity.setY(beforeLocation.y);
    }

    @Override
    public boolean isSignificant() {
        return (beforeLocation != null && afterLocation != null
                && !beforeLocation.equals(afterLocation));
    }

    public E getEntity() {
        return entity;
    }
}