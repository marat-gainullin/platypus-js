/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import java.util.Date;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.xml.XMLObject;

/**
 *
 * @author vv, mg
 */
public class ScriptUtils {

    public static final String WRAPPER_PROP_NAME = "wrapperComponent";
    protected static final String toDateFuncSource = ""
            + "function toJsDate(aJavaDate){ return aJavaDate != null?new Date(aJavaDate.time):null; }"
            + "";
    protected static final String parseJsonFuncSource = ""
            + "function parseJson(str){ return JSON.parse(str); }"
            + "";
    protected static final String writeJsonFuncSource = ""
            + "function writeJson(aObj){ return JSON.stringify(aObj); }"
            + "";
    protected static final String toXMLStringFuncSource = ""
            + "function toXMLString(aObj){ return aObj.toXMLString(); }"
            + "";
    protected static Function toDateFunc;
    protected static Function parseJsonFunc;
    protected static Function writeJsonFunc;
    protected static Function toXMLStringFunc;
    protected static ScriptableObject topLevelScope;

    private static void init() {
        if (topLevelScope == null) {
            Context ctx = Context.getCurrentContext();
            boolean wasContext = ctx != null;
            if (!wasContext) {
                ctx = ScriptUtils.enterContext();
            }
            try {
                topLevelScope = (ScriptableObject) ctx.initStandardObjects();
                toDateFunc = ctx.compileFunction(topLevelScope, toDateFuncSource, "toDateFunc", 0, null);
                parseJsonFunc = ctx.compileFunction(topLevelScope, parseJsonFuncSource, "parseJsonFunc", 0, null);
                writeJsonFunc = ctx.compileFunction(topLevelScope, writeJsonFuncSource, "writeJsonFunc", 0, null);
                toXMLStringFunc = ctx.compileFunction(topLevelScope, toXMLStringFuncSource, "toXMLStringFunc", 0, null);
            } finally {
                if (!wasContext) {
                    Context.exit();
                }
            }
        }
    }
    protected static WrapFactory wrapFactory;

    public static WrapFactory getWrapFactory() {
        return wrapFactory;
    }

    public static void setWrapFactory(WrapFactory aValue) {
        wrapFactory = aValue;
    }

    /**
     * Returns a global singleton js top-level scope. This scope is created once
     * per program.
     *
     * @return An instance of ScriptableObject
     */
    public static ScriptableObject getScope() {
        init();
        return topLevelScope;
    }

    public static Object js2Java(Object aValue) throws EvaluatorException {
        if (aValue instanceof IdScriptableObject) {
            IdScriptableObject scrO = (IdScriptableObject) aValue;
            String lClassName = scrO.getClassName();
            if (lClassName != null && !lClassName.isEmpty()) {
                if (lClassName.equals(java.lang.Boolean.class.getSimpleName())) {
                    aValue = scrO.getDefaultValue(null);
                } else if (lClassName.equals(java.lang.Number.class.getSimpleName())) {
                    aValue = scrO.getDefaultValue(null);
                } else if (lClassName.equals(java.lang.String.class.getSimpleName())) {
                    aValue = Context.jsToJava(aValue, java.lang.String.class);
                } else if (lClassName.equals(java.util.Date.class.getSimpleName())) {
                    aValue = Context.jsToJava(aValue, java.util.Date.class);
                    aValue = new java.sql.Date(((java.util.Date) aValue).getTime());
                }
            }
        } else if (aValue instanceof NativeJavaObject) {
            aValue = ((NativeJavaObject) aValue).unwrap();
        } else if (aValue instanceof Undefined) {
            aValue = null;
        }
        if (aValue instanceof Float) {
            Float fl = (Float) aValue;
            if (Math.abs(Math.round(fl) - fl) < 1.0e-20f) {
                aValue = Math.round(fl);
            }
        } else if (aValue instanceof Double) {
            Double dbl = (Double) aValue;
            if (Math.abs(Math.round(dbl) - dbl) < 1.0e-20f) {
                aValue = Math.round(dbl);
            }
        } else if (aValue instanceof ConsString) {
            return ((ConsString) aValue).toString();
        }
        return aValue;
    }

    public static Object javaToJS(Object aValue, Scriptable aScriptable) {
        if (aValue instanceof Date) {
            return toDateFunc.call(Context.getCurrentContext(), topLevelScope, null, new Object[]{Context.javaToJS(aValue, aScriptable)});
        } else if (aValue instanceof Number) {
            return Double.valueOf(((Number) aValue).doubleValue());
        } else if (aValue instanceof String) {
            return aValue;
        } else if (aValue instanceof Boolean) {
            return aValue;
        } else {
            return Context.javaToJS(aValue, aScriptable);
        }
    }

    public static Object parseJson(String json) {
        init();
        Context cx = Context.enter();
        try {
            return parseJsonFunc.call(cx, topLevelScope, null, new Object[]{json});
        } finally {
            Context.exit();
        }
    }

    public static String toJson(Object aObj) {
        init();
        Context cx = Context.enter();
        try {
            return (String) writeJsonFunc.call(cx, topLevelScope, null, new Object[]{aObj});
        } finally {
            Context.exit();
        }
    }

    public static String toXMLString(XMLObject aObj) {
        init();
        Context cx = Context.enter();
        try {
            return (String) toXMLStringFunc.call(cx, topLevelScope, null, new Object[]{aObj});
        } finally {
            Context.exit();
        }
    }

    public static AstRoot parseJs(String aSource) throws EvaluatorException {
        CompilerEnvirons compilerEnv = CompilerEnvirons.ideEnvirons();
        compilerEnv.setRecordingLocalJsDocComments(true);
        compilerEnv.setRecoverFromErrors(false);
        ErrorReporter compilationErrorReporter = compilerEnv.getErrorReporter();
        Parser p = new Parser(compilerEnv, compilationErrorReporter);
        return p.parse(aSource, "", 0); //NOI18N      
    }

    public static boolean isValidJsIdentifier(String str) {
        if (str != null && !str.trim().isEmpty()) {
            init();
            Context cx = Context.enter();
            try {
                return cx.compileFunction(topLevelScope, String.format("function %s() {}", str), null, 0, null) != null; //NOI18N
            } catch (Exception ex) {
                return false;
            } finally {
                Context.exit();
            }
        }
        return false;
    }

    public static Context enterContext() {
        Context cx = Context.enter();
        if (wrapFactory != null) {
            cx.setWrapFactory(wrapFactory);
        }
        cx.getWrapFactory().setJavaPrimitiveWrap(false);
        return cx;
    }

    public static String toString(Object value) {
        return Context.toString(value);
    }

    public static void putThreadLocal(String aName, Object aValue) {
        Context.getCurrentContext().putThreadLocal(aName, aValue);
    }

    public static Object getThreadLocal(String aName) {
        return Context.getCurrentContext().getThreadLocal(aName);
    }

    public static void removeThreadLocal(String aName) {
        Context.getCurrentContext().removeThreadLocal(aName);
    }
}
