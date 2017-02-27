/**
 * 
 * @author mg
 * @constructor
 */
function select_stateless_test() {
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess, aOnFailure) {
        model.customers.requery(function () {
            var expectedLength = model.customers.length;
            var completed = 0;
            function complete() {
                if (++completed === 5) {
                    aOnSuccess();
                }
            }
            function expecter(aData) {
                if (aData.length === expectedLength)
                    complete();
            };
            model.customers.query({}, expecter);
            model.customers.query({}, expecter);
            model.customers.requery(function () {
                if (model.customers.length === expectedLength)
                    complete();
            });
            model.customers.query({}, expecter);
            model.customers.query({}, expecter);
        });
    };
}
