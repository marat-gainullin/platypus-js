define(['./event', '../extend'], function(Event, extend){
    function WindowEvent(aWidget) {
        Event.call(this, aWidget, aWidget);
    }
    extend(WindowEvent, Event);
    return WindowEvent;
});