/**
 * 
 * @author mgainullin
 */
define(['orm'], function (Orm, ModuleName) {
    function fullModule() {
        var self = this,
                model = Orm.createModel(ModuleName),
                model1 = Orm.loadModel(ModuleName);
        
        self.model = model;
        self.model1 = model1;
    }
    return fullModule;
});
