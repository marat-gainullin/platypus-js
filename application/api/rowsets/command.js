(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Command");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Command(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Command Command
     */
    P.Command = function () {

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
         * @memberOf Command
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
         * @memberOf Command
         */
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            }
        });

        /**
         * Parameters of command.
         * @property parameters
         * @memberOf Command
         */
        Object.defineProperty(this, "parameters", {
            get: function() {
                var value = delegate.parameters;
                return P.boxAsJs(value);
            }
        });

        /**
         * Command sql text to be applied in a database.
         * @property command
         * @memberOf Command
         */
        Object.defineProperty(this, "command", {
            get: function() {
                var value = delegate.command;
                return P.boxAsJs(value);
            }
        });

        /**
         * Consumes the change, so other validators and database applier won't apply it.
         * @method consume
         * @memberOf Command
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