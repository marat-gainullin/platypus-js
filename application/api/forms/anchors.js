/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.forms.Anchors";
    var javaClass = Java.type(className);
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
    function Anchors(left, width, right, top, height, bottom) {
        var maxArgs = 6;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 6 ? new javaClass(B.boxAsJava(left), B.boxAsJava(width), B.boxAsJava(right), B.boxAsJava(top), B.boxAsJava(height), B.boxAsJava(bottom))
            : arguments.length === 5 ? new javaClass(B.boxAsJava(left), B.boxAsJava(width), B.boxAsJava(right), B.boxAsJava(top), B.boxAsJava(height))
            : arguments.length === 4 ? new javaClass(B.boxAsJava(left), B.boxAsJava(width), B.boxAsJava(right), B.boxAsJava(top))
            : arguments.length === 3 ? new javaClass(B.boxAsJava(left), B.boxAsJava(width), B.boxAsJava(right))
            : arguments.length === 2 ? new javaClass(B.boxAsJava(left), B.boxAsJava(width))
            : arguments.length === 1 ? new javaClass(B.boxAsJava(left))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(Anchors.superclass)
            Anchors.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Anchors(null, null, null, null, null, null, aDelegate);
    });
    return Anchors;
});