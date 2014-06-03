(function() {
    var javaClass = Java.type("com.eas.client.forms.IconResources");
    javaClass.setPublisher(function(aDelegate) {
        return new P.IconResources(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace IconResources
     */
    P.IconResources = function () {

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
        * Loads an image resource.
        * @param path a path or an URL to the icon resource
         * @method load
         * @memberOf IconResources
        */
        Object.defineProperty(this, "load", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.load.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();