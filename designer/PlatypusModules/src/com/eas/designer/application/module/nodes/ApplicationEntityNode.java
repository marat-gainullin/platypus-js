/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.nodes;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.designer.application.module.events.ApplicationEntityEventDesc;
import com.eas.designer.application.module.events.ApplicationEntityEventProperty;
import com.eas.designer.application.module.events.ApplicationEntityEventsAction;
import com.eas.designer.application.module.events.ApplicationModuleEvents;
import com.eas.designer.datamodel.nodes.EntityNode;
import com.eas.script.ScriptUtils;
import com.eas.script.StoredFunction;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author mg
 */
public class ApplicationEntityNode extends EntityNode<ApplicationDbEntity> {

    protected ApplicationModuleEvents moduleEvents;

    public ApplicationEntityNode(ApplicationDbEntity aEntity, ApplicationModuleEvents aModuleEvents, UndoRedo.Manager aUndoReciever, Lookup aLookup) throws Exception {
        super(aEntity, aUndoReciever, new ApplicationEntityNodeChildren(aEntity, aUndoReciever, aLookup), aLookup);
        moduleEvents = aModuleEvents;
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
            entity.getModel().getParameters().invalidateFieldsHash();
            return !name.isEmpty() && entity.getModel().getParameters().get(name) == null && entity.getModel().getEntityByName(name) == null && ScriptUtils.isValidJsIdentifier(name);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return false;
    }

    @Override
    public PropertyChangeEvent convertPropertyChangeEventToNode(PropertyChangeEvent evt) {
        String convertedPropertyName = ApplicationEntityEventDesc.convertEntityPropNameToNodePropName(evt.getPropertyName());
        if (nameToProperty.containsKey(convertedPropertyName)) {
            return new PropertyChangeEvent(this, convertedPropertyName, evt.getOldValue(), evt.getNewValue());
        }
        return null;
    }

    @Override
    public void processNodePropertyChange(PropertyChangeEvent nodeEvent) {
        Property<?> prop = nameToProperty.get(nodeEvent.getPropertyName());
        if (prop instanceof ApplicationEntityEventProperty) {
            try {
                eventChanged((ApplicationEntityEventProperty) prop, nodeEvent);
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        super.processNodePropertyChange(nodeEvent);
    }

    @Override
    protected void fillActions(List<Action> aList) {
        super.fillActions(aList);
        aList.add(1, null);
        aList.add(2, SystemAction.get(ApplicationEntityEventsAction.class));
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
        Sheet.Set eSet = new Sheet.Set();
        eSet.setDisplayName(NbBundle.getMessage(ApplicationEntityNode.class, "CTL_Events"));
        eSet.setShortDescription(NbBundle.getMessage(ApplicationEntityNode.class, "HINT_Events"));
        eSet.setName("events");
        eSet.setValue(PROPS_EVENTS_TAB_NAME, eSet.getDisplayName());
        ApplicationEntityEventDesc[] events = ApplicationEntityEventDesc.getApplicableEvents();
        for (ApplicationEntityEventDesc event : events) {
            ApplicationEntityEventProperty eventProp = new ApplicationEntityEventProperty(moduleEvents, entity, event);
            eSet.put(eventProp);
            nameToProperty.put(event.getName(), eventProp);
        }
        sheet.put(eSet);
        return sheet;
    }

    protected void eventChanged(ApplicationEntityEventProperty eProp, PropertyChangeEvent evt) throws Exception {
        ApplicationEntityEventDesc eventDesc = ApplicationEntityEventDesc.getApplicableEventByName(evt.getPropertyName());
        Object newValue = evt.getNewValue();
        if (newValue instanceof StoredFunction) {
            newValue = ((StoredFunction) newValue).getName();
        }
        ApplicationEntityEventProperty.Change change = null;
        Object oldValue = evt.getOldValue();
        if (oldValue instanceof StoredFunction) {
            oldValue = ((StoredFunction) oldValue).getName();
        }
        String oldHandler = (String) oldValue;
        if (oldHandler != null && oldHandler.isEmpty()) {
            oldHandler = null;
        }

        if (newValue instanceof ApplicationEntityEventProperty.Change) {
            change = (ApplicationEntityEventProperty.Change) newValue;
        } else if (newValue instanceof String) {
            if (oldHandler != null) {
                // there is already some handler
                String newHandler = (String) newValue;

                if ("".equals(newHandler)) { // NOI18N
                    // empty String => remove current handler
                    change = new ApplicationEntityEventProperty.Change();
                    change.getRemoved().add(oldHandler);
                } else { // non-empty String => rename current handler
                    change = new ApplicationEntityEventProperty.Change();
                    change.getRenamedNewNames().add((String) newValue);
                    change.getRenamedOldNames().add(oldHandler);
                }
            } else { // oldHandler == null
                if (!"".equals(newValue)) {  // no handler yet, set a new one
                    change = new ApplicationEntityEventProperty.Change();
                    change.getAdded().add((String) newValue);
                } else {
                    // nothing to do
                }
            }
        } else if (newValue == null) {
            if (oldHandler != null) {
                // there is already some handler attached
                // null String => remove current handler
                change = new ApplicationEntityEventProperty.Change();
                change.getRemoved().add(oldHandler);
            } else {
                // nothing to do
            }
        } else {
            throw new IllegalArgumentException();
        }

        if (change != null) {
            eProp.setSomethingChanged(true); // something was changed

            if (change.hasRemoved()) // there is handler to remove
            {
                for (String removed : change.getRemoved()) {
                    moduleEvents.decHandlerUse(eventDesc, removed);
                }
            }

            if (change.hasRenamed()) // there is handler to rename
            {
                for (int i = 0; i < change.getRenamedOldNames().size(); i++) {
                    String oldName = change.getRenamedOldNames().get(i);
                    String newName = change.getRenamedNewNames().get(i);

                    try {
                        moduleEvents.renameHandler(eventDesc, oldName, newName);
                        // hack: silently update all properties using the renamed handler
                        for (ApplicationDbEntity lentity : entity.getModel().getAllEntities().values()) {
                            if (lentity != entity) {
                                PropertyChangeListener[] listeners = lentity.getChangeSupport().getPropertyChangeListeners();
                                for (PropertyChangeListener l : listeners) {
                                    lentity.getChangeSupport().removePropertyChangeListener(l);
                                }
                                try {
                                    String targetHandlerValue = (String) eventDesc.getEntityGetMethod().invoke(lentity);
                                    if (targetHandlerValue != null && targetHandlerValue.equals(oldName)) {
                                        eventDesc.getEntitySetMethod().invoke(lentity, newName);
                                    }
                                } finally {
                                    for (PropertyChangeListener l : listeners) {
                                        lentity.getChangeSupport().addPropertyChangeListener(l);
                                    }
                                }
                            }
                        }
                    } catch (IllegalArgumentException ex) { // name already used
                        ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
                    }
                }
            }

            if (change.hasAdded()) // there is handler  to add
            {
                for (String handlerName : change.getAdded()) {
                    try {
                        moduleEvents.incHandlerUse(eventDesc, handlerName);
                    } catch (IllegalArgumentException ex) { // name already used
                        ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
                    }
                }
            }
        }
    }
}
