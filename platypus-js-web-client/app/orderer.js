define(function () {
    /**
     * Orderer constructor
     * @constructor
     * @param {Array} aKeysNames Array of key names
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
                key += datum instanceof Date ? JSON.stringify(datum) : ('' + datum);
            });
            return key;
        }

        this.inKeys = function (aKeyName) {
            return keyNames.indexOf(aKeyName) !== -1;
        };

        var map = new Map();
        this.add = function (anObject) {
            var key = calcKey(anObject);
            var subset = map.get(key);
            if (!subset) {
                subset = new Set();
                map.set(key, subset);
            }
            subset.add(anObject);
        };
        this.delete = function (anObject) {
            var key = calcKey(anObject);
            var subset = map.get(key);
            if (subset) {
                subset.delete(anObject);
                if (subset.size === 0) {
                    map.delete(key);
                }
            }
        };
        this.find = function (aCriteria) {
            var key = calcKey(aCriteria);
            var subset = map.get(key);
            if (!subset) {
                return [];
            } else {
                return Array.from(subset);
            }
        };
    }
    return Orderer;
});