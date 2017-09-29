define([
    'core/extend',
    '../event'], function(
        extend,
        Event){
    function BlurEvent(w) {
        Event.call(this, w, w);
    }
    extend(BlurEvent, Event);
    return BlurEvent;
});