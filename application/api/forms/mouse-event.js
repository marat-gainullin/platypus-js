/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.forms.events.MouseEvent";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor MouseEvent MouseEvent
     */
    function MouseEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(MouseEvent.superclass)
            MouseEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        /**
         * Alt key is down on this event.
         */
        this.altDown = true;
        Object.defineProperty(this, "altDown", {
            get: function() {
                var value = delegate.altDown;
                return B.boxAsJs(value);
            }
        });

        /**
         * Which, if any, of the mouse buttons has changed state.
         * Values: 0 - no button, 1 - button 1, 2 - button 2, 3 - button 3.
         */
        this.button = 0;
        Object.defineProperty(this, "button", {
            get: function() {
                var value = delegate.button;
                return B.boxAsJs(value);
            }
        });

        /**
         * The number of mouse clicks associated with this event.
         */
        this.clickCount = 0;
        Object.defineProperty(this, "clickCount", {
            get: function() {
                var value = delegate.clickCount;
                return B.boxAsJs(value);
            }
        });

        /**
         * Ctrl key is down on this event.
         */
        this.controlDown = true;
        Object.defineProperty(this, "controlDown", {
            get: function() {
                var value = delegate.controlDown;
                return B.boxAsJs(value);
            }
        });

        /**
         * Shift key is down on this event.
         */
        this.shiftDown = true;
        Object.defineProperty(this, "shiftDown", {
            get: function() {
                var value = delegate.shiftDown;
                return B.boxAsJs(value);
            }
        });

        /**
         * X cursor coordinate in component's space.
         */
        this.x = 0;
        Object.defineProperty(this, "x", {
            get: function() {
                var value = delegate.x;
                return B.boxAsJs(value);
            }
        });

        /**
         * Meta key is down on this event.
         */
        this.metaDown = true;
        Object.defineProperty(this, "metaDown", {
            get: function() {
                var value = delegate.metaDown;
                return B.boxAsJs(value);
            }
        });

        /**
         * Y cursor coordinate in component's space.
         */
        this.y = 0;
        Object.defineProperty(this, "y", {
            get: function() {
                var value = delegate.y;
                return B.boxAsJs(value);
            }
        });

        /**
         * The source object of the event.
         */
        this.source = new Object();
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return B.boxAsJs(value);
            }
        });

    }

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new MouseEvent(aDelegate);
    });
    return MouseEvent;
});