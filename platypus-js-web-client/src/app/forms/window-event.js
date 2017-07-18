define(['./event'], function(Event){
    function WindowEvent(aWidget) {
        Event.call(this, aWidget, aWidget);
    }
    extend(WindowEvent, Event);
    return WindowEvent;
});