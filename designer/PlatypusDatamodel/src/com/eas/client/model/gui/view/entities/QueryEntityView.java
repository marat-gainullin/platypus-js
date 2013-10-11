/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.view.entities;

import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.query.QueryEntity;

/**
 *
 * @author mg
 */
public class QueryEntityView extends EntityView<QueryEntity>{

    public QueryEntityView(QueryEntity aEntity, EntityViewsManager<QueryEntity> aMovesManager) throws Exception {
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
