(function() {
    var javaClass = Java.type("com.eas.dbcontrols.CellRenderEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CellRenderEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor CellRenderEvent CellRenderEvent
     */
    P.CellRenderEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.CellRenderEvent.superclass)
            P.CellRenderEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "columnId", {
            get: function() {
                var value = delegate.columnId;
                return P.boxAsJs(value);
            }
        });
        if(!P.CellRenderEvent){
            /**
             * The cell's column ID.
             * @property columnId
             * @memberOf CellRenderEvent
             */
            P.CellRenderEvent.prototype.columnId = {};
        }
        Object.defineProperty(this, "id", {
            get: function() {
                var value = delegate.id;
                return P.boxAsJs(value);
            }
        });
        if(!P.CellRenderEvent){
            /**
             * The primary key of the data object.
             * @property id
             * @memberOf CellRenderEvent
             */
            P.CellRenderEvent.prototype.id = {};
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.CellRenderEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf CellRenderEvent
             */
            P.CellRenderEvent.prototype.source = {};
        }
        Object.defineProperty(this, "cell", {
            get: function() {
                var value = delegate.cell;
                return P.boxAsJs(value);
            }
        });
        if(!P.CellRenderEvent){
            /**
             * The "abstract" cell.
             * @property cell
             * @memberOf CellRenderEvent
             */
            P.CellRenderEvent.prototype.cell = {};
        }
        Object.defineProperty(this, "object", {
            get: function() {
                var value = delegate.object;
                return P.boxAsJs(value);
            }
        });
        if(!P.CellRenderEvent){
            /**
             * The cell's row object.
             * @property object
             * @memberOf CellRenderEvent
             */
            P.CellRenderEvent.prototype.object = {};
        }
    };
})();