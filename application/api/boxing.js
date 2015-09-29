/* global Java */

define(function () {
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var JavaCollectionClass = Java.type("java.util.Collection");
    var JavaDateClass = Java.type("java.util.Date");
    var JavaStringClass = Java.type("java.lang.String");
    var JavaArrayClass = Java.type("java.lang.Object[]");

    function toPrimitive(aValue) {
        if (aValue && aValue.constructor) {
            var cName = aValue.constructor.name;
            if (cName === 'Date') {
                var converted = new JavaDateClass(aValue.getTime());
                return converted;
            } else if (cName === 'String') {
                return aValue + '';
            } else if (cName === 'Number') {
                return +aValue;
            } else if (cName === 'Boolean') {
                return !!aValue;
            }
        }
        return aValue;
    }
    var space = ScriptsClass.getSpace();
    space.setToPrimitiveFunc(toPrimitive);

    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    function boxAsJava(aValue) {
        //---------------------- Don't change to !== because of undefined 
        if (arguments.length > 0 && aValue != null) {
            if (aValue.unwrap) {
                aValue = aValue.unwrap();
            } else {
                aValue = toPrimitive(aValue);
            }
            return aValue;
        } else {// undefined -> null
            return null;
        }
    }
    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    function boxAsJs(aValue) {
        if (aValue) {
            if (aValue.getPublished) {
                aValue = aValue.getPublished();
            } else if (aValue instanceof JavaDateClass) {
                aValue = new Date(aValue.time);
            } else if (aValue instanceof JavaStringClass) {
                aValue += '';
            } else if (aValue instanceof JavaArrayClass) {
                var converted = [];
                for (var i = 0; i < aValue.length; i++) {
                    converted[converted.length] = boxAsJs(aValue[i]);
                }
                return converted;
            } else if (aValue instanceof JavaCollectionClass) {
                var converted = [];
                for each (var v in aValue) {
                    converted[converted.length] = boxAsJs(v);
                }
                return converted;
            }
            aValue = EngineUtilsClass.unwrap(aValue);
        }
        return aValue;
    }
    var module = {};
    Object.defineProperty(module, 'boxAsJs', {value: boxAsJs});
    Object.defineProperty(module, 'boxAsJava', {value: boxAsJava});
    return module;
});
