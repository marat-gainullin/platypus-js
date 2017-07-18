define(['./event'], function (Event) {
    function KeyEvent(aWidget, aEvent) {
        Event.call(this, aWidget, aEvent.target, aEvent);
        Object.defineProperty(this, "altDown", {
            get: function () {
                return aEvent.altKey;
            }
        });
        Object.defineProperty(this, "controlDown", {
            get: function () {
                return aEvent.ctrlKey;
            }
        });
        Object.defineProperty(this, "shiftDown", {
            get: function () {
                return aEvent.shiftKey;
            }
        });
        Object.defineProperty(this, "metaDown", {
            get: function () {
                return aEvent.metaKey;
            }
        });
        Object.defineProperty(this, "key", {
            get: function () {
                return aEvent.keyCode || 0;
            }
        });
        Object.defineProperty(this, "char", {
            get: function () {
                return aEvent.charCode || 0;
            }
        });
    }
    extend(KeyEvent, Event);
    return KeyEvent;
});
