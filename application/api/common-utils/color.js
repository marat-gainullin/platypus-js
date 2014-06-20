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
    P.Color = function Color(red, green, blue, alpha) {
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
        if(Color.superclass)
            Color.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Color", {value: Color});
    Object.defineProperty(Color.prototype, "WHITE", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.WHITE;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property WHITE
         * @memberOf Color
         */
        P.Color.prototype.WHITE = {};
    }
    Object.defineProperty(Color.prototype, "GRAY", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.GRAY;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property GRAY
         * @memberOf Color
         */
        P.Color.prototype.GRAY = {};
    }
    Object.defineProperty(Color.prototype, "BLUE", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.BLUE;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property BLUE
         * @memberOf Color
         */
        P.Color.prototype.BLUE = {};
    }
    Object.defineProperty(Color.prototype, "GREEN", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.GREEN;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property GREEN
         * @memberOf Color
         */
        P.Color.prototype.GREEN = {};
    }
    Object.defineProperty(Color.prototype, "RED", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.RED;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property RED
         * @memberOf Color
         */
        P.Color.prototype.RED = {};
    }
    Object.defineProperty(Color.prototype, "PINK", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.PINK;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property PINK
         * @memberOf Color
         */
        P.Color.prototype.PINK = {};
    }
    Object.defineProperty(Color.prototype, "LIGHT_GRAY", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.LIGHT_GRAY;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property LIGHT_GRAY
         * @memberOf Color
         */
        P.Color.prototype.LIGHT_GRAY = {};
    }
    Object.defineProperty(Color.prototype, "BLACK", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.BLACK;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property BLACK
         * @memberOf Color
         */
        P.Color.prototype.BLACK = {};
    }
    Object.defineProperty(Color.prototype, "MAGENTA", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.MAGENTA;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property MAGENTA
         * @memberOf Color
         */
        P.Color.prototype.MAGENTA = {};
    }
    Object.defineProperty(Color.prototype, "YELLOW", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.YELLOW;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property YELLOW
         * @memberOf Color
         */
        P.Color.prototype.YELLOW = {};
    }
    Object.defineProperty(Color.prototype, "CYAN", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.CYAN;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property CYAN
         * @memberOf Color
         */
        P.Color.prototype.CYAN = {};
    }
    Object.defineProperty(Color.prototype, "DARK_GRAY", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.DARK_GRAY;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property DARK_GRAY
         * @memberOf Color
         */
        P.Color.prototype.DARK_GRAY = {};
    }
    Object.defineProperty(Color.prototype, "ORANGE", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.ORANGE;
            return P.boxAsJs(value);
        }
    });
    if(!Color){
        /**
         * Generated property jsDoc.
         * @property ORANGE
         * @memberOf Color
         */
        P.Color.prototype.ORANGE = {};
    }
})();