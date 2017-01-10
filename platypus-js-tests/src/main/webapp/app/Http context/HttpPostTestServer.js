/**
 * 
 * @author alexey
 * @constructor
 * @public
 * @stateless
 */ 
function HttpPostTestServer() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    self.simplePostTest = function() {
        return 'ok!';
    };
    
    self.testBody = function(aInput) {
        var c = new P.HttpContext();
        return c.request.body;
    };
    
    /**
     * @post /restSimpleTest
     */
    self.restTest = function() {
        return 'ok!';
    };
    
    /**
     * @POST /restUPCASETest
     * @returns {undefined}
     */
    self.restUPTest = function() {
        return 'ok!';
    };
    
    /**
     * @post /restGetObjTest
     * @returns {undefined}
     */
    self.restGetObjTest = function() {
        return {result: 'ok!'};
    };
    
    /**
     * @post /restParamsTest
     * @returns {undefined}
     */
    self.restParamsTest = function() {
        var request = (new P.HttpContext()).request;
        var resp = {};
        for (var j in request.params) {
            resp[j] = request.params[j];
        }
        return resp;
    };
}
