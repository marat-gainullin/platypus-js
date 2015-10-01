package com.eas.client.application.js;

public class JsApi {
	
	public native static void init()/*-{
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
		$wnd.require = function (aDeps, aOnSuccess, aOnFailure) {
            if (!Array.isArray(aDeps))
                aDeps = [aDeps];
			@com.eas.client.application.Application::require(Lcom/eas/client/Utils$JsObject;Lcom/eas/client/Utils$JsObject;Lcom/eas/client/Utils$JsObject;)(aDeps, aOnSuccess, aOnFailure);
		}; 
		$wnd.define = function () {
	        if (arguments.length === 1 ||
	                arguments.length === 2) {
	            var aDeps = arguments.length > 1 ? arguments[0] : [];
	            var aModuleDefiner = arguments.length > 1 ? arguments[1] : arguments[0];
	            if (!Array.isArray(aDeps))
	                aDeps = [aDeps];
	            @com.eas.client.application.Application::define(Lcom/eas/client/Utils$JsObject;Lcom/eas/client/Utils$JsObject;)(aDeps, function(){
                	return typeof aModuleDefiner === 'function' ? aModuleDefiner.apply(null, arguments) : aModuleDefiner;
	            });
	        } else {
	            throw 'Module definition arguments mismatch';
	        }
		};
		
		function predefine(aDeps, aName, aDefiner){
			var resolved = [];
			for(var d = 0; d < aDeps.length; d++){
				var module = @com.eas.client.application.Application::prerequire(Ljava/lang/String;)(aDeps[d]);
				resolved.push(module);
			}
			@com.eas.client.application.Application::predefine(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aName, aDefiner(resolved));
		}
		
		predefine([], 'orderer', function(){
			function Orderer(aKeysNames) {
			    var keyNames = aKeysNames.sort();
			    keyNames.forEach(function (aKeyName) {
			    });
			    function calcKey(anObject) {
			        var key = '';
			        keyNames.forEach(function (aKeyName) {
			            var datum = anObject[aKeyName];
			            if (key.length > 0)
			                key += ' | ';
			            key += datum instanceof Date ? JSON.stringify(datum) : '' + datum;
			        });
			        return key;
			    }
			
			    this.inKeys = function (aKeyName) {
			        return keyNames.indexOf(aKeyName) !== -1;
			    };
			
			    var map = {};
			    this.add = function (anObject) {
			        var key = calcKey(anObject);
			        var subset = map[key];
			        if (!subset) {
			            subset = new Set();
			            map[key] = subset;
			        }
			        subset.add(anObject);
			    };
			    this['delete'] = function (anObject) {
			        var key = calcKey(anObject);
			        var subset = map[key];
			        if (subset) {
			            subset['delete'](anObject);
			            if (subset.size === 0) {
			                delete map[key];
			            }
			        }
			    };
			    this.find = function (aCriteria) {
			        var key = calcKey(aCriteria);
			        var subset = map[key];
			        if (!subset) {
			            return [];
			        } else {
			            var found = [];
			            subset.forEach(function (item) {
			                found.push(item);
			            });
			            return found;
			        }
			    };
			}
			return Orderer;
		});
		
		predefine([], 'managed', function(){
		    var releaseName = '-platypus-orm-release-func';
		     // Substitutes properties of anObject with observable properties using Object.defineProperty().
		     // ES6 Object.observe() is not suitable for ordering/orm tasks
		     // because of asynchonous nature of its events.
		     // @param anObject An object to be reorganized.
		     // @param aOnChange a callback called on every change of properties.
		     // @returns anObject by pass for convinence.
		    function manageObject(anObject, aOnChange, aOnBeforeChange) {
		        if (!anObject[releaseName]) {
		            var container = {};
		            for (var p in anObject) {
		                container[p] = anObject[p];
		                (function () {
		                    var _p = p;
		                    Object.defineProperty(anObject, ('' + _p), {
		                        enumerable: true,
		                        configurable: true,
		                        get: function () {
		                            return container[_p];
		                        },
		                        set: function (aValue) {
		                            var _oldValue = container[_p];
		                            var _beforeState = null;
		                            if(aOnBeforeChange)
		                                _beforeState = aOnBeforeChange(anObject, {source: anObject, propertyName: _p, oldValue: _oldValue, newValue: aValue});
		                            container[_p] = aValue;
		                            aOnChange(anObject, {source: anObject, propertyName: _p, oldValue: _oldValue, newValue: aValue, beforeState: _beforeState});
		                        }
		                    });
		                })();
		            }
		            Object.defineProperty(anObject, releaseName, {
		                configurable: true,
		                value: function () {
		                    delete anObject[releaseName];
		                    for (var p in anObject) {
		                        var pValue = anObject[p];
		                        delete anObject[p];
		                        anObject[p] = pValue;
		                    }
		                }});
		        }
		        return {release: function () {
		                anObject[releaseName]();
		            }};
		    }
		
		    function unmanageObject(anObject) {
		        if (anObject[releaseName]) {
		            anObject[releaseName]();
		        }
		    }
		    function manageArray(aTarget, aOnChange) {
		        Object.defineProperty(aTarget, "pop", {
		            value: function () {
		                var popped = Array.prototype.pop.call(aTarget);
		                if (popped) {
		                    aOnChange.spliced([], [popped]);
		                    if (popped === cursor) {
		                        aTarget.cursor = aTarget.length > 0 ? aTarget[aTarget.length - 1] : null;
		                    }
		                }
		                return popped;
		            }
		        });
		        Object.defineProperty(aTarget, "shift", {
		            value: function () {
		                var shifted = Array.prototype.shift.call(aTarget);
		                if (shifted) {
		                    aOnChange.spliced([], [shifted]);
		                    if (shifted === cursor) {
		                        aTarget.cursor = aTarget.length > 0 ? aTarget[0] : null;
		                    }
		                }
		                return shifted;
		            }
		        });
		        Object.defineProperty(aTarget, "push", {
		            value: function () {
		                var newLength = Array.prototype.push.apply(aTarget, arguments);
		                var added = [];
		                for (var a = 0; a < arguments.length; a++) {
		                    added.push(arguments[a]);
		                }
		                aOnChange.spliced(added, []);
		                if (added.length > 0)
		                    aTarget.cursor = added[added.length - 1];
		                return newLength;
		            }
		        });
		        Object.defineProperty(aTarget, "unshift", {
		            value: function () {
		                var newLength = Array.prototype.unshift.apply(aTarget, arguments);
		                var added = [];
		                for (var a = 0; a < arguments.length; a++) {
		                    added.push(arguments[a]);
		                }
		                aOnChange.spliced(added, []);
		                if (added.length > 0)
		                    aTarget.cursor = added[added.length - 1];
		                return newLength;
		            }
		        });
		        Object.defineProperty(aTarget, "reverse", {
		            value: function () {
		                var reversed = Array.prototype.reverse.apply(aTarget);
		                if (aTarget.length > 0) {
		                    aOnChange.spliced([], []);
		                }
		                return reversed;
		            }
		        });
		        Object.defineProperty(aTarget, "sort", {
		            value: function () {
		                var sorted = Array.prototype.sort.apply(aTarget, arguments);
		                if (aTarget.length > 0) {
		                    aOnChange.spliced([], []);
		                }
		                return sorted;
		            }
		        });
		        Object.defineProperty(aTarget, "splice", {
		            value: function () {
		                var beginDeleteAt = arguments[0];
		                if(beginDeleteAt < 0)
		                    beginDeleteAt = aTarget.length - beginDeleteAt;
		                var deleted = Array.prototype.splice.apply(aTarget, arguments);
		                var added = [];
		                for (var a = 2; a < arguments.length; a++) {
		                    var aAdded = arguments[a];
		                    added.push(aAdded);
		                }
		                aOnChange.spliced(added, deleted);
		                if (added.length > 0) {
		                    aTarget.cursor = added[added.length - 1];
		                } else {
		                    if (deleted.indexOf(cursor) !== -1){
		                        if(beginDeleteAt >= 0 && beginDeleteAt < aTarget.length)
		                            aTarget.cursor = aTarget[beginDeleteAt];
		                        else if(beginDeleteAt - 1 >= 0 && beginDeleteAt - 1 < aTarget.length)
		                            aTarget.cursor = aTarget[beginDeleteAt - 1];
		                        else
		                            aTarget.cursor = null;
		                    }
		                }
		                return deleted;
		            }
		        });
		        var cursor = null;
		        Object.defineProperty(aTarget, 'cursor', {
		            get: function () {
		                return cursor;
		            },
		            set: function (aValue) {
		                if (cursor !== aValue) {
		                    var oldCursor = cursor;
		                    cursor = aValue;
		                    aOnChange.scrolled(aTarget, oldCursor, cursor);
		                }
		            }
		        });
		        return aTarget;
		    }
		    var module = {};
		    Object.defineProperty(module, 'manageObject', {
		    	enumerable: true,
		    	value: manageObject
		    });
		    Object.defineProperty(module, 'unmanageObject', {
		    	enumerable: true,
		    	value: unmanageObject
		    });
		    Object.defineProperty(module, 'manageArray', {
		    	enumerable: true,
		    	value: manageArray
		    });
		    return module;
		});
		
		predefine([], 'extend', function(){
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
		    return extend;
		});
		
		predefine([], 'boxing', function(){
			function boxAsJs(aValue) {
				if(aValue == null)
					return null;
				else if(@com.eas.client.Utils::isNumber(Ljava/lang/Object;)(aValue))
					return aValue.@java.lang.Number::doubleValue()();
				else if(@com.eas.client.Utils::isBoolean(Ljava/lang/Object;)(aValue))
					return aValue.@java.lang.Boolean::booleanValue()();
				else // dates, strings, complex java objects handled in Utils.toJs()
					return aValue;
			}
			
			function boxAsJava(aValue) {
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
			var module = {};
			Object.defineProperty(module, 'boxAsJs', {
		    	enumerable: true,
		    	value: boxAsJs
			});
			Object.defineProperty(module, 'boxAsJava', {
		    	enumerable: true,
		    	value: boxAsJava
			});
			return module;
		});
		
		predefine([], 'md5', function(){
	        var hexcase = 0;   
	        var b64pad  = "";  
	
	        function hex_md5(s)    {
	            return rstr2hex(rstr_md5(str2rstr_utf8(s)));
	        }
	        function b64_md5(s)    {
	            return rstr2b64(rstr_md5(str2rstr_utf8(s)));
	        }
	        function any_md5(s, e){
	            return rstr2any(rstr_md5(str2rstr_utf8(s)), e);
	        }
	        function hex_hmac_md5(k, d){
	            return rstr2hex(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)));
	        }
	        function b64_hmac_md5(k, d){
	            return rstr2b64(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)));
	        }
	        function any_hmac_md5(k, d, e){
	            return rstr2any(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)), e);
	        }
	
	        function md5_vm_test(){
	            return hex_md5("abc").toLowerCase() == "900150983cd24fb0d6963f7d28e17f72";
	        }
	
	        function rstr_md5(s){
	            return binl2rstr(binl_md5(rstr2binl(s), s.length * 8));
	        }
	
	        function rstr_hmac_md5(key, data){
	            var bkey = rstr2binl(key);
	            if(bkey.length > 16) bkey = binl_md5(bkey, key.length * 8);
	
	            var ipad = Array(16), opad = Array(16);
	            for(var i = 0; i < 16; i++){
	                ipad[i] = bkey[i] ^ 0x36363636;
	                opad[i] = bkey[i] ^ 0x5C5C5C5C;
	            }
	
	            var hash = binl_md5(ipad.concat(rstr2binl(data)), 512 + data.length * 8);
	            return binl2rstr(binl_md5(opad.concat(hash), 512 + 128));
	        }
	
	        function rstr2hex(input){
	            try {
	                hexcase
	            } catch(e) {
	                hexcase=0;
	            }
	            var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
	            var output = "";
	            var x;
	            for(var i = 0; i < input.length; i++){
	                x = input.charCodeAt(i);
	                output += hex_tab.charAt((x >>> 4) & 0x0F)
	                +  hex_tab.charAt( x        & 0x0F);
	            }
	            return output;
	        }
	
	        function rstr2b64(input){
	            try {
	                b64pad
	            } catch(e) {
	                b64pad='';
	            }
	            var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	            var output = "";
	            var len = input.length;
	            for(var i = 0; i < len; i += 3){
	                var triplet = (input.charCodeAt(i) << 16)
	                | (i + 1 < len ? input.charCodeAt(i+1) << 8 : 0)
	                | (i + 2 < len ? input.charCodeAt(i+2)      : 0);
	                for(var j = 0; j < 4; j++){
	                    if(i * 8 + j * 6 > input.length * 8) output += b64pad;
	                    else output += tab.charAt((triplet >>> 6*(3-j)) & 0x3F);
	                }
	            }
	            return output;
	        }
	
	        function rstr2any(input, encoding){
	            var divisor = encoding.length;
	            var i, j, q, x, quotient;
	
	            var dividend = Array(Math.ceil(input.length / 2));
	            for(i = 0; i < dividend.length; i++){
	                dividend[i] = (input.charCodeAt(i * 2) << 8) | input.charCodeAt(i * 2 + 1);
	            }
	
	            var full_length = Math.ceil(input.length * 8 /
	                (Math.log(encoding.length) / Math.log(2)));
	            var remainders = Array(full_length);
	            for(j = 0; j < full_length; j++){
	                quotient = Array();
	                x = 0;
	                for(i = 0; i < dividend.length; i++){
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
	
	        function str2rstr_utf8(input){
	            var output = "";
	            var i = -1;
	            var x, y;
	
	            while(++i < input.length){
	                x = input.charCodeAt(i);
	                y = i + 1 < input.length ? input.charCodeAt(i + 1) : 0;
	                if(0xD800 <= x && x <= 0xDBFF && 0xDC00 <= y && y <= 0xDFFF){
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
	
	        function str2rstr_utf16le(input){
	            var output = "";
	            for(var i = 0; i < input.length; i++)
	                output += String.fromCharCode( input.charCodeAt(i)        & 0xFF,
	                    (input.charCodeAt(i) >>> 8) & 0xFF);
	            return output;
	        }
	
	        function str2rstr_utf16be(input){
	            var output = "";
	            for(var i = 0; i < input.length; i++)
	                output += String.fromCharCode((input.charCodeAt(i) >>> 8) & 0xFF,
	                    input.charCodeAt(i)        & 0xFF);
	            return output;
	        }
	
	        function rstr2binl(input){
	            var output = Array(input.length >> 2);
	            for(var i = 0; i < output.length; i++)
	                output[i] = 0;
	            for(var i = 0; i < input.length * 8; i += 8)
	                output[i>>5] |= (input.charCodeAt(i / 8) & 0xFF) << (i%32);
	            return output;
	        }
	
	        function binl2rstr(input){
	            var output = "";
	            for(var i = 0; i < input.length * 32; i += 8)
	                output += String.fromCharCode((input[i>>5] >>> (i % 32)) & 0xFF);
	            return output;
	        }
	
	        function binl_md5(x, len){
	            x[len >> 5] |= 0x80 << ((len) % 32);
	            x[(((len + 64) >>> 9) << 4) + 14] = len;
	
	            var a =  1732584193;
	            var b = -271733879;
	            var c = -1732584194;
	            var d =  271733878;
	
	            for(var i = 0; i < x.length; i += 16){
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
	
	        function md5_cmn(q, a, b, x, s, t){
	            return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s),b);
	        }
	        function md5_ff(a, b, c, d, x, s, t){
	            return md5_cmn((b & c) | ((~b) & d), a, b, x, s, t);
	        }
	        function md5_gg(a, b, c, d, x, s, t){
	            return md5_cmn((b & d) | (c & (~d)), a, b, x, s, t);
	        }
	        function md5_hh(a, b, c, d, x, s, t){
	            return md5_cmn(b ^ c ^ d, a, b, x, s, t);
	        }
	        function md5_ii(a, b, c, d, x, s, t){
	            return md5_cmn(c ^ (b | (~d)), a, b, x, s, t);
	        }
	
	        function safe_add(x, y){
	            var lsw = (x & 0xFFFF) + (y & 0xFFFF);
	            var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
	            return (msw << 16) | (lsw & 0xFFFF);
	        }
	
	        function bit_rol(num, cnt){
	            return (num << cnt) | (num >>> (32 - cnt));
	        }
	        
			function generate(aSource){
				return hex_md5(aSource).toLowerCase();
			}
			
	        var module = {};
	        
		    Object.defineProperty(module, 'generate', {
		    	enumerable: true,
		    	value: generate
		    });
	        return module;
		});
		
		predefine([], 'id', function(){
	        var module = {};
	        
			function generate(){
				return @com.eas.client.IDGenerator::genId()();
			}
	        
		    Object.defineProperty(module, 'generate', {
		    	enumerable: true,
		    	value: generate
		    });
	        return module;
		});
		
		predefine([], 'logger', function(){
	        var module = {};
			var nativeLogger = @com.eas.client.application.Application::platypusApplicationLogger;
			
			function severe(aMessage){
				nativeLogger.@java.util.logging.Logger::severe(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function warning(aMessage){
				nativeLogger.@java.util.logging.Logger::warning(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function info(aMessage){
				nativeLogger.@java.util.logging.Logger::info(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function fine(aMessage){
				nativeLogger.@java.util.logging.Logger::fine(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function finer(aMessage){
				nativeLogger.@java.util.logging.Logger::finer(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
			function finest(aMessage){
				nativeLogger.@java.util.logging.Logger::finest(Ljava/lang/String;)(aMessage!=null?""+aMessage:null);
			}
		    Object.defineProperty(module, 'severe', {
		    	enumerable: true,
		    	value: severe
		    });
		    Object.defineProperty(module, 'warning', {
		    	enumerable: true,
		    	value: warning
		    });
		    Object.defineProperty(module, 'info', {
		    	enumerable: true,
		    	value: info
		    });
		    Object.defineProperty(module, 'fine', {
		    	enumerable: true,
		    	value: fine
		    });
		    Object.defineProperty(module, 'finer', {
		    	enumerable: true,
		    	value: finer
		    });
		    Object.defineProperty(module, 'finest', {
		    	enumerable: true,
		    	value: finest
		    });
	        return module;
		});
		
		predefine([], 'common-utils/cursor', function(){
			return {
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
		});
		
		predefine([], 'common-utils/font', function(){
			function Font(aFamily, aStyle, aSize){
				var _self = this;
				Object.defineProperty(_self, "family", { get:function(){ return aFamily;} });
				Object.defineProperty(_self, "style", { get:function(){ return aStyle;} });
				Object.defineProperty(_self, "size", { get:function(){ return aSize;} });			
			}; 
			return Font;
		});

		predefine([], 'common-utils/color', function(){
			function Color(aRed, aGreen, aBlue, aAlpha){
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
			Color.black = new Color(0,0,0);
			Color.BLACK = new Color(0,0,0);
			Color.blue = new Color(0,0,0xff);
			Color.BLUE = new Color(0,0,0xff);
			Color.cyan = new Color(0,0xff,0xff);
			Color.CYAN = new Color(0,0xff,0xff);
			Color.DARK_GRAY = new Color(0x40, 0x40, 0x40);
			Color.darkGray = new Color(0x40, 0x40, 0x40);
			Color.gray = new Color(0x80, 0x80, 0x80);
			Color.GRAY = new Color(0x80, 0x80, 0x80);
			Color.green = new Color(0, 0xff, 0);
			Color.GREEN = new Color(0, 0xff, 0);
			Color.LIGHT_GRAY = new Color(0xC0, 0xC0, 0xC0);
			Color.lightGray = new Color(0xC0, 0xC0, 0xC0);
			Color.magenta = new Color(0xff, 0, 0xff);
			Color.MAGENTA = new Color(0xff, 0, 0xff);
			Color.orange = new Color(0xff, 0xC8, 0);
			Color.ORANGE = new Color(0xff, 0xC8, 0);
			Color.pink = new Color(0xFF, 0xAF, 0xAF);
			Color.PINK = new Color(0xFF, 0xAF, 0xAF);
			Color.red = new Color(0xFF, 0, 0);
			Color.RED = new Color(0xFF, 0, 0);
			Color.white = new Color(0xFF, 0xff, 0xff);
			Color.WHITE = new Color(0xFF, 0xff, 0xff);
			Color.yellow = new Color(0xFF, 0xff, 0);
			Color.YELLOW = new Color(0xFF, 0xff, 0);
			return Color;
		});

		predefine([], 'core/report', function(){
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
			return Report;
		});
		
		predefine(['boxing'], 'resource', function(B){
			var module = {};
			Object.defineProperty(module, 'upload', {
                enumerable: true,
				value : function(aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback) {
					return @com.eas.client.AppClient::jsUpload(Lcom/eas/client/published/PublishedFile;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback);
				}
			});
			Object.defineProperty(module, 'load', {
                enumerable: true,
				value : function(aResName, onSuccess, onFailure){
	            	var loaded = B.boxAsJs(@com.eas.client.AppClient::jsLoad(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aResName, onSuccess, onFailure));
	            	if(loaded)
	            		loaded.length = loaded.byteLength; 
	            	return loaded;
		        }
			});
			Object.defineProperty(module, 'loadText', {
                enumerable: true,
				value : function(aResName, onSuccess, onFailure){
	            	return B.boxAsJs(@com.eas.client.AppClient::jsLoadText(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aResName, onSuccess, onFailure));
		        }
			});
	        return module;
		});		

		predefine(['boxing'], 'environment', function(B){
		    var HTML5 = "HTML5 client";
		    var J2SE = "Java SE environment";
		
		    var module = {};
		    Object.defineProperty(module, "HTML5", {
		        enumerable: true,
		        value: HTML5
		    });
		    Object.defineProperty(module, "J2SE", {
		        enumerable: true,
		        value: J2SE
		    });
		    Object.defineProperty(module, "agent", {
		        enumerable: true,
		        value: HTML5
		    });
		
			var principal = {};
			
			Object.defineProperty(principal, "name", {get: function(){
				var appClient = @com.eas.client.AppClient::getInstance()();
				return '' + appClient.@com.eas.client.AppClient::getPrincipal()();
			}});
			Object.defineProperty(principal, "hasRole", {value: function(){
				return true;
			}});
			Object.defineProperty(principal, "logout", {value: function(onSuccess, onFailure){
				var appClient = @com.eas.client.AppClient::getInstance()();
				return appClient.@com.eas.client.AppClient::jsLogout(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
			}});
		    Object.defineProperty(module, "principal", {
		        enumerable: true,
		        value: principal
		    });
		    return module;
		});		
		
		predefine(['core/report'], 'rpc', function(Report){
			function requireRemotes(aRemotesNames, aOnSuccess, aOnFailure){
				var remotesNames = Array.isArray(aRemotesNames) ? aRemotesNames : [aRemotesNames];
				@com.eas.client.application.Application::jsLoadServerModules(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(remotesNames, aOnSuccess, aOnFailure);
			}		
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
					var nativeClient = @com.eas.client.AppClient::getInstance()();
					if(onSuccess) {
						nativeClient.@com.eas.client.AppClient::requestServerMethodExecution(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aFunctionName, params,
							function(aResult){
								if(typeof aResult === 'object' && aResult instanceof Report)
									onSuccess(aResult);
								else
									onSuccess(JSON.parse(aResult, @com.eas.client.Utils.JsObject::dateReviver()()));
							}, onFailure);
					} else {
						var result = nativeClient.@com.eas.client.AppClient::requestServerMethodExecution(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayString;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aModuleName, aFunctionName, params, null, null)
						return typeof result === 'object' && result instanceof Report ? result : JSON.parse(result, @com.eas.client.Utils.JsObject::dateReviver()()); 
					}
				};
			}
		    var serverModules = {};
			function Proxy(aModuleName){
				if(!(this instanceof Proxy))
					throw 'use Rpc.requireRemotes(...) please.';
				var moduleData = serverModules[aModuleName];
				if(!moduleData)
					throw 'No server module proxy for module: ' + aModuleName;
				if(!moduleData.isPermitted)
					throw 'Access to server module ' + aModuleName + ' is not permitted.';
				var self = this;
				for (var i = 0; i < moduleData.functions.length; i++) {
					var funcName = moduleData.functions[i];
					self[funcName] = generateFunction(aModuleName, funcName);
				}
			}
			var module = {};
		    Object.defineProperty(module, "Proxy", {
		        enumerable: true,
		        value: Proxy
		    });
		    Object.defineProperty(module, "requireRemotes", {
		        enumerable: true,
		        value: requireRemotes
		    });
		    return module;
		});		
		
		predefine([], 'invoke', function(){
			function invokeLater(aTarget) {
				@com.eas.client.Utils::invokeLater(Lcom/google/gwt/core/client/JavaScriptObject;)(aTarget);
			}
	        
			function invokeDelayed(aTimeout, aTarget) {
			    if (arguments.length < 2)
			        throw "invoke.delayed needs 2 arguments - timeout, callback.";
				@com.eas.client.Utils::invokeScheduled(ILcom/google/gwt/core/client/JavaScriptObject;)(+aTimeout, aTarget);
			}
		
			var module = {};
		    Object.defineProperty(module, "later", {
		        enumerable: true,
		        value: invokeLater
		    });
		    Object.defineProperty(module, "delayed", {
		        enumerable: true,
		        value: invokeDelayed
		    });
		    return module;
		});
		
		predefine([], 'orm', function(){
			function requireEntities(aEntities, aOnSuccess, aOnFailure){
				var entities;
				if(!Array.isArray(aEntities)){
					aEntities = aEntities + "";
					if(aEntities.length > 5 && aEntities.trim().substring(0, 5).toLowerCase() === "<?xml"){
						entities = [];
						var pattern = /queryId="(.+?)"/ig;
						var groups;
						while((groups = pattern.exec(aEntities)) != null){
							if(groups.length > 1){
								entities.push(groups[1]);
							}
						}
					}else{
						entities = [aEntities];
					}
				}else{
					entities = aEntities;
				}
				@com.eas.client.application.Application::jsLoadQueries(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(entities, aOnSuccess, aOnFailure);
			}
			function readModelDocument(aDocument, aTarget){
				if(!aTarget)
					aTarget = {};
				var nativeModel = @com.eas.client.model.store.XmlDom2Model::transform(Lcom/google/gwt/xml/client/Document;Lcom/google/gwt/core/client/JavaScriptObject;)(aDocument, aTarget);
				nativeModel.@com.eas.client.model.Model::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(aTarget);			
				return aTarget;
			}
			function readModel(aModelContent, aTarget){
				var doc = @com.google.gwt.xml.client.XMLParser::parse(Ljava/lang/String;)(aModelContent ? aModelContent + "" : "");
				return readModelDocument(doc, aTarget);
			}
			function loadModel(appElementName, aTarget) {
				var appElementDoc = aClient.@com.eas.client.AppClient::getModelDocument(Ljava/lang/String;)(appElementName);
				return readModelDocument(appElementDoc, aTarget);
			}		
            var module = {};
            Object.defineProperty(module, 'loadModel', {
                enumerable: true,
                value: loadModel
            });
            Object.defineProperty(module, 'readModel', {
                enumerable: true,
                value: readModel
            });
            Object.defineProperty(module, 'requireEntities', {
                enumerable: true,
                value: requireEntities
            });
		});
	}-*/;
}
