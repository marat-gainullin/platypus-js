(function() {
    var javaClass = Java.type("com.eas.server.httpservlet.HttpScriptContext");
    javaClass.setPublisher(function(aDelegate) {
        return new P.HttpScriptContext(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor HttpScriptContext HttpScriptContext
     */
    P.HttpScriptContext = function HttpScriptContext() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(HttpScriptContext.superclass)
            HttpScriptContext.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "HttpScriptContext", {value: HttpScriptContext});
    Object.defineProperty(HttpScriptContext.prototype, "request", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.request;
            return P.boxAsJs(value);
        }
    });
    if(!HttpScriptContext){
        /**
         * HTTP request, when invoked by HTTP protocol.
         * @property request
         * @memberOf HttpScriptContext
         */
        P.HttpScriptContext.prototype.request = {};
    }
    Object.defineProperty(HttpScriptContext.prototype, "response", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.response;
            return P.boxAsJs(value);
        }
    });
    if(!HttpScriptContext){
        /**
         * HTTP response, when invoked by HTTP protocol.
         * @property response
         * @memberOf HttpScriptContext
         */
        P.HttpScriptContext.prototype.response = {};
    }
})();