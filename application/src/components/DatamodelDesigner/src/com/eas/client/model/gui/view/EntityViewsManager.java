/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.model.gui.view.entities.EntityView;
import java.awt.Rectangle;

/**
 *
 * @author mg
 */
public interface EntityViewsManager<E extends Entity<?, ?, E>> {

    /**
     * Method is invoked when moving/resizing process is began.
     * @param aView EntityView instance the operation is performed with.
     */
    public void beginMoveSession(EntityView<E> aView);

    /**
     * Methos is invoked when moving/resizing process has been ended.
     * @param aView EntityView instance the operation is performed with.
     */
    public void endMoveSession(EntityView<E> aView);

    
    public void invalidateConnectors();
    
    /**
     *
     * @param aView EntityView instance the operation is performed with.
     */
    public void beforeMove(EntityView<E> aView);

    /**
     *
     * @param aView EntityView instance the operation is performed with.
     */
    public void afterMove(EntityView<E> aView);

    /**
     *
     * @param aView EntityView instance the operation is performed with.
     * @param dy
     */
    public void collapseExpand(EntityView<E> aView, int dy);

    /**
     * Detemines whetheir a rectangle is legal in field of entities's views.
     * @param aRect A rectangle to be tested.
     * @return true if <code>aRect</code> is leal and false if it is not.
     */
    public boolean isLegalBounds(Rectangle aRect);
    
    public void fieldParamSelectionResetted(E aEntity, Field aField, Parameter aParameter);
}
