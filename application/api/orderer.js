(function () {
    var setConstructor = null;
    if (typeof Set !== 'undefined') {
        setConstructor = Set;
    } else {
        var LinkedHashSetClass = Java.type('java.util.LinkedHashSet');
        setConstructor = function () {
            var container = new LinkedHashSetClass();
            this.add = function (aValue) {
                container.add(aValue);
            };
            this.delete = function (aValue) {
                container.remove(aValue);
            };
            Object.defineProperty(this, 'size', {get: function () {
                    return container.size();
                }});
        };
    }

    /**
     * Orderer constructor
     * @constructor
     * @param {Array} aKeysNames Array of names of key properties
     * @returns {Orderer}
     */
    function Orderer(aKeysNames) {
        var keyNames = aKeysNames.sort();
        function calcKey(anObject) {
            var key = '';
            keyNames.forEach(function (aKeyName) {
                var datum = anObject[aKeyName];
                if (key.length > 0)
                    key += ' | ';
                key += datum instanceof Date ? JSON.stringify(datum) : '' + datum;
            });
        }

        var map = {};
        this.add = function (anObject) {
            var key = calcKey(anObject);
            var subset = map[key];
            if (!subset) {
                subset = new setConstructor();
                map[key] = subset;
            }
            subset.add(anObject);
        };
        this.delete = function (anObject) {
            var key = calcKey(anObject);
            var subset = map[key];
            if (subset) {
                subset.delete(anObject);
                if (subset.size === 0) {
                    map[key] = undefined;
                }
            }
        };
    }
    Object.defineProperty(this.P, 'Orderer', {value:Orderer});
})();