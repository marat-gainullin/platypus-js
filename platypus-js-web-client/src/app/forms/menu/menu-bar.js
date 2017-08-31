define([
    '../../id',
    '../../extend',
    '../container'], function (
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
    }
    extend(MenuBar, Container);
});