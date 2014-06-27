(function() {
    var javaClass = Java.type("com.eas.gui.Font");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Font(null, null, null, aDelegate);
    });
    
    /**
     * Font object, which is used to render text in a visible way.
     * @param family a font family name, e.g. 'SansSerif'
     * @param style a FontStyle object
     * @param size the size of the font
     * @constructor Font Font
     */
    P.Font = function (family, style, size) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(family), P.boxAsJava(style), P.boxAsJava(size))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(family), P.boxAsJava(style))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(family))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Font.superclass)
            P.Font.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
})();