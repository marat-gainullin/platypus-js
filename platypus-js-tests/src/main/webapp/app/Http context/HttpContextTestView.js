/**
 * 
 * @author Andrew
 */
function HttpContextTestView() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    model.requery(function () {
    });

    form.button.onActionPerformed = function (event) {
        self.execute();
    };
    
    self.execute = function(){
        var testModule = new P.ServerModule("TestHttpContext");
        testModule.addParam(function (result) {
        }, function () {
            throw "Could not add param in response.";
        });
        testModule.addCookie(function (result) {
        }, function () {
            throw "Could not add cookie in response.";
        });
        testModule.changeBody(function (result) {
        }, function () {
            throw "Could not change body in response.";
        });
    };
}
