/* global P */

/**
 * 
 * @author mg
 * @stateless
 * @public 
 * @constructor
 */
function PrincipalTest() {
    var self = this, model = P.loadModel(this.constructor.name);

    var principal = require('security').principal();
    this.testName = function () {
        return principal.name;
    };

    this.testHasRole = function (aRole) {
        return principal.hasRole(aRole);
    };

}
