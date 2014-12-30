(function() {
    var javaClass = Java.type("com.eas.client.forms.events.ContainerEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ContainerEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ContainerEvent ContainerEvent
     */
    P.ContainerEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ContainerEvent.superclass)
            P.ContainerEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.ContainerEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf ContainerEvent
             */
            P.ContainerEvent.prototype.source = {};
        }
        Object.defineProperty(this, "child", {
            get: function() {
                var value = delegate.child;
                return P.boxAsJs(value);
            }
        });
        if(!P.ContainerEvent){
            /**
             * The child component the operation is performed on.
             * @property child
             * @memberOf ContainerEvent
             */
            P.ContainerEvent.prototype.child = {};
        }
    };
})();