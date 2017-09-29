define([
    'core/extend',
    './menu-element'], function (
        extend,
        MenuElement) {
    function MenuSeparator() {
        MenuElement.call(this);

        var self = this;

        this.opaque = true;

        this.element.classList.add('p-menu-separator');
    }
    extend(MenuSeparator, MenuElement);
    return MenuSeparator;
});