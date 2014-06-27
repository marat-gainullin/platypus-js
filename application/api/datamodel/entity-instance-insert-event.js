(function() {
    var javaClass = Java.type("com.eas.client.model.application.EntityInstanceInsertEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.EntityInstanceInsertEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor EntityInstanceInsertEvent EntityInstanceInsertEvent
     */
    P.EntityInstanceInsertEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.EntityInstanceInsertEvent.superclass)
            P.EntityInstanceInsertEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "inserted", {
            get: function() {
                var value = delegate.inserted;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceInsertEvent){
            /**
             * The inserted element.
             * @property inserted
             * @memberOf EntityInstanceInsertEvent
             */
            P.EntityInstanceInsertEvent.prototype.inserted = {};
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceInsertEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf EntityInstanceInsertEvent
             */
            P.EntityInstanceInsertEvent.prototype.source = {};
        }
        Object.defineProperty(this, "object", {
            get: function() {
                var value = delegate.object;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceInsertEvent){
            /**
             * The inserted element.
             * @property object
             * @memberOf EntityInstanceInsertEvent
             */
            P.EntityInstanceInsertEvent.prototype.object = {};
        }
    };
})();