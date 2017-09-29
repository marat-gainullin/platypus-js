define([
    'core/extend',
    './text-field'], function (
        extend,
        TextField) {
    function TextArea(shell) {
        var box = document.createElement('textarea');
        if(!shell)
            shell = box;
        
        TextField.call(this, '', box, shell);
        var self = this;
        shell.classList.add('p-scroll');
        shell.classList.add('p-vertical-scroll-filler');
        shell.classList.add('p-horizontal-scroll-filler');
    }
    extend(TextArea, TextField);
    return TextArea;
});
