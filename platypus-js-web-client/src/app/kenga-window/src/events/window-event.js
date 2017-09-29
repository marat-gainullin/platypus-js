define(['../event', 'core/extend'], function(Event, extend){
    function WindowEvent(w) {
        Event.call(this, w, w);
    }
    extend(WindowEvent, Event);
    return WindowEvent;
});