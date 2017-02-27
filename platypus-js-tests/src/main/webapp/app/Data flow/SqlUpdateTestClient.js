/**
 * 
 * @author mg
 * @constructor
 */
function SqlUpdateTestClient() {
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess, aOnFailure) {
        var proxy = new P.ServerModule('SqlUpdateTest');
        proxy.execute(function () {
            aOnSuccess();
        }, function (e) {
            aOnFailure(e);
        });
    };
}
