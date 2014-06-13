(function() {
    var javaClass = Java.type("com.bearsoft.rowset.changes.ChangeValue");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ChangeValue(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ChangeValue ChangeValue
     */
    P.ChangeValue = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * Name of changed property.
         * @property name
         * @memberOf ChangeValue
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });

        /**
         * New value.
         * @property value
         * @memberOf ChangeValue
         */
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.value;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();