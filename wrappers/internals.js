/**
 * Platypus.js internals initialization.
 * Don't edit unless you are a Platypus.js contributor.
 */
/* global Java*/
(function () {
    var global = this;
    if (typeof Set === 'undefined') {
        var LinkedHashSetClass = Java.type('java.util.LinkedHashSet');
        Set = function () {
            var container = new LinkedHashSetClass();
            this.add = function (aValue) {
                container.add(aValue);
            };
            this.delete = function (aValue) {
                container.remove(aValue);
            };
            Object.defineProperty(this, 'size', {get: function () {
                    return container.size();
                }});
            this.forEach = function (aCallback) {
                container.forEach(aCallback);
            };
        };
    }
    var LpcTransientClass = Java.type('com.eas.script.LpcTransient');
    var ScriptsClass = Java.type('com.eas.script.Scripts');
    var ScriptedResourceClass = Java.type('com.eas.client.scripts.ScriptedResource');
    var JavaStringArrayClass = Java.type('java.lang.String[]');
    var apiPath = ScriptsClass.getAbsoluteApiPath();
    var appPath = ScriptedResourceClass.getAbsoluteAppPath();
    var space = ScriptsClass.getSpace();

    function lookupCallerFile() {
        var calledFromFile = null;
        var error = new Error('path test error');
        if (error.stack) {
            var stack = error.stack.split('\n');
            if (stack.length > 1) {
                var sourceCall = stack[3];
                var stackFrameParsed = /\((.+):\d+\)/.exec(sourceCall);
                if (stackFrameParsed && stackFrameParsed.length > 1) {
                    calledFromFile = stackFrameParsed[1];
                }
            }
        }
        return calledFromFile + '.js';
    }

    /**
     * @static
     * @param {type} aDeps
     * @param {type} aOnSuccess
     * @param {type} aOnFailure
     * @returns {undefined}
     */
    function require(aDeps, aOnSuccess, aOnFailure) {
        var calledFromFile = lookupCallerFile();
        if (!Array.isArray(aDeps))
            aDeps = [aDeps];
        var sDeps = new JavaStringArrayClass(aDeps.length);
        for (var s = 0; s < aDeps.length; s++) {
            var sDep = aDeps[s];
            if (sDep.toLowerCase().endsWith('.js')) {
                sDep = sDep.substring(0, sDep.length - 3);
            }
            sDep = ScriptedResourceClass.toModuleId(apiPath, appPath, sDep, calledFromFile);
            sDeps[s] = sDep;
        }
        function gatherDefined() {
            var resolved = [];
            var defined = space.getDefined();
            for (var r = 0; r < sDeps.length; r++) {
                var rDep = sDeps[r];
                var depModule = defined[rDep] ? defined[rDep] : global[rDep];
                resolved.push(depModule);
            }
            return resolved;
        }
        if (aOnSuccess) {
            ScriptedResourceClass.require(sDeps, calledFromFile, function () {
                aOnSuccess.apply(null, gatherDefined());
            }, aOnFailure ? aOnFailure : null);
        } else {
            ScriptedResourceClass.require(sDeps, calledFromFile);
            var def = gatherDefined();
            return def.length === 1 ? def[0] : def;
        }
    }

    /**
     * @static
     * @returns {undefined}
     */
    function define() {
        if (arguments.length === 1 ||
                arguments.length === 2 || arguments.length === 3) {
            var calledFromFile = lookupCallerFile();
            var aModuleName = arguments.length === 3 ? arguments[0] : calledFromFile.substring(0, calledFromFile.length - 3);
            var aDeps = arguments.length === 3 ? arguments[1] : arguments.length === 2 ? arguments[0] : [];
            var aModuleDefiner = arguments.length === 3 ? arguments[2] : arguments.length === 2 ? arguments[1] : arguments[0];
            if (!Array.isArray(aDeps))
                aDeps = [aDeps];
            var sDeps = new JavaStringArrayClass(aDeps.length);
            for (var s = 0; s < aDeps.length; s++) {
                var sDep = aDeps[s];
                if (sDep.toLowerCase().endsWith('.js')) {
                    sDeps = sDep.substring(0, sDep.length - 3);
                }
                sDep = ScriptedResourceClass.toModuleId(apiPath, appPath, sDep, calledFromFile);
                sDeps[s] = sDep;
            }
            space.addAmdDefine(aModuleName, sDeps, function (aModuleName) {
                var defined = space.getDefined();
                var resolved = [];
                for (var d = 0; d < sDeps.length; d++) {
                    var rDep = sDeps[d];
                    var depModule = defined[rDep] ? defined[rDep] : global[rDep];
                    resolved.push(depModule);
                }
                resolved.push(aModuleName);
                var module = typeof aModuleDefiner === 'function' ? aModuleDefiner.apply(null, resolved) : aModuleDefiner;
                defined.put(aModuleName, module);
            });
        } else {
            throw 'Module definition arguments mismatch';
        }
    }
    define.amd = {};
    Object.defineProperty(global, 'define', {value: define});
    Object.defineProperty(global, 'require', {value: require});
    space.setGlobal(global);
    space.setLookupInGlobalFunc(
            function (aPropertyName) {
                return global[aPropertyName];
            });
    space.setPutInGlobalFunc(
            function (aPropertyName, aValue) {
                global[aPropertyName] = aValue;
            });
    space.setToDateFunc(
            function (aJavaDate) {
                return aJavaDate !== null ? new Date(+aJavaDate.getTime()) : null;
            });
    space.setParseJsonFunc(
            function (str) {
                return JSON.parse(str);
            });
    space.setParseJsonWithDatesFunc(
            function (str) {
                return JSON.parse(str, function (k, v) {
                    if (typeof v === 'string' && /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/.test(v)) {
                        return new Date(v);
                    } else {
                        return v;
                    }
                });
            });

    space.setWriteJsonFunc(
            function (aObj) {
                return JSON.stringify(aObj);
            });
    space.setExtendFunc(
            function (Child, Parent) {
                var prevChildProto = {};
                for (var m in Child.prototype) {
                    var member = Child.prototype[m];
                    if (typeof member === 'function') {
                        prevChildProto[m] = member;
                    }
                }
                var F = function () {
                };
                F.prototype = Parent.prototype;
                Child.prototype = new F();
                for (var m in prevChildProto)
                    Child.prototype[m] = prevChildProto[m];
                Child.prototype.constructor = Child;
                Child.superclass = Parent.prototype;
            });
    space.setScalarDefFunc(
            function (targetPublishedEntity, targetFieldName, sourceFieldName) {
                var _self = this;
                _self.enumerable = false;
                _self.configurable = true;
                _self.get = function () {
                    var criterion = {};
                    criterion[targetFieldName] = this[sourceFieldName];
                    var found = targetPublishedEntity.find(criterion);
                    return found && found.length === 1 ? found[0] : null;
                };
                _self.set = function (aValue) {
                    this[sourceFieldName] = aValue ? aValue[targetFieldName] : null;
                };
            });
    space.setIsArrayFunc(function (aInstance) {
        return aInstance instanceof Array;
    });
    space.setMakeObjFunc(function () {
        return {};
    });
    space.setMakeArrayFunc(function () {
        return [];
    });
    space.setLoadFunc(function (aSourceLocation) {
        return load(aSourceLocation);
    });
    require([
        'core/change-value',
        'core/jdbc-change-value',
        'core/command',
        'core/insert',
        'core/delete',
        'core/update']);
    var Report = require('core/report');
    var EngineUtilsClass = Java.type('jdk.nashorn.api.scripting.ScriptUtils');
    var HashMapClass = Java.type('java.util.HashMap');
    var DateCopyClass = Java.type('com.eas.script.copies.DateCopy');
    var RegExpCopyClass = Java.type('com.eas.script.copies.RegExpCopy');
    var ArrayCopyClass = Java.type('com.eas.script.copies.ArrayCopy');
    var ObjectCopyClass = Java.type('com.eas.script.copies.ObjectCopy');
    function copy(aValue, aMapping) {
        aValue = EngineUtilsClass.unwrap(aValue);
        if (!aMapping)
            aMapping = new HashMapClass();
        if (aValue === null || aValue === undefined)
            return null;
        else {
            var type = typeof aValue;
            if (type === 'number')
                return +aValue;
            else if (type === 'string')
                return aValue + '';
            else if (type === 'boolean')
                return !!aValue;
            else if (type === 'object') {
                if (aValue instanceof Date) {
                    return new DateCopyClass(aValue.getTime());
                } else if (aValue instanceof RegExp) {
                    var flags = '';
                    if (aValue.global)
                        flags += 'g';
                    if (aValue.ignoreCase)
                        flags += 'i';
                    if (aValue.multiline)
                        flags += 'm';
                    return new RegExpCopyClass(aValue.source, flags);
                } else if (aValue instanceof Number) {
                    return +aValue;
                } else if (aValue instanceof String) {
                    return aValue + '';
                } else if (aValue instanceof Boolean) {
                    return aValue == true; // Don't edit to !!aValue
                } else {
                    var isArray = aValue instanceof Array;
                    var copied = isArray ? new ArrayCopyClass() : new ObjectCopyClass();
                    aMapping.put(aValue, copied);
                    if(isArray){
                        for(var i = 0; i < aValue.length; i++){
                            var pValue = aValue[i];
                            if (typeof pValue !== 'function') {
                                var val = aMapping.containsKey(pValue) ? aMapping.get(pValue) : copy(pValue, aMapping);
                                copied.add(val);
                            }
                        }
                    }
                    for (var p in aValue) {
                        if(!isArray || isNaN(p)){
                            var pValue = aValue[p];
                            if (typeof pValue !== 'function') {
                                var val = aMapping.containsKey(pValue) ? aMapping.get(pValue) : copy(pValue, aMapping);
                                copied.put(p + '', val);
                            }
                        }
                    }
                    return copied;
                }
            }
        }
    }
    space.setCopyObjectFunc(function (aValue) {
        if (aValue instanceof Report) {
            aValue = aValue.unwrap();
        }
        if (aValue instanceof LpcTransientClass) {
            return aValue;
        } else {
            return copy(aValue);
        }
    });
    function restore(aValue, aMapping) {
        aValue = EngineUtilsClass.unwrap(aValue);
        if (!aMapping)
            aMapping = new HashMapClass();
        if (aValue === null || aValue === undefined)
            return null;
        else {
            var type = typeof aValue;
            if (type === 'number')
                return +aValue;
            else if (type === 'string')
                return aValue + '';
            else if (type === 'boolean')
                return !!aValue;
            else if (type === 'object') {
                if (aValue instanceof DateCopyClass) {
                    return new Date(aValue.getTime());
                } else if (aValue instanceof RegExpCopyClass) {
                    return new RegExp(aValue.getSource(), aValue.getFlags());
                } else if (aValue instanceof Number) {
                    return +aValue;
                } else if (aValue instanceof String) {
                    return aValue + '';
                } else if (aValue instanceof Boolean) {
                    return aValue == true; // Don't edit to !!aValue
                } else {
                    var isList = aValue instanceof ArrayCopyClass;
                    var restored = isList ? [] : {};
                    aMapping.put(aValue, restored);
                    if(isList){
                        for(var i = 0; i < aValue.size(); i++){
                            var pValue = aValue.get(i);
                            var val = aMapping.containsKey(pValue) ? aMapping.get(pValue) : restore(pValue, aMapping);
                            restored.push(val);
                        }
                    }
                    for each (var p in aValue.keySet()) {
                        var pValue = aValue.get(p);
                        var val = aMapping.containsKey(pValue) ? aMapping.get(pValue) : restore(pValue, aMapping);
                        restored[p] = val;
                    }
                    return restored;
                }
            }
        }
    }
    space.setRestoreObjectFunc(function (aValue) {
        if (aValue instanceof Report) {
            aValue = aValue.unwrap();
        }
        if (aValue instanceof LpcTransientClass) {
            return aValue;
        } else {
            return restore(aValue);
        }
    });
    if(ScriptsClass.isGlobalAPI()){
        var F = require('facade');
        F.export(global);
    }
})();