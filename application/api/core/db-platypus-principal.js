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
            value: function() {
                return delegate;
            }
        });
        if(P.DbPlatypusPrincipal.superclass)
            P.DbPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.DbPlatypusPrincipal){
            /**
             * The username..
             * @property name
             * @memberOf DbPlatypusPrincipal
             */
            P.DbPlatypusPrincipal.prototype.name = '';
        }
    };
})();