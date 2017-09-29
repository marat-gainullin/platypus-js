define([
    'core/extend',
    '../event'], function(
        extend,
        Event){
    function FocusEvent(w) {
        Event.call(this, w, w);
    }
    extend(FocusEvent, Event);
    return FocusEvent;
});