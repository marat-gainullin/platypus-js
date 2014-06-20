(function() {
    var javaClass = Java.type("com.eas.client.login.DbPlatypusPrincipal");
    javaClass.setPublisher(function(aDelegate) {
        return new P.DbPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor DbPlatypusPrincipal DbPlatypusPrincipal
     */
    P.DbPlatypusPrincipal = function DbPlatypusPrincipal() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(DbPlatypusPrincipal.superclass)
            DbPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "DbPlatypusPrincipal", {value: DbPlatypusPrincipal});
    Object.defineProperty(DbPlatypusPrincipal.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!DbPlatypusPrincipal){
        /**
         * The username..
         * @property name
         * @memberOf DbPlatypusPrincipal
         */
        P.DbPlatypusPrincipal.prototype.name = '';
    }
})();