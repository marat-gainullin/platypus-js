define([
    'ui/utils',
    'core/extend',
    'ui/widget'], function (
        Ui,
        extend,
        Widget) {
    function MenuElement() {
        Widget.call(this);
        var self = this;

        this.opaque = true;

        var subMenu = null;
        Object.defineProperty(this, 'subMenu', {
            get: function () {
                return subMenu;
            },
            set: function (aValue) {
                if (subMenu !== aValue) {
                    subMenu = aValue;
                    if (subMenu) {
                        self.element.classList.add('p-menu-item-submenu');
                    } else {
                        self.element.classList.remove('p-menu-item-submenu');
                    }
                }
            }
        });
        Ui.on(this.element, Ui.Events.MOUSEOVER, function (evt) {
            if (Ui.isMenuSession()) {
                self.parent.forEach(function (item) {
                    if(item.subMenu)
                        item.subMenu.close();
                });
                if (subMenu) {
                    subMenu.showRelativeTo(self.element, self.parent.element.className.indexOf('menu-bar') === -1);
                }
            }
        });
    }
    extend(MenuElement, Widget);
    return MenuElement;
});