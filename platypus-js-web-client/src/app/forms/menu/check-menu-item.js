define([
    '../../extend',
    './boolean-menu-element'], function (
        extend,
        BooleanMenuItem) {
    function CheckMenuItem(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = null;
        if (arguments.length < 1)
            text = '';
        var check = document.createElement('input');
        check.type = 'check';

        BooleanMenuItem.call(this, check, text, selected, onActionPerformed);
        var self = this;
    }
    extend(CheckMenuItem, BooleanMenuItem);
    return CheckMenuItem;
});