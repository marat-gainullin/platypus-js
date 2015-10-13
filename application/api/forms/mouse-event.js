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
        Object.defineProperty(this, "altDown", {
            get: function() {
                var value = delegate.altDown;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "button", {
            get: function() {
                var value = delegate.button;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "clickCount", {
            get: function() {
                var value = delegate.clickCount;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "controlDown", {
            get: function() {
                var value = delegate.controlDown;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "shiftDown", {
            get: function() {
                var value = delegate.shiftDown;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "x", {
            get: function() {
                var value = delegate.x;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "metaDown", {
            get: function() {
                var value = delegate.metaDown;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "y", {
            get: function() {
                var value = delegate.y;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return B.boxAsJs(value);
            }
        });

    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new MouseEvent(aDelegate);
    });
    return MouseEvent;
});