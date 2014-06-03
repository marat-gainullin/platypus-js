(function() {
    var javaClass = Java.type("com.eas.client.login.PlatypusPrincipal");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace PlatypusPrincipal
     */
    P.PlatypusPrincipal = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass();

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
         * @memberOf PlatypusPrincipal
        */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });

        /**
        * Checks if a user have a specified role.
        * @param role a role's name to test
        * @return <code>true</code> if the user has the provided role
         * @method hasRole
         * @memberOf PlatypusPrincipal
        */
        Object.defineProperty(this, "hasRole", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.hasRole.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();