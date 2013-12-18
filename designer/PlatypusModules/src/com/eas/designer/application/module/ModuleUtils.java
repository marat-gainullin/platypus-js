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
import com.eas.designer.application.module.events.ApplicationEntityEventDesc;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vv
 */
public class ModuleUtils {
    
    private static final Map<String, Class<?>> eventsNames2scriptEventsClasses = new HashMap<>();
    
    static {
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
    
    public static Class<?> getScriptEventClass(String anEventName) {
        return eventsNames2scriptEventsClasses.get(ApplicationEntityEventDesc.convertNodePropNameToEntityPropName(anEventName));
    }
}
