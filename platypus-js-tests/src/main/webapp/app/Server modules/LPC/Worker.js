/* global P */
/**
 * 
 * @author mg
 * @stateless
 * @constructor
 */
function Worker() {
    var self = this;

    self.execute = function (aAngle) {
        var cosSum = 0;
        for (var i = 0; i < 250; i++)
            cosSum += Math.sin(aAngle);
        return cosSum;
    };
}
