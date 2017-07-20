define(function () {
    /**
     * Component's layout anchors for AnchorsPane.
     * Two constraint values of three possible must be provided for X and Y axis, other constraints must be set to <code>null</code>. * Parameters values can be provided in pixels, per cents or numbers, e.g. '30px', '30' or 10%.
     * @param left a left anchor
     * @param width a width value
     * @param right a right anchor
     * @param top a top anchor
     * @param height a height value
     * @param bottom a bottom anchor
     * @constructor Anchors Anchors
     */
    function Anchors(left, width, right, top, height, bottom) {
        this.left = left;
        this.width = width;
        this.right = right;
        this.top = top;
        this.height = height;
        this.bottom = bottom;
    }
    return Anchors;
});