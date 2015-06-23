(function() {
    var className = "com.eas.client.login.SystemPlatypusPrincipal";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.SystemPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor SystemPlatypusPrincipal SystemPlatypusPrincipal
     */
    P.SystemPlatypusPrincipal = function () {
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
        if(P.SystemPlatypusPrincipal.superclass)
            P.SystemPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.SystemPlatypusPrincipal){
            /**
             * The username..
             * @property name
             * @memberOf SystemPlatypusPrincipal
             */
            P.SystemPlatypusPrincipal.prototype.name = '';
        }
    };
})();