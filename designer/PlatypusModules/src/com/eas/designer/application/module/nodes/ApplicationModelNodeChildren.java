/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.nodes;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationDbParametersEntity;
import com.eas.designer.application.module.events.ApplicationEntityEventProperty;
import com.eas.designer.application.module.events.ApplicationEntityEventsCookie;
import com.eas.designer.application.module.events.ApplicationModuleEvents;
import com.eas.designer.datamodel.nodes.EntityNode;
import com.eas.designer.datamodel.nodes.FieldsOrderSupport;
import com.eas.designer.datamodel.nodes.ModelNodeChildren;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author mg
 */
public class ApplicationModelNodeChildren extends ModelNodeChildren<ApplicationDbEntity, ApplicationDbModel> {

    protected ApplicationModuleEvents moduleEvents;

    public ApplicationModelNodeChildren(ApplicationDbModel aModel, ApplicationModuleEvents aModuleEvents, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super(aModel, aUndoReciever, aLookup);
        undoRecordrer = new ApplicationNodePropertiesUndoRecorder(aUndoReciever);
        moduleEvents = aModuleEvents;
    }

    @Override
    protected EntityNode<ApplicationDbEntity> createNode(ApplicationDbEntity key) throws Exception {
        ApplicationEntityNode appNode = newNodeInstance(key);
        for (PropertySet pSet : appNode.getPropertySets()) {
            for (Property<?> p : pSet.getProperties()) {
                if (p instanceof ApplicationEntityEventProperty) {
                    try {
                        ApplicationEntityEventProperty eventProp = (ApplicationEntityEventProperty) p;
                        String handlerName = eventProp.getEventHandler();
                        if (handlerName != null && !handlerName.isEmpty()) {
                            moduleEvents.incHandlerUseWithoutPositioning(eventProp.getEventDesc(), handlerName);
                        }
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            }
        }
        return appNode;
    }

    protected ApplicationEntityNode newNodeInstance(ApplicationDbEntity key) throws Exception {
        ApplicationEntityNode node;
        FieldsOrderSupport fos;
        Lookup lkp;
        if (key instanceof ApplicationDbParametersEntity) {
            fos = new FieldsOrderSupport();
            lkp = Lookups.fixed(key, moduleEvents, new ApplicationEntityEventsCookie(), fos);
            node = new ApplicationEntityNode(key, moduleEvents, undoReciever, new ProxyLookup(lookup, lkp));
            fos.setEntityNode(node);
        } else {
            lkp = Lookups.fixed(key, moduleEvents, new ApplicationEntityEventsCookie());
            node = new ApplicationEntityNode(key, moduleEvents, undoReciever, new ProxyLookup(lookup, lkp));
        }
        return node;
    }

    @Override
    protected void destroyNodes(Node[] aNodes) {
        for (Node node : aNodes) {
            assert node instanceof ApplicationEntityNode;
            ApplicationEntityNode appNode = (ApplicationEntityNode) node;
            for (PropertySet pSet : appNode.getPropertySets()) {
                for (Property<?> p : pSet.getProperties()) {
                    if (p instanceof ApplicationEntityEventProperty) {
                        try {
                            ApplicationEntityEventProperty eventProp = (ApplicationEntityEventProperty) p;
                            String handlerName = eventProp.getEventHandler();
                            if (handlerName != null && !handlerName.isEmpty()) {
                                moduleEvents.decHandlerUse(eventProp.getEventDesc(), handlerName);
                            }
                        } catch (Exception ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                }
            }
        }
        super.destroyNodes(aNodes);
    }
}
