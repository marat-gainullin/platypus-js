/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import org.openide.ErrorManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author vv
 */
public class OutputFieldsNodeChildren extends Children.Keys<Field> {

    PlatypusQueryDataObject dataObject;
    DataObjectPropertyChangeListener dataObjectPropertyChangeListener = new DataObjectPropertyChangeListener();

    public OutputFieldsNodeChildren(PlatypusQueryDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    protected void addNotify() {
        dataObject.addPropertyChangeListener(dataObjectPropertyChangeListener);
        outputFieldsToKeys();
    }

    @Override
    protected void removeNotify() {
        dataObject.removePropertyChangeListener(dataObjectPropertyChangeListener);
        setKeys(Collections.EMPTY_LIST);
        super.removeNotify();
    }

    private void outputFieldsToKeys() {
        Fields fields = getFields();
        if (fields != null) {
            setKeys(fields.toCollection());
        } else {
            setKeys(Collections.EMPTY_LIST);
        }
    }

    private Fields getFields() {
        Fields fields = null;
        try {
            fields = dataObject.getOutputFields();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return fields;
    }

    @Override
    protected Node[] createNodes(Field key) {
        try {
            return new Node[]{new OutputFieldNode(dataObject, key)};
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    protected class DataObjectPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (PlatypusQueryDataObject.OUTPUT_FIELDS.equals(evt.getPropertyName())) {
                outputFieldsToKeys();
            }
        }
    }
}
