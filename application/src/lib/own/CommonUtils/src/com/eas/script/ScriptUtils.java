package com.eas.script;

import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.URLReader;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.ScriptEnvironment;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;

/**
 *
 * @author vv, mg
 */
public class ScriptUtils {

    protected static JSObject toPrimitiveFunc;
    protected static JSObject lookupInGlobalFunc;
    protected static JSObject putInGlobalFunc;
    protected static JSObject getModuleFunc;
    protected static JSObject toDateFunc;
    protected static JSObject parseJsonFunc;
    protected static JSObject writeJsonFunc;
    //protected static JSObject toXMLStringFunc;
    protected static JSObject extendFunc;
    protected static JSObject scalarDefFunc;
    protected static JSObject collectionDefFunc;
    protected static ScriptEngine engine;

    public static void init() {
        if (engine == null) {
            engine = new ScriptEngineManager().getEngineByName("nashorn");
            try {
                engine.eval("load('classpath:platypus.js');");
            } catch (ScriptException ex) {
                Logger.getLogger(ScriptUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean isValidJsIdentifier(final String aName) {
        if (aName != null && !aName.trim().isEmpty()) {
            try {
                FunctionNode astRoot = parseJs(new Source("", String.format("function %s() {}", aName)));
                return astRoot != null && !astRoot.getBody().getStatements().isEmpty();
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    public static FunctionNode parseJs(Source aSource) {
        Options options = new Options(null);
        ScriptEnvironment env = new ScriptEnvironment(options, null, null);
        ErrorManager errors = new ErrorManager();
        Parser p = new Parser(env, aSource, errors);
        return p.parse();
    }

    public static Object exec(URL aSource) throws ScriptException {
        return engine.eval(new URLReader(aSource), engine.getContext());
    }

    public static Object exec(String aSource) throws ScriptException {
        return engine.eval(aSource, engine.getContext());
    }

    public static void setToPrimitiveFunc(JSObject aValue) {
        assert toPrimitiveFunc == null;
        toPrimitiveFunc = aValue;
    }

    public static void setLookupInGlobalFunc(JSObject aValue) {
        assert lookupInGlobalFunc == null;
        lookupInGlobalFunc = aValue;
    }

    public static void setPutInGlobalFunc(JSObject aValue){
        assert putInGlobalFunc == null;
        putInGlobalFunc = aValue;
    }
    
    public static void setGetModuleFunc(JSObject aValue) {
        assert getModuleFunc == null;
        getModuleFunc = aValue;
    }

    public static void setToDateFunc(JSObject aValue) {
        assert toDateFunc == null;
        toDateFunc = aValue;
    }

    public static void setParseJsonFunc(JSObject aValue) {
        assert parseJsonFunc == null;
        parseJsonFunc = aValue;
    }

    public static void setWriteJsonFunc(JSObject aValue) {
        assert writeJsonFunc == null;
        writeJsonFunc = aValue;
    }

    /*
     public static void setToXMLStringFunc(JSObject aValue) {
     assert toXMLStringFunc == null;
     toXMLStringFunc = aValue;
     }
     */
    public static void setExtendFunc(JSObject aValue) {
        assert extendFunc == null;
        extendFunc = aValue;
    }

    public static void setScalarDefFunc(JSObject aValue) {
        assert scalarDefFunc == null;
        scalarDefFunc = aValue;
    }

    public static void setCollectionDefFunc(JSObject aValue) {
        assert collectionDefFunc == null;
        collectionDefFunc = aValue;
    }

    public static Object toJava(Object aValue) {
        if (aValue instanceof JSObject) {
            assert toPrimitiveFunc != null : SCRIPT_NOT_INITIALIZED;
            aValue = toPrimitiveFunc.call(null, new Object[]{aValue});
        }
        return aValue;
    }

    public static Object toJs(Object aValue) {
        if (aValue instanceof Date) {// force js boxing of date, because of absence js literal of date value
            assert toDateFunc != null : SCRIPT_NOT_INITIALIZED;
            return toDateFunc.call(null, aValue);
        } else if (aValue instanceof HasPublished) {
            return ((HasPublished) aValue).getPublished();
        } else {
            return aValue;
        }
    }

    public static Object[] toJs(Object[] aArray) {
        Object[] publishedArgs = new Object[aArray.length];
        for (int i = 0; i < aArray.length; i++) {
            publishedArgs[i] = ScriptUtils.toJs(aArray[i]);
        }
        return publishedArgs;
    }

    public static Object parseJson(String json) {
        assert parseJsonFunc != null : SCRIPT_NOT_INITIALIZED;
        return parseJsonFunc.call(null, new Object[]{json});
    }
    protected static final String SCRIPT_NOT_INITIALIZED = "Platypus script function are not initialized.";

    public static String toJson(Object aObj) {
        return JSType.toString(writeJsonFunc.call(null, new Object[]{aObj}));
    }

    /*
     public static String toXMLString(XMLObject aObj) {
     assert toXMLStringFunc != null : SCRIPT_NOT_INITIALIZED;
     return JSType.toString(toXMLStringFunc.call(null, new Object[]{aObj}));
     }
     */
    public static void extend(JSObject aChild, JSObject aParent) {
        assert extendFunc != null : SCRIPT_NOT_INITIALIZED;
        extendFunc.call(null, new Object[]{aChild, aParent});
    }

    public static JSObject scalarPropertyDefinition(JSObject targetEntity, String targetFieldName, String sourceFieldName) {
        assert scalarDefFunc != null : SCRIPT_NOT_INITIALIZED;
        return (JSObject) scalarDefFunc.newObject(new Object[]{targetEntity, targetFieldName, sourceFieldName});
    }

    public static JSObject collectionPropertyDefinition(JSObject sourceEntity, String targetFieldName, String sourceFieldName) {
        assert collectionDefFunc != null : SCRIPT_NOT_INITIALIZED;
        return (JSObject) collectionDefFunc.newObject(new Object[]{sourceEntity, targetFieldName, sourceFieldName});
    }

    public static JSObject createModule(String aModuleName) {
        assert lookupInGlobalFunc != null;
        Object oConstructor = lookupInGlobalFunc.call(null, new Object[]{aModuleName});
        if (oConstructor instanceof JSObject && ((JSObject) oConstructor).isFunction()) {
            JSObject jsConstructor = (JSObject) oConstructor;
            return (JSObject) jsConstructor.newObject(new Object[]{});
        } else {
            return null;
        }
    }

    public static JSObject lookupInGlobal(String aName){
        assert lookupInGlobalFunc != null;
        return (JSObject)lookupInGlobalFunc.call(null, new Object[]{aName});
    }
    
    public static void putInGlobal(String aName, JSObject aValue){
        assert putInGlobalFunc != null;
        putInGlobalFunc.call(null, new Object[]{aName, aValue});
    }
    
    public static JSObject getCachedModule(String aModuleName) {
        assert getModuleFunc != null;
        return (JSObject) getModuleFunc.call(null, new Object[]{aModuleName});
    }
}
