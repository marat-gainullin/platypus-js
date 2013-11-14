/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * Dummy implementation, intended only for function name storing. This class is
 * utilized by data model and model-aware controls, while designing, and
 * serialization.
 *
 * @author mg
 */
public class StoredFunction implements Function {

    public static final String DUMMY_IMPLEMENTATION = "Can't support this call, since this is dummy implementation, intended only for function name storing.";
    protected String name;

    public StoredFunction(String aName) {
        name = aName;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object call(Context cntxt, Scriptable s, Scriptable s1, Object[] os) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public Scriptable construct(Context cntxt, Scriptable s, Object[] os) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public Object get(String string, Scriptable s) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public Object get(int i, Scriptable s) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public boolean has(String string, Scriptable s) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public boolean has(int i, Scriptable s) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public void put(String string, Scriptable s, Object o) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public void put(int i, Scriptable s, Object o) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public void delete(String string) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public void delete(int i) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public Scriptable getPrototype() {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public void setPrototype(Scriptable s) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public Scriptable getParentScope() {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public void setParentScope(Scriptable s) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public Object[] getIds() {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public Object getDefaultValue(Class<?> type) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }

    @Override
    public boolean hasInstance(Scriptable s) {
        throw new UnsupportedOperationException(DUMMY_IMPLEMENTATION);
    }
}
