define([
    '../event',
    'core/extend'], function (
        Event,
        extend) {
    function ItemEvent(source, item) {
        Event.call(this, source, item);

        Object.defineProperty(this, 'item', {
            get: function () {
                return item;
            }
        });
    }
    extend(ItemEvent, Event);
    return ItemEvent;
});