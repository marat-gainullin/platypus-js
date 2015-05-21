/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.nodes;

import com.eas.client.model.Entity;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.gui.edits.NewReferenceRelationEdit;
import com.eas.client.model.gui.view.model.ApplicationModelView;
import com.eas.designer.datamodel.nodes.EntityNode;
import com.eas.script.Scripts;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class ApplicationEntityNode extends EntityNode<ApplicationDbEntity> {

    public ApplicationEntityNode(ApplicationDbEntity aEntity, UndoRedo.Manager aUndoReciever, Lookup aLookup) throws Exception {
        super(aEntity, aUndoReciever, new ApplicationEntityNodeChildren(aEntity, aUndoReciever, aLookup), aLookup);
    }

    @Override
    public void setName(String val) {
        Object oldVal = entity.getName();
        if (val != null && !val.equals(oldVal)) {
            if (isValidName(val)) {
                entity.setName(val);
                super.setName(val);
            } else {
                IllegalArgumentException t = new IllegalArgumentException();
                throw Exceptions.attachLocalizedMessage(t, String.format(NbBundle.getMessage(ApplicationEntityNode.class, "MSG_InvalidEntityName"), val)); //NOI18N
            }
        }
    }

    protected boolean isValidName(String name) {
        try {
            return !name.isEmpty()
                    && (entity.getModel().getEntityByName(name) == null || getName().equalsIgnoreCase(name))
                    && Scripts.isValidJsIdentifier(name);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return false;
    }

    @Override
    public void processNodePropertyChange(PropertyChangeEvent nodeEvent) {
        Property<?> prop = nameToProperty.get(nodeEvent.getPropertyName());
        if (Entity.QUERY_ID_PROPERTY.equals(nodeEvent.getPropertyName())) {
            try {
                entity.getModel().validate();
                entity.getModel().fireAllQueriesChanged();
                ApplicationModelView.complementReferenceRelations((ReferenceRelation<ApplicationDbEntity> aRelation) -> {
                    NewReferenceRelationEdit edit = new NewReferenceRelationEdit(aRelation);
                    edit.redo();
                    undoReciever.addEdit(edit);
                }, entity.getModel()
                );
            } catch (Exception ex) {
                Logger.getLogger(EntityNode.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        super.processNodePropertyChange(nodeEvent);
    }

    @Override
    protected void fillActions(List<Action> aList) {
        super.fillActions(aList);
    }

    /**
     * Provides access for firing property changes
     *
     * @param name property name
     * @param oldValue old value of the property
     * @param newValue new value of the property
     */
    public void firePropertyChangeHelper(String name,
            Object oldValue, Object newValue) {
        super.firePropertyChange(name, oldValue, newValue);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        try {
            if (!isParametersEntity()) {
                Sheet.Set pSet = sheet.get(Sheet.PROPERTIES);
                PropertySupport.Reflection<String> queryIdProp = new PropertySupport.Reflection<>(entity, String.class, Entity.QUERY_ID_PROPERTY);
                queryIdProp.setName(Entity.QUERY_ID_PROPERTY);
                queryIdProp.setDisplayName("queryName");
                nameToProperty.put(Entity.QUERY_ID_PROPERTY, queryIdProp);
                pSet.put(queryIdProp);
                sheet.put(pSet);
            }
            Sheet.Set eSet = new Sheet.Set();
            eSet.setDisplayName(NbBundle.getMessage(ApplicationEntityNode.class, "CTL_Events"));
            eSet.setShortDescription(NbBundle.getMessage(ApplicationEntityNode.class, "HINT_Events"));
            sheet.put(eSet);
            return sheet;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EntityNode.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return sheet;
        }
    }
}
