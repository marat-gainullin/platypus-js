/* global P */

/**
 * 
 * @author mg
 * @constructor
 */
function AccounterClient() {
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess, aOnFailure) {
        var accounter = new P.ServerModule("Accounter");
        var dt = new Date();
        accounter.execute(100, function (aRes) {
            var dt1 = new Date();
            aOnSuccess(aRes);
            P.Logger.info("Accounter test time: " + (dt1 - dt) + "ms");
        }, aOnFailure);
    };
}
