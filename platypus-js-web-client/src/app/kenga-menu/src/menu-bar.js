define([
    'ui/utils',
    'core/id',
    'core/extend',
    'ui/container'], function (
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

        function startItem(target){
            var item = findWidgetByElement(target);
            if (item && item.subMenu) {
                Ui.startMenuSession(self);
                item.subMenu.showRelativeTo(item.element, false);
            }            
        }

        Ui.on(this.element, Ui.Events.MOUSEDOWN, function (evt) {
            evt.stopPropagation();
            startItem(evt.target);
        }, true);
        Ui.on(this.element, Ui.Events.CLICK, function (evt) {
            evt.stopPropagation();
            startItem(evt.target);
        }, true);
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