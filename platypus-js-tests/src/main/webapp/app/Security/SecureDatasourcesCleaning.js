/**
 * 
 * @author mg
 * @constructor
 * @public
 * @stateless
 */ 
function SecureDatasourcesCleaning() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    self.execute = function () {
        model.tempDataDeleter.executeUpdate();
    };
}
