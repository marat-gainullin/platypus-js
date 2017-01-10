/**
 * 
 * @author user
 */
function askUserName() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    // TODO : place your code here

    model.requery(function () {
        // TODO : place your code here
    });


    form.btnOk.onActionPerformed = function (event) {
        form.close(form.txtName.text);
    };

    self.showModal = function (aCallback) {
        form.showModal(aCallback);
        //        P.invokeLater(function () {
            form.toFront();
            form.txtName.focus();
//        });

    };
    form.txtName.onActionPerformed = function (event) {
        form.close(form.txtName.text);
    };
}
