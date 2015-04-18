(function() {
    var javaClass = Java.type("com.bearsoft.rowset.ordering.Filter");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Filter(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Filter Filter
     */
    P.Filter = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Filter.superclass)
            P.Filter.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "applied", {
            get: function() {
                var value = delegate.applied;
                return P.boxAsJs(value);
            }
        });
        if(!P.Filter){
            /**
             * Returns whether this filter is applied.
             * @property applied
             * @memberOf Filter
             */
            P.Filter.prototype.applied = true;
        }
    };
        /**
         * Applies the filter with values passed in. Values correspond to key fields in createFilter() call.
         * @param values Values for keys in createFilter() call.
         * @method apply
         * @memberOf Filter
         */
        P.Filter.prototype.apply = function(values) {
            var delegate = this.unwrap();
            var value = delegate.apply(P.boxAsJava(values));
            return P.boxAsJs(value);
        };

        /**
         * Cancels applied filter.
         * @method cancel
         * @memberOf Filter
         */
        P.Filter.prototype.cancel = function() {
            var delegate = this.unwrap();
            var value = delegate.cancel();
            return P.boxAsJs(value);
        };

})();