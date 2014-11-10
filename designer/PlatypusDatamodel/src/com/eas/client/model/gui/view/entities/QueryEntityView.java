/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.entities;

import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.query.QueryEntity;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author mg
 */
public class QueryEntityView extends EntityView<QueryEntity> {

    public QueryEntityView(QueryEntity aEntity, EntityViewsManager<QueryEntity> aMovesManager) throws Exception {
        super(aEntity, aMovesManager);
        entity.getChangeSupport().addPropertyChangeListener(QueryEntity.ALIAS_PROPERTY, (PropertyChangeEvent evt) -> {
            titleLabel.setText(getCheckedEntityTitle());
            titleLabel.invalidate();
            reLayout();
        });
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
