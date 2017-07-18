define(['./event'], function (Event) {
    function MouseEvent(aWidget, aEvent, aClickCount) {
        Event.call(this, aWidget, aEvent.target, aEvent);
        Object.defineProperty(this, "x", {
            get: function () {
                return aEvent.x;
            }
        });
        Object.defineProperty(this, "y", {
            get: function () {
                return aEvent.y;
            }
        });
        Object.defineProperty(this, "screenX", {
            get: function () {
                aEvent.screenX;
            }
        });
        Object.defineProperty(this, "screenY", {
            get: function () {
                aEvent.screenY;
            }
        });
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
        Object.defineProperty(this, "button", {
            get: function () {
                switch (aEvent.button) {
                    case 1/*BUTTON_LEFT*/ :
                        return 1;
                    case 2/*BUTTON_RIGHT*/ :
                        return 2;
                    case 4/*BUTTON_MIDDLE*/ :
                        return 3;
                    default :
                        return 0;
                }
            }
        });
        Object.defineProperty(this, "clickCount", {
            get: function () {
                if (aClickCount)
                    return aClickCount;
                else
                    return 0;
            }
        });
    }
    extend(MouseEvent, Event);
    return MouseEvent;
});