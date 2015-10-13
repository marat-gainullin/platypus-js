/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.gui.Cursor";
    var javaClass = Java.type(className);
    /**
     * Constructs new cursor object.
     * @param type Type of new cursor.
     * @constructor Cursor Cursor
     */
    function Cursor(type) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(B.boxAsJava(type))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(Cursor.superclass)
            Cursor.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
    Object.defineProperty(Cursor, "CROSSHAIR", {value: new Cursor(1)});
    Object.defineProperty(Cursor, "DEFAULT", {value: new Cursor(0)});
    Object.defineProperty(Cursor, "AUTO", {value: new Cursor(0)});
    Object.defineProperty(Cursor, "E_RESIZE", {value: new Cursor(11)});
    Object.defineProperty(Cursor, "HAND", {value: new Cursor(12)});
    Object.defineProperty(Cursor, "MOVE", {value: new Cursor(13)});
    Object.defineProperty(Cursor, "NE_RESIZE", {value: new Cursor(7)});
    Object.defineProperty(Cursor, "NW_RESIZE", {value: new Cursor(6)});
    Object.defineProperty(Cursor, "N_RESIZE", {value: new Cursor(8)});
    Object.defineProperty(Cursor, "SE_RESIZE", {value: new Cursor(5)});
    Object.defineProperty(Cursor, "SW_RESIZE", {value: new Cursor(4)});
    Object.defineProperty(Cursor, "S_RESIZE", {value: new Cursor(9)});
    Object.defineProperty(Cursor, "TEXT", {value: new Cursor(2)});
    Object.defineProperty(Cursor, "WAIT", {value: new Cursor(3)});
    Object.defineProperty(Cursor, "W_RESIZE", {value: new Cursor(10)});

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Cursor(null, aDelegate);
    });
    return Cursor;
});