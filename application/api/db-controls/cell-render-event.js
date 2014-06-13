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
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * The cell's column ID.
         * @property columnId
         * @memberOf CellRenderEvent
         */
        Object.defineProperty(this, "columnId", {
            get: function() {
                var value = delegate.columnId;
                return P.boxAsJs(value);
            }
        });

        /**
         * The primary key of the data object.
         * @property id
         * @memberOf CellRenderEvent
         */
        Object.defineProperty(this, "id", {
            get: function() {
                var value = delegate.id;
                return P.boxAsJs(value);
            }
        });

        /**
         * The source object of the event.
         * @property source
         * @memberOf CellRenderEvent
         */
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });

        /**
         * The "abstract" cell.
         * @property cell
         * @memberOf CellRenderEvent
         */
        Object.defineProperty(this, "cell", {
            get: function() {
                var value = delegate.cell;
                return P.boxAsJs(value);
            }
        });

        /**
         * The cell's row object.
         * @property object
         * @memberOf CellRenderEvent
         */
        Object.defineProperty(this, "object", {
            get: function() {
                var value = delegate.object;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();