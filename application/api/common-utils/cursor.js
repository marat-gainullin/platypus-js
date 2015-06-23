(function() {
    var className = "com.eas.gui.Cursor";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.Cursor(null, aDelegate);
    });
    
    /**
     * Constructs new cursor object.
     * @param type Type of new cursor.
     * @constructor Cursor Cursor
     */
    P.Cursor = function (type) {
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
        if(P.Cursor.superclass)
            P.Cursor.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
})();