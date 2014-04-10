/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationEntity.CursorPositionChangedEvent;
import com.eas.client.model.application.ApplicationEntity.CursorPositionWillChangeEvent;
import com.eas.client.model.application.ApplicationEntity.EntityInstanceChangeEvent;
import com.eas.client.model.application.ApplicationEntity.EntityInstanceDelete;
import com.eas.client.model.application.ApplicationEntity.EntityInstanceInsert;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vv
 */
public class ModuleUtils {

    private static final Map<String, Class<?>> eventsNames2scriptEventsClasses = new HashMap<>();
    private static final Map<String, Class<?>> scriptNames2PlatypusApiClasses = new HashMap<>();
    private static final Class[] apiClasses = {
        com.eas.client.scripts.PlatypusScriptedResource.class
    };

    static {
        initScriptNames2PlatypusApiClasses();
        intitEventsNames2ScriptEventClasses();
    }
    
    public static Class[] getPlatypusApiClasses() {
        return apiClasses;
    }

    public static Class<?> getPlatypusApiClassByName(String name) {
        return scriptNames2PlatypusApiClasses.get(name);
    }

    private static void intitEventsNames2ScriptEventClasses() {
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME, EntityInstanceChangeEvent.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME, EntityInstanceDelete.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME, EntityInstanceInsert.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME, ScriptSourcedEvent.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME, ScriptSourcedEvent.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME, CursorPositionChangedEvent.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME, EntityInstanceChangeEvent.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME, EntityInstanceDelete.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME, EntityInstanceInsert.class);
        eventsNames2scriptEventsClasses.put(ApplicationDbModel.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME, CursorPositionWillChangeEvent.class);
    }
    
    private static void initScriptNames2PlatypusApiClasses() {
        for (Class<?> clazz : apiClasses) {
            scriptNames2PlatypusApiClasses.put(getScriptConstructorName(clazz), clazz);
        }
    }

    public static String getScriptConstructorName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ScriptObj.class)) {
            ScriptObj objectInfo = clazz.getAnnotation(ScriptObj.class);
            if (!objectInfo.name().isEmpty()) {
                return objectInfo.name();
            }
        }
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.isAnnotationPresent(ScriptFunction.class)) {
                ScriptFunction scriptInfo = constructor.getAnnotation(ScriptFunction.class);
                if (!scriptInfo.name().isEmpty()) {
                    return scriptInfo.name();
                }
            }
        }
        return clazz.getSimpleName();
    }
}
