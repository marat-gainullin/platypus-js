/**
 * 
 * @author mg
 */
function WindowsTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function() {
        form.show();
        for (var i = 0; i < 3; i++) {
            var w = new design_binding_test();
            w.showOnDesktop(form.desktopPane);
        }
    };

    form.btnClone.onActionPerformed = function(event) {
        var clone = new WindowsTest();
        clone.show();
    };
    form.btnMinimize.onActionPerformed = function(event) {
        form.desktopPane.minimizeAll();
    };
    form.btnMaximize.onActionPerformed = function(event) {
        form.desktopPane.maximizeAll();
    };
    form.btnClose.onActionPerformed = function(event) {
        P.Logger.info(form.desktopPane.forms);
        P.Logger.info(form.desktopPane.forms.length);
        form.desktopPane.closeAll();
        P.Logger.info(form.desktopPane.forms);
        P.Logger.info(form.desktopPane.forms.length);
        if (form.desktopPane.forms.length!=0){
            throw "Not all widgets are closed"
        }
    };
    form.btnRestore.onActionPerformed = function(event) {
        form.desktopPane.restoreAll();
    };
}
