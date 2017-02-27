/* global P */

/**
 * 
 * @author mg
 * @public 
 * @constructor
 */
function RESTTest() {
    var self = this, model = P.loadModel(this.constructor.name);

    var customers = {ohio: 78, ivanovo: 67};

    /**
     * @get /customers
     * @returns {RESTTest.customers}
     */
    this.getCustomers = function (aCustomer) {
        if (aCustomer)
            return customers[aCustomer];
        else
            return customers;
    };

    /**
     * @post /customers
     * @returns {RESTTest.customers}
     */
    this.createCustomer = function () {
        var http = new P.HttpContext();
        var custNo = 'cust-' + P.ID.generate();
        customers[custNo] = JSON.parse(http.request.body);
        return custNo;
    };

    /**
     * @delete /customers
     * @returns {undefined}
     */
    this.deleteCustomer = function (aCustomer) {
        if (aCustomer) {
            var deleted = customers[aCustomer];
            delete customers[aCustomer];
        } else {
            customers = {};
        }
    };
}