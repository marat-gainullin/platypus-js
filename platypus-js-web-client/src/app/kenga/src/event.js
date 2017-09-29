define(function () {
    function Event(source, target, event) {
        Object.defineProperty(this, 'event', {
            get: function () {
                return event;
            }
        });
        Object.defineProperty(this, 'source', {
            get: function () {
                return source;
            }
        });
        Object.defineProperty(this, 'target', {
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