/**
 * 
 * @author mg
 */
define(['orm'], function (Orm, ModuleName) {
    function module_constructor() {
        var self = this, model = Orm.loadModel(ModuleName);
        
        self.execute = function () {
            return 2345;
        };
    }
    return module_constructor;
});
