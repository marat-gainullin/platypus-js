(function() {
    var javaClass = Java.type("com.eas.client.forms.api.VerticalPosition");
    javaClass.setPublisher(function(aDelegate) {
        return new P.VerticalPosition(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace VerticalPosition
     */
    P.VerticalPosition = function () {

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
         * @memberOf VerticalPosition
         */
        Object.defineProperty(this, "CENTER", {
            get: function() {
                var value = delegate.CENTER;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property TOP
         * @memberOf VerticalPosition
         */
        Object.defineProperty(this, "TOP", {
            get: function() {
                var value = delegate.TOP;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property BOTTOM
         * @memberOf VerticalPosition
         */
        Object.defineProperty(this, "BOTTOM", {
            get: function() {
                var value = delegate.BOTTOM;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();