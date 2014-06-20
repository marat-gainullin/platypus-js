(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.KeyEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.KeyEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor KeyEvent KeyEvent
     */
    P.KeyEvent = function KeyEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(KeyEvent.superclass)
            KeyEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "KeyEvent", {value: KeyEvent});
    Object.defineProperty(KeyEvent.prototype, "altDown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.altDown;
            return P.boxAsJs(value);
        }
    });
    if(!KeyEvent){
        /**
         * Alt key is down on this event.
         * @property altDown
         * @memberOf KeyEvent
         */
        P.KeyEvent.prototype.altDown = true;
    }
    Object.defineProperty(KeyEvent.prototype, "controlDown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.controlDown;
            return P.boxAsJs(value);
        }
    });
    if(!KeyEvent){
        /**
         * Ctrl key is down on this event.
         * @property controlDown
         * @memberOf KeyEvent
         */
        P.KeyEvent.prototype.controlDown = true;
    }
    Object.defineProperty(KeyEvent.prototype, "shiftDown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.shiftDown;
            return P.boxAsJs(value);
        }
    });
    if(!KeyEvent){
        /**
         * Shift key is down on this event.
         * @property shiftDown
         * @memberOf KeyEvent
         */
        P.KeyEvent.prototype.shiftDown = true;
    }
    Object.defineProperty(KeyEvent.prototype, "metaDown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.metaDown;
            return P.boxAsJs(value);
        }
    });
    if(!KeyEvent){
        /**
         * Meta key is down on this event.
         * @property metaDown
         * @memberOf KeyEvent
         */
        P.KeyEvent.prototype.metaDown = true;
    }
    Object.defineProperty(KeyEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!KeyEvent){
        /**
         * The source component object of the event.
         * @property source
         * @memberOf KeyEvent
         */
        P.KeyEvent.prototype.source = {};
    }
    Object.defineProperty(KeyEvent.prototype, "key", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.key;
            return P.boxAsJs(value);
        }
    });
    if(!KeyEvent){
        /**
         * Key code associated with this event.
         * @property key
         * @memberOf KeyEvent
         */
        P.KeyEvent.prototype.key = 0;
    }
})();