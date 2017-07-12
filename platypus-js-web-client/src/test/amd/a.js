/* global expect */

define('c', function(c){
    expect(c).toBeDefined();
    expect(c.moduleName).toEqual('c module');
    return {moduleName: 'a module'};
});