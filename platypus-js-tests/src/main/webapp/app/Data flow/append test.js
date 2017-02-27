/**
 * 
 * @author mg
 * @constructor
 */
function append_test() {
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess, aOnFailure) {
        model.customers.requery(function () {
            var originalLength = model.customers.length;
            model.customers.query({}, function (aData) {
                var appendedLength = aData.length;
                model.customers.append(aData);
                if (model.customers.length !== appendedLength + originalLength)
                    throw 'length violation 1';
                model.customers.query({}, function (aData) {
                    var appendedLength1 = aData.length;
                    model.customers.append(aData);
                    if (model.customers.length !== appendedLength + appendedLength1 + originalLength)
                        throw 'length violation 2';
                    model.customers.requery(function () {
                        if (model.customers.length !== originalLength)
                            throw 'length violation 3';
                        aOnSuccess();
                    });
                });
            });
        });
    };
}
