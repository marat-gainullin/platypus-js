define([
    'ui/utils',
    'core/id',
    'core/extend',
    'ui/container'], function (
        Ui,
        Id,
        extend,
        Container) {
    function Menu() {
        Container.call(this);
        var self = this;

        this.element.classList.add('p-menu');
        this.element.id = 'p-' + Id.generate();

        var gapsStyle = document.createElement('style');
        gapsStyle.innerHTML =
                'div#' + self.element.id + ' > .p-widget {' +
                'display: block;' +
                '}';
        this.element.appendChild(gapsStyle);

        function showRelativeTo(anElement, horizontal) {
            if (!self.element.parentElement) {
                this.element.classList.remove('p-menu-horizontal-rel');
                this.element.classList.remove('p-menu-vertical-rel');
                var top = Ui.absoluteTop(anElement);
                var left = Ui.absoluteLeft(anElement);
                if (horizontal) {
                    self.element.style.top = top + 'px';
                    self.element.style.left = (left + anElement.offsetWidth) + 'px';
                    this.element.classList.add('p-menu-horizontal-rel');
                } else {
                    self.element.style.top = (top + anElement.offsetHeight) + 'px';
                    self.element.style.left = left + 'px';
                    this.element.classList.add('p-menu-vertical-rel');
                }
                document.body.appendChild(self.element);
            }
        }
        Object.defineProperty(this, 'showRelativeTo', {
            get: function () {
                return showRelativeTo;
            }
        });

        function showAt(left, top) {
            if (self.element.parentElement)
                throw 'Menu is already shown';
            this.element.classList.remove('p-menu-horizontal-rel');
            this.element.classList.remove('p-menu-vertical-rel');
            self.element.style.top = top + 'px';
            self.element.style.left = left + 'px';
            document.body.appendChild(self.element);
        }
        Object.defineProperty(this, 'showAt', {
            get: function () {
                return showAt;
            }
        });

        function close() {
            if (self.element.parentElement) {
                self.element.parentElement.removeChild(self.element);
                self.forEach(function (item) {
                    if (item.subMenu)
                        item.subMenu.close();
                });
            }
        }
        Object.defineProperty(this, 'close', {
            get: function () {
                return close;
            }
        });
    }
    extend(Menu, Container);
    return Menu;
});