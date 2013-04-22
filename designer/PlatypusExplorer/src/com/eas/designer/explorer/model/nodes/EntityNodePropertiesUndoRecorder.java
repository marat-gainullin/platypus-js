/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.model.nodes;

import java.beans.PropertyChangeEvent;
import javax.swing.event.UndoableEditEvent;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class EntityNodePropertiesUndoRecorder extends NodePropertiesUndoRecorder {

    public EntityNodePropertiesUndoRecorder(UndoRedo.Manager aUndoReciever) {
        super(aUndoReciever);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!undoing && evt.getSource() instanceof EntityNode && (evt.getOldValue() != null || evt.getNewValue() != null)) {
            EntityNode<?> node = (EntityNode<?>) evt.getSource();
            try {
                NodePropertyUndoableEdit edit = new NodePropertyUndoableEdit(this, node.getEntity(), convertNodePropNameToEntityPropName(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
                undoReciever.undoableEditHappened(new UndoableEditEvent(node.getEntity(), edit));
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
