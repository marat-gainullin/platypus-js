/**
 * 
 * @author user
 * @constructor
 */ 
function ModelModyfiedTestClient() {
    var self = this, model = P.loadModel(this.constructor.name);
    
   self.execute = function (aOnSuccess, aOnFailure) {
        var proxy = new P.ServerModule('ModelModyfiedTest');
         proxy.execute(function () {
            aOnSuccess();
        }, function (e) {
            aOnFailure(e);
        });
    };
}
