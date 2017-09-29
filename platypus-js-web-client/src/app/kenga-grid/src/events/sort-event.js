define([
    'core/extend',
    '../event'], function(
        extend,
        Event){
    function SortEvent(w) {
        Event.call(this, w, w);
    }
    extend(SortEvent, Event);
    return SortEvent;
});