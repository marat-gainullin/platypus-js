define([
    '../../extend',
    '../widget'], function (
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
    }
    extend(MenuElement, Widget);
    return MenuElement;
});