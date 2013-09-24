/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.view;

import com.eas.client.model.Entity;
import com.eas.client.model.gui.view.entities.EntityView;

/**
 *
 * @author mg
 */
public interface EntityViewDoubleClickListener<E extends Entity<?, ?, E>>
{
    public void clicked(EntityView<E> eView, boolean fieldsClicked, boolean paramsClicked);
}
