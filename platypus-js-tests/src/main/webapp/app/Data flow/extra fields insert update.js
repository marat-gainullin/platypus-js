/**
 * 
 * @author mg
 * @constructor
 */
function extra_fields_insert_update() {
    var NEW_ORDER_ID = 789654;
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        model.extraFields.push({order_id: NEW_ORDER_ID, good_id: 4589, good: 4589, extra1: 'yuyu', extra2: 8598});
        // Keys auto generaion feature test
        if (model.extraFields[model.extraFields.length - 1].good_id === null)
            throw 'good_id violation';
        model.save(function () {
            model.extraFields.params.aOrderId = NEW_ORDER_ID;
            model.extraFields.requery(function () {
                var found = model.extraFields.find({order_id: NEW_ORDER_ID});
                if (!found)
                    throw 'found violation';
                if (found.length !== 1)
                    throw 'found.length violation';
                if (found[0].order_id !== NEW_ORDER_ID)
                    throw 'found[0].order_id violation';
                model.extraFields.remove(found[0]);
                model.save(function () {
                    model.extraFields.requery(function () {
                        if (model.extraFields.length !== 0) {
                            throw "extraFields.length violation";
                        }
                        aOnSuccess();
                    }, function (e) {
                        P.Logger.severe(e);
                    });
                }, function (e) {
                    P.Logger.severe(e);
                });
            }, function (e) {
                P.Logger.severe(e);
            });
        }, function (e) {
            P.Logger.severe(e);
        });
    };
}
