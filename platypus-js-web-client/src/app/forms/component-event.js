define(['./event'], function(Event){
    function ComponentEvent(aWidget) {
        Event.call(this, aWidget, aWidget);
    }
    extend(ComponentEvent, Event);
    return ComponentEvent;
});