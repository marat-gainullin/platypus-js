(function() {
    var javaClass = Java.type("com.eas.client.forms.api.Orientation");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Orientation(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Orientation Orientation
     */
    P.Orientation = function () {

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
         * Generated property jsDoc.
         * @property VERTICAL
         * @memberOf Orientation
         */
        Object.defineProperty(this, "VERTICAL", {
            get: function() {
                var value = delegate.VERTICAL;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property HORIZONTAL
         * @memberOf Orientation
         */
        Object.defineProperty(this, "HORIZONTAL", {
            get: function() {
                var value = delegate.HORIZONTAL;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();