define([
    'core/extend',
    './boolean-menu-item'], function (
        extend,
        BooleanMenuItem) {
    function RadioMenuItem(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = false;
        if (arguments.length < 1)
            text = '';
        var radio = document.createElement('input');
        radio.type = 'radio';

        BooleanMenuItem.call(this, radio, text, selected, onActionPerformed);
        var self = this;
    }
    extend(RadioMenuItem, BooleanMenuItem);
    return RadioMenuItem;
});