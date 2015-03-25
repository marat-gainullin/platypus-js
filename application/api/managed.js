(function () {
    var releaseName = '-platypus-orm-release-func';
    var addListenerName = '-platypus-orm-add-listener-func';
    var removeListenerName = '-platypus-orm-remove-listener-func';
    /**
     * Substitutes properties of anObject with observable properties using Object.defineProperty().
     * ES6 Object.observe() is not suitable for ordering/orm tasks
     * because of asynchonous nature of its events.
     * @param anObject An object to reorganized.
     * @returns anObject by pass for convinence.
     */
    function manageObject(anObject) {
        if (!anObject[releaseName]) {
            var container = {};
            var listeners = [];
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
                            var _listeners = [];
                            Array.prototype.push.apply(_listeners, listeners);
                            _listeners.forEach(function (listener) {
                                listener({source: anObject, propertyName: _p, oldValue: _oldValue, newValue: aValue});
                            });
                        }
                    });
                })();
            }
            Object.defineProperty(anObject, removeListenerName, {
                configurable: true,
                value: function (aListener) {
                    var idx = listeners.indexOf(aListener);
                    if (idx !== -1)
                        listeners.splice(idx, 1);
                }});
            Object.defineProperty(anObject, addListenerName, {
                configurable: true,
                value: function (aListener) {
                    listeners.push(aListener);
                    return {remove: function () {
                            anObject[removeListenerName](aListener);
                        }};
                }});
            Object.defineProperty(anObject, replaceContentName, {
                configurable: true,
                value: function (aContent) {
                    container = aContent;
                }});

            Object.defineProperety(anObject, releaseName, {
                configurable: true,
                value: function () {
                    delete anObject[releaseName];
                    delete anObject[addListenerName];
                    delete anObject[removeListenerName];
                    delete anObject[replaceContentName];
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
    function manageArray(aChangeLog, aElementClass) {
        var aTarget = [];
        function spliced(_added, _deleted, _skipChangeLog) {
            _deleted.forEach(function (aDeleted) {
                /*
                 aDeleted.fireChangesOfOppositeCollections();
                 aDeleted.fireChangesOfOppositeScalars();
                 */
                unmanageObject(aDeleted);
            });
            _added.forEach(function (aAdded) {
                manageObject(aAdded);
                /*
                 aAdded.fireChangesOfOppositeCollections();
                 aAdded.fireChangesOfOppositeScalars();
                 */
            });
            var _listeners = [];
            Array.prototype.push.apply(_listeners, listeners);
            _listeners.forEach(function (listener) {
                listener({source: aTarget, added: _added, deleted: _deleted});
            });
        }
        Object.defineProperty(aTarget, "pop", {
            value: function () {
                var popped = Array.prototype.pop.call(aTarget);
                if (popped) {
                    spliced([], [popped]);
                }
                return popped;
            }
        });
        Object.defineProperty(aTarget, "shift", {
            value: function () {
                var shifted = Array.prototype.shift.call(aTarget);
                if (shifted) {
                    spliced([], [shifted]);
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
                spliced(added, []);
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
                spliced(added, []);
                return newLength;
            }
        });
        Object.defineProperty(aTarget, "reverse", {
            value: function () {
                var reversed = Array.prototype.reverse.apply(aTarget);
                if (aTarget.length > 0) {
                    spliced([], []);
                }
                return reversed;
            }
        });
        Object.defineProperty(aTarget, "sort", {
            value: function () {
                var sorted = Array.prototype.sort.apply(aTarget, arguments);
                if (aTarget.length > 0) {
                    spliced([], []);
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
                spliced(added, deleted);
                return deleted;
            }
        });
        var listeners = [];
        Object.defineProperty(aTarget, removeListenerName, {value: function (aListener) {
                var idx = listeners.indexOf(aListener);
                if (idx !== -1)
                    listeners.splice(idx, 1);
            }});
        Object.defineProperty(aTarget, addListenerName, {value: function (aListener) {
                listeners.push(aListener);
                return {remove: function () {
                        aTarget[removeListenerName](aListener);
                    }};
            }});
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
    Object.defineProperty(this.P, 'manageObject', {value:manageObject});
    Object.defineProperty(this.P, 'unmanageObject', {value:unmanageObject});
    Object.defineProperty(this.P, 'manageArray', {value:manageArray});
    Object.defineProperty(this.P, 'copyArray', {value:copyArray});
})();