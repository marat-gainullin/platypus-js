package com.eas.designer.application.module.events;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.design.Designable;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.openide.nodes.Node.Cookie;

/**
 * Manages information about used component events and their handlers (in one
 * module or report).
 *
 * @author Tomas Pavek, mg
 */
public class ApplicationModuleEvents implements Cookie {

    // Event handlers: mapping event handler name to it's using counter
    private Map<String, Integer> eventHandlers = new HashMap<>();
    private PlatypusModuleDataObject dataObject;

    public ApplicationModuleEvents(PlatypusModuleDataObject aModel) {
        dataObject = aModel;
    }

    public int incHandlerUseWithoutPositioning(ApplicationEntityEventDesc aDesc, String aHandlerName) {
        Integer counter = eventHandlers.get(aHandlerName);
        if (counter == null) {
            counter = 0;
            dataObject.getCodeGenerator().generateEventHandler(aHandlerName, aDesc.getListenerMethod(), null);
        }
        eventHandlers.put(aHandlerName, ++counter);
        return counter;
    }

    public int incHandlerUse(ApplicationEntityEventDesc aDesc, String aHandlerName) {
        Integer counter = eventHandlers.get(aHandlerName);
        if (counter == null) {
            counter = 0;
            dataObject.getCodeGenerator().eventHandlerAdded(aDesc.getListenerMethod(), aHandlerName, null, null, true);
        } else // only positioning
        {
            positionOnHandler(aDesc, aHandlerName);
        }
        eventHandlers.put(aHandlerName, ++counter);
        return counter;
    }

    public int decHandlerUse(ApplicationEntityEventDesc aDesc, String aHandlerName) {
        Integer counter = eventHandlers.get(aHandlerName);
        if (counter != null) {
            eventHandlers.put(aHandlerName, --counter);
            if (counter == 0) {
                eventHandlers.remove(aHandlerName);
                dataObject.getCodeGenerator().eventHandlerRemoved(aHandlerName, true);
            }
            return counter;
        } else {
            return 0;
        }
    }

    public void renameHandler(ApplicationEntityEventDesc aDesc, String oldName, String newName) {
        if (!oldName.equals(newName)) {
            if (eventHandlers.containsKey(oldName)) {
                Integer oldCounter = eventHandlers.get(oldName);
                if (oldCounter == 1 && !eventHandlers.containsKey(newName)) {
                    dataObject.getCodeGenerator().eventHandlerRenamed(oldName, newName);
                    eventHandlers.remove(oldName);
                    eventHandlers.put(newName, new Integer(1));
                } else {
                    decHandlerUse(aDesc, oldName);
                    incHandlerUseWithoutPositioning(aDesc, newName);
                }
            }
        }
    }

    public void clear() {
        eventHandlers.clear();
    }

    public String[] getAllEventHandlers() {
        Set<String> nameSet = eventHandlers.keySet();
        String[] names = new String[nameSet.size()];
        nameSet.toArray(names);
        return names;
    }

    public String findFreeHandlerName(ApplicationDbEntity entity, ApplicationEntityEventDesc event) {
        String componentName = entity.getName(); // NOI18N
        if (componentName == null) {
            componentName = "";
        }
        String methodName = event.getListenerMethod().getName();
        Designable designableMethod = event.getListenerMethod().getAnnotation(Designable.class);
        if (designableMethod != null && designableMethod.displayName() != null && !designableMethod.displayName().isEmpty()) {
            methodName = designableMethod.displayName();
        }

        if (componentName.isEmpty()) {
            String baseName = methodName.substring(0, 1).toLowerCase()
                    + methodName.substring(1);
            return findFreeHandlerName(baseName);
        } else {
            String baseName = componentName
                    + methodName.substring(0, 1).toUpperCase()
                    + methodName.substring(1);
            return findFreeHandlerName(baseName);
        }
    }

    public String findFreeHandlerName(String baseName) {
        String name = baseName;
        int n = 0;
        while (eventHandlers.get(name) != null) {
            name = baseName + (++n);
        }
        return name;
    }

    public void positionOnHandler(ApplicationEntityEventDesc aDesc, String aHandlerName) {
        dataObject.getCodeGenerator().eventHandlerAdded(aDesc.getListenerMethod(), aHandlerName, null, null, false);
    }
}
