define([
    '../extend',
    '../ui',
    './container'], function (
        extend,
        Ui,
        Container) {
    function Anchors() {
        Container.call(this);
        var self = this;

        var constraints = new Map();

        function is(a) {
            return typeof a !== 'undefined' && a !== null;
        }

        function applyConstraints(w, aConstraints) {
            constraints.put(w, aConstraints);

            var left = aConstraints.left;
            var top = aConstraints.top;
            var right = aConstraints.right;
            var bottom = aConstraints.bottom;
            var width = aConstraints.width;
            var height = aConstraints.height;

            var ws = w.element.style;

            // horizontal
            if (is(left) && is(width)) {
                right = null;
            }
            if (is(left) && is(right)) {
                ws.left = left.value + left.unit;
                ws.right = right.value + right.unit;
            } else if (!is(left) && is(right)) {
                if (!width)
                    throw "Left may be absent in presence of width and right";
                ws.right = right.value + right.unit;
                ws.width = width.value + width.unit;
            } else if (!is(right) && is(left)) {
                if (!width)
                    throw "Right may be absent in presence of width and left";
                ws.left = left.value + left.unit;
                ws.width = width.value + width.unit;
            } else {
                throw "At least left with width, right with width or both (without width) must present";
            }
            // vertical
            if (is(top) && is(height)) {
                bottom = null;
            }
            if (is(top) && is(bottom)) {
                ws.top = top.value + top.unit;
                ws.bottom = bottom.value + bottom.unit;
            } else if (!is(top) && is(bottom)) {
                if (!is(height))
                    throw "Top may be absent in presence of height and bottom";
                ws.bottom = bottom.value + bottom.unit;
                ws.height = height.value + height.unit;
            } else if (is(top) && !is(bottom)) {
                if (!is(height))
                    throw "Bottom may be absent in presence of height and top";
                ws.top = top.value + top.unit;
                ws.height = height.value + height.unit;
            } else {
                throw "At least top with height, bottom with height or both (without height) must present";
            }
            ws.margin = 0 + 'px';
        }

        function add(w, indexOrAnchors) {
            add(w);
            applyConstraints(w, anchors);
        }

        var superRemove = this.remove;
        function remove(widgetOrIndex) {
            var w = superRemove(widgetOrIndex);
            constraints.remove(w);
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

        function ajustWidth(w, aValue) {
            var anchors = constraints.get(w);
            var containerWidth = self.element.offsetWidth;
            if (is(anchors.width)) {
                anchors.getWidth().setPlainValue(aValue, containerWidth);
            } else if (is(anchors.left) && is(anchors.right)) {
                anchors.getRight().setPlainValue(containerWidth - w.element.offsetLeft - aValue, containerWidth);
            }
            applyConstraints(w, anchors);
        }

        function ajustHeight(w, aValue) {
            var anchors = constraints.get(w);
            var containerHeight = self.element.getOffsetHeight();
            if (is(anchors.height)) {
                anchors.getHeight().setPlainValue(aValue, containerHeight);
            } else if (is(anchors.top) && is(anchors.bottom)) {
                anchors.getBottom().setPlainValue(containerHeight - w.element.offsetTop - aValue, containerHeight);
            }
            applyConstraints(w, anchors);
        }

        function ajustLeft(w, aValue) {
            var anchors = constraints.get(w);
            var containerWidth = self.element.offsetWidth;
            var childWidth = w.element.offsetWidth;
            if (is(anchors.left) && is(anchors.width)) {
                anchors.getLeft().setPlainValue(aValue, containerWidth);
            } else if (is(anchors.width) && is(anchors.right)) {
                anchors.getRight().setPlainValue(containerWidth - aValue - childWidth, containerWidth);
            } else if (is(anchors.left) && is(anchors.right)) {
                anchors.getLeft().setPlainValue(aValue, containerWidth);
                anchors.getRight().setPlainValue(containerWidth - aValue - childWidth, containerWidth);
            }
            applyConstraints(w, anchors);
        }

        function ajustTop(w, aValue) {
            var anchors = constraints.get(w);
            var containerHeight = self.element.offsetHeight;
            var childHeight = w.element.offsetHeight;
            if (is(anchors.top) && is(anchors.height)) {
                anchors.top.setPlainValue(aValue, containerHeight);
            } else if (is(anchors.height) && is(anchors.bottom)) {
                anchors.getBottom().setPlainValue(containerHeight - aValue - childHeight, containerHeight);
            } else if (is(anchors.top) && is(anchors.bottom)) {
                anchors.getTop().setPlainValue(aValue, containerHeight);
                anchors.getBottom().setPlainValue(containerHeight - aValue - childHeight, containerHeight);
            }
            applyConstraints(w, anchors);
        }

        function getTop(aWidget) {
            if (aWidget.parent !== this)
                throw "Widget should be a child of this container";
            return aWidget.element.offsetTop;
        }

        function getLeft(aWidget) {
            if (aWidget.parent !== this)
                throw "widget should be a child of this container";
            return aWidget.element.offsetLeft;
        }

    }
    extend(Anchors, Container);
    return Anchors;
});