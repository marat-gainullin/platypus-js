(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Insert");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Insert(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Insert Insert
     */
    P.Insert = function () {

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
         * @memberOf Insert
         */
        Object.defineProperty(this, "consumed", {
            get: function() {
                var value = delegate.consumed;
                return P.boxAsJs(value);
            }
        });

        /**
         * Data that will be inserted.
         * @property data
         * @memberOf Insert
         */
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return P.boxAsJs(value);
            }
        });

        /**
         * Indicates the change's type (Insert, Update, Delete or Command).
         * @property type
         * @memberOf Insert
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
         * @memberOf Insert
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