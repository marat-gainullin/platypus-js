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
import java.awt.Rectangle;

/**
 *
 * @author mg
 */
public class ResizeEntityEdit<E extends Entity<?, ?, E>> extends DatamodelEdit {

    protected Rectangle beforeRect = null;
    protected Rectangle afterRect = null;
    protected E entity;

    public ResizeEntityEdit(E aEntity, Rectangle aBeforeBounds, Rectangle aAfterBounds) {
        super();
        entity = aEntity;
        beforeRect = new Rectangle(aBeforeBounds);
        afterRect = new Rectangle(aAfterBounds);
    }

    public void setBeforeBounds(Rectangle rect) {
        beforeRect = new Rectangle(rect);
    }

    public void setAfterBounds(Rectangle rect) {
        afterRect = new Rectangle(rect);
    }

    @Override
    public boolean isNeedConnectors2Reroute() {
        return true;
    }

    @Override
    protected void redoWork() {
        entity.setX(afterRect.x);
        entity.setY(afterRect.y);
        entity.setWidth(afterRect.width);
        entity.setHeight(afterRect.height);
    }

    @Override
    protected void undoWork() {
        entity.setX(beforeRect.x);
        entity.setY(beforeRect.y);
        entity.setWidth(beforeRect.width);
        entity.setHeight(beforeRect.height);
    }

    @Override
    public boolean isSignificant() {
        return (beforeRect != null && afterRect != null
                && !beforeRect.equals(afterRect));
    }

    public E getEntity() {
        return entity;
    }
}
