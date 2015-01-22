(function() {
    var javaClass = Java.type("com.eas.client.forms.events.ActionEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ActionEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ActionEvent ActionEvent
     */
    P.ActionEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ActionEvent.superclass)
            P.ActionEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.ActionEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf ActionEvent
             */
            P.ActionEvent.prototype.source = {};
        }
    };
})();