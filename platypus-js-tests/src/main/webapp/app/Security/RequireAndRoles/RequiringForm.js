/**
 * 
 * @author mg
 */
function RequiringForm() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function() {
        form.show();
    };
    
    form.btnTest.onActionPerformed = function(event) {
        P.require("SecuredForm", function() {
            var f = new SecuredForm();
            f.show();
        }, function(error) {
            throw "Access is denied!" + error;
        });
    };
    form.btnLogout.onActionPerformed = function(event) {
        P.logout();
    };
}
