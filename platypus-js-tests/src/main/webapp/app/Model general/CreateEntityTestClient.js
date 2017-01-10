/**
 * 
 * @author mg
 * @constructor
 */ 
function CreateEntityTestClient() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    self.execute = function (aOnSuccess, aOnFailure) {
        var proxy = new P.ServerModule('Create_Entity_Test');
        proxy.execute(function () {
            aOnSuccess();
        }, function (e) {
            aOnFailure(e);
        });
    };
}
