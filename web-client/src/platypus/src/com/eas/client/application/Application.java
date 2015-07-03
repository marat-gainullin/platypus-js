/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.CallbackAdapter;
import com.eas.client.GroupingHandlerRegistration;
import com.eas.client.PlatypusLogFormatter;
import com.eas.client.Utils;
import com.eas.client.form.js.JsContainers;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.js.JsMenus;
import com.eas.client.form.js.JsModelWidgets;
import com.eas.client.form.js.JsWidgets;
import com.eas.client.model.js.JsManaged;
import com.eas.client.model.js.JsOrderer;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * 
 * @author mg
 */
public class Application {

	protected static class LoggingLoadHandler implements Loader.LoadHandler {

		public LoggingLoadHandler() {
			super();
		}

		@Override
		public void started(String anItemName) {
			final String message = "Loading... " + anItemName;
			platypusApplicationLogger.log(Level.INFO, message);
		}

		@Override
		public void loaded(String anItemName) {
			final String message = anItemName + " - Loaded";
			platypusApplicationLogger.log(Level.INFO, message);
		}
	}

	public static Logger platypusApplicationLogger;
	protected static Map<String, Query> appQueries = new HashMap<String, Query>();
	protected static Loader loader;
	protected static GroupingHandlerRegistration loaderHandlerRegistration = new GroupingHandlerRegistration();

	public static Query getAppQuery(String aQueryId) {
		Query query = appQueries.get(aQueryId);
		if (query != null) {
			AppClient client = query.getClient();
			query = query.copy();
			query.setClient(client);
		}
		return query;
	}

	public static Query putAppQuery(Query aQuery) {
		return appQueries.put(aQuery.getEntityName(), aQuery);
	}
	
	public static native boolean isCacheBustEnabled() /*-{
		return !!$wnd.P.cacheBust;
	}-*/;
	
	public static native JavaScriptObject createReport(String reportLocation)/*-{
		return new $wnd.P.Report(reportLocation);
	}-*/;

	/*
	 * protected static class ExecuteApplicationCallback extends RunnableAdapter
	 * {
	 * 
	 * protected String startForm; protected Set<Element> indicators;
	 * 
	 * public ExecuteApplicationCallback(String aStartForm, Set<Element>
	 * aIndicators) { super(); startForm = aStartForm; indicators = aIndicators;
	 * }
	 * 
	 * @Override protected void doWork() throws Exception { for (Element el :
	 * indicators) { el.<XElement> cast().unmask(); }
	 * loaderHandlerRegistration.removeHandler(); onReady(); } }
	 */

	/**
	 * This method is publicONLY because of tests!
	 * 
	 * @param aClient
	 * @throws Exception
	 */
	public native static void publish(AppClient aClient) throws Exception /*-{
	
		// Fix Function#name on browsers that do not support it (IE):
		if (!(function f() {}).name) {
		    Object.defineProperty($wnd.Function.prototype, 'name', {
		        get: function() {
		            var name = this.toString().match(/function\s*(\S*)\s*\(/)[1];
		            // For better performance only parse once, and then cache the
		            // result through a new accessor for repeated access.
		            Object.defineProperty(this, 'name', { value: name });
		            return name;
		        }
		    });
		}
	
	    // this === global;
	    var global = $wnd;
	    if(!global.P){
	        var oldP = global.P;
	        global.P = {};
	        global.P.restore = function() {
	            var ns = global.P;
	            global.P = oldP;
	            return ns;
	        };
	        //global.P = this; // global scope of api - for legacy applications
	        //global.P.restore = function() {
	        //throw 'Legacy API can not restore the global namespace.';
	        //};
	    }
		
		function invokeLater(aTarget) {
			@com.eas.client.Utils::invokeLater(Lcom/google/gwt/core/client/JavaScriptObject;)(aTarget);
		}
		
        Object.defineProperty($wnd.P, "invokeLater", {get: function () {
            return invokeLater;
        }});
        
		function invokeDelayed(aTimeout, aTarget) {
		    if (arguments.length < 2)
		        throw "invokeDelayed needs 2 arguments - timeout, callback.";
			@com.eas.client.Utils::invokeScheduled(ILcom/google/gwt/core/client/JavaScriptObject;)(+aTimeout, aTarget);
		}
		
        Object.defineProperty($wnd.P, "invokeDelayed", {get: function () {
            return invokeDelayed;
        }});
        
		function selectFile(aCallback, aFileFilter) {
			@com.eas.client.form.ControlsUtils::jsSelectFile(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(aCallback, aFileFilter);
		}
		
        Object.defineProperty($wnd.P, "selectFile", {get: function () {
            return selectFile;
        }});
        
		function selectColor(aCallback, aOldValue) {
			@com.eas.client.form.ControlsUtils::jsSelectColor(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aOldValue != null ? aOldValue + '' : '', aCallback);
		}
		
        Object.defineProperty($wnd.P, "selectColor", {get: function () {
            return selectColor;
        }});
        
		var Resource = {};
		Object.defineProperty(Resource, "upload", {get : function(){
				return function(aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback) {
					return @com.eas.client.application.AppClient::jsUpload(Lcom/eas/client/published/PublishedFile;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback);
				}
		}});
		Object.defineProperty(Resource, "load", {get : function(){
	        return function(aResName, onSuccess, onFailure){
            	var loaded = $wnd.P.boxAsJs(@com.eas.client.application.AppClient::jsLoad(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aResName, onSuccess, onFailure));
            	if(loaded)
            		loaded.length = loaded.byteLength; 
            	return loaded;
	        };
		}});
		Object.defineProperty(Resource, "loadText", {get : function(){
	        return function(aResName, onSuccess, onFailure){
            	return $wnd.P.boxAsJs(@com.eas.client.application.AppClient::jsLoadText(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aResName, onSuccess, onFailure));
	        };
		}});
		
        Object.defineProperty($wnd.P, "Resource", {get: function () {
            return Resource;
        }});
        
		var principal = {};
		
		Object.defineProperty(principal, "name", {get: function(){
			var appClient = @com.eas.client.application.AppClient::getInstance()();
			return '' + appClient.@com.eas.client.application.AppClient::getPrincipal()();
		}});
		Object.defineProperty(principal, "hasRole", {value: function(){
			return true;
		}});
		Object.defineProperty(principal, "logout", {value: function(onSuccess, onFailure){
			var appClient = @com.eas.client.application.AppClient::getInstance()();
			return appClient.@com.eas.client.application.AppClient::jsLogout(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
		}});
		Object.defineProperty($wnd.P, "principal", {value: principal});
		// legacy API
		$wnd.P.logout = function(onSuccess, onFailure) {
			return principal.logout(onSuccess, onFailure);
		}
		
		$wnd.P.getElementComputedStyle = function(_elem) {
			if (typeof _elem.currentStyle != 'undefined'){
				return _elem.currentStyle; 
		    } else {
		    	return document.defaultView.getComputedStyle(_elem, null); 
		    }			
		}
	
		$wnd.P.boxAsJs = function(aValue) {
			if(aValue == null)
				return null;
			else if(@com.eas.client.Utils::isNumber(Ljava/lang/Object;)(aValue))
				return aValue.@java.lang.Number::doubleValue()();
			else if(@com.eas.client.Utils::isBoolean(Ljava/lang/Object;)(aValue))
				return aValue.@java.lang.Boolean::booleanValue()();
			else // dates, strings, complex java objects handled in Utils.toJs()
				return aValue;
		}
		
		$wnd.P.boxAsJava = function (aValue) {
			var valueType = typeof(aValue);
			if(valueType == "string")
				return aValue;
			else if(valueType == "number")
				return @java.lang.Double::new(D)(aValue);
			else if(valueType == "boolean")
				return @java.lang.Boolean::new(Z)(aValue);
			else // dates, strings, complex js objects handled in Utils.toJava() 
				return aValue;
		}
		
	    function extend(Child, Parent) {
	        var prevChildProto = {};
	        for (var m in Child.prototype) {
	            var member = Child.prototype[m];
	            if (typeof member === 'function') {
	                prevChildProto[m] = member;
	            }
	        }
	        var F = function() {
	        };
	        F.prototype = Parent.prototype;
	        Child.prototype = new F();
	        for (var m in prevChildProto)
	            Child.prototype[m] = prevChildProto[m];
	        Child.prototype.constructor = Child;
	        Child.superclass = Parent.prototype;
	    }
	    Object.defineProperty($wnd.P, "extend", {value: extend});
	        
        var hexcase = 0;   
        var b64pad  = "";  

        function hex_md5(s)    {
            return rstr2hex(rstr_md5(str2rstr_utf8(s)));
        }
        function b64_md5(s)    {
            return rstr2b64(rstr_md5(str2rstr_utf8(s)));
        }
        function any_md5(s, e) {
            return rstr2any(rstr_md5(str2rstr_utf8(s)), e);
        }
        function hex_hmac_md5(k, d)
        {
            return rstr2hex(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)));
        }
        function b64_hmac_md5(k, d)
        {
            return rstr2b64(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)));
        }
        function any_hmac_md5(k, d, e)
        {
            return rstr2any(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)), e);
        }

        function md5_vm_test()
        {
            return hex_md5("abc").toLowerCase() == "900150983cd24fb0d6963f7d28e17f72";
        }

        function rstr_md5(s)
        {
            return binl2rstr(binl_md5(rstr2binl(s), s.length * 8));
        }

        function rstr_hmac_md5(key, data)
        {
            var bkey = rstr2binl(key);
            if(bkey.length > 16) bkey = binl_md5(bkey, key.length * 8);

            var ipad = Array(16), opad = Array(16);
            for(var i = 0; i < 16; i++)
            {
                ipad[i] = bkey[i] ^ 0x36363636;
                opad[i] = bkey[i] ^ 0x5C5C5C5C;
            }

            var hash = binl_md5(ipad.concat(rstr2binl(data)), 512 + data.length * 8);
            return binl2rstr(binl_md5(opad.concat(hash), 512 + 128));
        }

        function rstr2hex(input)
        {
            try {
                hexcase
            } catch(e) {
                hexcase=0;
            }
            var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
            var output = "";
            var x;
            for(var i = 0; i < input.length; i++)
            {
                x = input.charCodeAt(i);
                output += hex_tab.charAt((x >>> 4) & 0x0F)
                +  hex_tab.charAt( x        & 0x0F);
            }
            return output;
        }

        function rstr2b64(input)
        {
            try {
                b64pad
            } catch(e) {
                b64pad='';
            }
            var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
            var output = "";
            var len = input.length;
            for(var i = 0; i < len; i += 3)
            {
                var triplet = (input.charCodeAt(i) << 16)
                | (i + 1 < len ? input.charCodeAt(i+1) << 8 : 0)
                | (i + 2 < len ? input.charCodeAt(i+2)      : 0);
                for(var j = 0; j < 4; j++)
                {
                    if(i * 8 + j * 6 > input.length * 8) output += b64pad;
                    else output += tab.charAt((triplet >>> 6*(3-j)) & 0x3F);
                }
            }
            return output;
        }

        function rstr2any(input, encoding)
        {
            var divisor = encoding.length;
            var i, j, q, x, quotient;

            var dividend = Array(Math.ceil(input.length / 2));
            for(i = 0; i < dividend.length; i++)
            {
                dividend[i] = (input.charCodeAt(i * 2) << 8) | input.charCodeAt(i * 2 + 1);
            }

            var full_length = Math.ceil(input.length * 8 /
                (Math.log(encoding.length) / Math.log(2)));
            var remainders = Array(full_length);
            for(j = 0; j < full_length; j++)
            {
                quotient = Array();
                x = 0;
                for(i = 0; i < dividend.length; i++)
                {
                    x = (x << 16) + dividend[i];
                    q = Math.floor(x / divisor);
                    x -= q * divisor;
                    if(quotient.length > 0 || q > 0)
                        quotient[quotient.length] = q;
                }
                remainders[j] = x;
                dividend = quotient;
            }

            var output = "";
            for(i = remainders.length - 1; i >= 0; i--)
                output += encoding.charAt(remainders[i]);

            return output;
        }

        function str2rstr_utf8(input)
        {
            var output = "";
            var i = -1;
            var x, y;

            while(++i < input.length)
            {
                x = input.charCodeAt(i);
                y = i + 1 < input.length ? input.charCodeAt(i + 1) : 0;
                if(0xD800 <= x && x <= 0xDBFF && 0xDC00 <= y && y <= 0xDFFF)
                {
                    x = 0x10000 + ((x & 0x03FF) << 10) + (y & 0x03FF);
                    i++;
                }

                if(x <= 0x7F)
                    output += String.fromCharCode(x);
                else if(x <= 0x7FF)
                    output += String.fromCharCode(0xC0 | ((x >>> 6 ) & 0x1F),
                        0x80 | ( x         & 0x3F));
                else if(x <= 0xFFFF)
                    output += String.fromCharCode(0xE0 | ((x >>> 12) & 0x0F),
                        0x80 | ((x >>> 6 ) & 0x3F),
                        0x80 | ( x         & 0x3F));
                else if(x <= 0x1FFFFF)
                    output += String.fromCharCode(0xF0 | ((x >>> 18) & 0x07),
                        0x80 | ((x >>> 12) & 0x3F),
                        0x80 | ((x >>> 6 ) & 0x3F),
                        0x80 | ( x         & 0x3F));
            }
            return output;
        }

        function str2rstr_utf16le(input)
        {
            var output = "";
            for(var i = 0; i < input.length; i++)
                output += String.fromCharCode( input.charCodeAt(i)        & 0xFF,
                    (input.charCodeAt(i) >>> 8) & 0xFF);
            return output;
        }

        function str2rstr_utf16be(input)
        {
            var output = "";
            for(var i = 0; i < input.length; i++)
                output += String.fromCharCode((input.charCodeAt(i) >>> 8) & 0xFF,
                    input.charCodeAt(i)        & 0xFF);
            return output;
        }

        function rstr2binl(input)
        {
            var output = Array(input.length >> 2);
            for(var i = 0; i < output.length; i++)
                output[i] = 0;
            for(var i = 0; i < input.length * 8; i += 8)
                output[i>>5] |= (input.charCodeAt(i / 8) & 0xFF) << (i%32);
            return output;
        }

        function binl2rstr(input)
        {
            var output = "";
            for(var i = 0; i < input.length * 32; i += 8)
                output += String.fromCharCode((input[i>>5] >>> (i % 32)) & 0xFF);
            return output;
        }

        function binl_md5(x, len)
        {
            x[len >> 5] |= 0x80 << ((len) % 32);
            x[(((len + 64) >>> 9) << 4) + 14] = len;

            var a =  1732584193;
            var b = -271733879;
            var c = -1732584194;
            var d =  271733878;

            for(var i = 0; i < x.length; i += 16)
            {
                var olda = a;
                var oldb = b;
                var oldc = c;
                var oldd = d;

                a = md5_ff(a, b, c, d, x[i+ 0], 7 , -680876936);
                d = md5_ff(d, a, b, c, x[i+ 1], 12, -389564586);
                c = md5_ff(c, d, a, b, x[i+ 2], 17,  606105819);
                b = md5_ff(b, c, d, a, x[i+ 3], 22, -1044525330);
                a = md5_ff(a, b, c, d, x[i+ 4], 7 , -176418897);
                d = md5_ff(d, a, b, c, x[i+ 5], 12,  1200080426);
                c = md5_ff(c, d, a, b, x[i+ 6], 17, -1473231341);
                b = md5_ff(b, c, d, a, x[i+ 7], 22, -45705983);
                a = md5_ff(a, b, c, d, x[i+ 8], 7 ,  1770035416);
                d = md5_ff(d, a, b, c, x[i+ 9], 12, -1958414417);
                c = md5_ff(c, d, a, b, x[i+10], 17, -42063);
                b = md5_ff(b, c, d, a, x[i+11], 22, -1990404162);
                a = md5_ff(a, b, c, d, x[i+12], 7 ,  1804603682);
                d = md5_ff(d, a, b, c, x[i+13], 12, -40341101);
                c = md5_ff(c, d, a, b, x[i+14], 17, -1502002290);
                b = md5_ff(b, c, d, a, x[i+15], 22,  1236535329);

                a = md5_gg(a, b, c, d, x[i+ 1], 5 , -165796510);
                d = md5_gg(d, a, b, c, x[i+ 6], 9 , -1069501632);
                c = md5_gg(c, d, a, b, x[i+11], 14,  643717713);
                b = md5_gg(b, c, d, a, x[i+ 0], 20, -373897302);
                a = md5_gg(a, b, c, d, x[i+ 5], 5 , -701558691);
                d = md5_gg(d, a, b, c, x[i+10], 9 ,  38016083);
                c = md5_gg(c, d, a, b, x[i+15], 14, -660478335);
                b = md5_gg(b, c, d, a, x[i+ 4], 20, -405537848);
                a = md5_gg(a, b, c, d, x[i+ 9], 5 ,  568446438);
                d = md5_gg(d, a, b, c, x[i+14], 9 , -1019803690);
                c = md5_gg(c, d, a, b, x[i+ 3], 14, -187363961);
                b = md5_gg(b, c, d, a, x[i+ 8], 20,  1163531501);
                a = md5_gg(a, b, c, d, x[i+13], 5 , -1444681467);
                d = md5_gg(d, a, b, c, x[i+ 2], 9 , -51403784);
                c = md5_gg(c, d, a, b, x[i+ 7], 14,  1735328473);
                b = md5_gg(b, c, d, a, x[i+12], 20, -1926607734);

                a = md5_hh(a, b, c, d, x[i+ 5], 4 , -378558);
                d = md5_hh(d, a, b, c, x[i+ 8], 11, -2022574463);
                c = md5_hh(c, d, a, b, x[i+11], 16,  1839030562);
                b = md5_hh(b, c, d, a, x[i+14], 23, -35309556);
                a = md5_hh(a, b, c, d, x[i+ 1], 4 , -1530992060);
                d = md5_hh(d, a, b, c, x[i+ 4], 11,  1272893353);
                c = md5_hh(c, d, a, b, x[i+ 7], 16, -155497632);
                b = md5_hh(b, c, d, a, x[i+10], 23, -1094730640);
                a = md5_hh(a, b, c, d, x[i+13], 4 ,  681279174);
                d = md5_hh(d, a, b, c, x[i+ 0], 11, -358537222);
                c = md5_hh(c, d, a, b, x[i+ 3], 16, -722521979);
                b = md5_hh(b, c, d, a, x[i+ 6], 23,  76029189);
                a = md5_hh(a, b, c, d, x[i+ 9], 4 , -640364487);
                d = md5_hh(d, a, b, c, x[i+12], 11, -421815835);
                c = md5_hh(c, d, a, b, x[i+15], 16,  530742520);
                b = md5_hh(b, c, d, a, x[i+ 2], 23, -995338651);

                a = md5_ii(a, b, c, d, x[i+ 0], 6 , -198630844);
                d = md5_ii(d, a, b, c, x[i+ 7], 10,  1126891415);
                c = md5_ii(c, d, a, b, x[i+14], 15, -1416354905);
                b = md5_ii(b, c, d, a, x[i+ 5], 21, -57434055);
                a = md5_ii(a, b, c, d, x[i+12], 6 ,  1700485571);
                d = md5_ii(d, a, b, c, x[i+ 3], 10, -1894986606);
                c = md5_ii(c, d, a, b, x[i+10], 15, -1051523);
                b = md5_ii(b, c, d, a, x[i+ 1], 21, -2054922799);
                a = md5_ii(a, b, c, d, x[i+ 8], 6 ,  1873313359);
                d = md5_ii(d, a, b, c, x[i+15], 10, -30611744);
                c = md5_ii(c, d, a, b, x[i+ 6], 15, -1560198380);
                b = md5_ii(b, c, d, a, x[i+13], 21,  1309151649);
                a = md5_ii(a, b, c, d, x[i+ 4], 6 , -145523070);
                d = md5_ii(d, a, b, c, x[i+11], 10, -1120210379);
                c = md5_ii(c, d, a, b, x[i+ 2], 15,  718787259);
                b = md5_ii(b, c, d, a, x[i+ 9], 21, -343485551);

                a = safe_add(a, olda);
                b = safe_add(b, oldb);
                c = safe_add(c, oldc);
                d = safe_add(d, oldd);
            }
            return Array(a, b, c, d);
        }

        function md5_cmn(q, a, b, x, s, t)
        {
            return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s),b);
        }
        function md5_ff(a, b, c, d, x, s, t)
        {
            return md5_cmn((b & c) | ((~b) & d), a, b, x, s, t);
        }
        function md5_gg(a, b, c, d, x, s, t)
        {
            return md5_cmn((b & d) | (c & (~d)), a, b, x, s, t);
        }
        function md5_hh(a, b, c, d, x, s, t)
        {
            return md5_cmn(b ^ c ^ d, a, b, x, s, t);
        }
        function md5_ii(a, b, c, d, x, s, t)
        {
            return md5_cmn(c ^ (b | (~d)), a, b, x, s, t);
        }

        function safe_add(x, y)
        {
            var lsw = (x & 0xFFFF) + (y & 0xFFFF);
            var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
            return (msw << 16) | (lsw & 0xFFFF);
        }

        function bit_rol(num, cnt)
        {
            return (num << cnt) | (num >>> (32 - cnt));
        }

		$wnd.P.loadModel = function(appElementName, aTarget) {
			if(!aTarget)
				aTarget = {};
			var appElementDoc = aClient.@com.eas.client.application.AppClient::getModelDocument(Ljava/lang/String;)(appElementName);
			var nativeModel = @com.eas.client.model.store.XmlDom2Model::transform(Lcom/google/gwt/xml/client/Document;Lcom/google/gwt/core/client/JavaScriptObject;)(appElementDoc, aTarget);
			nativeModel.@com.eas.client.model.Model::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(aTarget);			
			return aTarget;
		};
		$wnd.P.loadForm = function(appElementName, aModel, aTarget) {
			var appElementDoc = aClient.@com.eas.client.application.AppClient::getFormDocument(Ljava/lang/String;)(appElementName);
			var factory = @com.eas.client.form.store.XmlDom2Form::transform(Lcom/google/gwt/xml/client/Document;Lcom/google/gwt/core/client/JavaScriptObject;)(appElementDoc, aModel);
			var nativeForm = factory.@com.eas.client.form.FormFactory::getForm()(); 
			if(aTarget)
				$wnd.P.Form.call(aTarget, null, appElementName, nativeForm);
			else
				aTarget = new $wnd.P.Form(null, appElementName, nativeForm);
			if(!aTarget.title)
				aTarget.title = appElementName;
			var nwList = factory.@com.eas.client.form.FormFactory::getWidgetsList()();
			for(var i = 0; i < nwList.@java.util.List::size()(); i++){
				var nWidget = nwList.@java.util.List::get(I)(i);
				var pWidget = nWidget.@com.eas.client.form.published.HasPublished::getPublished()();
				if(pWidget.name)
					aTarget[pWidget.name] = pWidget;
			}
			return aTarget;
		};
		$wnd.P.HTML5 = "Html5 client";
		$wnd.P.J2SE = "Java SE client";
		$wnd.P.agent = $wnd.P.HTML5; 
		var cachedModules = {};
		Object.defineProperty($wnd.P, "modules", {
			get : function(){
				return cachedModules;
			}
		});
		function Form(aView, aFormKey){
			var aComponent = arguments.length > 2 ? arguments[2] : null;

			var published = this;
			if(!aComponent){ 
				if(aView)
					aComponent = @com.eas.client.form.PlatypusWindow::new(Lcom/google/gwt/user/client/ui/Widget;)(aView.unwrap());
				else
					aComponent = @com.eas.client.form.PlatypusWindow::new()();
			} 	
			published.unwrap = function() {
				return aComponent;
			};
			if(aFormKey){
				aComponent.@com.eas.client.form.PlatypusWindow::setFormKey(Ljava/lang/String;)(aFormKey);
			}
			aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		}
		$wnd.P.Form = Form;
		$wnd.P.Form.getShownForm = function(aFormKey){
			return @com.eas.client.form.PlatypusWindow::getShownForm(Ljava/lang/String;)(aFormKey);
		};
		function Report(reportLocation){
			function download(aName){
				var a = $doc.createElement('a');
				a.download = "";
				if(aName)
					a.download = aName;
				a.style.display = 'none';
				a.style.visibility = 'hidden';
				a.href = reportLocation;
				$doc.body.appendChild(a);
				a.click();
				$doc.body.removeChild(a);
			}
			this.show = function(){
				download();
			};
			this.print = function(){
				download();
			};
			this.save = function(aName){
				download(aName);
			};
		}
		$wnd.P.Report = Report;
		
		function generateFunction(aModuleName, aFunctionName) {
			return function() {
				var onSuccess = null;
				var onFailure = null;
				var argsLength = arguments.length;
				if(arguments.length > 1 && typeof arguments[arguments.length - 1] == "function" && typeof arguments[arguments.length - 2] == "function"){
					onSuccess = arguments[arguments.length - 2];
					onFailure = arguments[arguments.length - 1];
					argsLength -= 2;
				}else if(arguments.length > 1 && typeof arguments[arguments.length - 1] == "undefined" && typeof arguments[arguments.length - 2] == "function"){
					onSuccess = arguments[arguments.length - 2];
					argsLength -= 2;
				}else if(arguments.length > 0 && typeof arguments[arguments.length - 1] == "function"){
					onSuccess = arguments[arguments.length - 1];
					argsLength -= 1;
				}
				var params = [];
				for (var j = 0; j < argsLength; j++) {
					var to = typeof arguments[j];
					if(to !== 'undefined' && to !== 'function'){ 
						params[j] = JSON.stringify(arguments[j]);
					}else{
						break;
					}
				}
				var nativeClient = @com.eas.client.application.AppClient::getInstance()();
				if(onSuccess) {
					nativeClient.@com.eas.client.application.AppClient::requestServerMethodExecution(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aFunctionName, params,
						function(aResult){
							if(typeof aResult === 'object' && aResult instanceof Report)
								onSuccess(aResult);
							else
								onSuccess(JSON.parse(aResult, @com.eas.client.Utils.JsObject::dateReviver()()));
						}, onFailure);
				} else {
					var result = nativeClient.@com.eas.client.application.AppClient::requestServerMethodExecution(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aFunctionName, params, null, null)
					return typeof result === 'object' && result instanceof Report ? result : JSON.parse(result, @com.eas.client.Utils.JsObject::dateReviver()()); 
				}
			};
		}
	    $wnd.P.serverModules = {};
		$wnd.P.ServerModule = function(aModuleName){
			if(!(this instanceof $wnd.P.ServerModule))
				throw 'use P.ServerModule(...) please.';
			var moduleData = $wnd.P.serverModules[aModuleName];
			if(!moduleData)
				throw 'No server module proxy for module: ' + aModuleName;
			if(!moduleData.isPermitted)
				throw 'Access to server module ' + aModuleName + ' is not permitted.';
			var self = this;
			for (var i = 0; i < moduleData.functions.length; i++) {
				var funcName = moduleData.functions[i];
				self[funcName] = generateFunction(aModuleName, funcName);
			}
		};
		Object.defineProperty($wnd.P.Form, "shown", {
			get : function() {
				return @com.eas.client.form.PlatypusWindow::getShownForms()();
			}
		});
		Object.defineProperty($wnd.P.Form, "onChange", {
			get : function() {
				return @com.eas.client.form.PlatypusWindow::getOnChange()();
			},
			set : function(aValue) {
				@com.eas.client.form.PlatypusWindow::setOnChange(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		$wnd.P.require = function (aDeps, aOnSuccess, aOnFailure) {
			var deps = Array.isArray(aDeps) ? aDeps : [aDeps];
			@com.eas.client.application.Application::require(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(deps, aOnSuccess, aOnFailure);
		} 
		function _Icons() {
			var _self = this;
			Object.defineProperty(_self, "load", { 
				value: function(aIconName, aOnSuccess, aOnFailure) {
					@com.eas.client.application.PlatypusImageResource::jsLoad(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aIconName != null ? '' + aIconName : null, aOnSuccess, aOnFailure);
				} 
			});
		}
		$wnd.P.Icon = new _Icons();
		$wnd.P.Icons = $wnd.P.Icon;
		function _Color(aRed, aGreen, aBlue, aAlpha){
			var _red = 0, _green = 0, _blue = 0, _alpha = 0xff;
			if(arguments.length == 1){
				var _color = @com.eas.client.form.ControlsUtils::parseColor(Ljava/lang/String;)(aRed + '');
				if(_color){
					_red = _color.red;
					_green = _color.green;
					_blue = _color.blue;
				}else
					throw "String like \"#cfcfcf\" is expected.";
			}else if(arguments.length >= 3){
				if(aRed)
					_red = aRed;
				if(aGreen)
					_green = aGreen;
				if(aBlue)
					_blue = aBlue;
				if(aAlpha)
					_alpha = aAlpha;
			}else{
				throw "String like \"#cfcfcf\" or three color components with optional alpha is expected.";
			}
			var _self = this;
			Object.defineProperty(_self, "red", { get:function(){ return _red;} });
			Object.defineProperty(_self, "green", { get:function(){ return _green;} });
			Object.defineProperty(_self, "blue", { get:function(){ return _blue;} });
			Object.defineProperty(_self, "alpha", { get:function(){ return _alpha;} });
			_self.toStyled = function(){
				return "rgba("+_self.red+","+_self.green+","+_self.blue+","+_self.alpha/255.0+")"; 
			}
			_self.toString = function(){
				var sred = (new Number(_self.red)).toString(16);
				if(sred.length == 1)
					sred = "0"+sred;
				var sgreen = (new Number(_self.green)).toString(16);
				if(sgreen.length == 1)
					sgreen = "0"+sgreen;
				var sblue = (new Number(_self.blue)).toString(16);
				if(sblue.length == 1)
					sblue = "0"+sblue;
				return "#"+sred+sgreen+sblue;
			}
		}; 
		$wnd.P.Color = _Color;
		$wnd.P.Color.black = new $wnd.P.Color(0,0,0);
		$wnd.P.Color.BLACK = new $wnd.P.Color(0,0,0);
		$wnd.P.Color.blue = new $wnd.P.Color(0,0,0xff);
		$wnd.P.Color.BLUE = new $wnd.P.Color(0,0,0xff);
		$wnd.P.Color.cyan = new $wnd.P.Color(0,0xff,0xff);
		$wnd.P.Color.CYAN = new $wnd.P.Color(0,0xff,0xff);
		$wnd.P.Color.DARK_GRAY = new $wnd.P.Color(0x40, 0x40, 0x40);
		$wnd.P.Color.darkGray = new $wnd.P.Color(0x40, 0x40, 0x40);
		$wnd.P.Color.gray = new $wnd.P.Color(0x80, 0x80, 0x80);
		$wnd.P.Color.GRAY = new $wnd.P.Color(0x80, 0x80, 0x80);
		$wnd.P.Color.green = new $wnd.P.Color(0, 0xff, 0);
		$wnd.P.Color.GREEN = new $wnd.P.Color(0, 0xff, 0);
		$wnd.P.Color.LIGHT_GRAY = new $wnd.P.Color(0xC0, 0xC0, 0xC0);
		$wnd.P.Color.lightGray = new $wnd.P.Color(0xC0, 0xC0, 0xC0);
		$wnd.P.Color.magenta = new $wnd.P.Color(0xff, 0, 0xff);
		$wnd.P.Color.MAGENTA = new $wnd.P.Color(0xff, 0, 0xff);
		$wnd.P.Color.orange = new $wnd.P.Color(0xff, 0xC8, 0);
		$wnd.P.Color.ORANGE = new $wnd.P.Color(0xff, 0xC8, 0);
		$wnd.P.Color.pink = new $wnd.P.Color(0xFF, 0xAF, 0xAF);
		$wnd.P.Color.PINK = new $wnd.P.Color(0xFF, 0xAF, 0xAF);
		$wnd.P.Color.red = new $wnd.P.Color(0xFF, 0, 0);
		$wnd.P.Color.RED = new $wnd.P.Color(0xFF, 0, 0);
		$wnd.P.Color.white = new $wnd.P.Color(0xFF, 0xff, 0xff);
		$wnd.P.Color.WHITE = new $wnd.P.Color(0xFF, 0xff, 0xff);
		$wnd.P.Color.yellow = new $wnd.P.Color(0xFF, 0xff, 0);
		$wnd.P.Color.YELLOW = new $wnd.P.Color(0xFF, 0xff, 0);
		$wnd.P.Colors = $wnd.P.Color;
		
		function _Font(aFamily, aStyle, aSize){
			var _self = this;
			Object.defineProperty(_self, "family", { get:function(){ return aFamily;} });
			Object.defineProperty(_self, "style", { get:function(){ return aStyle;} });
			Object.defineProperty(_self, "size", { get:function(){ return aSize;} });			
		}; 
		$wnd.P.Font = _Font;
		$wnd.P.Cursor = {
		    CROSSHAIR : "crosshair",
		    DEFAULT : "default",
		    AUTO : "auto",
		    E_RESIZE : "e-resize",
		    // help ?
		    // progress ?
		    HAND : "pointer",
		    MOVE : "move",
		    NE_RESIZE : "ne-resize",
		    NW_RESIZE : "nw-resize",
		    N_RESIZE : "n-resize",
		    SE_RESIZE : "se-resize",
		    SW_RESIZE : "sw-resize",
		    S_RESIZE : "s-resize",
		    TEXT : "text",
		    WAIT : "wait",
		    W_RESIZE : "w-resize"
		};
		$wnd.P.IDGenerator = {
			genID : function(){
				return @com.eas.client.IDGenerator::genId()();
			}
		};
		$wnd.P.MD5Generator = {
			generate : function(aSource){
				return hex_md5(aSource).toLowerCase();
			}
		};
		$wnd.P.MD5 = {
			generate : function(aSource){
				return hex_md5(aSource).toLowerCase();
			}
		};
		$wnd.P.ID = {
			generate : function(){
				return @com.eas.client.IDGenerator::genId()();
			}
		};
		$wnd.P.Logger = new (function(){
			var nativeLogger = @com.eas.client.application.Application::platypusApplicationLogger;
			this.severe = function(aMessage){
				nativeLogger.@java.util.logging.Logger::severe(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			this.warning = function(aMessage){
				nativeLogger.@java.util.logging.Logger::warning(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			this.info = function(aMessage){
				nativeLogger.@java.util.logging.Logger::info(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			this.fine = function(aMessage){
				nativeLogger.@java.util.logging.Logger::fine(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			this.finer = function(aMessage){
				nativeLogger.@java.util.logging.Logger::finer(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			this.finest = function(aMessage){
				nativeLogger.@java.util.logging.Logger::finest(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
		})();
		
	    $wnd.P.VK_ALT = @com.google.gwt.event.dom.client.KeyCodes::KEY_ALT;
	    $wnd.P.VK_BACKSPACE = @com.google.gwt.event.dom.client.KeyCodes::KEY_BACKSPACE;
	    $wnd.P.VK_BACKSPACE = @com.google.gwt.event.dom.client.KeyCodes::KEY_BACKSPACE;
	    $wnd.P.VK_DELETE = @com.google.gwt.event.dom.client.KeyCodes::KEY_DELETE;
	    $wnd.P.VK_DOWN = @com.google.gwt.event.dom.client.KeyCodes::KEY_DOWN;
	    $wnd.P.VK_END = @com.google.gwt.event.dom.client.KeyCodes::KEY_END;
	    $wnd.P.VK_ENTER = @com.google.gwt.event.dom.client.KeyCodes::KEY_ENTER;
	    $wnd.P.VK_ESCAPE = @com.google.gwt.event.dom.client.KeyCodes::KEY_ESCAPE;
	    $wnd.P.VK_HOME = @com.google.gwt.event.dom.client.KeyCodes::KEY_HOME;
	    $wnd.P.VK_LEFT = @com.google.gwt.event.dom.client.KeyCodes::KEY_LEFT;
	    $wnd.P.VK_PAGEDOWN = @com.google.gwt.event.dom.client.KeyCodes::KEY_PAGEDOWN;
	    $wnd.P.VK_PAGEUP = @com.google.gwt.event.dom.client.KeyCodes::KEY_PAGEUP;
	    $wnd.P.VK_RIGHT = @com.google.gwt.event.dom.client.KeyCodes::KEY_RIGHT;
	    $wnd.P.VK_SHIFT = @com.google.gwt.event.dom.client.KeyCodes::KEY_SHIFT;
	    $wnd.P.VK_TAB = @com.google.gwt.event.dom.client.KeyCodes::KEY_TAB;
        $wnd.P.VK_UP = @com.google.gwt.event.dom.client.KeyCodes::KEY_UP;
	}-*/;

	public static void run() throws Exception {
		run(AppClient.getInstance());
	}

	public static void run(AppClient client) throws Exception {
		if (LogConfiguration.loggingIsEnabled()) {
			platypusApplicationLogger = Logger.getLogger("platypusApplication");
			Formatter f = new PlatypusLogFormatter(true);
			Handler[] handlers = Logger.getLogger("").getHandlers();
			for (Handler h : handlers) {
				h.setFormatter(f);
			}
		}
		publish(client);
		JsManaged.init();
		JsOrderer.init();
		JsWidgets.init();
		JsMenus.init();
		JsContainers.init();
		JsModelWidgets.init();
		JsEvents.init();
		loader = new Loader(client);
		loaderHandlerRegistration.add(loader.addHandler(new LoggingLoadHandler()));
		client.requestLoggedInUser(new CallbackAdapter<String, String>() {

			@Override
			protected void doWork(String aResult) throws Exception {
				onReady();
			}

			@Override
			public void onFailure(String reason) {
				onError(reason);
				Logger.getLogger(Application.class.getName()).log(Level.SEVERE, reason);
			}
		});
	}

	protected static native void onReady()/*-{
		if ($wnd.P.ready)
			$wnd.P.ready();
	}-*/;

	protected static native void onError(String aMessage)/*-{
		if ($wnd.P.onError)
			$wnd.P.onError(aMessage);
	}-*/;

	private static String toAppModuleId(String aRelative, String aStartPoint) {
		Element div = Document.get().createDivElement();
		div.setInnerHTML("<a href=\"" + aStartPoint + "/" + aRelative + "\">o</a>");
		String absolute = div.getFirstChildElement().<AnchorElement> cast().getHref();
		String hostContextPrefix = AppClient.relativeUri() + AppClient.APP_RESOURCE_PREFIX;
		String appModuleId = absolute.substring(hostContextPrefix.length());
		return appModuleId;
	}

	private static String extractFileName(StackTraceElement aFrame) {
		String fileName = aFrame.getFileName();
		if (fileName != null) {
			int atIndex = fileName.indexOf("@");
			if (atIndex != -1) {
				fileName = fileName.substring(0, atIndex);
			}
			return fileName;
		} else {
			return null;
		}
	}

	public static void require(final JavaScriptObject aDeps, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) {
		String calledFromDir = null;
		try {
			throw new Exception("test");
		} catch (Exception ex) {
			String calledFromFile = null;
			StackTraceElement[] stackFrames = ex.getStackTrace();
			String firstFileName = extractFileName(stackFrames[0]);
			if (firstFileName != null) {
				for (int frameIdx = 1; frameIdx < stackFrames.length; frameIdx++) {
					String fileName = extractFileName(stackFrames[frameIdx]);
					if (fileName != null && !fileName.equals(firstFileName)) {
						calledFromFile = fileName;
						break;
					}
				}
			}
			if (calledFromFile != null) {
				int lastSlashIndex = calledFromFile.lastIndexOf('/');
				calledFromDir = calledFromFile.substring(0, lastSlashIndex);
			}
		}
		final Set<String> deps = new HashSet<String>();
		JsArrayString depsValues = aDeps.<JsArrayString> cast();
		for (int i = 0; i < depsValues.length(); i++) {
			String dep = depsValues.get(i);
			if (calledFromDir != null && dep.startsWith("./") || dep.startsWith("../")) {
				String normalized = toAppModuleId(dep, calledFromDir);
				deps.add(normalized);
			} else {
				deps.add(dep);
			}
		}
		try {
			loader.load(deps, new CallbackAdapter<Void, String>() {

				@Override
				public void onFailure(String reason) {
					if (aOnFailure != null) {
						try {
							Utils.executeScriptEventString(aOnFailure, aOnFailure, reason);
						} catch (Exception ex) {
							Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
						}
					} else {
						Logger.getLogger(Application.class.getName()).log(Level.WARNING, "Require failed and callback is missing. Required modules are: " + aDeps.toString());
					}
				}

				@Override
				protected void doWork(Void aResult) throws Exception {
					if (aOnSuccess != null)
						Utils.invokeJsFunction(aOnSuccess);
					else
						Logger.getLogger(Application.class.getName()).log(Level.WARNING, "Require succeded, but callback is missing. Required modules are: " + aDeps.toString());
				}
			}, new HashSet<String>());
		} catch (Exception ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
