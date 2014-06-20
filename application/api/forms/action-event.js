(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.ActionEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ActionEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ActionEvent ActionEvent
     */
    P.ActionEvent = function ActionEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ActionEvent.superclass)
            ActionEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ActionEvent", {value: ActionEvent});
    Object.defineProperty(ActionEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!ActionEvent){
        /**
         * The source component object of the event.
         * @property source
         * @memberOf ActionEvent
         */
        P.ActionEvent.prototype.source = {};
    }
})();