define([
    'core/extend',
    'ui/container'], function (
        extend,
        Container) {
    function Anchors() {
        Container.call(this);
        var self = this;
        
        this.element.classList.add('p-anchors');

        var constraints = new Map();

        function is(a) {
            return typeof a !== 'undefined' && a !== null && a !== '';
        }

        function u(value) {
            if (is(value)) {
                value = value + '';
                if (!value.endsWith('px') && !value.endsWith('%'))
                    return value + 'px';
                else
                    return value;
            } else {
                return null;
            }
        }

        function applyAnchors(w, _anchors) {
            var anchors = {
                left: u(_anchors.left),
                top: u(_anchors.top),
                right: u(_anchors.right),
                bottom: u(_anchors.bottom),
                width: u(_anchors.width),
                height: u(_anchors.height)
            };

            constraints.set(w, anchors);

            var ws = w.element.style;

            // horizontal
            if (is(anchors.left) && is(anchors.width)) {
                anchors.right = null;
            }
            if (is(anchors.left) && is(anchors.right)) {
                ws.left = anchors.left;
                ws.right = anchors.right;
            } else if (!is(anchors.left) && is(anchors.right)) {
                if (!anchors.width)
                    throw "Left may be absent in presence of width and right";
                ws.right = anchors.right;
                ws.width = anchors.width;
            } else if (!is(anchors.right) && is(anchors.left)) {
                if (!anchors.width)
                    throw "Right may be absent in presence of width and left";
                ws.left = anchors.left;
                ws.width = anchors.width;
            } else {
                throw "At least left with width, right with width or both (without width) must present";
            }
            // vertical
            if (is(anchors.top) && is(anchors.height)) {
                anchors.bottom = null;
            }
            if (is(anchors.top) && is(anchors.bottom)) {
                ws.top = anchors.top;
                ws.bottom = anchors.bottom;
            } else if (!is(anchors.top) && is(anchors.bottom)) {
                if (!is(anchors.height))
                    throw "Top may be absent in presence of height and bottom";
                ws.bottom = anchors.bottom;
                ws.height = anchors.height;
            } else if (is(anchors.top) && !is(anchors.bottom)) {
                if (!is(anchors.height))
                    throw "Bottom may be absent in presence of height and top";
                ws.top = anchors.top;
                ws.height = anchors.height;
            } else {
                throw "At least top with height, bottom with height or both (without height) must present";
            }
            ws.margin = 0 + 'px';
        }

        var superAdd = this.add;
        function add(w, indexOrAnchors) {
            if (w) {
                if (w.parent === self)
                    throw 'A widget is already added to this container';
                if (isNaN(indexOrAnchors)) {
                    var anchors = indexOrAnchors ? indexOrAnchors : {left: w.left, top: w.top, width: w.width, height: w.height};
                    superAdd(w);
                    applyAnchors(w, anchors);
                } else {
                    var index = indexOrAnchors;
                    superAdd(w, index);
                    applyAnchors(w, {
                        left: w.left,
                        right: w.right,
                        top: w.top,
                        bottom: w.bottom,
                        width: w.width,
                        height: w.height
                    });
                }
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        var superRemove = this.remove;
        function remove(widgetOrIndex) {
            var w = superRemove(widgetOrIndex);
            constraints.delete(w);
            return w;
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        var superClear = this.clear;
        function clear() {
            superClear();
            constraints.clear();
        }
        Object.defineProperty(this, 'clear', {
            get: function () {
                return clear;
            }
        });

        function updatePlainValue(anchor, value, containerSize) {
            if (anchor.endsWith('px'))
                return value + 'px';
            else if (anchor.endsWith('%'))
                return (value / containerSize * 100) + '%';
            else
                return value + 'px';
        }

        function ajustWidth(w, aValue) {
            var anchors = constraints.get(w);
            var containerWidth = self.element.offsetWidth;
            if (is(anchors.width)) {
                anchors.width = updatePlainValue(anchors.width, aValue, containerWidth);
            } else if (is(anchors.left) && is(anchors.right)) {
                anchors.right = updatePlainValue(anchors.right, containerWidth - w.element.offsetLeft - aValue, containerWidth);
            }
            applyAnchors(w, anchors);
        }
        Object.defineProperty(this, 'ajustWidth', {
            get: function () {
                return ajustWidth;
            }
        });

        function ajustHeight(w, aValue) {
            var anchors = constraints.get(w);
            var containerHeight = self.element.offsetHeight;
            if (is(anchors.height)) {
                anchors.height = updatePlainValue(anchors.height, aValue, containerHeight);
            } else if (is(anchors.top) && is(anchors.bottom)) {
                anchors.bottom = updatePlainValue(anchors.bottom, containerHeight - w.element.offsetTop - aValue, containerHeight);
            }
            applyAnchors(w, anchors);
        }
        Object.defineProperty(this, 'ajustHeight', {
            get: function () {
                return ajustHeight;
            }
        });

        function ajustLeft(w, aValue) {
            var anchors = constraints.get(w);
            var containerWidth = self.element.offsetWidth;
            var childWidth = w.element.offsetWidth;
            if (is(anchors.left) && is(anchors.width)) {
                anchors.left = updatePlainValue(anchors.left, aValue, containerWidth);
            } else if (is(anchors.width) && is(anchors.right)) {
                anchors.right = updatePlainValue(anchors.right, containerWidth - aValue - childWidth, containerWidth);
            } else if (is(anchors.left) && is(anchors.right)) {
                anchors.left = updatePlainValue(anchors.left, aValue, containerWidth);
                anchors.right = updatePlainValue(anchors.right, containerWidth - aValue - childWidth, containerWidth);
            }
            applyAnchors(w, anchors);
        }
        Object.defineProperty(this, 'ajustLeft', {
            get: function () {
                return ajustLeft;
            }
        });

        function ajustTop(w, aValue) {
            var anchors = constraints.get(w);
            var containerHeight = self.element.offsetHeight;
            var childHeight = w.element.offsetHeight;
            if (is(anchors.top) && is(anchors.height)) {
                anchors.top = updatePlainValue(anchors.top, aValue, containerHeight);
            } else if (is(anchors.height) && is(anchors.bottom)) {
                anchors.bottom = updatePlainValue(anchors.bottom, containerHeight - aValue - childHeight, containerHeight);
            } else if (is(anchors.top) && is(anchors.bottom)) {
                anchors.top = updatePlainValue(anchors.top, aValue, containerHeight);
                anchors.bottom = updatePlainValue(anchors.bottom, containerHeight - aValue - childHeight, containerHeight);
            }
            applyAnchors(w, anchors);
        }
        Object.defineProperty(this, 'ajustTop', {
            get: function () {
                return ajustTop;
            }
        });

    }
    extend(Anchors, Container);
    return Anchors;
});
