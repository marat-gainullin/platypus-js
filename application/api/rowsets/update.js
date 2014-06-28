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
            value: function() {
                return delegate;
            }
        });
        if(P.Update.superclass)
            P.Update.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "consumed", {
            get: function() {
                var value = delegate.consumed;
                return P.boxAsJs(value);
            }
        });
        if(!P.Update){
            /**
             * Indicated if the change is consumed.
             * @property consumed
             * @memberOf Update
             */
            P.Update.prototype.consumed = true;
        }
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return P.boxAsJs(value);
            }
        });
        if(!P.Update){
            /**
             * Data to be applied within a target datasource
             * @property data
             * @memberOf Update
             */
            P.Update.prototype.data = [];
        }
        Object.defineProperty(this, "keys", {
            get: function() {
                var value = delegate.keys;
                return P.boxAsJs(value);
            }
        });
        if(!P.Update){
            /**
             * Keys used for indentifying data changes within a target datasource
             * @property keys
             * @memberOf Update
             */
            P.Update.prototype.keys = [];
        }
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            }
        });
        if(!P.Update){
            /**
             * Indicates the change's type (Insert, Update, Delete or Command).
             * @property type
             * @memberOf Update
             */
            P.Update.prototype.type = '';
        }
    };
        /**
         * Consumes the change, so other validators and database applier won't apply it.
         * @method consume
         * @memberOf Update
         */
        P.Update.prototype.consume = function() {
            var delegate = this.unwrap();
            var value = delegate.consume();
            return P.boxAsJs(value);
        };

})();