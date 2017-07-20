define([
    '../extend',
    './box-pane'], function (
        extend,
        Box) {
    function Toolbar(hgap) {
        Box.call(this, hgap);

        var self = this;
        
        delete this.orientation;
        delete this.vgap;
        
        this.element.classList.add("toolbar");
        this.element.classList.add("btn-group");
    }
    extend(Toolbar, Box);
    return Toolbar;
});