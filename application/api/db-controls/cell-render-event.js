(function() {
    var javaClass = Java.type("com.eas.dbcontrols.CellRenderEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CellRenderEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor CellRenderEvent CellRenderEvent
     */
    P.CellRenderEvent = function CellRenderEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(CellRenderEvent.superclass)
            CellRenderEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "CellRenderEvent", {value: CellRenderEvent});
    Object.defineProperty(CellRenderEvent.prototype, "columnId", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.columnId;
            return P.boxAsJs(value);
        }
    });
    if(!CellRenderEvent){
        /**
         * The cell's column ID.
         * @property columnId
         * @memberOf CellRenderEvent
         */
        P.CellRenderEvent.prototype.columnId = {};
    }
    Object.defineProperty(CellRenderEvent.prototype, "id", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.id;
            return P.boxAsJs(value);
        }
    });
    if(!CellRenderEvent){
        /**
         * The primary key of the data object.
         * @property id
         * @memberOf CellRenderEvent
         */
        P.CellRenderEvent.prototype.id = {};
    }
    Object.defineProperty(CellRenderEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!CellRenderEvent){
        /**
         * The source object of the event.
         * @property source
         * @memberOf CellRenderEvent
         */
        P.CellRenderEvent.prototype.source = {};
    }
    Object.defineProperty(CellRenderEvent.prototype, "cell", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.cell;
            return P.boxAsJs(value);
        }
    });
    if(!CellRenderEvent){
        /**
         * The "abstract" cell.
         * @property cell
         * @memberOf CellRenderEvent
         */
        P.CellRenderEvent.prototype.cell = {};
    }
    Object.defineProperty(CellRenderEvent.prototype, "object", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.object;
            return P.boxAsJs(value);
        }
    });
    if(!CellRenderEvent){
        /**
         * The cell's row object.
         * @property object
         * @memberOf CellRenderEvent
         */
        P.CellRenderEvent.prototype.object = {};
    }
})();