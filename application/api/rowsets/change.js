(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Change");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Change(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace Change
     */
    P.Change = function () {

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
         * Indicated if the change is consumed.
         * @property consumed
         * @memberOf Change
         */
        Object.defineProperty(this, "consumed", {
            get: function() {
                var value = delegate.consumed;
                return P.boxAsJs(value);
            }
        });

        /**
         * Indicates the change's type (Insert, Update, Delete or Command).
         * @property type
         * @memberOf Change
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
         * @memberOf Change
         */
        Object.defineProperty(this, "consume", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.consume.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();