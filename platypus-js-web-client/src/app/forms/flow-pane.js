define([
    '../extend',
    './container'], function (
        extend,
        Container) {
    function Flow(hgap, vgap) {
        Container.call(this);

        var self = this;

        this.element.style.whiteSpace = 'normal';
        this.element.style.lineHeight = 0 + 'px';
        this.element.style.overflow = 'auto';

        if (arguments.length < 2) {
            vgap = 0;
        }
        if (arguments.length < 1) {
            vgap = 0;
            hgap = 0;
        }

        Object.defineProperty(this, "hgap", {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                hgap = aValue;
                self.forEach(function (w) {
                    w.element.style.marginLeft = hgap + 'px';
                });
            }
        });
        Object.defineProperty(this, "vgap", {
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                vgap = aValue;
                self.forEach(function (w) {
                    w.element.style.marginTop = vgap + 'px';
                });
            }
        });


        var superAdd = this.add;
        function add(w, beforeIndex) {
            if (w) {
                if (w.parent === self)
                    throw 'A widget already added to this container';
                format(w);
                superAdd(w, beforeIndex);
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function format(w) {
            var ws = w.element.style;
            ws.marginLeft = hgap + 'px';
            ws.marginTop = vgap + 'px';
            ws.display = 'inline-block';
            ws.verticalAlign = 'bottom';
        }

        function getTop(aWidget) {
            if (aWidget.parent !== this)
                throw "widget should be a child of this container";
            return aWidget.element.offsetTop;
        }
        Object.defineProperty(this, 'getTop', {
            get: function () {
                return getTop;
            }
        });

        function getLeft(aWidget) {
            if (aWidget.parent !== this)
                throw "widget should be a child of this container";
            return aWidget.element.offsetLeft;
        }
        Object.defineProperty(this, 'getLeft', {
            get: function () {
                return getLeft;
            }
        });
    }
    extend(Flow, Container);
    return Flow;
});