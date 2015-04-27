/**
 * Platypus.js's designer internals initialization.
 */
(function() {
    var ScriptUtils = Java.type('com.eas.script.ScriptUtils');
    this.P = {loadModel: function() {
        }};
    ScriptUtils.setToPrimitiveFunc(function (aValue) {
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
    ScriptUtils.setToDateFunc(
            function (aJavaDate) {
                return aJavaDate !== null ? new Date(aJavaDate.time) : null;
            });
    ScriptUtils.setMakeObjFunc(function () {
        return {};
    });
    ScriptUtils.setMakeArrayFunc(function () {
        return [];
    });
})();