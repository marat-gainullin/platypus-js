(function() {
    var javaClass = Java.type("com.eas.gui.Cursor");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Cursor(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Cursor Cursor
     */
    P.Cursor = function () {

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
         * @property NE_RESIZE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "NE_RESIZE", {
            get: function() {
                var value = delegate.NE_RESIZE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property CROSSHAIR
         * @memberOf Cursor
         */
        Object.defineProperty(this, "CROSSHAIR", {
            get: function() {
                var value = delegate.CROSSHAIR;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property N_RESIZE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "N_RESIZE", {
            get: function() {
                var value = delegate.N_RESIZE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property SW_RESIZE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "SW_RESIZE", {
            get: function() {
                var value = delegate.SW_RESIZE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property TEXT
         * @memberOf Cursor
         */
        Object.defineProperty(this, "TEXT", {
            get: function() {
                var value = delegate.TEXT;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property W_RESIZE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "W_RESIZE", {
            get: function() {
                var value = delegate.W_RESIZE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property HAND
         * @memberOf Cursor
         */
        Object.defineProperty(this, "HAND", {
            get: function() {
                var value = delegate.HAND;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property E_RESIZE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "E_RESIZE", {
            get: function() {
                var value = delegate.E_RESIZE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property SE_RESIZE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "SE_RESIZE", {
            get: function() {
                var value = delegate.SE_RESIZE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property AUTO
         * @memberOf Cursor
         */
        Object.defineProperty(this, "AUTO", {
            get: function() {
                var value = delegate.AUTO;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property S_RESIZE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "S_RESIZE", {
            get: function() {
                var value = delegate.S_RESIZE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property MOVE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "MOVE", {
            get: function() {
                var value = delegate.MOVE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property NW_RESIZE
         * @memberOf Cursor
         */
        Object.defineProperty(this, "NW_RESIZE", {
            get: function() {
                var value = delegate.NW_RESIZE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property WAIT
         * @memberOf Cursor
         */
        Object.defineProperty(this, "WAIT", {
            get: function() {
                var value = delegate.WAIT;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property DEFAULT
         * @memberOf Cursor
         */
        Object.defineProperty(this, "DEFAULT", {
            get: function() {
                var value = delegate.DEFAULT;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();