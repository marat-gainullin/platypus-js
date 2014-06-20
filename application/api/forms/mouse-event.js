(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.MouseEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.MouseEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor MouseEvent MouseEvent
     */
    P.MouseEvent = function MouseEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(MouseEvent.superclass)
            MouseEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "MouseEvent", {value: MouseEvent});
    Object.defineProperty(MouseEvent.prototype, "button", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.button;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * Which, if any, of the mouse buttons has changed state.
         * Values: 0 - no button, 1 - button 1, 2 - button 2, 3 - button 3.
         * @property button
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.button = 0;
    }
    Object.defineProperty(MouseEvent.prototype, "altDown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.altDown;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * Alt key is down on this event.
         * @property altDown
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.altDown = true;
    }
    Object.defineProperty(MouseEvent.prototype, "controlDown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.controlDown;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * Ctrl key is down on this event.
         * @property controlDown
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.controlDown = true;
    }
    Object.defineProperty(MouseEvent.prototype, "shiftDown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.shiftDown;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * Shift key is down on this event.
         * @property shiftDown
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.shiftDown = true;
    }
    Object.defineProperty(MouseEvent.prototype, "clickCount", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.clickCount;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * The number of mouse clicks associated with this event.
         * @property clickCount
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.clickCount = 0;
    }
    Object.defineProperty(MouseEvent.prototype, "X", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.X;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * X cursor coordinate in component's space.
         * @property X
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.X = 0;
    }
    Object.defineProperty(MouseEvent.prototype, "metaDown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.metaDown;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * Meta key is down on this event.
         * @property metaDown
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.metaDown = true;
    }
    Object.defineProperty(MouseEvent.prototype, "Y", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.Y;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * Y cursor coordinate in component's space.
         * @property Y
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.Y = 0;
    }
    Object.defineProperty(MouseEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!MouseEvent){
        /**
         * The source component object of the event.
         * @property source
         * @memberOf MouseEvent
         */
        P.MouseEvent.prototype.source = {};
    }
})();