/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.forms.events.KeyEvent";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor KeyEvent KeyEvent
     */
    function KeyEvent() {
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
        if(KeyEvent.superclass)
            KeyEvent.superclass.constructor.apply(this, arguments);
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
         * Char associated with this event.
         */
        this.char = '';
        Object.defineProperty(this, "char", {
            get: function() {
                var value = delegate.char;
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
         * The source object of the event.
         */
        this.source = new Object();
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return B.boxAsJs(value);
            }
        });

        /**
         * Key code associated with this event.
         */
        this.key = 0;
        Object.defineProperty(this, "key", {
            get: function() {
                var value = delegate.key;
                return B.boxAsJs(value);
            }
        });

    }

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new KeyEvent(aDelegate);
    });
    return KeyEvent;
});