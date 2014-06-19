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
        /**
         * HTTP request, when invoked by HTTP protocol.
         * @property request
         * @memberOf HttpScriptContext
         */
        Object.defineProperty(this, "request", {
            get: function() {
                var value = delegate.request;
                return P.boxAsJs(value);
            }
        });

        /**
         * HTTP response, when invoked by HTTP protocol.
         * @property response
         * @memberOf HttpScriptContext
         */
        Object.defineProperty(this, "response", {
            get: function() {
                var value = delegate.response;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();