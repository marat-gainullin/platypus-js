(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Command");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Command(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Command Command
     */
    P.Command = function Command() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Command.superclass)
            Command.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Command", {value: Command});
    Object.defineProperty(Command.prototype, "consumed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.consumed;
            return P.boxAsJs(value);
        }
    });
    if(!Command){
        /**
         * Indicated if the change is consumed.
         * @property consumed
         * @memberOf Command
         */
        P.Command.prototype.consumed = true;
    }
    Object.defineProperty(Command.prototype, "type", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.type;
            return P.boxAsJs(value);
        }
    });
    if(!Command){
        /**
         * Indicates the change's type (Insert, Update, Delete or Command).
         * @property type
         * @memberOf Command
         */
        P.Command.prototype.type = '';
    }
    Object.defineProperty(Command.prototype, "parameters", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parameters;
            return P.boxAsJs(value);
        }
    });
    if(!Command){
        /**
         * Parameters of command.
         * @property parameters
         * @memberOf Command
         */
        P.Command.prototype.parameters = [];
    }
    Object.defineProperty(Command.prototype, "command", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.command;
            return P.boxAsJs(value);
        }
    });
    if(!Command){
        /**
         * Command sql text to be applied in a database.
         * @property command
         * @memberOf Command
         */
        P.Command.prototype.command = '';
    }
    Object.defineProperty(Command.prototype, "consume", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.consume();
            return P.boxAsJs(value);
        }
    });
    if(!Command){
        /**
         * Consumes the change, so other validators and database applier won't apply it.
         * @method consume
         * @memberOf Command
         */
        P.Command.prototype.consume = function(){};
    }
})();