(function() {
    var className = "com.eas.server.httpservlet.HttpPlatypusPrincipal";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.HttpPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor HttpPlatypusPrincipal HttpPlatypusPrincipal
     */
    P.HttpPlatypusPrincipal = function () {
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
        if(P.HttpPlatypusPrincipal.superclass)
            P.HttpPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.HttpPlatypusPrincipal){
            /**
             * The username..
             * @property name
             * @memberOf HttpPlatypusPrincipal
             */
            P.HttpPlatypusPrincipal.prototype.name = '';
        }
    };
})();