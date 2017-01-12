/* global Java*/
(function () {
    var ScriptsClass = Java.type('com.eas.script.Scripts');
    var space = ScriptsClass.getSpace();

    space.setIsArrayFunc(function (aInstance) {
        return aInstance instanceof Array;
    });
    space.setMakeObjFunc(function () {
        return {};
    });
    space.setMakeArrayFunc(function () {
        return [];
    });

    var JavaDateClass = Java.type("java.util.Date");
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
    space.setToPrimitiveFunc(toPrimitive);
})();
