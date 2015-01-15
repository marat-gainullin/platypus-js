package com.bearsoft.rowset;

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class Utils {

	public static class JsObject extends JavaScriptObject {
		protected JsObject() {
		}

		public final native Object getJava(String aName)/*-{
			return $wnd.P.boxAsJava(this[aName]);
		}-*/;

		public final native boolean getBoolean(String aName)/*-{
			return !!this[aName];
		}-*/;

		public final native String getString(String aName)/*-{
			var v = this[aName];
			return v != null ? v + '' : null;
		}-*/;

		public final native int getInteger(String aName)/*-{
			return +this[aName];
		}-*/;

		public final native double getDouble(String aName)/*-{
			return +this[aName];
		}-*/;
		
		public final native JavaScriptObject getJs(String aName)/*-{
			return this[aName];
		}-*/;

		public final native void setJs(String aName, JavaScriptObject aValue)/*-{
			return this[aName] = aValue;
		}-*/;
		
		public final native void setJava(String aName, Object aValue)/*-{
			return this[aName] = $wnd.P.boxAsJs(aValue);
		}-*/;
	
		public final native boolean has(String aName)/*-{
			return typeof this[aName] != 'undefined';
		}-*/;

		public final native JavaScriptObject deleteProperty(String aName)/*-{
			delete this[aName];
		}-*/;

		public final native void defineProperty(String aName, JavaScriptObject aDefinition)/*-{
			Object.defineProperty(this, aName, aDefinition);
		}-*/;
		
		public final native void inject(String aName, JavaScriptObject aValue)/*-{
			if (aName != null) {
				Object.defineProperty(this, aName, {
					get : function() {
						return aValue;
					}
				});
			}
		}-*/;

		public final native boolean isArray()/*-{
			return Array.isArray(this);
		}-*/;
		
		public final native JsArrayString keys()/*-{
			return Object.keys(this);
		}-*/;
		
		public final native JavaScriptObject getSlot(int i)/*-{
			return this[i];
		}-*/;
		
		public final native void setSlot(int i, JavaScriptObject aValue)/*-{
			this[i] = aValue;
		}-*/;
		
		public final native void setSlot(int i, int aValue)/*-{
			this[i] = aValue;
		}-*/;
		
		public final native void setSlot(int i, String aValue)/*-{
			this[i] = aValue;
		}-*/;
		
		public final native void setSlot(int i, boolean aValue)/*-{
			this[i] = aValue;
		}-*/;
		
		public final native Object apply(JavaScriptObject aThis, JavaScriptObject aArgs)/*-{
			return this.apply(aThis, aArgs != null ? aArgs : []);
		}-*/;
	}

	public static native JavaScriptObject publishCancellable(Cancellable aValue)/*-{
		return {
			abort : function() {
				aValue.@com.bearsoft.rowset.Cancellable::cancel()();
			}
		};
	}-*/;

	public static native JavaScriptObject publishRunnable(Runnable aValue)/*-{
		return (function() {
			aValue.@java.lang.Runnable::run()();
		});
	}-*/;

	public static native JavaScriptObject stringToArrayBuffer(String aValue) throws Exception/*-{
		if (aValue) {
			var buffer = new ArrayBuffer(aValue.length);
			var bufferView = new Uint8Array(buffer);
			for ( var i = 0; i < aValue.length; i++)
				bufferView[i] = aValue.charCodeAt(i);
			return buffer;
		} else
			return null;
	}-*/;

	public static void invokeLater(final JavaScriptObject aTarget) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				invokeJsFunction(aTarget);
			}
		});
	}

	public static void invokeScheduled(int aTimeout, final JavaScriptObject aTarget) {
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				invokeJsFunction(aTarget);
				return false;
			}
		}, aTimeout);
	}

	public static String format(final String format, final String... args) {
		String[] split = format.split("%s");
		final StringBuffer msg = new StringBuffer();
		for (int pos = 0; pos < split.length - 1; pos += 1) {
			msg.append(split[pos]);
			msg.append(args[pos]);
		}
		msg.append(split[split.length - 1]);
		return msg.toString();
	}

	public static final Document doc = Document.get();

	public native static Boolean executeScriptEventBoolean(JavaScriptObject aEventThis, JavaScriptObject aHandler, Object aArg) throws Exception/*-{
		if (aHandler != null) {
			var res = aHandler.call(aEventThis, $wnd.P.boxAsJs(aArg));
			if (res != undefined && res != null)
				return @java.lang.Boolean::new(Z)((false != res));
			else
				return null;
		} else
			return null;
	}-*/;

	public native static Double executeScriptEventDouble(JavaScriptObject aEventThis, JavaScriptObject aHandler, Object aArg) throws Exception/*-{
		if (aHandler != null) {
			var res = aHandler.call(aEventThis, $wnd.P.boxAsJs(aArg));
			if (res != undefined && res != null)
				return @java.lang.Double::new(D)(res * 1);
			else
				return null;
		} else
			return null;
	}-*/;

	public native static String executeScriptEventString(JavaScriptObject aEventThis, JavaScriptObject aHandler, Object aArg) throws Exception/*-{
		if (aHandler != null) {
			var res = aHandler.call(aEventThis, $wnd.P.boxAsJs(aArg));
			if (res != undefined && res != null)
				return (res + '');
			else
				return null;
		} else
			return null;
	}-*/;

	public native static Date executeScriptEventDate(JavaScriptObject aEventThis, JavaScriptObject aHandler, Object aArg) throws Exception/*-{
		if (aHandler != null) {
			var res = new Date(aHandler.call(aEventThis, $wnd.P.boxAsJs(aArg)));
			if (res != undefined && res != null)
				return @com.bearsoft.rowset.Utils::double2Date(D)(res.getTime());
			else
				return null;
		} else
			return null;
	}-*/;

	public native static void executeScriptEventVoid(JavaScriptObject aEventThis, JavaScriptObject aHandler, Object aArg) throws Exception/*-{
		if (aHandler != null) {
			aHandler.call(aEventThis, $wnd.P.boxAsJs(aArg));
		}
	}-*/;

	public native static Object jsonParse(String aData) throws Exception /*-{
		return $wnd.P.boxAsJava(JSON.parse(aData));
	}-*/;

	public native static void invokeJsFunction(JavaScriptObject aHandler) /*-{
		if (aHandler) {
			aHandler();
		}
	}-*/;

	public static boolean isNumber(Object aValue) {
		return aValue instanceof Number;
	}

	public static boolean isBoolean(Object aValue) {
		return aValue instanceof Boolean;
	}

	public static boolean isDate(Object aValue) {
		return aValue instanceof Date;
	}

	public static Date double2Date(double aTime) {
		return new Date(Double.valueOf(aTime).longValue());
	}

	public static double date2Double(Date aDate) {
		return Long.valueOf(aDate.getTime()).doubleValue();
	}

	public static native JavaScriptObject toScriptDate(Number aValue)/*-{
		if (aValue == null)
			return null;
		else
			return new Date(aValue.@java.lang.Number::doubleValue()());
	}-*/;

	public static Object toJs(Object aValue) {
		if (aValue instanceof Number)
			return ((Number) aValue).doubleValue();
		else if (aValue instanceof Date)
			return toScriptDate(Long.valueOf(((Date) aValue).getTime()));
		else if (aValue instanceof String || aValue instanceof Boolean)
			return aValue;
		else
			return aValue;
	}

	public static native Object unwrap(JavaScriptObject aValue) throws Exception/*-{
		if (aValue == null || aValue == undefined)
			return null;
		else if (aValue instanceof Date || aValue instanceof $wnd.Date || aValue.constructor.name == "Date")
			return @com.bearsoft.rowset.Utils::double2Date(D)(aValue.getTime());
		else if(aValue instanceof Boolean || aValue instanceof $wnd.Boolean || aValue.constructor.name == "Boolean")
			return new @java.lang.Boolean::new(Z)((aValue == true));
		else if(aValue instanceof String || aValue instanceof $wnd.String || aValue.constructor.name == "String")
			return new @java.lang.String::new(Ljava/lang/String;)(aValue+'');
		else if(!isNaN(aValue)) {
			return new @java.lang.Double::new(D)((new Number(aValue)) * 1);
		} else
			return aValue;
	}-*/;

	public static Object toJava(Object aValue) throws Exception {
		if (aValue instanceof JavaScriptObject)
			return unwrap((JavaScriptObject) aValue);
		else if (aValue instanceof Number || aValue instanceof Boolean || aValue instanceof String)
			return aValue;
		else
			return null;
	}

	public static Element getElementByTagName(Element aTag, String aName) {
		NodeList children = aTag.getElementsByTagName(aName);
		if (children.getLength() == 1) {
			if (children.item(0) instanceof Element)
				return (Element) children.item(0);
			else
				throw new IllegalStateException("XML node '" + aName + "' must be a tag node");
		} else if (children.getLength() > 1)
			throw new IllegalStateException("Tag '" + aName + "' must be only one");
		return null;
	}

	public static Element scanForElementByTagName(Element aTag, String aName) {
		NodeList nl = aTag.getChildNodes();
		if (nl != null) {
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeName().equals(aName)) {
					if (n instanceof Element)
						return (Element) n;
					else
						throw new IllegalStateException(aName + " must be a tag node.");
				}
			}
		}
		return null;
	}

	public static boolean getBooleanAttribute(Element aTag, String aName, boolean aDefValue) throws Exception {
		if (aTag.hasAttribute(aName))
			return Boolean.valueOf(aTag.getAttribute(aName));
		else
			return aDefValue;
	}

	public static int getIntegerAttribute(Element aTag, String aName, int aDefValue) throws Exception {
		if (aTag.hasAttribute(aName))
			return Integer.valueOf(aTag.getAttribute(aName));
		else
			return aDefValue;
	}

	public static int getPxAttribute(Element aTag, String aName, int aDefValue) throws Exception {
		if (aTag.hasAttribute(aName)){
			String value = aTag.getAttribute(aName);
			if(value.endsWith("px"))
				value = value.substring(0, value.length() - 2);
			return Integer.valueOf(value);
		}else
			return aDefValue;
	}
	
	public static float getFloatAttribute(Element aTag, String aName, float aDefValue) throws Exception {
		if (aTag.hasAttribute(aName))
			return Float.valueOf(aTag.getAttribute(aName));
		else
			return aDefValue;
	}

	public static int javaArrayLength(Object[] aValue) {
		return aValue.length;
	}

	public static Object javaArrayItem(Object[] aValue, int aIndex) {
		return aValue[aIndex];
	}

	/**
	 * Turns a java array into js array.
	 * 
	 * @param aValue
	 *            Array with prepared items already processed with Utils.toJs().
	 * @return js array.
	 */
	public native static JavaScriptObject toJsArray(Object[] aValue)/*-{
		var res = [];
		var inLength = @com.bearsoft.rowset.Utils::javaArrayLength([Ljava/lang/Object;)(aValue);
		for ( var i = 0; i < inLength; i++)
			res[res.length] = $wnd.P.boxAsJs(@com.bearsoft.rowset.Utils::javaArrayItem([Ljava/lang/Object;I)(aValue, i))
		return res;
	}-*/;

	public static native Map<String, String> extractSimpleProperties(JavaScriptObject aObject) throws Exception /*-{
   	    var m = @java.util.HashMap::new()();
        for (var key in aObject) {
            var val = aObject[key];
            if (!(val instanceof Object) || val instanceof Date || 
                val instanceof Number || val instanceof String || 
                val instanceof Boolean) {
                m.@java.util.HashMap::put(Ljava/lang/Object;Ljava/lang/Object;)(key, JSON.stringify(val));  	
            }		
        }	
        return m;
    }-*/;
}
