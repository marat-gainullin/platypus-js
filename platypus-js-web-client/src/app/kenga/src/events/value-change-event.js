define(['../event', 'core/extend'], function(Event, extend){
    function ValueChangeEvent(w, oldValue, newValue) {
        Event.call(this, w, w);
        Object.defineProperty(this, 'newValue', {
            get: function(){
                return newValue;
            }
        });
        Object.defineProperty(this, 'oldValue', {
            get: function(){
                return oldValue;
            }
        });
    }
    extend(ValueChangeEvent, Event);
    return ValueChangeEvent;
});