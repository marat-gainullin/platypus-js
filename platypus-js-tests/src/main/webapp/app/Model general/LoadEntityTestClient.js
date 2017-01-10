/**
 * 
 * @author mg
 * @constructor
 */ 
function LoadEntityTestClient() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    self.execute = function (aOnSuccess, aOnFailure) {
        var proxy = new P.ServerModule('Load_Entity_Test');
        proxy.execute(function () {
            aOnSuccess();
        }, function () {
            aOnFailure();
        });
    };
}
