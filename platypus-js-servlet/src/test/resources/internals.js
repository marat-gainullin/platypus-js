/* global Java*/
(function () {
    var ScriptsClass = Java.type('com.eas.script.Scripts');
    var space = ScriptsClass.getSpace();

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
