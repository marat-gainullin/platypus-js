/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.model.gui.view.entities.EntityView;

/**
 *
 * @author mg
 */
public interface EntityViewsManager<E extends Entity<?, ?, E>> {

    public void invalidateConnectors();

    public void collapseExpand(EntityView<E> aView, int dy);

    public void fieldParamSelectionResetted(E aEntity, Field aField, Parameter aParameter);
}
