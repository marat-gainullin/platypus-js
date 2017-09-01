define([
    '../../ui',
    '../../id',
    '../../extend',
    '../container'], function (
        Ui,
        Id,
        extend,
        Container) {
    function MenuBar() {
        Container.call(this);
        var self = this;

        this.element.classList.add('p-menu-bar');
        this.element.id = 'p-' + Id.generate();

        var gapsStyle = document.createElement('style');
        gapsStyle.innerHTML =
                'div#' + self.element.id + ' > .p-widget {' +
                'height: 100%;' +
                'display: inline-block;' +
                '}';
        this.element.appendChild(gapsStyle);

        function findWidgetByElement(anElement) {
            var currentElement = anElement;
            while (currentElement !== null && !('p-widget' in currentElement) && currentElement !== document.body)
                currentElement = currentElement.parentElement;
            return currentElement !== document.body && currentElement !== null ? currentElement['p-widget'] : null;
        }

        Ui.on(this.element, Ui.Events.MOUSEDOWN, function (evt) {
            var item = findWidgetByElement(evt.target);
            if (item && item.subMenu) {
                Ui.startMenuSession(self);
                item.subMenu.showRelativeTo(item.element, false);
            }
        });
        function close() {
            if (self.element.parentElement) {
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
    extend(MenuBar, Container);
    return MenuBar;
});