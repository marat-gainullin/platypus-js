(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.KeyEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.KeyEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor KeyEvent KeyEvent
     */
    P.KeyEvent = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * Alt key is down on this event.
         * @property altDown
         * @memberOf KeyEvent
         */
        Object.defineProperty(this, "altDown", {
            get: function() {
                var value = delegate.altDown;
                return P.boxAsJs(value);
            }
        });

        /**
         * Ctrl key is down on this event.
         * @property controlDown
         * @memberOf KeyEvent
         */
        Object.defineProperty(this, "controlDown", {
            get: function() {
                var value = delegate.controlDown;
                return P.boxAsJs(value);
            }
        });

        /**
         * Shift key is down on this event.
         * @property shiftDown
         * @memberOf KeyEvent
         */
        Object.defineProperty(this, "shiftDown", {
            get: function() {
                var value = delegate.shiftDown;
                return P.boxAsJs(value);
            }
        });

        /**
         * Meta key is down on this event.
         * @property metaDown
         * @memberOf KeyEvent
         */
        Object.defineProperty(this, "metaDown", {
            get: function() {
                var value = delegate.metaDown;
                return P.boxAsJs(value);
            }
        });

        /**
         * The source component object of the event.
         * @property source
         * @memberOf KeyEvent
         */
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });

        /**
         * Key code associated with this event.
         * @property key
         * @memberOf KeyEvent
         */
        Object.defineProperty(this, "key", {
            get: function() {
                var value = delegate.key;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();