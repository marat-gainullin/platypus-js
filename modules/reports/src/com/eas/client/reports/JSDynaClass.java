/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import java.util.HashMap;
import java.util.Map;
import jdk.nashorn.api.scripting.JSObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

/**
 *
 * @author Andrew
 */
public class JSDynaClass implements DynaClass {

    private final JSObject delegate;
    private final String name;
    private final Map<String, DynaProperty> properties = new HashMap<>();

    public JSDynaClass(JSObject aDelegate, String aName) {
        super();
        delegate = aDelegate;
        name = aName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DynaProperty getDynaProperty(String aName) {
        if (name != null) {
            if (properties.isEmpty()) {
                if (delegate.hasMember(aName)) {
                    Object oMember = delegate.getMember(aName);
                    if (!(oMember instanceof JSObject) || !((JSObject) oMember).isFunction()) {
                        return new DynaProperty(aName);
                    }
                    return null;
                }
            } else {
                return properties.get(aName);
            }
        }
        throw new IllegalArgumentException("No property name specified");
    }

    @Override
    public DynaProperty[] getDynaProperties() {
        if (properties.isEmpty()) {
            delegate.keySet().forEach((String key) -> {
                Object oMember = delegate.getMember(key);
                if (!(oMember instanceof JSObject) || !((JSObject) oMember).isFunction()) {
                    properties.put(key, new DynaProperty(key));
                }
            });
        }
        return properties.values().toArray(new DynaProperty[]{});
    }

    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
