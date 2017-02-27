/**
 * 
 * @author mg
 */
function absolutePaneTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };

    form.btnLeft.onActionPerformed = function(event) {
        form.panel.left -= 10;
    };
    form.btnRight.onActionPerformed = function(event) {
        form.panel.left += 10;
    };
    form.btnUp.onActionPerformed = function(event) {
        form.panel.top -= 10;
    };
    form.btnDown.onActionPerformed = function(event) {
        form.panel.top += 10;
    };
    form.btnWider.onActionPerformed = function(event) {
        form.panel.width += 10;
    };
    form.btnThiner.onActionPerformed = function(event) {
        form.panel.width -= 10;
    };
    form.btnTaller.onActionPerformed = function(event) {
        form.panel.height += 10;
    };
    form.btnSmaller.onActionPerformed = function(event) {
        form.panel.height -= 10;
    };
}
