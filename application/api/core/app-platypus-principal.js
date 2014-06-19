(function() {
    var javaClass = Java.type("com.eas.client.login.AppPlatypusPrincipal");
    javaClass.setPublisher(function(aDelegate) {
        return new P.AppPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor AppPlatypusPrincipal AppPlatypusPrincipal
     */
    P.AppPlatypusPrincipal = function AppPlatypusPrincipal() {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(AppPlatypusPrincipal.superclass)
            AppPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        /**
         * The username..
         * @property name
         * @memberOf AppPlatypusPrincipal
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