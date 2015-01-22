(function() {
    var javaClass = Java.type("com.eas.client.forms.Anchors");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Anchors(null, null, null, null, null, null, aDelegate);
    });
    
    /**
     * Component's layout anchors for AnchorsPane.
     * Two constraint values of three possible must be provided for X and Y axis, other constraints must be set to <code>null</code>.* Parameters values can be provided in pixels, per cents or numbers, e.g. '30px', '30' or 10%.
     * @param left a left anchor
     * @param width a width value
     * @param right a right anchor
     * @param top a top anchor
     * @param height a height value
     * @param bottom a bottom anchor
     * @constructor Anchors Anchors
     */
    P.Anchors = function (left, width, right, top, height, bottom) {
        var maxArgs = 6;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 6 ? new javaClass(P.boxAsJava(left), P.boxAsJava(width), P.boxAsJava(right), P.boxAsJava(top), P.boxAsJava(height), P.boxAsJava(bottom))
            : arguments.length === 5 ? new javaClass(P.boxAsJava(left), P.boxAsJava(width), P.boxAsJava(right), P.boxAsJava(top), P.boxAsJava(height))
            : arguments.length === 4 ? new javaClass(P.boxAsJava(left), P.boxAsJava(width), P.boxAsJava(right), P.boxAsJava(top))
            : arguments.length === 3 ? new javaClass(P.boxAsJava(left), P.boxAsJava(width), P.boxAsJava(right))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(left), P.boxAsJava(width))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(left))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Anchors.superclass)
            P.Anchors.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
})();