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
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "AppPlatypusPrincipal", {value: AppPlatypusPrincipal});
    Object.defineProperty(AppPlatypusPrincipal.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!AppPlatypusPrincipal){
        /**
         * The username..
         * @property name
         * @memberOf AppPlatypusPrincipal
         */
        P.AppPlatypusPrincipal.prototype.name = '';
    }
})();