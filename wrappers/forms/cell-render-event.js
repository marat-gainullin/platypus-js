/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.forms.events.CellRenderEvent";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor CellRenderEvent CellRenderEvent
     */
    function CellRenderEvent() {
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
        if(CellRenderEvent.superclass)
            CellRenderEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        /**
         * The cell's column.
         */
        this.column = new Object();
        Object.defineProperty(this, "column", {
            get: function() {
                var value = delegate.column;
                return B.boxAsJs(value);
            }
        });

        /**
         * The source object of the event.
         */
        this.source = new Object();
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return B.boxAsJs(value);
            }
        });

        /**
         * The "abstract" cell.
         */
        this.cell = new Object();
        Object.defineProperty(this, "cell", {
            get: function() {
                var value = delegate.cell;
                return B.boxAsJs(value);
            }
        });

        /**
         * The cell's object.
         */
        this.object = new Object();
        Object.defineProperty(this, "object", {
            get: function() {
                var value = delegate.object;
                return value;
            }
        });

    }

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new CellRenderEvent(aDelegate);
    });
    return CellRenderEvent;
});