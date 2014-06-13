(function() {
    var javaClass = Java.type("com.bearsoft.gui.grid.data.CellData");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CellData(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor CellData CellData
     */
    P.CellData = function () {

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
         * The cell's data.
         * @property data
         * @memberOf CellData
         */
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return P.boxAsJs(value);
            }
        });

        /**
         * The displayed text.
         * @property display
         * @memberOf CellData
         */
        Object.defineProperty(this, "display", {
            get: function() {
                var value = delegate.display;
                return P.boxAsJs(value);
            }
        });

        /**
         * The cell's style.
         * @property style
         * @memberOf CellData
         */
        Object.defineProperty(this, "style", {
            get: function() {
                var value = delegate.style;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();