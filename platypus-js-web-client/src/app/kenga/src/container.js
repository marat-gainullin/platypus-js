define([
    'core/extend',
    './widget',
    './events/container-event',
    'core/invoke',
    'core/logger'], function (
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

        var onAdd;
        var addReg;
        Object.defineProperty(this, 'onAdd', {
            get: function () {
                return onAdd;
            },
            set: function (aValue) {
                if (onAdd !== aValue) {
                    if (addReg) {
                        addReg.removeHandler();
                        addReg = null;
                    }
                    onAdd = aValue;
                    if (onAdd) {
                        addReg = addAddHandler(function (event) {
                            if (onAdd) {
                                onAdd(event);
                            }
                        });
                    }
                }
            }
        });

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

        var onRemove;
        var removeReg;
        Object.defineProperty(this, 'onRemove', {
            get: function () {
                return onRemove;
            },
            set: function (aValue) {
                if (onRemove !== aValue) {
                    if (removeReg) {
                        removeReg.removeHandler();
                        removeReg = null;
                    }
                    onRemove = aValue;
                    if (onRemove) {
                        removeReg = addRemoveHandler(function (event) {
                            if (onRemove) {
                                onRemove(event);
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