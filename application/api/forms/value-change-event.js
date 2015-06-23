(function() {
    var className = "com.eas.client.forms.events.ValueChangeEvent";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.ValueChangeEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ValueChangeEvent ValueChangeEvent
     */
    P.ValueChangeEvent = function () {
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
        if(P.ValueChangeEvent.superclass)
            P.ValueChangeEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "newValue", {
            get: function() {
                var value = delegate.newValue;
                return P.boxAsJs(value);
            }
        });
        if(!P.ValueChangeEvent){
            /**
             * Generated property jsDoc.
             * @property newValue
             * @memberOf ValueChangeEvent
             */
            P.ValueChangeEvent.prototype.newValue = {};
        }
        Object.defineProperty(this, "oldValue", {
            get: function() {
                var value = delegate.oldValue;
                return P.boxAsJs(value);
            }
        });
        if(!P.ValueChangeEvent){
            /**
             * Generated property jsDoc.
             * @property oldValue
             * @memberOf ValueChangeEvent
             */
            P.ValueChangeEvent.prototype.oldValue = {};
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.ValueChangeEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf ValueChangeEvent
             */
            P.ValueChangeEvent.prototype.source = {};
        }
    };
})();