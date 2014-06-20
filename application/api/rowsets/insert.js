(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Insert");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Insert(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Insert Insert
     */
    P.Insert = function Insert() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Insert.superclass)
            Insert.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Insert", {value: Insert});
    Object.defineProperty(Insert.prototype, "consumed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.consumed;
            return P.boxAsJs(value);
        }
    });
    if(!Insert){
        /**
         * Indicated if the change is consumed.
         * @property consumed
         * @memberOf Insert
         */
        P.Insert.prototype.consumed = true;
    }
    Object.defineProperty(Insert.prototype, "data", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.data;
            return P.boxAsJs(value);
        }
    });
    if(!Insert){
        /**
         * Data that will be inserted.
         * @property data
         * @memberOf Insert
         */
        P.Insert.prototype.data = [];
    }
    Object.defineProperty(Insert.prototype, "type", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.type;
            return P.boxAsJs(value);
        }
    });
    if(!Insert){
        /**
         * Indicates the change's type (Insert, Update, Delete or Command).
         * @property type
         * @memberOf Insert
         */
        P.Insert.prototype.type = '';
    }
    Object.defineProperty(Insert.prototype, "consume", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.consume();
            return P.boxAsJs(value);
        }
    });
    if(!Insert){
        /**
         * Consumes the change, so other validators and database applier won't apply it.
         * @method consume
         * @memberOf Insert
         */
        P.Insert.prototype.consume = function(){};
    }
})();