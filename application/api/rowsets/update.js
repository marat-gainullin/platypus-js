(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Update");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Update(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Update Update
     */
    P.Update = function Update() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Update.superclass)
            Update.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Update", {value: Update});
    Object.defineProperty(Update.prototype, "consumed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.consumed;
            return P.boxAsJs(value);
        }
    });
    if(!Update){
        /**
         * Indicated if the change is consumed.
         * @property consumed
         * @memberOf Update
         */
        P.Update.prototype.consumed = true;
    }
    Object.defineProperty(Update.prototype, "data", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.data;
            return P.boxAsJs(value);
        }
    });
    if(!Update){
        /**
         * Data to be applied within a target datasource
         * @property data
         * @memberOf Update
         */
        P.Update.prototype.data = [];
    }
    Object.defineProperty(Update.prototype, "keys", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.keys;
            return P.boxAsJs(value);
        }
    });
    if(!Update){
        /**
         * Keys used for indentifying data changes within a target datasource
         * @property keys
         * @memberOf Update
         */
        P.Update.prototype.keys = [];
    }
    Object.defineProperty(Update.prototype, "type", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.type;
            return P.boxAsJs(value);
        }
    });
    if(!Update){
        /**
         * Indicates the change's type (Insert, Update, Delete or Command).
         * @property type
         * @memberOf Update
         */
        P.Update.prototype.type = '';
    }
    Object.defineProperty(Update.prototype, "consume", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.consume();
            return P.boxAsJs(value);
        }
    });
    if(!Update){
        /**
         * Consumes the change, so other validators and database applier won't apply it.
         * @method consume
         * @memberOf Update
         */
        P.Update.prototype.consume = function(){};
    }
})();