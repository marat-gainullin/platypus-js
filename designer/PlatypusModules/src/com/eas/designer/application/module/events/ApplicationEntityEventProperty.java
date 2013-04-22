package com.eas.designer.application.module.events;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.design.Designable;
import com.eas.script.StoredFunction;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author mg
 */
public class ApplicationEntityEventProperty extends PropertySupport.ReadWrite<String> {

    private static String NO_EVENT = NbBundle.getMessage(ApplicationEntityEventProperty.class, "CTL_NoEvent"); // NOI18N
    private static boolean somethingChanged; // flag for "postSetAction" relevance
    private static boolean invalidValueTried; // flag for "postSetAction" relevance
    private ApplicationDbEntity entity;
    private ApplicationEntityEventDesc eventDesc;
    private ApplicationModuleEvents moduleEvents;

    public ApplicationEntityEventProperty(ApplicationModuleEvents aModuleEvents, ApplicationDbEntity aEntity, ApplicationEntityEventDesc aEventDesc) {
        super(aEventDesc.getName(),
                String.class,
                aEventDesc.getListenerMethod().getName(),
                aEventDesc.getListenerMethod().getName());
        moduleEvents = aModuleEvents;
        entity = aEntity;
        eventDesc = aEventDesc;
        setShortDescription(aEventDesc.getEventSetDescriptor().getListenerType().getName());
        Designable designableMethod = aEventDesc.getListenerMethod().getAnnotation(Designable.class);
        if (designableMethod != null) {
            if (designableMethod.displayName() != null && !designableMethod.displayName().isEmpty()) {
                setDisplayName(designableMethod.displayName());
            }
            if (designableMethod.description() != null && !designableMethod.description().isEmpty()) {
                setShortDescription(designableMethod.description());
            }
        }
    }

    public boolean hasEventHandler() throws Exception {
        String eventHandler = getHandlerByEventDesc();
        return eventHandler != null && !eventHandler.isEmpty();
    }

    public boolean hasEventHandler(String handler) throws Exception {
        String eventHandler = getHandlerByEventDesc();
        if (eventHandler != null) {
            return eventHandler.equals(handler);
        } else {
            return false;
        }
    }

    public String getEventHandler() throws Exception {
        String eventHandler = getHandlerByEventDesc();
        return eventHandler;
    }

    boolean setEventHandler(String handlerName) throws Exception {
        if (handlerName != null) {
            setHandlerByEventDesc(handlerName);
            return true;
        } else {
            return false;
        }
    }

    boolean removeEventHandler() throws Exception {
        String eventHandler = getHandlerByEventDesc();
        if (eventHandler != null) {
            setHandlerByEventDesc(null);
            return true;
        } else {
            return false;
        }
    }

    boolean renameEventHandler(String oldHandlerName, String newHandlerName) throws Exception {
        String eventHandler = getHandlerByEventDesc();
        if (eventHandler == null) {
            return false;
        }
        if (!eventHandler.equals(oldHandlerName) || eventHandler.equals(newHandlerName)) {
            return false;
        }
        setHandlerByEventDesc(newHandlerName);
        return true;
    }

    ApplicationModuleEvents getModuleEvents() {
        return moduleEvents;
    }

    public ApplicationEntityEventDesc getEventDesc() {
        return eventDesc;
    }

    public final ApplicationDbEntity getEntity() {
        return entity;
    }

    private String getHandlerByEventDesc() throws Exception {
        StoredFunction handler = (StoredFunction) eventDesc.getEntityGetMethod().invoke(entity);
        if (handler != null) {
            if (handler.getName() != null && handler.getName().isEmpty()) {
                return null;
            } else {
                return handler.getName();
            }
        }
        return null;
    }

    private boolean setHandlerByEventDesc(String handlerName) throws Exception {
        if (handlerName != null && handlerName.isEmpty()) {
            handlerName = null;
        }
        eventDesc.getEntitySetMethod().invoke(entity, new StoredFunction(handlerName));
        return true;
    }

    // -------
    /**
     * Getter for the value of the property. It returns name of the last
     * selected event handler (for property sheet), not the Event object.
     *
     * @return String name of the selected event handler attached to the event
     */
    @Override
    public String getValue() {
        try {
            return getEventHandler();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    /**
     * Setter for the value of the property. It accepts String (for adding new
     * or renaming the last selected event handler), or Change object
     * (describing multiple changes in event handlers), or null (to refresh
     * property sheet due to a change in handlers made outside).
     */
    @Override
    public void setValue(String aValue) {
        try {
            setEventHandler(aValue);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public Object getValue(String key) {
        if ("canEditAsText".equals(key)) // NOI18N
        {
            return Boolean.TRUE;
        }

        if ("postSetAction".equals(key)) // NOI18N
        {
            return new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    // if Enter was pressed without a change or existing handler
                    // chosen, switch to editor
                    try {
                        if (!somethingChanged && !invalidValueTried && (getEventHandler() != null)) {
                            getModuleEvents().positionOnHandler(eventDesc, getEventHandler());
                        }
                        somethingChanged = false;
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            };
        }

        return super.getValue(key);
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    /**
     * Returns property editor for this property.
     *
     * @return the property editor for adding/removing/renaming event handlers
     */
    @Override
    public PropertyEditor getPropertyEditor() {
        return new EventEditor();
    }

    public void setSomethingChanged(boolean b) {
        somethingChanged = b;
    }

    // --------
    /**
     * Helper class describing changes in event handlers attached to an event.
     */
    public static class Change {

        public boolean hasAdded() {
            return added != null && added.size() > 0;
        }

        public boolean hasRemoved() {
            return removed != null && removed.size() > 0;
        }

        public boolean hasRenamed() {
            return renamedOldName != null && renamedOldName.size() > 0;
        }

        public List<String> getAdded() {
            if (added == null) {
                added = new ArrayList<>();
            }
            return added;
        }

        public List<String> getRemoved() {
            if (removed == null) {
                removed = new ArrayList<>();
            }
            return removed;
        }

        public List<String> getRenamedOldNames() {
            if (renamedOldName == null) {
                renamedOldName = new ArrayList<>();
            }
            return renamedOldName;
        }

        public List<String> getRenamedNewNames() {
            if (renamedNewName == null) {
                renamedNewName = new ArrayList<>();
            }
            return renamedNewName;
        }
        private List<String> added;
        private List<String> removed;
        private List<String> renamedOldName;
        private List<String> renamedNewName;
    }

    // --------
    private class EventEditor extends PropertyEditorSupport {

        @Override
        public String getAsText() {
            if (this.getValue() == null) {
                return NO_EVENT;
            }
            return this.getValue().toString();
        }

        @Override
        public void setAsText(String txt) {
            if (NO_EVENT.equals(txt) && (getValue() == null)) {
                invalidValueTried = false;
                setValue(null);
                return;
            }
            if (!"".equals(txt) && !Utilities.isJavaIdentifier(txt)) { // NOI18N
                // invalid handler name entered
                invalidValueTried = true;
                IllegalArgumentException iae = new IllegalArgumentException();
                String annotation = MessageFormat.format(
                        "FMT_MSG_InvalidJsIdentifier", // NOI18N
                        new Object[]{txt});
                ErrorManager.getDefault().annotate(
                        iae, ErrorManager.ERROR, "Not a javascript identifier", // NOI18N
                        annotation, null, null);
                throw iae;
            }
            if ("".equals(txt) && (this.getValue() == null)) {
                // empty string entered when no event handler exist
                invalidValueTried = true;
                IllegalArgumentException iae = new IllegalArgumentException();
                String emptyStringTxt = NbBundle.getMessage(ApplicationEntityEventProperty.class, "FMT_MSG_EmptyString"); // NOI18N
                String annotation = MessageFormat.format(
                        "FMT_MSG_InvalidJavaIdentifier", // NOI18N
                        new Object[]{emptyStringTxt});
                ErrorManager.getDefault().annotate(
                        iae, ErrorManager.ERROR, "Not a js identifier", // NOI18N
                        annotation, null, null);
                throw iae;
            }
            invalidValueTried = false;
            setValue(txt);
        }

        @Override
        public String[] getTags() {
            try {
                String[] tags = new String[]{getEventHandler()};
                if (tags[0] == null && getValue() == null) {
                    tags = new String[]{getModuleEvents().findFreeHandlerName(entity, eventDesc)};
                }
                return tags.length > 0 ? tags : null;
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
                return null;
            }
        }

        @Override
        public boolean supportsCustomEditor() {
            return false;
        }

        @Override
        public java.awt.Component getCustomEditor() {
            return null;
        }
    }
}
