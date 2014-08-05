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

    var parseDates = function(aObject) {
        if (typeof aObject === 'string' || aObject && aObject.constructor && aObject.constructor.name === 'String') {
            var strValue = '' + aObject;
            if(/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/.test(strValue)){
                return new Date(strValue);
            }
        } else if (typeof aObject === 'object') {
            for (var prop in aObject) {
                aObject[prop] = parseDates(aObject[prop]);
            }
        }
        return aObject;
    };

    ScriptUtils.setParseDatesFunc(parseDates);
    
    ScriptUtils.setWriteJsonFunc(
            function(aObj) {
                return JSON.stringify(aObj);
            });
    ScriptUtils.setExtendFunc(
            function(Child, Parent) {
                var prevChildProto = {};
                for(var m in Child.prototype){
                    var member = Child.prototype[m];
                    if(typeof member === 'function'){
                        prevChildProto[m] = member;
                    }
                }
                var F = function() {
                };
                F.prototype = Parent.prototype;
                Child.prototype = new F();
                for(var m in prevChildProto)
                    Child.prototype[m] = prevChildProto[m];
                Child.prototype.constructor = Child;
                Child.superclass = Parent.prototype;
            });
    ScriptUtils.setScalarDefFunc(
            function(targetPublishedEntity, targetFieldName, sourceFieldName) {
                var _self = this;
                _self.enumerable = true;
                _self.configurable = false;
                _self.get = function() {
                    var found = targetPublishedEntity.find(targetPublishedEntity.schema[targetFieldName], this[sourceFieldName]);
                    return found === null || found.length === 0 ? null : (found.length === 1 ? found[0] : found);
                };
                _self.set = function(aValue) {
                    this[sourceFieldName] = aValue ? aValue[targetFieldName] : null;
                };
            });
    ScriptUtils.setCollectionDefFunc(
            function(sourcePublishedEntity, targetFieldName, sourceFieldName) {
                var _self = this;
                _self.enumerable = true;
                _self.configurable = false;
                _self.get = function() {
                    return sourcePublishedEntity.find(sourcePublishedEntity.schema[sourceFieldName], this[targetFieldName]);
                };
            });
    ScriptUtils.setIsArrayFunc(function(aInstance){
        return aInstance instanceof Array; 
    });        
})();