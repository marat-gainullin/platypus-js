package com.eas.core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;

public class Predefine {

	private static JavaScriptObject boxing;
	private static JavaScriptObject logger;
	public static Logger platypusApplicationLogger = Logger.getLogger("platypusApplication");
	protected static Map<String, JavaScriptObject> defined = new HashMap<>();

	public static boolean isNumber(Object aValue) {
		return aValue instanceof Number;
	}

	public static boolean isBoolean(Object aValue) {
		return aValue instanceof Boolean;
	}

	public static Map<String, JavaScriptObject> getDefined() {
		return defined;
	}

	public static native JavaScriptObject prerequire(String aName)/*-{
		var defined = @com.eas.core.Predefine::defined;
		return defined.@java.util.Map::get(Ljava/lang/Object;)(aName);
	}-*/;

	public static native void predefine(JavaScriptObject aDeps, String aName, JavaScriptObject aDefiner)/*-{
		var defined = @com.eas.core.Predefine::defined;
		var resolved = [];
		for (var d = 0; d < aDeps.length; d++) {
			var module = defined.@java.util.Map::get(Ljava/lang/Object;)(aDeps[d]);
			resolved.push(module);
		}
		var module = aDefiner.apply(null, resolved);
		defined.@java.util.Map::put(Ljava/lang/Object;Ljava/lang/Object;)(aName, module);
	}-*/;

	public native static void init()/*-{
		function predefine(aDeps, aName, aDefiner) {
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}

		predefine([], 'boxing', function() {
			function boxAsJs(aValue) {
				if (aValue == null)
					return null;
				else if (@com.eas.core.Predefine::isNumber(Ljava/lang/Object;)(aValue))
					return aValue.@java.lang.Number::doubleValue()();
				else if (@com.eas.core.Predefine::isBoolean(Ljava/lang/Object;)(aValue))
					return aValue.@java.lang.Boolean::booleanValue()();
				else
					// dates, strings, complex java objects handled in Utils.toJs()
					return aValue;
			}

			function boxAsJava(aValue) {
				var valueType = typeof (aValue);
				if (valueType == "string")
					return aValue;
				else if (valueType == "number")
					return @java.lang.Double::new(D)(aValue);
				else if (valueType == "boolean")
					return @java.lang.Boolean::new(Z)(aValue);
				else
					// dates, strings, complex js objects handled in Utils.toJava() 
					return aValue;
			}
			var module = {};
			Object.defineProperty(module, 'boxAsJs', {
				enumerable : true,
				value : boxAsJs
			});
			Object.defineProperty(module, 'boxAsJava', {
				enumerable : true,
				value : boxAsJava
			});
			@com.eas.core.Predefine::boxing = module;
			return module;
		});

		predefine([], 'logger', function() {
			var module = {};
			var nativeLogger = @com.eas.core.Predefine::platypusApplicationLogger;

			function severe(aMessage) {
				nativeLogger.@java.util.logging.Logger::severe(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function warning(aMessage) {
				nativeLogger.@java.util.logging.Logger::warning(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function info(aMessage) {
				nativeLogger.@java.util.logging.Logger::info(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function fine(aMessage) {
				nativeLogger.@java.util.logging.Logger::fine(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function finer(aMessage) {
				nativeLogger.@java.util.logging.Logger::finer(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function finest(aMessage) {
				nativeLogger.@java.util.logging.Logger::finest(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			Object.defineProperty(module, 'severe', {
				enumerable : true,
				value : severe
			});
			Object.defineProperty(module, 'warning', {
				enumerable : true,
				value : warning
			});
			Object.defineProperty(module, 'info', {
				enumerable : true,
				value : info
			});
			Object.defineProperty(module, 'fine', {
				enumerable : true,
				value : fine
			});
			Object.defineProperty(module, 'finer', {
				enumerable : true,
				value : finer
			});
			Object.defineProperty(module, 'finest', {
				enumerable : true,
				value : finest
			});
			@com.eas.core.Predefine::logger = module;
			return module;
		});

	}-*/;
}
