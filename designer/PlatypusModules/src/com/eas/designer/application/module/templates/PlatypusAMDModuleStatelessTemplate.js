/**
 * 
 * @author ${user}
 * @stateless
 * @public 
 * @module ${appElementName}
 */
define(['orm'], function (Orm, ModuleName) {
    function module_constructor() {
        var self = this, model = Orm.loadModel(ModuleName);
    
        // TODO : place your code here
    }
    return module_constructor;
});
