(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.MouseEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.MouseEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor MouseEvent MouseEvent
     */
    P.MouseEvent = function () {

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
         * Which, if any, of the mouse buttons has changed state.
         * Values: 0 - no button, 1 - button 1, 2 - button 2, 3 - button 3.
         * @property button
         * @memberOf MouseEvent
         */
        Object.defineProperty(this, "button", {
            get: function() {
                var value = delegate.button;
                return P.boxAsJs(value);
            }
        });

        /**
         * Alt key is down on this event.
         * @property altDown
         * @memberOf MouseEvent
         */
        Object.defineProperty(this, "altDown", {
            get: function() {
                var value = delegate.altDown;
                return P.boxAsJs(value);
            }
        });

        /**
         * The number of mouse clicks associated with this event.
         * @property clickCount
         * @memberOf MouseEvent
         */
        Object.defineProperty(this, "clickCount", {
            get: function() {
                var value = delegate.clickCount;
                return P.boxAsJs(value);
            }
        });

        /**
         * Ctrl key is down on this event.
         * @property controlDown
         * @memberOf MouseEvent
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
         * @memberOf MouseEvent
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
         * @memberOf MouseEvent
         */
        Object.defineProperty(this, "metaDown", {
            get: function() {
                var value = delegate.metaDown;
                return P.boxAsJs(value);
            }
        });

        /**
         * X cursor coordinate in component's space.
         * @property X
         * @memberOf MouseEvent
         */
        Object.defineProperty(this, "X", {
            get: function() {
                var value = delegate.X;
                return P.boxAsJs(value);
            }
        });

        /**
         * Y cursor coordinate in component's space.
         * @property Y
         * @memberOf MouseEvent
         */
        Object.defineProperty(this, "Y", {
            get: function() {
                var value = delegate.Y;
                return P.boxAsJs(value);
            }
        });

        /**
         * The source component object of the event.
         * @property source
         * @memberOf MouseEvent
         */
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();