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
        Object.defineProperty(this, "entity", {
            get: function() {
                var value = delegate.entity;
                return P.boxAsJs(value);
            }
        });
        if(!P.Update){
            /**
             * Indicates the change's destination entity.
             * @property entity
             * @memberOf Update
             */
            P.Update.prototype.entity = '';
        }
    };
})();