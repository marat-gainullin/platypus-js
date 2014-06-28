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
            value: function() {
                return delegate;
            }
        });
        if(P.Delete.superclass)
            P.Delete.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "consumed", {
            get: function() {
                var value = delegate.consumed;
                return P.boxAsJs(value);
            }
        });
        if(!P.Delete){
            /**
             * Indicated if the change is consumed.
             * @property consumed
             * @memberOf Delete
             */
            P.Delete.prototype.consumed = true;
        }
        Object.defineProperty(this, "keys", {
            get: function() {
                var value = delegate.keys;
                return P.boxAsJs(value);
            }
        });
        if(!P.Delete){
            /**
             * Keys values used for identification of deleted data.
             * @property keys
             * @memberOf Delete
             */
            P.Delete.prototype.keys = [];
        }
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            }
        });
        if(!P.Delete){
            /**
             * Indicates the change's type (Insert, Update, Delete or Command).
             * @property type
             * @memberOf Delete
             */
            P.Delete.prototype.type = '';
        }
    };
        /**
         * Consumes the change, so other validators and database applier won't apply it.
         * @method consume
         * @memberOf Delete
         */
        P.Delete.prototype.consume = function() {
            var delegate = this.unwrap();
            var value = delegate.consume();
            return P.boxAsJs(value);
        };

})();