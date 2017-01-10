/**
 * 
 * @author mg
 * @constructor
 */
function DependenciesTest() {
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        P.require(['Dependencies/AMD sample', "Dependency", "Dependencies/plain-dependency.js"], function (AmdSample) {
            var dep = new Dependency();
            var autoDep = new AutoDependency();
            var r2345 = new AmdSample().execute();
            if(r2345 !== 2345)
                throw 'AMD depedent failed';
            P.Logger.info("Variables from dependencies: " + (d1 + d2 + d3));
            if (d1 + d2 + d3 === 60) {
                aOnSuccess();
            } else {
                throw "Dependent failed";
            }
        }, function(e){
            P.Logger.severe(e);
        });
    };
}
