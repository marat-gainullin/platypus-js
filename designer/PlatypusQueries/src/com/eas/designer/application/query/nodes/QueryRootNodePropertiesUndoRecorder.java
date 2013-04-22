/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.designer.explorer.model.nodes.NodePropertiesUndoRecorder;
import com.eas.designer.explorer.model.nodes.NodePropertyUndoableEdit;
import java.beans.PropertyChangeEvent;
import javax.swing.event.UndoableEditEvent;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;

/**
 *
 * @author mg
 */
public class QueryRootNodePropertiesUndoRecorder extends NodePropertiesUndoRecorder {

    public QueryRootNodePropertiesUndoRecorder(UndoRedo.Manager aUndoReciever) {
        super(aUndoReciever);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!undoing && evt.getSource() instanceof QueryRootNode && (evt.getOldValue() != null || evt.getNewValue() != null)) {
            QueryRootNode node = (QueryRootNode) evt.getSource();
            try {
                NodePropertyUndoableEdit edit = new NodePropertyUndoableEdit(this, node.getDataObject(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                undoReciever.undoableEditHappened(new UndoableEditEvent(node.getDataObject(), edit));
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }
}
