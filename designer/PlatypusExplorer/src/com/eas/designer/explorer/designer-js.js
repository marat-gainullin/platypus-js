/**
 * Platypus.js's designer internals initialization.
 */
(function(aSpace) {
    var global = this;
    this.P = {loadModel: function() {
        }};
    aSpace.setLookupInGlobalFunc(
            function (aPropertyName) {
                return global[aPropertyName];
            });
    var DateClass = Java.type('java.util.Date');
    aSpace.setToPrimitiveFunc(function (aValue) {
        if (aValue && aValue.constructor) {
            var cName = aValue.constructor.name;
            if (cName === 'Date') {
                var converted = new DateClass(aValue.getTime());
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
    aSpace.setToDateFunc(
            function (aJavaDate) {
                return aJavaDate !== null ? new Date(aJavaDate.time) : null;
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
    return global;
})(space);