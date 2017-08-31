define([
    '../../id',
    '../../extend',
    '../../ui',
    '../container'], function (
        Id,
        extend,
        Ui,
        Container) {
    function Box(orientation, hgap, vgap) {
        Container.call(this);

        var self = this;

        if (arguments.length < 3)
            vgap = 0;
        if (arguments.length < 2)
            hgap = 0;
        if (arguments.length < 1)
            orientation = Ui.Orientation.HORIZONTAL;

        this.element.classList.add('p-box-horizontal');
        this.element.classList.add('p-horizontal-scroll-filler');
        this.element.id = 'p-' + Id.generate();

        var gapsStyle = document.createElement('style');
        this.element.appendChild(gapsStyle);
        
        function formatChildren() {
            if (orientation === Ui.Orientation.HORIZONTAL) {
                self.element.classList.remove('p-box-vertical');
                self.element.classList.remove('p-vertical-scroll-filler');
                self.element.classList.add('p-box-horizontal');
                self.element.classList.add('p-horizontal-scroll-filler');
                gapsStyle.innerHTML =
                        'div#' + self.element.id + ' > .p-widget {' +
                        'height: 100%;' +
                        'display: inline-block;' +
                        '}' + 
                        'div#' + self.element.id + ' > .p-widget:nth-child(n + ' + 3 + ') {' +
                        'margin-left: ' + hgap + 'px;' +
                        '}';
            } else {
                self.element.classList.add('p-box-vertical');
                self.element.classList.add('p-vertical-scroll-filler');
                self.element.classList.remove('p-box-horizontal');
                self.element.classList.remove('p-horizontal-scroll-filler');
                gapsStyle.innerHTML =
                        'div#' + self.element.id + ' > .p-widget {' +
                        'display: block;' +
                        '}' + 
                        'div#' + self.element.id + ' > .p-widget:nth-child(n + ' + 3 + ') {' +
                        'margin-top: ' + vgap + 'px;' +
                        '}';
            }
        }
        formatChildren();

        Object.defineProperty(this, "hgap", {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                if (hgap >= 0 && hgap !== aValue) {
                    hgap = aValue;
                    formatChildren();
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
                    formatChildren();
                }
            }
        });
        Object.defineProperty(this, "orientation", {
            configurable: true,
            get: function () {
                return orientation;
            },
            set: function (aValue) {
                if (orientation !== aValue) {
                    orientation = aValue;
                    formatChildren();
                }
            }
        });

        var superAdd = this.add;
        function add(w) {
            if (orientation === Ui.Orientation.HORIZONTAL) {
                superAdd(w);
            } else {
                superAdd(w);
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function ajustLeft(w, aValue) {
        }
        Object.defineProperty(this, 'ajustLeft', {
            get: function () {
                return ajustLeft;
            }
        });

        function ajustWidth(w, aValue) {
            if(orientation === Ui.Orientation.HORIZONTAL){
                w.element.style.width = aValue + 'px';
            }
        }
        Object.defineProperty(this, 'ajustWidth', {
            get: function () {
                return ajustWidth;
            }
        });

        function ajustTop(w, aValue) {
        }
        Object.defineProperty(this, 'ajustTop', {
            get: function () {
                return ajustTop;
            }
        });
        function ajustHeight(w, aValue) {
            if(orientation === Ui.Orientation.VERTICAL){
                w.element.style.height = aValue + 'px';
            }
        }
        Object.defineProperty(this, 'ajustHeight', {
            get: function () {
                return ajustHeight;
            }
        });
    }
    extend(Box, Container);
    return Box;
});