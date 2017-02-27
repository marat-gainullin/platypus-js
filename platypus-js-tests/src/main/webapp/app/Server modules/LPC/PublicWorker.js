/* global P */

/**
 * 
 * @author mg
 * @stateless
 * @public
 * @constructor
 */ 
function PublicWorker() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    self.execute = function (aAngle) {
        return Math.sin(aAngle);
    };
}
