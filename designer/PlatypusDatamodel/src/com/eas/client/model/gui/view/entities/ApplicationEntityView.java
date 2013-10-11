/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.entities;

import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.gui.view.EntityViewsManager;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ApplicationEntityView extends EntityView<ApplicationDbEntity> {

    public ApplicationEntityView(ApplicationDbEntity aEntity, EntityViewsManager<ApplicationDbEntity> aMovesManager) throws Exception {
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

    @Override
    public Set<Relation<ApplicationDbEntity>> getInOutRelations() {
        Set<Relation<ApplicationDbEntity>> inOut = new HashSet<>();
        inOut.addAll(super.getInOutRelations());
        inOut.addAll(entity.getModel().getReferenceRelationsByEntity(entity));
        return inOut;
    }
}
