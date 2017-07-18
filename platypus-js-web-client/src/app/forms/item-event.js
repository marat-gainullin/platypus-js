define(['./event'], function(Event){
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