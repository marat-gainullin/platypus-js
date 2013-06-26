/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.ControlsWrapper;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.scripts.ScriptRunnerPrototype;
import com.eas.controls.FormEventsExecutor;
import com.eas.script.NativeJavaHostObject;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.*;

/**
 *
 * @author mg
 */
public class FormRunnerPrototype extends IdScriptableObject {

    protected static class FormWrapFactory extends WrapFactory {

        @Override
        public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
            if (javaObject instanceof Component<?>) {
                Component<?> comp = (Component<?>) javaObject;
                NativeJavaHostObject jho = ControlsWrapper.getJsWrapper(comp);
                if (jho == null) {
                    jho = new NativeJavaHostObject(scope, javaObject, staticType);
                    ControlsWrapper.setJsWrapper(comp, jho);
                }
                return jho;
            } else {
                return new NativeJavaHostObject(scope, javaObject, staticType);
            }
        }
    }
    protected static final int Id_constructor = 1,
            Id_toString = 2,
            Id_toLocaleString = 3,
            Id_toSource = 4,
            MAX_PROTOTYPE_ID = 4;
    private static final String ID_CONSTRUCTOR = "constructor";
    private static final String ID_TOSTRING = "toString";
    private static final String ID_TOLOCALESTRING = "toLocaleString";
    private static final String ID_TOSOURCE = "toSource";
    private static final String ONLY_CONSTRUCTOR_MSG = "Can't call %s(...). Only new %s(...) is allowed.";
    private static final String CONSTRUCTOR_PARAMETER_MISSING = "For new %s(...) constructor, form name/id parameter is required.";
    //private static final String METHOD_PARAMETER_MISSING = "Method parameter missimg.";

    static {
        ScriptUtils.setWrapFactory(new FormWrapFactory());
    }
    protected static FormRunnerPrototype formPrototype;

    protected static FormRunnerPrototype getInstance() {
        if (formPrototype == null) {
            formPrototype = new FormRunnerPrototype();
            formPrototype.setPrototype(ScriptRunnerPrototype.getInstance());
        }
        return formPrototype;
    }

    public static void init(Scriptable scope, boolean sealed) {
        init(scope, sealed, getInstance());
        formPrototype.setPrototype(ScriptRunnerPrototype.getInstance());
    }

    public static void init(Scriptable scope, boolean sealed, FormRunnerPrototype obj) {
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, false);
        if (obj != getInstance()) {
            obj.setPrototype(getInstance());
        }
        if (sealed) {
            obj.sealObject();
        }
    }

    @Override
    public String getClassName() {
        return "Form";
    }

    public FormRunnerPrototype() {
        super();
    }

    public IdFunctionObject getConstructor() {
        Object oConstructor = get(ID_CONSTRUCTOR, this);
        if (oConstructor instanceof IdFunctionObject) {
            return (IdFunctionObject) oConstructor;
        } else {
            return null;
        }
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
        initPrototypeMethod(getClassName(), id, s, arity);
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

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
            Scriptable thisObj, Object[] args) {
        if (!f.hasTag(getClassName())) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == Id_constructor) {
            if (args.length >= 1) {
                if (thisObj == null) {
                    if (args.length >= 1 && args[0] != null) {
                        if (thisObj == null) {
                            try {
                                ScriptRunner clientWrapper = ScriptRunnerPrototype.lookupScriptRunner(scope);
                                assert clientWrapper != null : ScriptRunnerPrototype.BAD_SCRIPT_SCOPE_MSG;
                                FormRunner formRunner = new FormRunner(args[0].toString(), clientWrapper.getClient(), ScriptUtils.getScope(), clientWrapper.getPrincipalHost(), clientWrapper.getCompiledScriptDocumentsHost());
                                formRunner.setPrototype(this);
                                return formRunner;
                            } catch (Exception ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        } else {
                            throw new IllegalArgumentException(String.format(ONLY_CONSTRUCTOR_MSG, getClassName(), getClassName()));
                        }
                    } else {
                        throw new IllegalArgumentException(String.format(CONSTRUCTOR_PARAMETER_MISSING, getClassName(), getClassName()));
                    }
                } else {
                    throw incompatibleCallError(f);
                }
            } else {
                throw new IllegalArgumentException(String.format(CONSTRUCTOR_PARAMETER_MISSING, getClassName(), getClassName()));
            }
        }
        // The rest of Form.prototype methods require thisObj to be FormRunner

        if (!(thisObj instanceof FormRunner)) {
            throw incompatibleCallError(f);
        }
        FormRunner thisFormRunner = (FormRunner) thisObj;
        switch (id) {
            case Id_toString:
            case Id_toLocaleString: {
                // toLocaleString is just an alias for toString for now
                return thisFormRunner.getApplicationElementId();//String.format("Platypus form ( %s )", thisFormRunner.getApplicationElementId());
            }
            case Id_toSource:
                return "function Form(){\n/*compiled code*/\n}";
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public static FormEventsExecutor lookupFormEventsExecutor(Scriptable aScope) {
        Scriptable currentScope = aScope;
        while (currentScope != null && !(currentScope instanceof FormEventsExecutor)) {
            currentScope = currentScope.getParentScope();
        }
        return (FormEventsExecutor) currentScope;
    }

    public static FormRunner lookupFormFormRunner(Scriptable aScope) {
        Scriptable currentScope = aScope;
        while (currentScope != null && !(currentScope instanceof FormRunner)) {
            currentScope = currentScope.getParentScope();
        }
        return (FormRunner) currentScope;
    }
}
