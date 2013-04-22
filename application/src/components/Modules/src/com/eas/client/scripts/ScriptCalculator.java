/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class ScriptCalculator extends ScriptableObject {

    protected Map<String, String> evals = new HashMap<>();

    public ScriptCalculator() {
        super();
        super.defineFunctionProperties(new String[]{"add", "remove"}, ScriptCalculator.class, ScriptableObject.READONLY);
    }

    public void add(String aName, String aFormule) {
        evals.put(aName, aFormule);
    }

    public void remove(String aName) {
        evals.remove(aName);
    }

    @Override
    public String getClassName() {
        return ScriptCalculator.class.getSimpleName();
    }
    
    @Override
    public boolean has(String name, Scriptable start) {
        boolean isHasNative = super.has(name, start);
        if (!isHasNative) {
            return evals.containsKey(name);
        }
        return isHasNative;
    }

    @Override
    public Object get(String name, Scriptable start) {
        Object nativeGot = super.get(name, start);
        if (nativeGot == Scriptable.NOT_FOUND) {
            if (evals.containsKey(name)) {
                String toEval = evals.get(name);
                Object calced = Context.getCurrentContext().evaluateString(start, toEval, name + "_formule", 0, null);
                super.put(name, start, calced);
                return calced;
            } else {
                return Scriptable.NOT_FOUND;
            }
        }
        return nativeGot;
    }
}
