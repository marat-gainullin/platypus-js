define([
    '../extend',
    './widget',
    './events/container-event',
    '../invoke',
    '../logger'], function (
        extend,
        Widget,
        ContainerEvent,
        Invoke,
        Logger) {
    function Container(shell, content) {
        if (!shell)
            shell = document.createElement('div');
        if (!content)
            content = shell;
        Widget.call(this, shell);
        var self = this;

        var children = [];

        this.element.classList.add('p-container');

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
            configurable: true,
            get: function () {
                return child;
            }
        });
        function _children() {
            Logger.warning("'children()' function is obsolete. Use 'count', 'child' and 'forEach' please");
            return children.slice(0, children.length);
        }
        Object.defineProperty(this, 'children', {
            configurable: true,
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

        function add(w, beforeIndex) {
            if (w.parent === self)
                throw 'A widget is already child of this container';
            w.parent = self;
            if (isNaN(beforeIndex)) {
                content.appendChild(w.element);
                children.push(w);
            } else {
                if (beforeIndex < children.length) {
                    content.insertBefore(w.element, children[beforeIndex].element);
                } else {
                    content.appendChild(w.element);
                }
                children.splice(beforeIndex, 0, w);
            }
            fireAdded(w);
        }

        Object.defineProperty(this, 'add', {
            configurable: true,
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
                var removed = children.splice(idx, 1)[0];
                removed.parent = null;
                removed.element.parentElement.removeChild(removed.element);
                fireRemoved(w);
                return removed;
            } else {
                return null;
            }
        }

        Object.defineProperty(this, 'remove', {
            configurable: true,
            get: function () {
                return remove;
            }
        });

        function clear() {
            children.forEach(function (child) {
                child.parent = null;
                child.element.parentElement.removeChild(child.element);
                fireRemoved(child);
            });
            children.splice(0, children.length);
        }

        Object.defineProperty(this, 'clear', {
            configurable: true,
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
            configurable: true,
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
            configurable: true,
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
    extend(Container, Widget);
    return Container;
});