/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.nodes;

import com.eas.designer.application.module.events.ApplicationEntityEventDesc;
import com.eas.designer.explorer.model.nodes.EntityNodePropertiesUndoRecorder;
import org.openide.awt.UndoRedo;

/**
 *
 * @author mg
 */
public class ApplicationNodePropertiesUndoRecorder extends EntityNodePropertiesUndoRecorder {

    public ApplicationNodePropertiesUndoRecorder(UndoRedo.Manager aUndoReciever) {
        super(aUndoReciever);
    }

    @Override
    protected String convertNodePropNameToEntityPropName(String propertyName) {
        return ApplicationEntityEventDesc.convertNodePropNameToEntityPropName(propertyName);
    }
}
