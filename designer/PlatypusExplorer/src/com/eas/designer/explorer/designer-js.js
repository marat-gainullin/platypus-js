/**
 * Platypus.js's designer internals initialization.
 */
(function (aSpace) {
    var global = this;
    this.P = {
        loadModel: function () {
        },
        define: function () {
            var moduleDefiner = arguments.length === 1 ? arguments[0] : arguments.length === 2 ? arguments[1] : null;
            if (moduleDefiner) {
                var moduleName = aSpace.getFileNameFromContext();
                var module = moduleDefiner.call(null, moduleName);
                aSpace.getDefined().put(moduleName, module);
            }
        }
    };
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
                return +aValue;
            } else if (cName === 'Boolean') {
                return !!aValue;
            }
        }
        return aValue;
    });
    aSpace.setToDateFunc(
            function (aJavaDate) {
                return aJavaDate !== null ? new Date(+aJavaDate.getTime()) : null;
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
    aSpace.setListenFunc(function listenInstance(aTarget, aPath, aPropListener) {
        return {
            unlisten: function () {
            }
        };
    });
    aSpace.setListenElementsFunc(function listenElements(aData, aPropListener) {
        return {
            unlisten: function () {
            }
        };
    });

    return global;
})(space);