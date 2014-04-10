/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.nodes;

import com.eas.designer.datamodel.nodes.EntityNodePropertiesUndoRecorder;
import org.openide.awt.UndoRedo;

/**
 *
 * @author mg
 */
public class ApplicationNodePropertiesUndoRecorder extends EntityNodePropertiesUndoRecorder {

    public ApplicationNodePropertiesUndoRecorder(UndoRedo.Manager aUndoReciever) {
        super(aUndoReciever);
    }
}
