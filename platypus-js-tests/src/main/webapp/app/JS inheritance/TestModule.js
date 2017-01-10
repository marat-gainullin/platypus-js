/**
 * 
 * @author mg
 * @name TestModule
 */

function TestModule() {

    var self = this, model = P.loadModel(this.constructor.name);

    self.sum = function (a, b) {
        return a + b;
    };
}