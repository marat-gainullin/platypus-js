define(['./event', '../extend'], function(Event, extend){
    function WindowEvent(w) {
        Event.call(this, w, w);
    }
    extend(WindowEvent, Event);
    return WindowEvent;
});