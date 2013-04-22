/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.events;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowsetListener;
import com.eas.design.Undesignable;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class RowsetEventsDescriptor extends EventSetDescriptor {

    protected static RowsetEventsDescriptor instance;

    public RowsetEventsDescriptor() throws IntrospectionException {
        super(Rowset.class, "rowsetEvents", RowsetListener.class, filterMethods(RowsetListener.class.getMethods()), "add" + RowsetListener.class.getSimpleName(), "remove" + RowsetListener.class.getSimpleName());
    }

    private static String[] filterMethods(Method[] aMethods) {
        List<String> names = new ArrayList<>();
        for (Method m : aMethods) {
            if (m.getAnnotation(Undesignable.class) == null) {
                names.add(m.getName());
            }
        }
        return names.toArray(new String[]{});
    }

    public static RowsetEventsDescriptor getInstance() throws IntrospectionException {
        if (instance == null) {
            instance = new RowsetEventsDescriptor();
        }
        return instance;
    }
}
