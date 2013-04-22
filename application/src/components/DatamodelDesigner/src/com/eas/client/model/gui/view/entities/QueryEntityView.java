/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.view.entities;

import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryParametersEntity;

/**
 *
 * @author mg
 */
public class QueryEntityView extends EntityView<QueryEntity>{

    public QueryEntityView(QueryEntity aEntity, EntityViewsManager<QueryEntity> aMovesManager) {
        super(aEntity, aMovesManager);
    }

    @Override
    protected boolean isEditable() {
        return entity instanceof QueryParametersEntity;
    }

    @Override
    protected boolean isParameterized() {
        return true;
    }
}
