/**
 * 
 * @author mg
 * @name SimpleStyleTest
 */
function SimpleStyleTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    form.btnTest.onActionPerformed = function (evt) {
        var viewForeground = form.view; //.foreground;
        var viewBackground = form.view.background;
        var viewFont = form.view.font;
        var viewCursor = form.view.cursor;
        var viewOpaque = form.view.opaque;
        form.panel.background = P.Color.BLUE;
        var viewBG = form.panel.background;
        form.label.text = viewBG;
        var t = 0;
    };

    function rowsetRequeried(evt) {
        // TODO Добавьте здесь свой код:
    }

}