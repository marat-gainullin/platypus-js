(function() {
    var javaClass = Java.type("com.eas.client.login.SystemPlatypusPrincipal");
    javaClass.setPublisher(function(aDelegate) {
        return new P.SystemPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor SystemPlatypusPrincipal SystemPlatypusPrincipal
     */
    P.SystemPlatypusPrincipal = function SystemPlatypusPrincipal() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(SystemPlatypusPrincipal.superclass)
            SystemPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "SystemPlatypusPrincipal", {value: SystemPlatypusPrincipal});
    Object.defineProperty(SystemPlatypusPrincipal.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!SystemPlatypusPrincipal){
        /**
         * The username..
         * @property name
         * @memberOf SystemPlatypusPrincipal
         */
        P.SystemPlatypusPrincipal.prototype.name = '';
    }
})();