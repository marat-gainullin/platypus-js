/**
 * Platypus.js internals initialization.
 * Don't edit unless you are a Platypus.js contributor.
 */
(function() {
    var ScriptUtils = Java.type('com.eas.script.ScriptUtils');
    ScriptUtils.setToPrimitiveFunc(function(aValue) {
        if (aValue && aValue.constructor) {
            var cName = aValue.constructor.name;
            if (cName === 'Date') {
                var dateClass = Java.type('java.util.Date');
                var converted = new dateClass(aValue.getTime());
                return converted;
            } else if (cName === 'String') {
                return aValue + '';
            } else if (cName === 'Number') {
                return aValue * 1;
            } else if (cName === 'Boolean') {
                return !!aValue;
            }
        }
        return aValue;
    });
    ScriptUtils.setLookupInGlobalFunc(
            function(aPropertyName) {
                return this[aPropertyName];
            });
    ScriptUtils.setPutInGlobalFunc(
            function(aPropertyName, aValue) {
                this[aPropertyName] = aValue;
            });
    ScriptUtils.setToDateFunc(
            function(aJavaDate) {
                return aJavaDate !== null ? new Date(aJavaDate.time) : null;
            });
    ScriptUtils.setParseJsonFunc(
            function(str) {
                return JSON.parse(str);
            });
    ScriptUtils.setParseDatesFunc(
            function(aObject) {
                function tryToDate(aValue) {
                    try {
                        var timestamp = Date.parse(aValue);
                        if (!isNaN(timestamp)) {
                            return new Date(timestamp);
                        }
                    } catch (e) {
                    }
                    return aValue;
                }
                if (aObject) {
                    for (var prop in aObject) {
                        var value = aObject[prop];
                        if (typeof (value) == "object") {
                            P.JSONToDate(value);
                        } else if (value && value.constructor) {
                            var cName = value.constructor.name;
                            if (cName === 'String') {
                                aObject[prop] = tryToDate(value + '');
                            }
                        } else if (typeof (value) == "string") {
                            aObject[prop] = tryToDate(value);
                        }
                    }
                }
            });
    ScriptUtils.setWriteJsonFunc(
            function(aObj) {
                return JSON.stringify(aObj);
            });
    ScriptUtils.setExtendFunc(
            function(Child, Parent) {
                var F = function() {
                };
                F.prototype = Parent.prototype;
                Child.prototype = new F();
                Child.prototype.constructor = Child;
                Child.superclass = Parent.prototype;
            });
    ScriptUtils.setScalarDefFunc(
            function(targetEntity, targetFieldName, sourceFieldName) {
                var _self = this;
                _self.enumerable = true;
                _self.configurable = false;
                _self.get = function() {
                    var found = targetEntity.find(targetEntity.schema[targetFieldName], this[sourceFieldName]);
                    return found.length === 0 ? null : (found.length === 1 ? found[0] : found);
                };
                _self.set = function(aValue) {
                    this[sourceFieldName] = aValue ? aValue[targetFieldName] : null;
                };
            });
    ScriptUtils.setCollectionDefFunc(
            function(sourceEntity, targetFieldName, sourceFieldName) {
                var _self = this;
                _self.enumerable = true;
                _self.configurable = false;
                _self.get = function() {
                    var res = sourceEntity.find(sourceEntity.schema[sourceFieldName], this[targetFieldName]);
                    if (res && res.length > 0) {
                        return res;
                    } else {
                        var emptyCollectionPropName = '-x-empty-collection-' + sourceFieldName;
                        var emptyCollection = this[emptyCollectionPropName];
                        if (!emptyCollection) {
                            emptyCollection = [];
                            this[emptyCollectionPropName] = emptyCollection;
                        }
                        return emptyCollection;
                    }
                };
            });
})();