/**
 * 
 * @author mg
 */
function EasHRValidatorTest() {
    var self = this
            , model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        model.hrLocations.requery(function () {
            model.hrLocations[1].postal_code = '-1';
            model.save(function (aChangedCount) {
                throw 'EasHRValidatorTest save violation';
            }, function (e) {
                model.revert();
                aOnSuccess();
            });
        }, function (e) {
            P.Logger.severe(e);
        });
    };
}
