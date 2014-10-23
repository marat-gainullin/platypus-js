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
        Object.defineProperty(this, "entity", {
            get: function() {
                var value = delegate.entity;
                return P.boxAsJs(value);
            }
        });
        if(!P.Delete){
            /**
             * Indicates the change's destination entity.
             * @property entity
             * @memberOf Delete
             */
            P.Delete.prototype.entity = '';
        }
    };
})();