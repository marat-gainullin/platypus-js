(function() {
    var javaClass = Java.type("com.eas.client.forms.events.ItemEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ItemEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ItemEvent ItemEvent
     */
    P.ItemEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ItemEvent.superclass)
            P.ItemEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "item", {
            get: function() {
                var value = delegate.item;
                return P.boxAsJs(value);
            }
        });
        if(!P.ItemEvent){
            /**
             * Generated property jsDoc.
             * @property item
             * @memberOf ItemEvent
             */
            P.ItemEvent.prototype.item = {};
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.ItemEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf ItemEvent
             */
            P.ItemEvent.prototype.source = {};
        }
    };
})();