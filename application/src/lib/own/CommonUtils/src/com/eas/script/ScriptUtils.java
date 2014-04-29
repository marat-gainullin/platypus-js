package com.eas.script;

import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.URLReader;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.objects.NativeBoolean;
import jdk.nashorn.internal.objects.NativeDate;
import jdk.nashorn.internal.objects.NativeNumber;
import jdk.nashorn.internal.objects.NativeString;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.ScriptEnvironment;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.Undefined;
import jdk.nashorn.internal.runtime.options.Options;

/**
 *
 * @author vv, mg
 */
public class ScriptUtils {

    //public static final String WRAPPER_PROP_NAME = "wrapperComponent";
    /*
     protected static final String toDateFuncSource = ""
     + "Java.type(\"com.eas.script.ScriptUtils\").set("
     + "function toJsDate(aJavaDate){ return aJavaDate != null ? new Date(aJavaDate.time) : null;"
     + "});";
     */
    protected static final String parseJsonFuncSource = ""
            + "Java.type(\"com.eas.script.ScriptUtils\").setParseJsonFunc("
            + "function (str){ return JSON.parse(str);"
            + "});";
    protected static final String writeJsonFuncSource = ""
            + "Java.type(\"com.eas.script.ScriptUtils\").setWriteJsonFunc("
            + "function (aObj){ return JSON.stringify(aObj);"
            + "});";
    /*
     protected static final String toXMLStringFuncSource = ""
     + "Java.type(\"com.eas.script.ScriptUtils\").setToXMLStringFunc("
     + "function toXMLString(aObj){ return aObj.toXMLString();"
     + "});";
     */
    protected static final String extendFuncSource = ""
            + "Java.type(\"com.eas.script.ScriptUtils\").setExtendFunc("
            + "function (Child, Parent) {"
            + "  var F = function() {"
            + "  };"
            + "  F.prototype = Parent.prototype;"
            + "  Child.prototype = new F();"
            + "  Child.prototype.constructor = Child;"
            + "  Child.superclass = Parent.prototype;"
            + "});";
    protected static final String scalarDefFuncSource = ""
            + "Java.type(\"com.eas.script.ScriptUtils\").setScalarDefFunc("
            + "function(targetEntity, targetFieldName, sourceFieldName){"
            + "    var _self = this;"
            + "    _self.enumerable = true;"
            + "    _self.configurable = false;"
            + "    _self.get = function(){"
            + "        var found = targetEntity.find(targetEntity.schema[targetFieldName], this[sourceFieldName]);"
            + "        return found.length == 0 ? null : (found.length == 1 ? found[0] : found);"
            + "    };"
            + "    _self.set = function(aValue){"
            + "        this[sourceFieldName] = aValue ? aValue[targetFieldName] : null;"
            + "    };"
            + "});"
            + "";
    protected static final String collectionDefFuncSource = ""
            + "Java.type(\"com.eas.script.ScriptUtils\").setCollectionDefFunc("
            + "function(sourceEntity, targetFieldName, sourceFieldName){"
            + "    var _self = this;"
            + "    _self.enumerable = true;"
            + "    _self.configurable = false;"
            + "    _self.get = function(){"
            + "        var res = sourceEntity.find(sourceEntity.schema[sourceFieldName], this[targetFieldName]);"
            + "        if(res && res.length > 0){"
            + "            return res;"
            + "        }else{"
            + "            var emptyCollectionPropName = '-x-empty-collection-'+sourceFieldName;"
            + "            var emptyCollection = this[emptyCollectionPropName];"
            + "            if(!emptyCollection){"
            + "                emptyCollection = [];"
            + "                this[emptyCollectionPropName] = emptyCollection;"
            + "            }"
            + "            return emptyCollection;"
            + "        }"
            + "    };"
            + "});"
            + "";

    //protected static JSObject toDateFunc;
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
                //engine.eval(toDateFuncSource);
                //assert toDateFunc != null;
                engine.eval(parseJsonFuncSource);
                assert parseJsonFunc != null;
                engine.eval(writeJsonFuncSource);
                assert writeJsonFunc != null;
                //engine.eval(toXMLStringFuncSource);
                //assert toXMLStringFunc != null;
                engine.eval(extendFuncSource);
                assert extendFunc != null;
                engine.eval(scalarDefFuncSource);
                assert scalarDefFunc != null;
                engine.eval(collectionDefFuncSource);
                assert collectionDefFunc != null;
                engine.eval("load(\"classpath:platypus.js\")");
            } catch (Exception ex) {
                Logger.getLogger(ScriptUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean isValidJsIdentifier(final String aName) {
        if (aName != null && !aName.trim().isEmpty()) {
            try {
                return parseJs(new Source(null, String.format("function %s() {}", aName))) != null;
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
        aValue = ScriptObjectMirror.unwrap(aValue, Global.instance());
        if (aValue instanceof NativeDate) {
            return NativeDate.valueOf(aValue);
        } else if (aValue instanceof NativeString) {
            aValue = NativeString.valueOf(aValue);
        } else if (aValue instanceof NativeNumber) {
            aValue = NativeNumber.valueOf(aValue);
        } else if (aValue instanceof NativeBoolean) {
            aValue = NativeBoolean.valueOf(aValue);
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
        }
        return aValue;
    }

    public static Object toJs(Object aValue) {
        if (aValue instanceof Date) {// force js boxing of date, because of absence js literal of date value
            return ScriptObjectMirror.wrap(NativeDate.construct(true, null, ((Date) aValue).getTime()), Global.instance());
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
        return (JSObject) ScriptObjectMirror.wrap(scalarDefFunc.newObject(new Object[]{targetEntity, targetFieldName, sourceFieldName}), Global.instance());
    }

    public static JSObject collectionPropertyDefinition(JSObject sourceEntity, String targetFieldName, String sourceFieldName) {
        assert collectionDefFunc != null : SCRIPT_NOT_INITIALIZED;
        return (JSObject) ScriptObjectMirror.wrap(collectionDefFunc.newObject(new Object[]{sourceEntity, targetFieldName, sourceFieldName}), Global.instance());
    }

    public static JSObject createModule(String aModuleName) throws ScriptException {
        Object oConstructor = ScriptObjectMirror.wrap(Global.instance().get(aModuleName), Global.instance());
        if (oConstructor instanceof JSObject && ((JSObject) oConstructor).isFunction()) {
            JSObject jsConstructor = (JSObject) oConstructor;
            return (JSObject) jsConstructor.newObject(new Object[]{});
        } else {
            return null;
        }
    }
}
