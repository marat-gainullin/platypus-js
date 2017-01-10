/**
 * @public
 * @author mg, ak
 * @constructor
 */

function ParallelRequireTest() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    self.execute = function (aOnSuccess) {
        var maxCount = 2;
        var cnt = 0;
        function success() {
            cnt++;
            if (cnt === maxCount)
                aOnSuccess ? aOnSuccess() : P.Logger.info('Passed');
        }
        
        for (var j = 0; j < maxCount; j++) {
            P.require(["Dependencies/parallel-dependency.js"], function () {
                if (parallel_test())
                    success();
            });
        }
    };
}
