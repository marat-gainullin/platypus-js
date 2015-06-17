package com.eas.client;

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class Utils {

	public static class JsObject extends JavaScriptObject {
		protected JsObject() {
		}

		public static native String formatDateValueWithJSON(double aDateTimeValue)/*-{
			return JSON.stringify(new Date(aDateTimeValue));
		}-*/;

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

		public final native void setInteger(String aName, int aValue)/*-{
			this[aName] = aValue;
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

		public final void inject(String aName, JavaScriptObject aValue) {
			inject(aName, aValue, false);
		}

		public final native void inject(String aName, JavaScriptObject aValue, boolean aConfigurable)/*-{
			if (aName != null) {
				Object.defineProperty(this, aName, {
					configurable : aConfigurable,
					get : function() {
						return aValue;
					}
				});
			}
		}-*/;

		public final native void inject(String aName, JavaScriptObject aValue, boolean aEnumerable, boolean aConfigurable)/*-{
			if (aName != null) {
				Object.defineProperty(this, aName, {
					enumerable : aEnumerable,
					configurable : aConfigurable,
					get : function() {
						return aValue;
					}
				});
			}
		}-*/;

		public final native boolean isArray()/*-{
			return Array.isArray(this);
		}-*/;

		public final native int length()/*-{
			return this.length;
		}-*/;

		public final native JavaScriptObject splice(int aIndex, int howManyToDelete, JavaScriptObject toInsert)/*-{
			return this.splice(aIndex, howManyToDelete, toInsert);
		}-*/;

		public final native JavaScriptObject splice(int aIndex, int howManyToDelete)/*-{
			return this.splice(aIndex, howManyToDelete);
		}-*/;

		public final native JsArrayString keys()/*-{
			return Object.keys(this);
		}-*/;

		public final native String getStringSlot(int i)/*-{
			return this[i];
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

		public final native Object call(JavaScriptObject aThis, Object aArg)/*-{
			return this.call(aThis, $wnd.P.boxAsJs(aArg));
		}-*/;

		public final native Object call(JavaScriptObject aThis, Object aArg, boolean aFlag)/*-{
			return this.call(aThis, $wnd.P.boxAsJs(aArg), aFlag);
		}-*/;

		public final native JavaScriptObject newObject()/*-{
			var constr = this;
			return new constr();
		}-*/;

		public static native JavaScriptObject dateReviver()/*-{
			return function(k, v){
				if(typeof v === 'string' && /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/.test(v)){
                	return new Date(v);
				}else{
					return v;
				}
			};
		}-*/;
		
		public static native JavaScriptObject parseJSON(String aText)/*-{
			return JSON.parse(aText);
		}-*/;
		
		public static native String stringifyJSON(Object aValue)/*-{
			return JSON.stringify(aValue);
		}-*/;
	
		public static native JavaScriptObject parseJSONDateReviver(String aText)/*-{
			return JSON.parse(aText, @com.eas.client.Utils.JsObject::dateReviver()());
		}-*/;

		public static native String writeJSON(JavaScriptObject changeLog)/*-{
			return JSON.stringify(changeLog);
		}-*/;
	}

	private static final String addListenerName = "-platypus-listener-add-func";
	private static final String removeListenerName = "-platypus-listener-remove-func";
	private static final String fireChangeName = "-platypus-change-fire-func";

	public static native JavaScriptObject listenable(JavaScriptObject aTarget)/*-{
		var listeners = new Set();
		Object.defineProperty(aTarget, @com.eas.client.Utils::addListenerName, {
			value : function(aListener) {
				listeners.add(aListener);
			}
		});
		Object.defineProperty(aTarget, @com.eas.client.Utils::removeListenerName, {
			value : function(aListener) {
				listeners['delete'](aListener);
			}
		});
		Object.defineProperty(aTarget, @com.eas.client.Utils::fireChangeName, {
			value : function(aChange) {
				Object.freeze(aChange);
				var _listeners = [];
				listeners.forEach(function(aListener) {
					_listeners.push(aListener);
				});
				_listeners.forEach(function(aListener) {
					aListener(aChange);
				});
			}
		});
		return function() {
			unlistenable(aTarget);
		};
	}-*/;

	public static native void unlistenable(JavaScriptObject aTarget)/*-{
		delete aTarget[@com.eas.client.Utils::addListenerName];
		delete aTarget[@com.eas.client.Utils::removeListenerName];
	}-*/;

	public static native JavaScriptObject listen(JavaScriptObject aTarget, JavaScriptObject aListener)/*-{
		var addListener = aTarget[@com.eas.client.Utils::addListenerName];
		if (addListener) {
			addListener(aListener);
			return function() {
				aTarget[@com.eas.client.Utils::removeListenerName](aListener);
			};
		} else {
			return null;
		}
	}-*/;

	public static native void unlisten(JavaScriptObject aTarget, JavaScriptObject aListener)/*-{
		aTarget[@com.eas.client.Utils::removeListenerName](aListener);
	}-*/;

	public static native void fire(JavaScriptObject aTarget, JavaScriptObject aChange)/*-{
		try {
			aTarget[@com.eas.client.Utils::fireChangeName](aChange);
		} catch (e) {
			$wnd.P.Logger.severe(e);
		}
	}-*/;

	public static native JavaScriptObject publishCancellable(Cancellable aValue)/*-{
		return {
			abort : function() {
				aValue.@com.eas.client.Cancellable::cancel()();
			}
		};
	}-*/;

	public static native JavaScriptObject publishRunnable(Runnable aValue)/*-{
		return (function() {
			aValue.@java.lang.Runnable::run()();
		});
	}-*/;

	public interface OnChangeHandler {
		public void onChange(JavaScriptObject anEvent);
	}

	private static native JavaScriptObject publishOnChangeHandler(OnChangeHandler aValue)/*-{
		return function(aArg) {
			aValue.@com.eas.client.Utils.OnChangeHandler::onChange(Lcom/google/gwt/core/client/JavaScriptObject;)(aArg);
		};
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
				return @com.eas.client.Utils::double2Date(D)(res.getTime());
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

	public native static String jsonStringify(Object aToJsedObject) /*-{
		return JSON.stringify($wnd.P.boxAsJs(aToJsedObject));
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

	public static native Object unwrap(JavaScriptObject aValue)/*-{
		if (aValue == null || aValue == undefined)
			return null;
		else if (aValue instanceof Date || aValue instanceof $wnd.Date || aValue.constructor.name == "Date")
			return @com.eas.client.Utils::double2Date(D)(aValue.getTime());
		else if(aValue instanceof Boolean || aValue instanceof $wnd.Boolean || aValue.constructor.name == "Boolean")
			return new @java.lang.Boolean::new(Z)((aValue == true));
		else if(aValue instanceof String || aValue instanceof $wnd.String || aValue.constructor.name == "String")
			return new @java.lang.String::new(Ljava/lang/String;)(aValue+'');
		else if(!isNaN(aValue)) {
			return new @java.lang.Double::new(D)((new Number(aValue)) * 1);
		} else
			return aValue;
	}-*/;

	public static Object toJava(Object aValue) {
		if (aValue instanceof JavaScriptObject)
			return unwrap((JavaScriptObject) aValue);
		else if (aValue instanceof Number || aValue instanceof Boolean || aValue instanceof String || aValue instanceof Date)
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
		if (aTag.hasAttribute(aName)) {
			String value = aTag.getAttribute(aName);
			if (value.endsWith("px"))
				value = value.substring(0, value.length() - 2);
			return Integer.valueOf(value);
		} else
			return aDefValue;
	}

	public static float getFloatAttribute(Element aTag, String aName, float aDefValue) throws Exception {
		if (aTag.hasAttribute(aName))
			return Float.valueOf(aTag.getAttribute(aName));
		else
			return aDefValue;
	}

	public static double getDoubleAttribute(Element aTag, String aName, double aDefValue) throws Exception {
		if (aTag.hasAttribute(aName))
			return Double.valueOf(aTag.getAttribute(aName));
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
		var inLength = @com.eas.client.Utils::javaArrayLength([Ljava/lang/Object;)(aValue);
		for ( var i = 0; i < inLength; i++)
			res[res.length] = $wnd.P.boxAsJs(@com.eas.client.Utils::javaArrayItem([Ljava/lang/Object;I)(aValue, i))
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

	public static native Object getPathData(JavaScriptObject anElement, String aPath)/*-{
		if (anElement != null && aPath != null && aPath != '') {
			var target = anElement;
			var path = aPath.split('.');
			var propName = path[0];
			for ( var i = 1; i < path.length; i++) {
				var target = target[propName];
				if (!target) {
					propName = null;
				} else
					propName = path[i];
			}
			if (propName != null) {
				var javaValue = $wnd.P.boxAsJava(target[propName]);
				return javaValue;
			} else
				return null;
		} else
			return null;
	}-*/;

	public static native void setPathData(JavaScriptObject anElement, String aPath, Object aValue)/*-{
		if (aPath != null && aPath != '') {
			var target = anElement;
			var path = aPath.split('.');
			var propName = path[0];
			for ( var i = 1; i < path.length; i++) {
				var target = target[propName];
				if (!target) {
					propName = null;
				} else {
					propName = path[i];
				}
			}
			if (propName != null) {
				var jsData = $wnd.P.boxAsJs(aValue);
				target[propName] = jsData;
			}
		}
	}-*/;

	private static native JavaScriptObject observeElements(JavaScriptObject aTarget, JavaScriptObject aPropListener)/*-{
		function subscribe(aData, aListener) {
			var nHandler = @com.eas.client.Utils::listen(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aData, aListener);
			if (nHandler != null) {
				return nHandler;
			} else if ($wnd.Object.observe) {
				var observer = function(changes) {
					aListener();
				};
				if (Array.isArray(aData)) {
					Array.observe(aData, observer);
					return function() {
						Array.unobserve(aData, observer);
					};
				} else {
					Object.observe(aData, observer);
					return function() {
						Object.unobserve(aData, observer);
					};
				}
			}
			return null;
		}
		var subscribed = [];
		for ( var i = 0; i < aTarget.length; i++) {
			var remover = subscribe(aTarget[i], aPropListener);
			if (remover) {
				subscribed.push(remover);
			}
		}
		return {
			unlisten : function() {
				subscribed.forEach(function(aEntry) {
					aEntry();
				});
			}
		};
	}-*/;

	private static native JavaScriptObject observePath(JavaScriptObject aTarget, String aPath, JavaScriptObject aPropListener)/*-{
	    function subscribe(aData, aListener, aPropName) {
        	var nHandler = @com.eas.client.Utils::listen(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aData, function(aChange){
        		if(!aPropName || aChange.propertyName == aPropName){
        			aListener(aChange);
        		}
        	});
        	if(nHandler != null){
    			return nHandler;
        	}else if($wnd.Object.observe){
	        	var observer = function(changes){
	        		var touched = false;
	        		changes.forEach(function(change){
	        			if(change.name == aPropName || (change.type == 'splice' && aPropName == 'length'))
	        				touched = true;
	        		});
	        		if(touched){
	        			aListener(typeof aData[aPropName] != 'undefined' ? aData[aPropName] : null);
	        		}
	        	};
	        	if(Array.isArray(aData)){
		        	Array.observe(aData, observer);
		        	return function (){
		        		Array.unobserve(aData, observer);
		        	};
	        	}else{
		        	Object.observe(aData, observer);
		        	return function (){
		        		Object.unobserve(aData, observer);
		        	};
	        	}
	        }
	        return null;
	    }
	    var subscribed = [];
	    function listenPath() {
	        subscribed = [];
	        var data = aTarget;
	        var path = aPath.split('.');
	        for (var i = 0; i < path.length; i++) {
	            var propName = path[i];
	            var listener = i === path.length - 1 ? aPropListener : function (evt) {
	                subscribed.forEach(function (aEntry) {
	                    aEntry();
	                });
	                listenPath();
	                aPropListener(evt);
	            };
	            var cookie = subscribe(data, listener, propName);
	            if (cookie) {
	                subscribed.push(cookie);
	                if (data[propName])
	                    data = data[propName];
	                else
	                    break;
	            } else {
	                break;
	            }
	        }
	    }
	    if (aTarget) {
	        listenPath();
	    }
	    return {
	        unlisten: function () {
	            subscribed.forEach(function (aEntry) {
	                aEntry();
	            });
	        }
	    };
	}-*/;

	public static HandlerRegistration listenPath(JavaScriptObject anElement, String aPath, OnChangeHandler aListener) {
		final JsObject listener = observePath(anElement, aPath, publishOnChangeHandler(aListener)).cast();
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				JsObject unlisten = listener.getJs("unlisten").cast();
				unlisten.apply(listener, null);
			}
		};
	}

	public static HandlerRegistration listenElements(JavaScriptObject anElements, OnChangeHandler aListener) {
		final JsObject listener = observeElements(anElements, publishOnChangeHandler(aListener)).cast();
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				JsObject unlisten = listener.getJs("unlisten").cast();
				unlisten.apply(listener, null);
			}
		};
	}
}
