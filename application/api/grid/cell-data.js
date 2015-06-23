(function() {
    var className = "com.bearsoft.gui.grid.data.CellData";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
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
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(P.CellData.superclass)
            P.CellData.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return P.boxAsJs(value);
            }
        });
        if(!P.CellData){
            /**
             * The cell's data.
             * @property data
             * @memberOf CellData
             */
            P.CellData.prototype.data = {};
        }
        Object.defineProperty(this, "display", {
            get: function() {
                var value = delegate.display;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.display = P.boxAsJava(aValue);
            }
        });
        if(!P.CellData){
            /**
             * The displayed text.
             * @property display
             * @memberOf CellData
             */
            P.CellData.prototype.display = {};
        }
    };
})();