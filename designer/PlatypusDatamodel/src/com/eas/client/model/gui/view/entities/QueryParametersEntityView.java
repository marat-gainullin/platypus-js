/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.entities;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryParametersEntity;
import java.awt.BorderLayout;
import java.awt.Point;

/**
 *
 * @author mg
 */
public class QueryParametersEntityView extends QueryEntityView {

    public QueryParametersEntityView(QueryParametersEntity aEntity, EntityViewsManager<QueryEntity> aMovesManager) throws Exception {
        super(aEntity, aMovesManager);
    }

    @Override
    protected void initFieldsComponents() {
        Fields entitiyFields = entity.getFields();
        fieldsModel.setFields(entitiyFields);
        add(paramsFieldsScroll, BorderLayout.CENTER);
    }

    @Override
    protected boolean isEditable() {
        return true;
    }

    @Override
    protected boolean isParameterized() {
        return false;
    }

    @Override
    public Point getParameterPosition(Parameter aParameter, boolean isLeft) {
        return super.getFieldPosition(aParameter, isLeft);
    }

    @Override
    public String getParameterDisplayLabel(Field aParameter) {
        return super.getFieldDisplayLabel(aParameter);
    }
}
