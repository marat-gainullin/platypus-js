/**
 * Secure report.js
 * @name 133239674465069
 *
 * Created on Mar 22, 2012, 9:12:24 AM
 * @module
 * @rolesAllowed role1, role2
 */

function SecureReport() {

    var self = this, model = P.loadModel(this.constructor.name);

    /**
     * @rolesAllowed role2
     */
    self.getReport = function () {
        var template = P.loadTemplate(this.constructor.name, {model: model, name: "test"});
        return template.generateReport();
    };
}