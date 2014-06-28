(function() {
    var javaClass = Java.type("com.eas.client.login.AnonymousPlatypusPrincipal");
    javaClass.setPublisher(function(aDelegate) {
        return new P.AnonymousPlatypusPrincipal(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor AnonymousPlatypusPrincipal AnonymousPlatypusPrincipal
     */
    P.AnonymousPlatypusPrincipal = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.AnonymousPlatypusPrincipal.superclass)
            P.AnonymousPlatypusPrincipal.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.AnonymousPlatypusPrincipal){
            /**
             * The username..
             * @property name
             * @memberOf AnonymousPlatypusPrincipal
             */
            P.AnonymousPlatypusPrincipal.prototype.name = '';
        }
    };
})();