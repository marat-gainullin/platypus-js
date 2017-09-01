define([
    '../../logger',
    '../../invoke',
    '../widget',
    '../events/container-event',
    '../events/item-event'], function (
        Logger,
        Invoke,
        Widget,
        ContainerEvent,
        SelectionEvent) {
    function ButtonGroup() {
        var self = this;

        var selectionHandlers = new Set();

        function addSelectionHandler(handler) {
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

        Object.defineProperty(this, 'addSelectionHandler', {
            get: function () {
                return addSelectionHandler;
            }
        });

        var onItemSelected;

        var selectedReg;
        Object.defineProperty(this, 'onItemSelected', {
            get: function () {
                return onItemSelected;
            },
            set: function (aValue) {
                if (onItemSelected !== aValue) {
                    if (selectedReg) {
                        selectedReg.removeHandler();
                        selectedReg = null;
                    }
                    onItemSelected = aValue;
                    if (onItemSelected) {
                        selectedReg = addSelectionHandler(function (event) {
                            if (onItemSelected) {
                                onItemSelected(event);
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

        var onComponentAdded;
        var componentAddedReg;
        Object.defineProperty(this, 'onComponentAdded', {
            get: function () {
                return onComponentAdded;
            },
            set: function (aValue) {
                if (onComponentAdded !== aValue) {
                    if (componentAddedReg) {
                        componentAddedReg.removeHandler();
                        componentAddedReg = null;
                    }
                    onComponentAdded = aValue;
                    if (onComponentAdded) {
                        componentAddedReg = addAddHandler(function (event) {
                            if (onComponentAdded) {
                                onComponentAdded(event);
                            }
                        });
                    }
                }
            }
        });

        var onComponentRemoved;
        var componentRemovedReg;
        Object.defineProperty(this, 'onComponentRemoved', {
            get: function () {
                return onComponentRemoved;
            },
            set: function (aValue) {
                if (onComponentRemoved !== aValue) {
                    if (componentRemovedReg) {
                        componentRemovedReg.removeHandler();
                        componentRemovedReg = null;
                    }
                    onComponentRemoved = aValue;
                    if (onComponentRemoved) {
                        componentRemovedReg = addRemoveHandler(function (event) {
                            if (onComponentRemoved) {
                                onComponentRemoved(event);
                            }
                        });
                    }
                }
            }
        });
    }
    return ButtonGroup;
});