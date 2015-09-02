/**
 * Platypus.js internals initialization.
 * Don't edit unless you are a Platypus.js contributor.
 */
/* global P, Java*/
/**
 * 
 * @returns {undefined}
 */
(function () {
    P.require('core/report.js');
    var global = this;
    var aSpace = global['-platypus-scripts-space'];
    var ReportClass = Java.type("com.eas.client.report.Report");
    aSpace.setLookupInGlobalFunc(
            function (aPropertyName) {
                return this[aPropertyName];
            });
    aSpace.setPutInGlobalFunc(
            function (aPropertyName, aValue) {
                this[aPropertyName] = aValue;
            });
    aSpace.setToDateFunc(
            function (aJavaDate) {
                return aJavaDate !== null ? new Date(aJavaDate.time) : null;
            });
    aSpace.setParseJsonFunc(
            function (str) {
                return JSON.parse(str);
            });
    aSpace.setParseJsonWithDatesFunc(
            function (str) {
                return JSON.parse(str, function (k, v) {
                    if (typeof v === 'string' && /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/.test(v)) {
                        return new Date(v);
                    } else {
                        return v;
                    }
                });
            });

    aSpace.setWriteJsonFunc(
            function (aObj) {
                return JSON.stringify(aObj);
            });
    aSpace.setExtendFunc(
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
    aSpace.setScalarDefFunc(
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
    aSpace.setIsArrayFunc(function (aInstance) {
        return aInstance instanceof Array;
    });
    aSpace.setMakeObjFunc(function () {
        return {};
    });
    aSpace.setMakeArrayFunc(function () {
        return [];
    });
    aSpace.setLoadFunc(function (aSourceLocation) {
        return load(aSourceLocation);
    });
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var HashMapClass = Java.type('java.util.HashMap');
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
                    return new Date(aValue.getTime());
                } else if (aValue instanceof RegExp) {
                    var flags = '';
                    if (aValue.global)
                        flags += 'g';
                    if (aValue.ignoreCase)
                        flags += 'i';
                    if (aValue.multiline)
                        flags += 'm';
                    return new RegExp(aValue.source, flags);
                } else if (aValue instanceof Number) {
                    return +aValue;
                } else if (aValue instanceof String) {
                    return aValue + '';
                } else if (aValue instanceof Boolean) {
                    return !!aValue;
                } else {
                    var copied = aValue instanceof Array ? [] : {};
                    aMapping.put(aValue, copied);
                    for (var p in aValue) {
                        var pValue = aValue[p];
                        if (typeof pValue !== 'function') {
                            if (aMapping.containsKey(pValue))
                                copied[p] = aMapping.get(pValue);
                            else
                                copied[p] = copy(pValue, aMapping);
                        }
                    }
                    return copied;
                }
            }
        }
    }
    aSpace.setCopyObjectFunc(function (aValue, aConsumer) {
        if (aValue instanceof P.Report) {
            aValue = aValue.unwrap();
        }
        if(aValue instanceof ReportClass){
            aConsumer(aValue);
        } else {
            aConsumer(copy(aValue));
        }
    });
})();