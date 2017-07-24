define([
    '../extend',
    './container',
    '../id'
], function (
        extend,
        Container,
        Id) {
    function Flow(hgap, vgap) {
        Container.call(this);

        var self = this;

        if (arguments.length < 2) {
            vgap = 0;
        }
        if (arguments.length < 1) {
            vgap = 0;
            hgap = 0;
        }

        this.element.classList.add('p-flow');

        this.element.id = Id.generate();

        var style = document.createElement('style');
        function formatChildren() {
            style.innerHTML =
                    'div#' + self.element.id + ' div {' +
                    'margin-left: ' + hgap + 'px;' +
                    'margin-top: ' + vgap + 'px;' +
                    'display: ' + 'inline-block;' +
                    'verticalAlign: bottom;' +
                    '}';
        }
        formatChildren();
        this.element.appendChild(style);

        Object.defineProperty(this, "hgap", {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                hgap = aValue;
                formatChildren();
            }
        });
        Object.defineProperty(this, "vgap", {
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                vgap = aValue;
                formatChildren();
            }
        });

        var superAdd = this.add;
        function add(w, beforeIndex) {
            if (w) {
                if (w.parent === self)
                    throw 'A widget already added to this container';
                superAdd(w, beforeIndex);
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

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