(function() {
    var javaClass = Java.type("com.eas.client.forms.events.KeyEvent");
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
            value: function() {
                return delegate;
            }
        });
        if(P.KeyEvent.superclass)
            P.KeyEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "altDown", {
            get: function() {
                var value = delegate.altDown;
                return P.boxAsJs(value);
            }
        });
        if(!P.KeyEvent){
            /**
             * Alt key is down on this event.
             * @property altDown
             * @memberOf KeyEvent
             */
            P.KeyEvent.prototype.altDown = true;
        }
        Object.defineProperty(this, "controlDown", {
            get: function() {
                var value = delegate.controlDown;
                return P.boxAsJs(value);
            }
        });
        if(!P.KeyEvent){
            /**
             * Ctrl key is down on this event.
             * @property controlDown
             * @memberOf KeyEvent
             */
            P.KeyEvent.prototype.controlDown = true;
        }
        Object.defineProperty(this, "shiftDown", {
            get: function() {
                var value = delegate.shiftDown;
                return P.boxAsJs(value);
            }
        });
        if(!P.KeyEvent){
            /**
             * Shift key is down on this event.
             * @property shiftDown
             * @memberOf KeyEvent
             */
            P.KeyEvent.prototype.shiftDown = true;
        }
        Object.defineProperty(this, "char", {
            get: function() {
                var value = delegate.char;
                return P.boxAsJs(value);
            }
        });
        if(!P.KeyEvent){
            /**
             * Char associated with this event.
             * @property char
             * @memberOf KeyEvent
             */
            P.KeyEvent.prototype.char = '';
        }
        Object.defineProperty(this, "metaDown", {
            get: function() {
                var value = delegate.metaDown;
                return P.boxAsJs(value);
            }
        });
        if(!P.KeyEvent){
            /**
             * Meta key is down on this event.
             * @property metaDown
             * @memberOf KeyEvent
             */
            P.KeyEvent.prototype.metaDown = true;
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.KeyEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf KeyEvent
             */
            P.KeyEvent.prototype.source = {};
        }
        Object.defineProperty(this, "key", {
            get: function() {
                var value = delegate.key;
                return P.boxAsJs(value);
            }
        });
        if(!P.KeyEvent){
            /**
             * Key code associated with this event.
             * @property key
             * @memberOf KeyEvent
             */
            P.KeyEvent.prototype.key = 0;
        }
    };
})();