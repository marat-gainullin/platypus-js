(function() {
    var javaClass = Java.type("com.eas.client.login.AnonymousPlatypusPrincipal");
    javaClass.setPublisher(function(aDelegate) {
        return new P.AnonymousPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor AnonymousPlatypusPrincipal AnonymousPlatypusPrincipal
     */
    P.AnonymousPlatypusPrincipal = function AnonymousPlatypusPrincipal() {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(AnonymousPlatypusPrincipal.superclass)
            AnonymousPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        /**
         * The username..
         * @property name
         * @memberOf AnonymousPlatypusPrincipal
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