(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Delete");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Delete(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Delete Delete
     */
    P.Delete = function Delete() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Delete.superclass)
            Delete.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Delete", {value: Delete});
    Object.defineProperty(Delete.prototype, "consumed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.consumed;
            return P.boxAsJs(value);
        }
    });
    if(!Delete){
        /**
         * Indicated if the change is consumed.
         * @property consumed
         * @memberOf Delete
         */
        P.Delete.prototype.consumed = true;
    }
    Object.defineProperty(Delete.prototype, "keys", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.keys;
            return P.boxAsJs(value);
        }
    });
    if(!Delete){
        /**
         * Keys values used for identification of deleted data.
         * @property keys
         * @memberOf Delete
         */
        P.Delete.prototype.keys = [];
    }
    Object.defineProperty(Delete.prototype, "type", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.type;
            return P.boxAsJs(value);
        }
    });
    if(!Delete){
        /**
         * Indicates the change's type (Insert, Update, Delete or Command).
         * @property type
         * @memberOf Delete
         */
        P.Delete.prototype.type = '';
    }
    Object.defineProperty(Delete.prototype, "consume", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.consume();
            return P.boxAsJs(value);
        }
    });
    if(!Delete){
        /**
         * Consumes the change, so other validators and database applier won't apply it.
         * @method consume
         * @memberOf Delete
         */
        P.Delete.prototype.consume = function(){};
    }
})();