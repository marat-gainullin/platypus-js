(function() {
    var javaClass = Java.type("com.eas.gui.ScriptColor");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Color(null, null, null, null, aDelegate);
    });
    
    /**
     * The <code>Color</code> class is used to encapsulate colors in the default RGB color space.* @param red Red compontent (optional)
     * @param green Green compontent (optional)
     * @param blue Blue compontent (optional)
     * @param alpha Alpha compontent (optional)
     * @constructor Color Color
     */
    P.Color = function (red, green, blue, alpha) {
        var maxArgs = 4;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 4 ? new javaClass(P.boxAsJava(red), P.boxAsJava(green), P.boxAsJava(blue), P.boxAsJava(alpha))
            : arguments.length === 3 ? new javaClass(P.boxAsJava(red), P.boxAsJava(green), P.boxAsJava(blue))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(red), P.boxAsJava(green))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(red))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Color.superclass)
            P.Color.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "WHITE", {
            get: function() {
                var value = delegate.WHITE;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property WHITE
             * @memberOf Color
             */
            P.Color.prototype.WHITE = {};
        }
        Object.defineProperty(this, "GRAY", {
            get: function() {
                var value = delegate.GRAY;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property GRAY
             * @memberOf Color
             */
            P.Color.prototype.GRAY = {};
        }
        Object.defineProperty(this, "BLUE", {
            get: function() {
                var value = delegate.BLUE;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property BLUE
             * @memberOf Color
             */
            P.Color.prototype.BLUE = {};
        }
        Object.defineProperty(this, "GREEN", {
            get: function() {
                var value = delegate.GREEN;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property GREEN
             * @memberOf Color
             */
            P.Color.prototype.GREEN = {};
        }
        Object.defineProperty(this, "RED", {
            get: function() {
                var value = delegate.RED;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property RED
             * @memberOf Color
             */
            P.Color.prototype.RED = {};
        }
        Object.defineProperty(this, "PINK", {
            get: function() {
                var value = delegate.PINK;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property PINK
             * @memberOf Color
             */
            P.Color.prototype.PINK = {};
        }
        Object.defineProperty(this, "LIGHT_GRAY", {
            get: function() {
                var value = delegate.LIGHT_GRAY;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property LIGHT_GRAY
             * @memberOf Color
             */
            P.Color.prototype.LIGHT_GRAY = {};
        }
        Object.defineProperty(this, "BLACK", {
            get: function() {
                var value = delegate.BLACK;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property BLACK
             * @memberOf Color
             */
            P.Color.prototype.BLACK = {};
        }
        Object.defineProperty(this, "MAGENTA", {
            get: function() {
                var value = delegate.MAGENTA;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property MAGENTA
             * @memberOf Color
             */
            P.Color.prototype.MAGENTA = {};
        }
        Object.defineProperty(this, "YELLOW", {
            get: function() {
                var value = delegate.YELLOW;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property YELLOW
             * @memberOf Color
             */
            P.Color.prototype.YELLOW = {};
        }
        Object.defineProperty(this, "CYAN", {
            get: function() {
                var value = delegate.CYAN;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property CYAN
             * @memberOf Color
             */
            P.Color.prototype.CYAN = {};
        }
        Object.defineProperty(this, "DARK_GRAY", {
            get: function() {
                var value = delegate.DARK_GRAY;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property DARK_GRAY
             * @memberOf Color
             */
            P.Color.prototype.DARK_GRAY = {};
        }
        Object.defineProperty(this, "ORANGE", {
            get: function() {
                var value = delegate.ORANGE;
                return P.boxAsJs(value);
            }
        });
        if(!P.Color){
            /**
             * Generated property jsDoc.
             * @property ORANGE
             * @memberOf Color
             */
            P.Color.prototype.ORANGE = {};
        }
    };
})();