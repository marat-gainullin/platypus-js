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
            value: function() {
                return delegate;
            }
        });
        if(P.Command.superclass)
            P.Command.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            }
        });
        if(!P.Command){
            /**
             * Indicates the change's type (Insert, Update, Delete or Command).
             * @property type
             * @memberOf Command
             */
            P.Command.prototype.type = '';
        }
        Object.defineProperty(this, "parameters", {
            get: function() {
                var value = delegate.parameters;
                return P.boxAsJs(value);
            }
        });
        if(!P.Command){
            /**
             * Parameters of command.
             * @property parameters
             * @memberOf Command
             */
            P.Command.prototype.parameters = [];
        }
        Object.defineProperty(this, "command", {
            get: function() {
                var value = delegate.command;
                return P.boxAsJs(value);
            }
        });
        if(!P.Command){
            /**
             * Command sql text to be applied in a database.
             * @property command
             * @memberOf Command
             */
            P.Command.prototype.command = '';
        }
        Object.defineProperty(this, "entity", {
            get: function() {
                var value = delegate.entity;
                return P.boxAsJs(value);
            }
        });
        if(!P.Command){
            /**
             * Indicates the change's destination entity.
             * @property entity
             * @memberOf Command
             */
            P.Command.prototype.entity = '';
        }
    };
})();