define([
    '../../id',
    '../../extend',
    '../container'], function (
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
    }
    extend(Menu, Container);
});