define(['../event'], function (Event) {
    function CellRenderEvent(aSource, viewIndex, element, aRendered, aCell) {
        Event.call(this, aSource, aSource);
        Object.defineProperty(this, "viewIndex", {
            get: function () {
                return viewIndex;
            }
        });
        Object.defineProperty(this, "element", {
            get: function () {
                return element;
            }
        });
        Object.defineProperty(this, "object", {
            get: function () {
                return aRendered;
            }
        });
        Object.defineProperty(this, "cell", {
            get: function () {
                return aCell;
            }
        });
    }
    extend(CellRenderEvent, Event);
    return CellRenderEvent;
});