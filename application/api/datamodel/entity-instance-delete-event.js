(function() {
    var className = "com.eas.client.model.application.EntityInstanceDeleteEvent";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.EntityInstanceDeleteEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor EntityInstanceDeleteEvent EntityInstanceDeleteEvent
     */
    P.EntityInstanceDeleteEvent = function () {
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
        if(P.EntityInstanceDeleteEvent.superclass)
            P.EntityInstanceDeleteEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "deleted", {
            get: function() {
                var value = delegate.deleted;
                return value;
            }
        });
        if(!P.EntityInstanceDeleteEvent){
            /**
             * The deleted element.
             * @property deleted
             * @memberOf EntityInstanceDeleteEvent
             */
            P.EntityInstanceDeleteEvent.prototype.deleted = {};
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceDeleteEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf EntityInstanceDeleteEvent
             */
            P.EntityInstanceDeleteEvent.prototype.source = {};
        }
    };
})();