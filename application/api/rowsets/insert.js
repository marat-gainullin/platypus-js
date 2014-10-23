(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.Insert");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Insert(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Insert Insert
     */
    P.Insert = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Insert.superclass)
            P.Insert.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return P.boxAsJs(value);
            }
        });
        if(!P.Insert){
            /**
             * Data that will be inserted.
             * @property data
             * @memberOf Insert
             */
            P.Insert.prototype.data = [];
        }
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            }
        });
        if(!P.Insert){
            /**
             * Indicates the change's type (Insert, Update, Delete or Command).
             * @property type
             * @memberOf Insert
             */
            P.Insert.prototype.type = '';
        }
        Object.defineProperty(this, "entity", {
            get: function() {
                var value = delegate.entity;
                return P.boxAsJs(value);
            }
        });
        if(!P.Insert){
            /**
             * Indicates the change's destination entity.
             * @property entity
             * @memberOf Insert
             */
            P.Insert.prototype.entity = '';
        }
    };
})();