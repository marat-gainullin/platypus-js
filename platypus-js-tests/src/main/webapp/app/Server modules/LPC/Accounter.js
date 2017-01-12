/* global P */
/**
 * 
 * @author mg
 * @resident
 * @public
 * @constructor
 */
function Accounter() {
    var self = this;//, model = P.loadModel(this.constructor.name);

    self.execute = function (aCycles, onSuccess, onFailure) {
        var worker = new P.ServerModule("Worker");
        var calls = 0;
        var errors = [];
        var sinusesSum = 0;
        function complete(aSin) {
            sinusesSum += aSin;
            if (++calls === aCycles) {
                if (errors.length > 0)
                    onFailure(errors.join('\n'));
                else
                    onSuccess(sinusesSum);
            }
        }
        function sinusCalcSucceded(aRes) {
            complete(aRes);
        }
        function sinusCalcFailed(aError) {
            P.Logger.severe(aError);
            errors.push(aError);
            complete(0);
        }
        for (var i = 0; i < aCycles; i++) {
            worker.execute(30 + i, sinusCalcSucceded, sinusCalcFailed);
        }
    };
}
