define([
    '../event',
    'core/extend'], function (
        Event,
        extend) {
    function ComponentEvent(w) {
        Event.call(this, w, w);
    }
    extend(ComponentEvent, Event);
    return ComponentEvent;
});