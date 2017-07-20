define([
    '../extend',
    '../ui',
    './container'], function (
        extend,
        Ui,
        Container) {
    function Box(orientation, hgap, vgap) {
        Container.call(this);

        var self = this;

        self.element.style.whiteSpace = 'nowrap';
        self.element.style.display = 'inline-block';
        self.element.style.overflow = 'hidden';
        self.element.style.position = 'relative';

        function applyHGap() {
            if (orientation === Ui.Orientation.HORIZONTAL) {
                self.forEach(function (w) {
                    w.element.style.marginLeft = hgap + 'px';
                });
            }
        }

        function applyVGap() {
            if (orientation === Ui.Orientation.HORIZONTAL) {
                self.forEach(function (w) {
                    w.element.style.marginTop = vgap + 'px';
                });
            }
        }
        applyHGap();
        applyVGap();

        Object.defineProperty(this, "hgap", {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                if (hgap >= 0 && hgap !== aValue) {
                    hgap = aValue;
                    applyHGap();
                }
            }
        });
        Object.defineProperty(this, "vgap", {
            configurable: true,
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                if (vgap >= 0 && vgap !== aValue) {
                    vgap = aValue;
                    applyVGap();
                }
            }
        });
        function applyOrientation() {
            self.forEach(function (w) {
                format(w);
            });
        }
        applyOrientation();
        Object.defineProperty(this, "orientation", {
            configurable: true,
            get: function () {
                return orientation;
            },
            set: function (aValue) {
                if (orientation !== aValue) {
                    orientation = aValue;
                    applyOrientation();
                }
            }
        });


        function format(w) {
            var visible = w.visible;
            var ws = w.element.style;
            ws.marginLeft = 0 + 'px';
            ws.marginRight = 0 + 'px';
            ws.marginTop = 0 + 'px';
            if (orientation === Ui.Orientation.HORIZONTAL) {
                if (self.element.firstElementChild !== w.element) {
                    ws.marginLeft = hgap + 'px';
                    ws.marginRight = 0 + 'px';
                }
                ws.top = '';
                ws.bottom = '';
                ws.position = 'relative';
                ws.height = 100 + '%';
                ws.display = visible ? 'inline-block' : 'none';
                ws.float = '';
            } else {
                if (self.element.firstElementChild !== w.element) {
                    ws.marginTop = vgap + 'px';
                    ws.marginBottom = 0 + 'px';
                }
                ws.position = 'relative';
                ws.display = visible ? 'block' : 'none';
                ws.left = 0 + 'px';
                ws.right = '';
                ws.width = 100 + 'px';
            }
            ws.verticalAlign = 'middle';
        }

        var superAdd = this.add;
        function add(w) {
            if (orientation === Ui.Orientation.HORIZONTAL) {
                superAdd(w);
                format(w);
            } else {
                superAdd(w);
                format(w);
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function getTop(w) {
            if (w.parent !== self)
                throw "widget should be a child of this container";
            return orientation === Ui.Orientation.HORIZONTAL ? 0 : w.element.offsetTop;
        }
        Object.defineProperty(this, 'getTop', {
            get: function () {
                return getTop;
            }
        });

        function getLeft(w) {
            if (w.parent !== self)
                throw "widget should be a child of this container";
            return orientation === Ui.Orientation.HORIZONTAL ? w.element.offsetLeft : 0;
        }
        Object.defineProperty(this, 'getLeft', {
            get: function () {
                return getLeft;
            }
        });
    }
    extend(Box, Container);
    return Box;
});