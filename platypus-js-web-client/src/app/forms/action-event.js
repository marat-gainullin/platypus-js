define(['./event'], function(Event){
    function ActionEvent(w) {
        Event.call(this, w, w);
    }
    extend(ActionEvent, Event);
    return ActionEvent;
});