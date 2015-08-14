(function() {
    var className = "com.eas.client.changes.JdbcChangeValue";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.JdbcChangeValue(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor JdbcChangeValue JdbcChangeValue
     */
    P.JdbcChangeValue = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(P.JdbcChangeValue.superclass)
            P.JdbcChangeValue.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.JdbcChangeValue){
            /**
             * Name of changed property.
             * @property name
             * @memberOf JdbcChangeValue
             */
            P.JdbcChangeValue.prototype.name = '';
        }
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.value;
                return P.boxAsJs(value);
            }
        });
        if(!P.JdbcChangeValue){
            /**
             * Value of changed property.
             * @property value
             * @memberOf JdbcChangeValue
             */
            P.JdbcChangeValue.prototype.value = {};
        }
    };
})();