(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.CellRenderEvent");
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
        Object.defineProperty(this, "column", {
            get: function() {
                var value = delegate.column;
                return P.boxAsJs(value);
            }
        });
        if(!P.CellRenderEvent){
            /**
             * The cell's column.
             * @property column
             * @memberOf CellRenderEvent
             */
            P.CellRenderEvent.prototype.column = {};
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
             * The cell's object.
             * @property object
             * @memberOf CellRenderEvent
             */
            P.CellRenderEvent.prototype.object = {};
        }
    };
})();