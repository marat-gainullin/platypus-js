(function() {
    var javaClass = Java.type("com.bearsoft.gui.grid.data.CellData");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CellData(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor CellData CellData
     */
    P.CellData = function CellData() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(CellData.superclass)
            CellData.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "CellData", {value: CellData});
    Object.defineProperty(CellData.prototype, "data", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.data;
            return P.boxAsJs(value);
        }
    });
    if(!CellData){
        /**
         * The cell's data.
         * @property data
         * @memberOf CellData
         */
        P.CellData.prototype.data = {};
    }
    Object.defineProperty(CellData.prototype, "display", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.display;
            return P.boxAsJs(value);
        }
    });
    if(!CellData){
        /**
         * The displayed text.
         * @property display
         * @memberOf CellData
         */
        P.CellData.prototype.display = {};
    }
    Object.defineProperty(CellData.prototype, "style", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.style;
            return P.boxAsJs(value);
        }
    });
    if(!CellData){
        /**
         * The cell's style.
         * @property style
         * @memberOf CellData
         */
        P.CellData.prototype.style = {};
    }
})();