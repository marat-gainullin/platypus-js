define(['../extend', './widget', './container-event'], function (extend, Widget, ContainerEvent) {
    function Container() {
        Widget.call(this);
        var self = this;
        
        var children = [];

        this.element.style.position = 'relative';
        this.element.style.overflow = 'hidden';

        Object.defineProperty(this, 'count', {
            get: function () {
                return children.length;
            }
        });

        function get(index) {
            if (index >= 0 && index < children.length) {
                return children[index];
            } else {
                return null;
            }
        }
        Object.defineProperty(this, 'get', {
            get: function () {
                return get;
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
            w.parent = this;
            if (arguments.length > 1) {
                children.splice(beforeIndex, 0, w);
                if (beforeIndex < children.length) {
                    self.element.insertBefore(w.element, children[beforeIndex].element);
                } else {
                    self.element.appendChild(w.element);
                }
            } else {
                children.push(w);
                self.element.appendChild(w.element);
            }
            fireAdded(w);
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
            var removed = children.splice(idx, 1);
            if (removed.length > 0) {
                removed[0].parent = null;
                removed[0].element.removeFromParent();
                fireRemoved(w);
                return removed[0];
            } else {
                return removed;
            }
        }

        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });
        
        function clear() {
            children.forEach(function (child) {
                child.parent = null;
                child.element.removeFromParent();
                fireRemoved(child);
            });
            children.splice(0, children.length);
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
            var event = new ContainerEvent(this, w);
            addHandlers.forEach(function (h) {
                h(event);
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
            var event = new ContainerEvent(this, w);
            removeHandlers.forEach(function (h) {
                h.onRemove(event);
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