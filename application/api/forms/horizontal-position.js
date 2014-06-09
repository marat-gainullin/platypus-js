(function() {
    var javaClass = Java.type("com.eas.client.forms.api.HorizontalPosition");
    javaClass.setPublisher(function(aDelegate) {
        return new P.HorizontalPosition(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor HorizontalPosition HorizontalPosition
     */
    P.HorizontalPosition = function () {

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
         * @property CENTER
         * @memberOf HorizontalPosition
         */
        Object.defineProperty(this, "CENTER", {
            get: function() {
                var value = delegate.CENTER;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property LEFT
         * @memberOf HorizontalPosition
         */
        Object.defineProperty(this, "LEFT", {
            get: function() {
                var value = delegate.LEFT;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property RIGHT
         * @memberOf HorizontalPosition
         */
        Object.defineProperty(this, "RIGHT", {
            get: function() {
                var value = delegate.RIGHT;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();