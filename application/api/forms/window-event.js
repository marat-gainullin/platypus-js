(function() {
    var className = "com.eas.client.forms.events.WindowEvent";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.WindowEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor WindowEvent WindowEvent
     */
    P.WindowEvent = function () {
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
        if(P.WindowEvent.superclass)
            P.WindowEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.WindowEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf WindowEvent
             */
            P.WindowEvent.prototype.source = {};
        }
    };
})();