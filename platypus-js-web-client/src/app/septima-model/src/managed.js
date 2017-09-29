define([
    'core/logger'], function (
        Logger) {
    var releaseName = '-platypus-orm-release-func';
    /** 
     * Substitutes properties of anObject with observable properties using Object.defineProperty()
     * @param anObject An object to be reorganized.
     * @param aOnChange a callback called on every change of properties.
     * @param aOnBeforeChange a callback called on before every change of properties.
     * @returns anObject by pass for convinence.
     */
    function manageObject(anObject, aOnChange, aOnBeforeChange) {
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
                            if (_oldValue != aValue) {
                                var _beforeState = null;
                                if (aOnBeforeChange)
                                    _beforeState = aOnBeforeChange(anObject, {source: anObject, propertyName: _p, oldValue: _oldValue, newValue: aValue});
                                container[_p] = aValue;
                                aOnChange(anObject, {source: anObject, propertyName: _p, oldValue: _oldValue, newValue: aValue, beforeState: _beforeState});
                            }
                        }
                    });
                })();
            }
            Object.defineProperty(anObject, releaseName, {
                configurable: true,
                value: function () {
                    delete anObject[releaseName];
                    for (var p in anObject) {
                        var pValue = anObject[p];
                        delete anObject[p];
                        anObject[p] = pValue;
                    }
                }
            });
        }
        return {
            release: function () {
                anObject[releaseName]();
            }
        };
    }

    function unmanageObject(anObject) {
        if (anObject[releaseName]) {
            anObject[releaseName]();
        }
    }
    function manageArray(aTarget, aOnChange) {
        function pop() {
            var popped = Array.prototype.pop.call(aTarget);
            if (popped) {
                aOnChange.spliced([], [popped]);
            }
            return popped;
        }
        function shift() {
            var shifted = Array.prototype.shift.call(aTarget);
            if (shifted) {
                aOnChange.spliced([], [shifted]);
            }
            return shifted;
        }
        function push() {
            var newLength = Array.prototype.push.apply(aTarget, arguments);
            var added = [];
            for (var a = 0; a < arguments.length; a++) {
                added.push(arguments[a]);
            }
            aOnChange.spliced(added, []);
            if (added.length > 0)
                aTarget.cursor = added[added.length - 1];
            return newLength;
        }
        function unshift() {
            var newLength = Array.prototype.unshift.apply(aTarget, arguments);
            var added = [];
            for (var a = 0; a < arguments.length; a++) {
                added.push(arguments[a]);
            }
            aOnChange.spliced(added, []);
            if (added.length > 0)
                aTarget.cursor = added[added.length - 1];
            return newLength;
        }
        function reverse() {
            var reversed = Array.prototype.reverse.apply(aTarget);
            if (aTarget.length > 0) {
                aOnChange.spliced([], []);
            }
            return reversed;
        }
        function sort() {
            var sorted = Array.prototype.sort.apply(aTarget, arguments);
            if (aTarget.length > 0) {
                aOnChange.spliced([], []);
            }
            return sorted;
        }
        function splice() {
            var beginDeleteAt = arguments[0];
            if (beginDeleteAt < 0)
                beginDeleteAt = aTarget.length - beginDeleteAt;
            var deleted = Array.prototype.splice.apply(aTarget, arguments);
            var added = [];
            for (var a = 2; a < arguments.length; a++) {
                var aAdded = arguments[a];
                added.push(aAdded);
            }
            aOnChange.spliced(added, deleted);
            return deleted;
        }
        Object.defineProperty(aTarget, "pop", {
            get: function () {
                return pop;
            }
        });
        Object.defineProperty(aTarget, "shift", {
            get: function () {
                return shift;
            }
        });
        Object.defineProperty(aTarget, "push", {
            get: function () {
                return push;
            }
        });
        Object.defineProperty(aTarget, "unshift", {
            get: function () {
                return unshift;
            }
        });
        Object.defineProperty(aTarget, "reverse", {
            get: function () {
                return reverse;
            }
        });
        Object.defineProperty(aTarget, "sort", {
            get: function () {
                return sort;
            }
        });
        Object.defineProperty(aTarget, "splice", {
            get: function () {
                return splice;
            }
        });
        return aTarget;
    }

    var addListenerName = "-platypus-listener-add-func";
    var removeListenerName = "-platypus-listener-remove-func";
    var fireChangeName = "-platypus-change-fire-func";

    function listenable(aTarget) {
        var listeners = new Set();
        Object.defineProperty(aTarget, addListenerName, {
            value: function (aListener) {
                listeners.add(aListener);
            }
        });
        Object.defineProperty(aTarget, removeListenerName, {
            value: function (aListener) {
                listeners.delete(aListener);
            }
        });
        Object.defineProperty(aTarget, fireChangeName, {
            value: function (aChange) {
                Object.freeze(aChange);
                var _listeners = [];
                listeners.forEach(function (aListener) {
                    _listeners.push(aListener);
                });
                _listeners.forEach(function (aListener) {
                    aListener(aChange);
                });
            }
        });
        return function () {
            unlistenable(aTarget);
        };
    }

    function unlistenable(aTarget) {
        delete aTarget[addListenerName];
        delete aTarget[removeListenerName];
    }

    function listen(aTarget, aListener) {
        var addListener = aTarget[addListenerName];
        if (addListener) {
            addListener(aListener);
            return function () {
                aTarget[removeListenerName](aListener);
            };
        } else {
            return null;
        }
    }

    function unlisten(aTarget, aListener) {
        var removeListener = aTarget[removeListenerName];
        if(removeListener)
            removeListener(aListener);
    }

    function fire(aTarget, aChange) {
        try {
            aTarget[fireChangeName](aChange);
        } catch (e) {
            Logger.severe(e);
        }
    }

    var module = {};
    Object.defineProperty(module, 'manageObject', {
        enumerable: true,
        value: manageObject
    });
    Object.defineProperty(module, 'unmanageObject', {
        enumerable: true,
        value: unmanageObject
    });
    Object.defineProperty(module, 'manageArray', {
        enumerable: true,
        value: manageArray
    });
    Object.defineProperty(module, 'fire', {
        enumerable: true,
        value: fire
    });
    Object.defineProperty(module, 'listenable', {
        enumerable: true,
        value: listenable
    });
    Object.defineProperty(module, 'unlistenable', {
        enumerable: true,
        value: unlistenable
    });
    Object.defineProperty(module, 'listen', {
        enumerable: true,
        value: listen
    });
    Object.defineProperty(module, 'unlisten', {
        enumerable: true,
        value: unlisten
    });
    return module;
});