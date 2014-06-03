(function() {
    var javaClass = Java.type("com.eas.gui.FontStyle");
    javaClass.setPublisher(function(aDelegate) {
        return new P.FontStyle(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace FontStyle
     */
    P.FontStyle = function () {

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
         * @property ITALIC
         * @memberOf FontStyle
         */
        Object.defineProperty(this, "ITALIC", {
            get: function() {
                var value = delegate.ITALIC;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property BOLD
         * @memberOf FontStyle
         */
        Object.defineProperty(this, "BOLD", {
            get: function() {
                var value = delegate.BOLD;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property BOLD_ITALIC
         * @memberOf FontStyle
         */
        Object.defineProperty(this, "BOLD_ITALIC", {
            get: function() {
                var value = delegate.BOLD_ITALIC;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property NORMAL
         * @memberOf FontStyle
         */
        Object.defineProperty(this, "NORMAL", {
            get: function() {
                var value = delegate.NORMAL;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();