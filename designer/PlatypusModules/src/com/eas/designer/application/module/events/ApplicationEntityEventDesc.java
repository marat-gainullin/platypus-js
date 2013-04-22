/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.events;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import java.beans.EventSetDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import org.mozilla.javascript.Function;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class ApplicationEntityEventDesc {

    private static Map<String, ApplicationEntityEventDesc> eventsByName = new TreeMap<>();

    static {
        try {
            for (Method m : RowsetEventsDescriptor.getInstance().getListenerMethods()) {
                ApplicationEntityEventDesc desc = new ApplicationEntityEventDesc(RowsetEventsDescriptor.getInstance(), m);
                eventsByName.put(desc.getName(), desc);
            }
        } catch (Exception ex) {
            // no op
        }
    }
    private EventSetDescriptor eventSetDescriptor;
    private Method listenerMethod;
    private Method entityGetMethod;
    private Method entitySetMethod;

    public ApplicationEntityEventDesc(EventSetDescriptor anEventSetDescriptor, Method aListenerMethod) {
        super();
        try {
            eventSetDescriptor = anEventSetDescriptor;
            listenerMethod = aListenerMethod;
            String entityPropertyName = convertNodePropNameToEntityPropName(aListenerMethod.getName());
            entityGetMethod = ApplicationDbEntity.class.getMethod("get" + entityPropertyName.substring(0, 1).toUpperCase() + entityPropertyName.substring(1));
            entitySetMethod = ApplicationDbEntity.class.getMethod("set" + entityPropertyName.substring(0, 1).toUpperCase() + entityPropertyName.substring(1), Function.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static String convertEntityPropNameToNodePropName(String aEntityPropName) {
        switch (aEntityPropName) {
            case ApplicationDbModel.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME:
                return "rowDeleted";
            case ApplicationDbModel.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME:
                return "rowChanged";
            case ApplicationDbModel.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME:
                return "rowInserted";
            case ApplicationDbModel.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME:
                return "rowsetFiltered";
            case ApplicationDbModel.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME:
                return "rowsetRequeried";
            case ApplicationDbModel.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME:
                return "rowsetScrolled";
            case ApplicationDbModel.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME:
                return "willChangeRow";
            case ApplicationDbModel.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME:
                return "willDeleteRow";
            case ApplicationDbModel.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME:
                return "willInsertRow";
            case ApplicationDbModel.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME:
                return "willScroll";
            default:
                return aEntityPropName;
        }
    }

    public static String convertNodePropNameToEntityPropName(String aNodePropName) {
        switch (aNodePropName) {
            case "rowDeleted":
                return ApplicationDbModel.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME;
            case "rowChanged":
                return ApplicationDbModel.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME;
            case "rowInserted":
                return ApplicationDbModel.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME;
            case "rowsetFiltered":
                return ApplicationDbModel.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME;
            case "rowsetRequeried":
                return ApplicationDbModel.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME;
            case "rowsetScrolled":
                return ApplicationDbModel.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME;
            case "willChangeRow":
                return ApplicationDbModel.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME;
            case "willDeleteRow":
                return ApplicationDbModel.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME;
            case "willInsertRow":
                return ApplicationDbModel.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME;
            case "willScroll":
                return ApplicationDbModel.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME;
            default:
                return aNodePropName;
        }
    }

    // --------
    public String getName() {
        return listenerMethod.getName();
    }
    /*
     public String getId() {
     return ApplicationModuleEvents.getEventIdName(listenerMethod);
     }
     */

    public final EventSetDescriptor getEventSetDescriptor() {
        return eventSetDescriptor;
    }

    public final Method getListenerMethod() {
        return listenerMethod;
    }

    public Method getEntityGetMethod() {
        return entityGetMethod;
    }

    public Method getEntitySetMethod() {
        return entitySetMethod;
    }

    public static ApplicationEntityEventDesc[] getApplicableEvents() {
        return eventsByName.values().toArray(new ApplicationEntityEventDesc[]{});
    }

    public static ApplicationEntityEventDesc getApplicableEventByName(String aName) {
        return eventsByName.get(aName);
    }
}
