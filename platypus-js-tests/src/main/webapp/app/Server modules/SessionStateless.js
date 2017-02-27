/**
 * 
 * @author Andrew
 * @stateless
 * @public 
 * @constructor
 */
function SessionStateless() {
    var self = this, model = P.loadModel(this.constructor.name);
    var callCount = 0;
    
    self.getCallsCount = function() {
        return callCount;  
    };
    
    self.incCallsCount = function(aCounts) {
        callCount += aCounts;  
    };
}
