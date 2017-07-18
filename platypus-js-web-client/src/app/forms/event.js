define(function () {
    function Event(source, target, event) {
        Object.defineProperty(module, 'event', {
            get: function () {
                return event;
            }
        });
        Object.defineProperty(module, 'source', {
            get: function () {
                return source;
            }
        });
        Object.defineProperty(module, 'target', {
            get: function () {
                if (target && target['p-widget'])
                    return target['p-widget'];
                else
                    return target;
            }
        });
    }
    return Event;
});