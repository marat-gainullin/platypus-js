/**
 * 
 * @author mg
 * @constructor
 */
function SqlExecuteUpdateTestClient() {
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess, aOnFailure) {
        var proxy = new P.ServerModule('SqlExecuteUpdateTest');
        proxy.execute(function () {
            aOnSuccess();
        }, function (e) {
            aOnFailure(e);
        });
    };
}
