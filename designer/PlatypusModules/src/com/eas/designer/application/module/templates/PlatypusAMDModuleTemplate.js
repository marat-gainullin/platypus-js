/**
 * 
 * @author ${user}
 * @module ${appElementName}
 */
define(['orm'], function (Orm, ModuleName) {
    return function () {
        var self = this, model = Orm.loadModel(ModuleName);

        // TODO : place constructor code here

        self.execute = function () {
            // TODO : place application code here
        };
    };
});
