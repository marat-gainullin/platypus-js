(function() {
    var className = "com.eas.server.websocket.WebSocketPlatypusPrincipal";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.WebSocketPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor WebSocketPlatypusPrincipal WebSocketPlatypusPrincipal
     */
    P.WebSocketPlatypusPrincipal = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.WebSocketPlatypusPrincipal.superclass)
            P.WebSocketPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketPlatypusPrincipal){
            /**
             * The username..
             * @property name
             * @memberOf WebSocketPlatypusPrincipal
             */
            P.WebSocketPlatypusPrincipal.prototype.name = '';
        }
    };
})();