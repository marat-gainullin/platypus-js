define(['./event', '../extend'], function(Event, extend){
    function ItemEvent(source, item){
        Event.call(this, source, source);
        Object.defineProperty(module, 'item', {
            get: function(){
                return item;
            }
        });
    }
    extend(ItemEvent, Event);
});