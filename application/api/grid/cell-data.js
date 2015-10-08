/* global Java */

define(['boxing'], function(B) {
    var className = "com.bearsoft.gui.grid.data.CellData";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor CellData CellData
     */
    function CellData() {
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
        if(CellData.superclass)
            CellData.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "display", {
            get: function() {
                var value = delegate.display;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.display = B.boxAsJava(aValue);
            }
        });

    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new CellData(aDelegate);
    });
    return CellData;
});