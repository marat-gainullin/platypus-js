define([
    'core/extend',
    './boolean-menu-item'], function (
        extend,
        BooleanMenuItem) {
    function CheckMenuItem(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = false;
        if (arguments.length < 1)
            text = '';
        var check = document.createElement('input');
        check.type = 'checkbox';

        BooleanMenuItem.call(this, check, text, selected, onActionPerformed);
        var self = this;
    }
    extend(CheckMenuItem, BooleanMenuItem);
    return CheckMenuItem;
});