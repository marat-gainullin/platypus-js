define(['./event'], function(Event){
    function ActionEvent(aWidget) {
        Event.call(this, aWidget, aWidget);
    }
    extend(ActionEvent, Event);
    return ActionEvent;
});