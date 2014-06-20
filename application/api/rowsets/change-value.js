(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.ChangeValue");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ChangeValue(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ChangeValue ChangeValue
     */
    P.ChangeValue = function ChangeValue() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ChangeValue.superclass)
            ChangeValue.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ChangeValue", {value: ChangeValue});
    Object.defineProperty(ChangeValue.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ChangeValue){
        /**
         * Name of changed property.
         * @property name
         * @memberOf ChangeValue
         */
        P.ChangeValue.prototype.name = '';
    }
    Object.defineProperty(ChangeValue.prototype, "value", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.value;
            return P.boxAsJs(value);
        }
    });
    if(!ChangeValue){
        /**
         * New value.
         * @property value
         * @memberOf ChangeValue
         */
        P.ChangeValue.prototype.value = {};
    }
})();