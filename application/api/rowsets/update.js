(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Update");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Update(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Update Update
     */
    P.Update = function () {

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
         * @memberOf Update
         */
        Object.defineProperty(this, "consumed", {
            get: function() {
                var value = delegate.consumed;
                return P.boxAsJs(value);
            }
        });

        /**
         * Data to be applied within a target datasource
         * @property data
         * @memberOf Update
         */
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return P.boxAsJs(value);
            }
        });

        /**
         * Keys used for indentifying data changes within a target datasource
         * @property keys
         * @memberOf Update
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
         * @memberOf Update
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
         * @memberOf Update
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