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

        this.element.id = 'p-' + Id.generate();

        var style = document.createElement('style');
        function formatChildren() {
            style.innerHTML =
                    'div#' + self.element.id + ' > .p-widget {' +
                    'margin-left: ' + hgap + 'px;' +
                    'margin-top: ' + vgap + 'px;' +
                    '}';
        }
        formatChildren();
        this.element.appendChild(style);

        Object.defineProperty(this, "hgap", {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                if(hgap !== aValue){
                    hgap = aValue;
                    formatChildren();
                    self.element.style.paddingRight = hgap + 'px';
                }
            }
        });
        Object.defineProperty(this, "vgap", {
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                if(vgap !== aValue){
                    vgap = aValue;
                    formatChildren();
                    self.element.style.paddingBottom = vgap + 'px';
                }
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
    }
    extend(Flow, Container);
    return Flow;
});