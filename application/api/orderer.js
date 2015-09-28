define(function () {
    /**
     * Orderer constructor
     * @constructor
     * @param {Array} aKeysNames Array of names of key properties
     * @returns {Orderer}
     */
    function Orderer(aKeysNames) {
        var keyNames = aKeysNames.sort();
        keyNames.forEach(function (aKeyName) {
        });
        function calcKey(anObject) {
            var key = '';
            keyNames.forEach(function (aKeyName) {
                var datum = anObject[aKeyName];
                if (key.length > 0)
                    key += ' | ';
                key += datum instanceof Date ? JSON.stringify(datum) : '' + datum;
            });
            return key;
        }

        this.inKeys = function (aKeyName) {
            return keyNames.indexOf(aKeyName) !== -1;
        };

        var map = {};
        this.add = function (anObject) {
            var key = calcKey(anObject);
            var subset = map[key];
            if (!subset) {
                subset = new Set();
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
                    delete map[key];
                }
            }
        };
        this.find = function (aCriteria) {
            var key = calcKey(aCriteria);
            var subset = map[key];
            if (!subset) {
                return [];
            } else {
                var found = [];
                subset.forEach(function (item) {
                    found.push(item);
                });
                return found;
            }
        };
    }
    return Orderer;
});