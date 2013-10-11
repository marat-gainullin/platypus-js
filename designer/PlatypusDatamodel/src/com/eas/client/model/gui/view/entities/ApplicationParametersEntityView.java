/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.view.entities;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbParametersEntity;
import com.eas.client.model.gui.view.EntityViewsManager;
import java.awt.Point;

/**
 *
 * @author mg
 */
public class ApplicationParametersEntityView extends ApplicationEntityView{

    public ApplicationParametersEntityView(ApplicationDbParametersEntity aEntity, EntityViewsManager<ApplicationDbEntity> aMovesManager) throws Exception {
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

    @Override
    public Point getParameterPosition(Parameter aParameter, boolean isLeft) {
        return super.getFieldPosition(aParameter, isLeft);
    }

    @Override
    public String getParameterDisplayLabel(Field aParameter) {
        return super.getFieldDisplayLabel(aParameter);
    }
    
}
