(function () {
    var releaseName = '-platypus-orm-release-func';
    /**
     * Substitutes properties of anObject with observable properties using Object.defineProperty().
     * ES6 Object.observe() is not suitable for ordering/orm tasks
     * because of asynchonous nature of its events.
     * @param anObject An object to be reorganized.
     * @param aOnChange a callback called on every change of properties.
     * @returns anObject by pass for convinence.
     */
    function manageObject(anObject, aOnChange) {
        if (!anObject[releaseName]) {
            var container = {};
            for (var p in anObject) {
                container[p] = anObject[p];
                (function () {
                    var _p = p;
                    Object.defineProperty(anObject, ('' + _p), {
                        enumerable: true,
                        configurable: true,
                        get: function () {
                            return container[_p];
                        },
                        set: function (aValue) {
                            var _oldValue = container[_p];
                            container[_p] = aValue;
                            aOnChange({source: anObject, propertyName: _p, oldValue: _oldValue, newValue: aValue});
                        }
                    });
                })();
            }
            Object.defineProperety(anObject, releaseName, {
                configurable: true,
                value: function () {
                    delete anObject[releaseName];
                    for (var p in anObject) {
                        var pValue = anObject[p];
                        delete anObject[p];
                        anObject[p] = pValue;
                    }
                }});
        }
        return {release: function () {
                anObject[releaseName]();
            }};
    }

    function unmanageObject(anObject) {
        if (anObject[releaseName]) {
            anObject[releaseName]();
        }
    }
    function manageArray(aTarget, aOnSpliced) {
        Object.defineProperty(aTarget, "pop", {
            value: function () {
                var popped = Array.prototype.pop.call(aTarget);
                if (popped) {
                    aOnSpliced([], [popped]);
                }
                return popped;
            }
        });
        Object.defineProperty(aTarget, "shift", {
            value: function () {
                var shifted = Array.prototype.shift.call(aTarget);
                if (shifted) {
                    aOnSpliced([], [shifted]);
                }
                return shifted;
            }
        });
        Object.defineProperty(aTarget, "push", {
            value: function () {
                var newLength = Array.prototype.push.apply(aTarget, arguments);
                var added = [];
                for (var a = 0; a < arguments.length; a++) {
                    added.push(arguments[a]);
                }
                aOnSpliced(added, []);
                return newLength;
            }
        });
        Object.defineProperty(aTarget, "unshift", {
            value: function () {
                var newLength = Array.prototype.unshift.apply(aTarget, arguments);
                var added = [];
                for (var a = 0; a < arguments.length; a++) {
                    added.push(arguments[a]);
                }
                aOnSpliced(added, []);
                return newLength;
            }
        });
        Object.defineProperty(aTarget, "reverse", {
            value: function () {
                var reversed = Array.prototype.reverse.apply(aTarget);
                if (aTarget.length > 0) {
                    aOnSpliced([], []);
                }
                return reversed;
            }
        });
        Object.defineProperty(aTarget, "sort", {
            value: function () {
                var sorted = Array.prototype.sort.apply(aTarget, arguments);
                if (aTarget.length > 0) {
                    aOnSpliced([], []);
                }
                return sorted;
            }
        });
        Object.defineProperty(aTarget, "splice", {
            value: function () {
                var deleted = Array.prototype.splice.apply(aTarget, arguments);
                var added = [];
                for (var a = 2; a < arguments.length; a++) {
                    var aAdded = arguments[a];
                    added.push(aAdded);
                }
                aOnSpliced(added, deleted);
                return deleted;
            }
        });
        return aTarget;
    }

    function copyArray(aTarget) {
        var snapshot = [];
        aTarget.forEach(function (aItem) {
            var copy = {};
            for (var p in aItem) {
                copy[p] = aItem[p];
            }
            snapshot.push(copy);
        });
        return snapshot;
    }
    Object.defineProperty(this.P, 'manageObject', {value: manageObject});
    Object.defineProperty(this.P, 'unmanageObject', {value: unmanageObject});
    Object.defineProperty(this.P, 'manageArray', {value: manageArray});
    Object.defineProperty(this.P, 'copyArray', {value: copyArray});
})();