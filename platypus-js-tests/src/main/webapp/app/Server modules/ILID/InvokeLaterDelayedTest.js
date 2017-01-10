/* global P */

/**
 * 
 * @author Марат
 * @stateless
 * @public 
 * @constructor
 */
function InvokeLaterDelayedTest() {
    var self = this, model = P.loadModel(this.constructor.name);

    this.execute = function (aOnSuccess) {
        this.test(100, aOnSuccess);
    };
    
    this.test = function (aCalls, aOnSuccess) {
        var executed = 0;
        var calls1 = 0;
        for (var i = 0; i < aCalls; i++) {
            P.invokeLater(function () {
                if (++calls1 === aCalls) {
                    if (++executed === 2){
                        aOnSuccess();
                    }
                }
            });
        }
        var calls2 = 0;
        for (var i = 0; i < aCalls; i++) {
            P.invokeDelayed(10, function () {
                if (++calls2 === aCalls) {
                    if (++executed === 2){
                        aOnSuccess();
                    }
                }
            });
        }
    };
}
