(function() {
    var javaClass = Java.type("com.eas.server.Session");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Session(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Session Session
     */
    P.Session = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Session.superclass)
            P.Session.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "modules", {
            get: function() {
                var value = delegate.modules;
                return P.boxAsJs(value);
            }
        });
        if(!P.Session){
            /**
             * Contains modules collection of this session.
             * @property modules
             * @memberOf Session
             */
            P.Session.prototype.modules = {};
        }
    };
})();