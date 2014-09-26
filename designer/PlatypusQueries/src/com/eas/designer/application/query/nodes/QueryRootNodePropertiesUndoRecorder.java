/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.datamodel.nodes.NodePropertiesUndoRecorder;
import com.eas.designer.datamodel.nodes.NodePropertyUndoableEdit;
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
        if (!undoing
                && evt.getSource() instanceof QueryRootNode
                && PlatypusQueryDataObject.CONN_PROP_NAME.equalsIgnoreCase(evt.getPropertyName())
                && (evt.getOldValue() != null || evt.getNewValue() != null)) {
            QueryRootNode node = (QueryRootNode) evt.getSource();
            try {
                PlatypusQueryDataObject dataObject = (PlatypusQueryDataObject) node.getDataObject();
                NodePropertyUndoableEdit edit = new NodePropertyUndoableEdit(this, dataObject, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                undoReciever.undoableEditHappened(new UndoableEditEvent(node.getDataObject(), edit));
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }
}
