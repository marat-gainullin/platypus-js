define([
    'core/logger',
    'core/invoke',
    'ui/widget',
    'ui/events/container-event',
    'ui/events/item-event'], function (
        Logger,
        Invoke,
        Widget,
        ContainerEvent,
        SelectionEvent) {
    function ButtonGroup() {
        var self = this;

        var selectionHandlers = new Set();

        function addSelectHandler(handler) {
            selectionHandlers.add(handler);
            return {
                removeHandler: function () {
                    selectionHandlers.delete(handler);
                }
            };
        }

        function fireSelected(aItem) {
            var event = new SelectionEvent(self, aItem);
            selectionHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        Object.defineProperty(this, 'addSelectHandler', {
            get: function () {
                return addSelectHandler;
            }
        });

        var onSelect;
        var selectReg;
        Object.defineProperty(this, 'onSelect', {
            get: function () {
                return onSelect;
            },
            set: function (aValue) {
                if (onSelect !== aValue) {
                    if (selectReg) {
                        selectReg.removeHandler();
                        selectReg = null;
                    }
                    onSelect = aValue;
                    if (onSelect) {
                        selectReg = addSelectHandler(function (event) {
                            if (onSelect) {
                                onSelect(event);
                            }
                        });
                    }
                }
            }
        });

        var children = [];

        Object.defineProperty(this, 'count', {
            get: function () {
                return children.length;
            }
        });

        function child(index) {
            if (index >= 0 && index < children.length) {
                return children[index];
            } else {
                return null;
            }
        }
        Object.defineProperty(this, 'child', {
            get: function () {
                return child;
            }
        });
        function _children() {
            Logger.warning("'children()' function is obsolete. Use 'count', 'child' and 'forEach' please");
            return children.slice(0, children.length);
        }
        Object.defineProperty(this, 'children', {
            get: function () {
                return _children;
            }
        });

        function forEach(action) {
            children.forEach(action);
        }

        Object.defineProperty(this, 'forEach', {
            get: function () {
                return forEach;
            }
        });

        function indexOf(w) {
            return children.indexOf(w);
        }

        Object.defineProperty(this, 'indexOf', {
            get: function () {
                return indexOf;
            }
        });

        var valueChangeRegs = new Map();

        function add(w, beforeIndex) {
            if (w.buttonGroup !== self) {
                w.buttonGroup = self;
            } else {
                if (isNaN(beforeIndex)) {
                    children.push(w);
                } else {
                    children.splice(beforeIndex, 0, w);
                }
                if (w.addValueChangeHandler) {
                    var valueChangeReg = w.addValueChangeHandler(function (evt) {
                        if (evt.newValue) {
                            children.filter(function (item) {
                                return item !== evt.source;
                            }).forEach(function (item) {
                                item.value = false;
                            });
                            fireSelected(evt.source);
                        }
                    });
                    valueChangeRegs.set(w, valueChangeReg);
                }
                fireAdded(w);
            }
        }

        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function remove(w) {
            var idx;
            if (w instanceof Widget)
                idx = children.indexOf(w);
            else
                idx = w;
            if (idx >= 0 && idx < children.length) {
                var removed = children[idx];
                if (removed.buttonGroup === self) {
                    removed.buttonGroup = null;
                } else {
                    removed = children.splice(idx, 1)[0];
                    var valueChangeReg = valueChangeRegs.get(removed);
                    valueChangeRegs.delete(removed);
                    if (valueChangeReg)
                        valueChangeReg.removeHandler();
                    fireRemoved(w);
                    return removed;
                }
            } else {
                return null;
            }
        }

        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        function clear() {
            var toRemove = _children();
            toRemove.forEach(function (child) {
                remove(child);
            });
        }

        Object.defineProperty(this, 'clear', {
            get: function () {
                return clear;
            }
        });

        var addHandlers = new Set();

        function addAddHandler(handler) {
            addHandlers.add(handler);
            return {
                removeHandler: function () {
                    addHandlers.delete(handler);
                }
            };
        }

        Object.defineProperty(this, 'addAddHandler', {
            get: function () {
                return addAddHandler;
            }
        });

        function fireAdded(w) {
            var event = new ContainerEvent(self, w);
            addHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var removeHandlers = new Set();

        function addRemoveHandler(handler) {
            removeHandlers.add(handler);
            return {
                removeHandler: function () {
                    removeHandlers.delete(handler);
                }
            };
        }

        Object.defineProperty(this, 'addRemoveHandler', {
            get: function () {
                return addRemoveHandler;
            }
        });

        function fireRemoved(w) {
            var event = new ContainerEvent(self, w);
            removeHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onAdded;
        var addedReg;
        Object.defineProperty(this, 'onAdded', {
            get: function () {
                return onAdded;
            },
            set: function (aValue) {
                if (onAdded !== aValue) {
                    if (addedReg) {
                        addedReg.removeHandler();
                        addedReg = null;
                    }
                    onAdded = aValue;
                    if (onAdded) {
                        addedReg = addAddHandler(function (event) {
                            if (onAdded) {
                                onAdded(event);
                            }
                        });
                    }
                }
            }
        });

        var onRemoved;
        var removedReg;
        Object.defineProperty(this, 'onRemoved', {
            get: function () {
                return onRemoved;
            },
            set: function (aValue) {
                if (onRemoved !== aValue) {
                    if (removedReg) {
                        removedReg.removeHandler();
                        removedReg = null;
                    }
                    onRemoved = aValue;
                    if (onRemoved) {
                        removedReg = addRemoveHandler(function (event) {
                            if (onRemoved) {
                                onRemoved(event);
                            }
                        });
                    }
                }
            }
        });
    }
    return ButtonGroup;
});