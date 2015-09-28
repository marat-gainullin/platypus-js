/* global Java */

define(['boxing'], function(P) {
    /**
     * Constructs new cursor object.
     * @param type Type of new cursor.
     * @constructor Cursor Cursor
     */
    function Cursor(type) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(type))
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

    var className = "com.eas.gui.Cursor";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Cursor(null, aDelegate);
    });
    return Cursor;
});