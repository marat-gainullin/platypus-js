(function() {
    var javaClass = Java.type("com.eas.client.login.SystemPlatypusPrincipal");
    javaClass.setPublisher(function(aDelegate) {
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
        /**
         * Checks if a user have a specified role.
         * @param role a role's name to test.
         * @return <code>true</code> if the user has the role.
         * @method hasRole
         * @memberOf SystemPlatypusPrincipal
         */
        P.SystemPlatypusPrincipal.prototype.hasRole = function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.hasRole(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        };

        /**
         * Logs out from  user's session on a server.
         * @param onSuccess The function to be invoked after the logout (optional).
         * @param onFailure The function to be invoked when exception raised while logout process (optional).
         * @method logout
         * @memberOf SystemPlatypusPrincipal
         */
        P.SystemPlatypusPrincipal.prototype.logout = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.logout(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

})();