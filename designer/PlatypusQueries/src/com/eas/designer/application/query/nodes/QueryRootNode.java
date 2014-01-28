/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.datamodel.nodes.EntityNode;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.designer.datamodel.nodes.ModelNodeChildren;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class QueryRootNode extends ModelNode<QueryEntity, QueryModel> implements PropertyChangeListener {

    public static final String ICON_PATH = "com/eas/designer/application/query/query.png";

    public QueryRootNode(ModelNodeChildren<QueryEntity, QueryModel> aChildren, PlatypusQueryDataObject aDataObject) throws Exception {
        super(aChildren, aDataObject);
        setIconBaseWithExtension(ICON_PATH);
        dataObject.addPropertyChangeListener(this);
    }

    @Override
    public void destroy() throws IOException {
        dataObject.removePropertyChangeListener(this);
        ((ModelNodeChildren)getChildren()).removeNotify();
        super.destroy();
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    protected Sheet createSheet() {
        try {
            Sheet sheet = new Sheet();
            Sheet.Set pSet = Sheet.createPropertiesSet();
            pSet.setValue(EntityNode.PROPS_EVENTS_TAB_NAME, pSet.getDisplayName());
            sheet.put(pSet);
            
            PropertySupport.Reflection<Boolean> publicQueryProp = new PropertySupport.Reflection<Boolean>(dataObject, boolean.class, PlatypusQueryDataObject.PUBLIC_PROP_NAME) {
                @Override
                public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Boolean oldval = super.getValue();
                    super.setValue(val);
                    firePropertyChange(getName(), oldval, val);
                }
            };
            publicQueryProp.setName(PlatypusQueryDataObject.PUBLIC_PROP_NAME);
            publicQueryProp.setShortDescription(NbBundle.getMessage(QueryRootNode.class, "MSG_PublicQueryPropertyShortDescription"));//NOI18N
            publicQueryProp.setDisplayName(NbBundle.getMessage(QueryRootNode.class, PlatypusQueryDataObject.PUBLIC_PROP_NAME));
            pSet.put(publicQueryProp);
            
            PropertySupport.Reflection<Boolean> procProp = new PropertySupport.Reflection<Boolean>(dataObject, boolean.class, PlatypusQueryDataObject.PROCEDURE_PROP_NAME) {
                @Override
                public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Boolean oldval = super.getValue();
                    super.setValue(val);
                    firePropertyChange(getName(), oldval, val);
                }
            };
            procProp.setName(PlatypusQueryDataObject.PROCEDURE_PROP_NAME);
            procProp.setShortDescription(NbBundle.getMessage(QueryRootNode.class, "MSG_ProcedurePropertyShortDescription"));//NOI18N
            procProp.setDisplayName(NbBundle.getMessage(QueryRootNode.class, PlatypusQueryDataObject.PROCEDURE_PROP_NAME));
            pSet.put(procProp);
            
            PropertySupport.Reflection<Boolean> manualProp = new PropertySupport.Reflection<Boolean>(dataObject, boolean.class, PlatypusQueryDataObject.MANUAL_PROP_NAME) {
                @Override
                public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Boolean oldval = super.getValue();
                    super.setValue(val);
                    firePropertyChange(getName(), oldval, val);
                }
            };
            manualProp.setName(PlatypusQueryDataObject.MANUAL_PROP_NAME);
            manualProp.setShortDescription(NbBundle.getMessage(QueryRootNode.class, "MSG_ManualPropertyShortDescription"));//NOI18N
            manualProp.setDisplayName(NbBundle.getMessage(QueryRootNode.class, PlatypusQueryDataObject.MANUAL_PROP_NAME));
            pSet.put(manualProp);
            
            PropertySupport.Reflection<Boolean> readonlyProp = new PropertySupport.Reflection<Boolean>(dataObject, boolean.class, PlatypusQueryDataObject.READONLY_PROP_NAME) {
                @Override
                public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Boolean oldval = super.getValue();
                    super.setValue(val);
                    firePropertyChange(getName(), oldval, val);
                }
            };
            readonlyProp.setName(PlatypusQueryDataObject.READONLY_PROP_NAME);
            readonlyProp.setShortDescription(NbBundle.getMessage(QueryRootNode.class, "MSG_ReadonlyPropertyShortDescription"));//NOI18N
            readonlyProp.setDisplayName(NbBundle.getMessage(QueryRootNode.class, PlatypusQueryDataObject.READONLY_PROP_NAME));
            pSet.put(readonlyProp);
            
            PropertySupport.Reflection<String> connProp = new PropertySupport.Reflection<String>(dataObject, String.class, PlatypusQueryDataObject.CONN_PROP_NAME) {
                protected PropertyEditor editor = new QueryConnectionPropertyEditor((PlatypusQueryDataObject)dataObject);

                @Override
                public PropertyEditor getPropertyEditor() {
                    return editor;
                }

                @Override
                public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    String oldValue = getValue();
                    super.setValue(val);
                    firePropertyChange(PlatypusQueryDataObject.CONN_PROP_NAME, oldValue, val);
                }
            };
            connProp.setName(PlatypusQueryDataObject.CONN_PROP_NAME);
            connProp.setShortDescription(NbBundle.getMessage(QueryRootNode.class, "MSG_ConnPropertyShortDescription"));//NOI18N
            connProp.setDisplayName(NbBundle.getMessage(QueryRootNode.class, PlatypusQueryDataObject.CONN_PROP_NAME));
            pSet.put(connProp);
            return sheet;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return super.createSheet();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PlatypusQueryDataObject.PROCEDURE_PROP_NAME.equals(evt.getPropertyName())
                || PlatypusQueryDataObject.MANUAL_PROP_NAME.equals(evt.getPropertyName())
                || PlatypusQueryDataObject.READONLY_PROP_NAME.equals(evt.getPropertyName())
                || PlatypusQueryDataObject.CONN_PROP_NAME.equals(evt.getPropertyName())) {
            firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    }
}
