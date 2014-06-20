(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.ContainerEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ContainerEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ContainerEvent ContainerEvent
     */
    P.ContainerEvent = function ContainerEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ContainerEvent.superclass)
            ContainerEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ContainerEvent", {value: ContainerEvent});
    Object.defineProperty(ContainerEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!ContainerEvent){
        /**
         * The source component object of the event.
         * @property source
         * @memberOf ContainerEvent
         */
        P.ContainerEvent.prototype.source = {};
    }
    Object.defineProperty(ContainerEvent.prototype, "child", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.child;
            return P.boxAsJs(value);
        }
    });
    if(!ContainerEvent){
        /**
         * The child component the operation is performed on.
         * @property child
         * @memberOf ContainerEvent
         */
        P.ContainerEvent.prototype.child = {};
    }
})();