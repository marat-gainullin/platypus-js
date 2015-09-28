/* global Java */

define(['boxing'], function(P) {
    /**
     * Font object, which is used to render text in a visible way.
     * @param family a font family name, e.g. 'SansSerif'
     * @param style a FontStyle object
     * @param size the size of the font
     * @constructor Font Font
     */
    function Font(family, style, size) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(family), P.boxAsJava(style), P.boxAsJava(size))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(family), P.boxAsJava(style))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(family))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(Font.superclass)
            Font.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };

    var className = "com.eas.gui.Font";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Font(null, null, null, aDelegate);
    });
    return Font;
});