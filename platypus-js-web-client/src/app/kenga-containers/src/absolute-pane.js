define([
    'core/extend',
    './anchors-pane'], function (
        extend,
        Anchors) {
    function Absolute() {
        Anchors.call(this);
    }
    extend(Absolute, Anchors);
    return Absolute;
});
