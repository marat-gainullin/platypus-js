define([
    '../extend',
    '../ui',
    './container'
], function (
        extend,
        Ui,
        Container) {
    function BorderPane(hgap, vgap) {
        Container.call(this);

        var self = this;

        var center;
        var left;
        var right;
        var top;
        var bottom;
        var bottomHeight;
        var topHeight;
        var leftWidth;
        var rightWidth;

        var hgap = 0;
        var vgap = 0;

        Object.defineProperty(this, 'hgap', {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                hgap = aValue;
                recalcCenterMargins();
            }
        });

        Object.defineProperty(this, 'vgap', {
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                vgap = aValue;
                recalcCenterMargins();
            }
        });

        var superAdd = this.add;
        function add(w, aPlace) {
            if (arguments.lenth < 2) {
                self.centerComponent = w;
            } else {
                switch (aPlace) {
                    case Ui.HorizontalPosition.LEFT:
                        self.leftComponent = w;
                        break;
                    case Ui.HorizontalPosition.RIGHT:
                        self.rightComponent = w;
                        break;
                    case Ui.VerticalPosition.TOP:
                        self.topComponent = w;
                        break;
                    case Ui.VerticalPosition.BOTTOM:
                        self.bottomComponent = w;
                        break;
                    default:
                        self.centerComponent = w;
                        break;
                }
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        Object.defineProperty(this, 'leftComponent', {
            get: function () {
                return left;
            },
            set: function (w) {
                if (w !== left) {
                    if (left) {
                        superRemove(left);
                    }
                    left = w;
                    if (left) {
                        leftWidth = left.width;
                        if (!leftWidth)
                            leftWidth = 30;
                        formatLeft();
                        superAdd(left);
                    }
                }
            }
        });
        Object.defineProperty(this, 'rightComponent', {
            get: function () {
                return right;
            },
            set: function (w) {
                if (w !== right) {
                    if (right) {
                        superRemove(right);
                    }
                    right = w;
                    if (right) {
                        rightWidth = right.width;
                        if (!rightWidth)
                            rightWidth = 30;
                        formatRight();
                        superAdd(right);
                    }
                }
            }
        });
        Object.defineProperty(this, 'topComponent', {
            get: function () {
                return top;
            },
            set: function (w) {
                if (w !== top) {
                    if (top) {
                        superRemove(top);
                    }
                    top = w;
                    if (top) {
                        topHeight = top.height;
                        if (!topHeight)
                            topHeight = 30;
                        formatTop();
                        superAdd(top);
                    }
                }
            }
        });
        Object.defineProperty(this, 'bottomComponent', {
            get: function () {
                return bottom;
            },
            set: function (w) {
                if (w !== bottom) {
                    if (bottom) {
                        superRemove(bottom);
                    }
                    bottom = w;
                    if (bottom) {
                        bottomHeight = bottom.height;
                        if (!bottomHeight)
                            bottomHeight = 30;
                        formatBottom();
                        superAdd(bottom);
                    }
                }
            }
        });
        Object.defineProperty(this, 'centerComponent', {
            get: function () {
                return center;
            },
            set: function (w) {
                if (w !== center) {
                    if (center) {
                        superRemove(center);
                    }
                    center = w;
                    if (center) {
                        centerHeight = center.height;
                        formatCenter();
                        recalcCenterMargins();
                        superAdd(center);
                    }
                }
            }
        });

        function formatLeft() {
            if (left) {
                var ls = left.element.style;
                ls.position = 'absolute';
                ls.overflow = 'hidden';
                ls.left = 0 + 'px';
                ls.width = leftWidth + 'px';
                ls.top = topHeight + 'px';
                ls.bottom = bottomHeight + 'px';
            }
        }

        function formatRight() {
            if (right) {
                var rs = right.element.style;
                rs.position = 'absolute';
                rs.pverflow = 'hidden';
                rs.right = 0 + 'px';
                rs.width = rightWidth + 'px';
                rs.top = topHeight + 'px';
                rs.bottom = bottomHeight + 'px';
            }
        }

        function formatTop() {
            if (top) {
                var ts = top.element.style;
                ts.position = 'absolute';
                ts.overflow = 'hidden';
                ts.top = 0 + 'px';
                ts.height = topHeight + 'px';
                ts.left = leftWidth + 'px';
                ts.right = rightWidth + 'px';
            }
        }

        function formatBottom() {
            if (bottom) {
                var bs = bottom.element.style;
                bs.position = 'absolute';
                bs.overflow = 'hidden';
                bs.bottom = 0 + 'px';
                bs.height = bottomHeight + 'px';
                bs.left = leftWidth + 'px';
                bs.right = rightWidth + 'px';
            }
        }

        function formatCenter() {
            if (center) {
                var cs = center.element.style;
                cs.position = 'absolute';
                cs.overflow = 'hidden';
                cs.bottom = bottomHeight + 'px';
                cs.top = topHeight + 'px';
                cs.left = leftWidth + 'px';
                cs.right = rightWidth + 'px';
            }
        }

        function checkParts(w) {
            if (left === w) {
                left = null;
                leftWidth = 0;
            }
            if (right === w) {
                right = null;
                rightWidth = 0;
            }
            if (top === w) {
                top = null;
                topHeight = 0;
            }
            if (bottom === w) {
                bottom = null;
                bottomHeight = 0;
            }
            if (center === w) {
                center = null;
            }
        }

        var superRemove = this.remove;
        function remove(widgetOrIndex) {
            var removed = superRemove(widgetOrIndex);
            checkParts(removed);
            return removed;
        }

        function recalcCenterMargins() {
            if (center) {
                var cs = center.element.style;
                cs.marginLeft = hgap + 'px';
                cs.marginRight = hgap + 'px';
                cs.marginTop = vgap + 'px';
                cs.marginBottom = vgap + 'px';
            }
        }

        function ajustWidth(w, width) {
            if (left === w) {
                leftWidth = width;
                formatLeft();
                formatTop();
                formatBottom();
                formatCenter();
            } else if (right === w) {
                rightWidth = width;
                formatRight();
                formatTop();
                formatBottom();
                formatCenter();
            }
        }
        Object.defineProperty(this, 'ajustWidth', {
            get: function () {
                return ajustWidth;
            }
        });

        function ajustHeight(w, height) {
            if (top === w) {
                topHeight = height;
                formatTop();
                formatLeft();
                formatRight();
                formatCenter();
            } else if (bottom === w) {
                bottomHeight = height;
                formatBottom();
                formatLeft();
                formatRight();
                formatCenter();
            }
        }
        Object.defineProperty(this, 'ajustHeight', {
            get: function () {
                return ajustHeight;
            }
        });

        function getTop(w) {
            if (w.parent !== this)
                throw "Widget should be a child of this container";
            return w.element.offsetTop;
        }
        Object.defineProperty(this, 'getTop', {
            get: function () {
                return getTop;
            }
        });

        function getLeft(w) {
            if (w.parent !== this)
                throw "Widget should be a child of this container";
            return w.element.getOffsetLeft();
        }
        Object.defineProperty(this, 'getLeft', {
            get: function () {
                return getLeft;
            }
        });
    }
    extend(BorderPane, Container);
    return BorderPane;
});