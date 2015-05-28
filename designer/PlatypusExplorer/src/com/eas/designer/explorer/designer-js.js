/**
 * Platypus.js's designer internals initialization.
 */
(function(aSpace) {
    this.P = {loadModel: function() {
        }};
    aSpace.setLookupInGlobalFunc(
            function (aPropertyName) {
                return this[aPropertyName];
            });
    aSpace.setToPrimitiveFunc(function (aValue) {
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
    return this;
})(space);