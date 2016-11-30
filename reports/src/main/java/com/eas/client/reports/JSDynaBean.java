/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.script.Scripts;
import com.eas.util.IdGenerator;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jdk.nashorn.api.scripting.JSObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;

/**
 *
 * @author Andrew
 */
public class JSDynaBean implements DynaBean {

    private final String REPORT_DYNA_CLASS_PREFIX = "PlatypusReportClass_";
    private final JSObject delegate;
    private JSDynaClass dynaClass;
    private final int timezoneOffset;

    public JSDynaBean(JSObject aDelegate, int aTimezoneOffset) {
        super();
        delegate = aDelegate;
        timezoneOffset = aTimezoneOffset;
    }

    @Override
    public boolean contains(String aName, String aKey) {
        Object value = delegate.getMember(aName);
        if (value == null) {
            throw new NullPointerException("No mapped value for '" + aName + "(" + aKey + ")'");
        } else if (value instanceof JSObject) {
            if (!((JSObject) value).isFunction()) {
                return ((JSObject) value).hasMember(aKey);
            } else {
                return false;
            }
        } else if (value instanceof Map) {
            return ((Map) value).containsKey(aKey);
        } else {
            throw new IllegalArgumentException("Non-mapped property for '" + aName + "(" + aKey + ")'");
        }
    }

    @Override
    public Object get(String aName) {
        return wrap(delegate.getMember(aName), timezoneOffset);
    }

    @Override
    public Object get(String aName, int aIndex) {
        Object value = delegate.getMember(aName);
        if (value == null) {
            throw new NullPointerException("No indexed value for '" + aName + "[" + aIndex + "]'");
        } else if (value instanceof JSObject) {
            return wrap(((JSObject) value).getSlot(aIndex), timezoneOffset);
        } else if (value.getClass().isArray()) {
            return wrap(Array.get(value, aIndex), timezoneOffset);
        } else if (value instanceof List) {
            return wrap(((List) value).get(aIndex), timezoneOffset);
        } else {
            throw new IllegalArgumentException("Non-indexed property for '" + aName + "[" + aIndex + "]'");
        }
    }

    @Override
    public Object get(String aName, String aKey) {
        Object value = delegate.getMember(aName);
        if (value == null) {
            throw new NullPointerException("No mapped value for '" + aName + "(" + aKey + ")'");
        } else if (value instanceof JSObject) {
            return wrap(((JSObject) value).getMember(aKey), timezoneOffset);
        } else if (value instanceof Map) {
            return wrap(((Map) value).get(aKey), timezoneOffset);
        } else {
            throw new IllegalArgumentException("Non-mapped property for '" + aName + "(" + aKey + ")'");
        }
    }

    public static Object wrap(Object aValue, int aTimezoneOffset) {
        aValue = Scripts.getSpace().toJava(aValue);
        if (aValue instanceof Date) {
            return convertDateToExcelDate((Date)aValue, aTimezoneOffset);
        } else if (aValue instanceof Number
                || aValue instanceof Boolean
                || aValue instanceof CharSequence
                || aValue == null) {
            return aValue;
        } else if (aValue instanceof JSObject) {
            JSObject jsValue = (JSObject) aValue;
            if (jsValue.isArray() || Scripts.getSpace().isArrayDeep(jsValue)) {
                return new JSDynaList(jsValue, aTimezoneOffset);
            } else if (!jsValue.isFunction()) {
                return new JSDynaBean(jsValue, aTimezoneOffset);
            } else {
                return null;
            }
        } else {
            return aValue;
        }
    }

    public static double convertDateToExcelDate(Date aValue, int aTimezoneOffset) {
        return ((double)(aValue.getTime() - aTimezoneOffset * 60 * 1000) / 86400000) + 25569;
    }

    @Override
    public DynaClass getDynaClass() {
        if (dynaClass == null) {
            dynaClass = new JSDynaClass(delegate, REPORT_DYNA_CLASS_PREFIX + IdGenerator.genStringId());
        }
        return dynaClass;
    }

    @Override
    public void remove(String string, String string1) {
        throw new UnsupportedOperationException("Not supported in JSDynaBean.");
    }

    @Override
    public void set(String string, Object o) {
        throw new UnsupportedOperationException("Not supported in JSDynaBean.");
    }

    @Override
    public void set(String string, int i, Object o) {
        throw new UnsupportedOperationException("Not supported in JSDynaBean.");
    }

    @Override
    public void set(String string, String string1, Object o) {
        throw new UnsupportedOperationException("Not supported in JSDynaBean.");
    }

}
