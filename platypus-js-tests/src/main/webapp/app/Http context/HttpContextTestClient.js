/**
 * 
 * @author Andrew
 */
function HttpContextTestClient() {
    var self = this
            , model = P.loadModel(this.constructor.name);

    this.execute = function (aOnSuccess) {
        var testModule = new P.ServerModule("TestHttpContext");
        testModule.addParam(function (result) {
            testModule.addCookie(function (result) {
                testModule.changeBody(function (result) {
                    P.Logger.info(result);
                    aOnSuccess();
                }, function () {
                    throw "Could not change body in response.";
                });
            }, function () {
                throw "Could not add cookie in response.";
            });
        }, function () {
            throw "Could not add param in response.";
        });
    };
}
