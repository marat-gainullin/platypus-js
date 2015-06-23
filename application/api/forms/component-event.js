(function() {
    var className = "com.eas.client.forms.events.ComponentEvent";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.ComponentEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ComponentEvent ComponentEvent
     */
    P.ComponentEvent = function () {
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
        if(P.ComponentEvent.superclass)
            P.ComponentEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.ComponentEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf ComponentEvent
             */
            P.ComponentEvent.prototype.source = {};
        }
    };
})();