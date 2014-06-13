(function() {
    var javaClass = Java.type("com.eas.client.login.DbPlatypusPrincipal");
    javaClass.setPublisher(function(aDelegate) {
        return new P.DbPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor DbPlatypusPrincipal DbPlatypusPrincipal
     */
    P.DbPlatypusPrincipal = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * The username..
         * @property name
         * @memberOf DbPlatypusPrincipal
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();