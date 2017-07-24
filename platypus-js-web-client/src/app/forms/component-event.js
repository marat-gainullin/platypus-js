define(['./event', '../extend'], function(Event, extend){
    function ComponentEvent(aWidget) {
        Event.call(this, aWidget, aWidget);
    }
    extend(ComponentEvent, Event);
    return ComponentEvent;
});