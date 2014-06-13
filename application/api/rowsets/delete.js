(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Delete");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Delete(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Delete Delete
     */
    P.Delete = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * Indicated if the change is consumed.
         * @property consumed
         * @memberOf Delete
         */
        Object.defineProperty(this, "consumed", {
            get: function() {
                var value = delegate.consumed;
                return P.boxAsJs(value);
            }
        });

        /**
         * Keys values used for identification of deleted data.
         * @property keys
         * @memberOf Delete
         */
        Object.defineProperty(this, "keys", {
            get: function() {
                var value = delegate.keys;
                return P.boxAsJs(value);
            }
        });

        /**
         * Indicates the change's type (Insert, Update, Delete or Command).
         * @property type
         * @memberOf Delete
         */
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            }
        });

        /**
         * Consumes the change, so other validators and database applier won't apply it.
         * @method consume
         * @memberOf Delete
         */
        Object.defineProperty(this, "consume", {
            get: function() {
                return function() {
                    var value = delegate.consume();
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();