/**
 * 
 * @author mg
 */
function Login_Logout_Test_View() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    form.btnLogout.onActionPerformed = function (event) {
        P.principal.logout();
        for (var i = 0; i < 50; i++) {
            P.require('ModelAPI', function () {
            });
        }
    };
}