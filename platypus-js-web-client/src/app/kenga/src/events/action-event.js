define([
    'core/extend',
    '../event'], function(
        extend,
        Event){
    function ActionEvent(w) {
        Event.call(this, w, w);
    }
    extend(ActionEvent, Event);
    return ActionEvent;
});