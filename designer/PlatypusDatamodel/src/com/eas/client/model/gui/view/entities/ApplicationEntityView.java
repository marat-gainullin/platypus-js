/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.view.entities;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.gui.view.EntityViewsManager;

/**
 *
 * @author mg
 */
public class ApplicationEntityView extends EntityView<ApplicationDbEntity>{

    public ApplicationEntityView(ApplicationDbEntity aEntity, EntityViewsManager<ApplicationDbEntity> aMovesManager) {
        super(aEntity, aMovesManager);
    }

    @Override
    protected boolean isEditable() {
        return false;
    }

    @Override
    protected boolean isParameterized() {
        return true;
    }
}
