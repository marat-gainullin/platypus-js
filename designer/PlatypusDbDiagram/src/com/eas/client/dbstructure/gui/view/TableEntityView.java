/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.view;

import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.gui.view.entities.EntityView;

/**
 *
 * @author mg
 */
public class TableEntityView extends EntityView<FieldsEntity> {

    public TableEntityView(FieldsEntity aEntity, EntityViewsManager<FieldsEntity> aMovesManager) throws Exception {
        super(aEntity, aMovesManager);
    }

    @Override
    protected boolean isEditable() {
        return true;
    }

    @Override
    protected boolean isParameterized() {
        return false;
    }
}
