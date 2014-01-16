/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.model.script.RowsetHostObject;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;

/**
 * Experimental. Pends of Web client.
 * Entity creation integration with Rhino (cases of script as: var dogs = new Entity('dogsQuery'); dogs.requery()...).
 * @author mg
 */
public class EntityPrototype extends IdScriptableObject {

    public static final String BAD_SCRIPT_SCOPE_MSG = "Can't find reqired script runner scope!";
    private static final String ID_CONSTRUCTOR = "constructor";
    private static final String ID_TOSOURCE = "toSource";
    private static final String ID_TOSTRING = "toString";
    private static final String ID_TOLOCALESTRING = "toLocaleString";
    private static final String ENTITY_TAG = "Entity";
    private static final String ONLY_CONSTRUCTOR_MSG = "Can't call %s(...). Only new %s(...) is allowed.";
    private static final String CONSTRUCTOR_PARAMETER_MISSING = "For new %s(...) constructor, application entity name/id parameter is required.";

    public static void init(Scriptable scope, boolean sealed) {
        EntityPrototype obj = new EntityPrototype();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
    }

    @Override
    public String getClassName() {
        return ENTITY_TAG;
    }

    public EntityPrototype() {
        super();
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case Id_constructor:
                arity = 1;
                s = ID_CONSTRUCTOR;
                break;
            case Id_toString:
                arity = 1;
                s = ID_TOSTRING;
                break;
            case Id_toLocaleString:
                arity = 1;
                s = ID_TOLOCALESTRING;
                break;
            case Id_toSource:
                arity = 0;
                s = ID_TOSOURCE;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(ENTITY_TAG, id, s, arity);
    }

    @Override
    protected int findPrototypeId(String s) {
        switch (s) {
            case ID_CONSTRUCTOR:
                return Id_constructor;
            case ID_TOSTRING:
                return Id_toString;
            case ID_TOLOCALESTRING:
                return Id_toLocaleString;
            case ID_TOSOURCE:
                return Id_toSource;
            default:
                return 0;
        }
    }
    private static final int Id_constructor = 1,
            Id_toString = 2,
            Id_toLocaleString = 3,
            Id_toSource = 4,
            MAX_PROTOTYPE_ID = 4;

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
            Scriptable thisObj, Object[] args) {
        if (!f.hasTag(ENTITY_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == Id_constructor) {
            if (args.length >= 1) {
                if (thisObj == null) {
                    if (args[0] != null) {
                        String queryId = ScriptUtils.js2Java(args[0]).toString();
                        if (thisObj == null) {
                            try {
                                ScriptRunner modelWrapper = lookupScriptRunner(scope);
                                assert modelWrapper != null : BAD_SCRIPT_SCOPE_MSG;
                                Scriptable rowsetHost = modelWrapper.getModel().createQuery(queryId);
                                rowsetHost.setPrototype(this);
                                return rowsetHost;
                            } catch (Exception ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        } else {
                            throw new IllegalArgumentException(String.format(ONLY_CONSTRUCTOR_MSG, ENTITY_TAG, ENTITY_TAG));
                        }
                    } else {
                        throw new IllegalArgumentException(String.format(CONSTRUCTOR_PARAMETER_MISSING, ENTITY_TAG, ENTITY_TAG));
                    }
                } else {
                    throw incompatibleCallError(f);
                }
            } else {
                throw incompatibleCallError(f);
            }
        }

        // The rest of Entity.prototype methods require thisObj to be RowsetHostObject

        if (!(thisObj instanceof RowsetHostObject<?>)) {
            throw incompatibleCallError(f);
        }
        RowsetHostObject<?> thisRowsetHost = (RowsetHostObject<?>) thisObj;
        switch (id) {

            case Id_toString:
            case Id_toLocaleString: {
                // toLocaleString is just an alias for toString for now
                return String.format("Platypus entity ( %s )", ((ScriptableRowset)thisRowsetHost.unwrap()).getEntity().getQueryId());
            }

            case Id_toSource:
                return String.format("function %s(){\n/*compiled code*/\n}", ENTITY_TAG);

            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public static ScriptRunner lookupScriptRunner(Scriptable aScope) {
        Scriptable currentScope = aScope;
        while (currentScope != null && !(currentScope instanceof ScriptRunner)) {
            currentScope = currentScope.getParentScope();
        }
        return (ScriptRunner) currentScope;
    }
    
    public static RowsetHostObject<?> lookupEntity(Scriptable aScope) {
        Scriptable currentScope = aScope;
        while (currentScope != null && !(currentScope instanceof RowsetHostObject<?>)) {
            currentScope = currentScope.getParentScope();
        }
        return (RowsetHostObject<?>) currentScope;
    }
}
