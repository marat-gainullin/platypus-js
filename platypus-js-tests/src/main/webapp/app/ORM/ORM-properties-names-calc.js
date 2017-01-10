/**
 * 
 * @author mg
 * @constructor
 */
function ORM_properties_names_calc() {
    var self = this
            , model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        model.requery(function () {
            model.ORM_properties_names_test.forEach(function (anElement) {
                if(typeof anElement.cName === 'undefined' || typeof anElement.cAdress === 'undefined' || typeof anElement.cId === 'undefined')
                    throw 'ORM names in from sql query violation';
                P.Logger.info("anElement.cName: " + anElement.cName);
                P.Logger.info("anElement.cAdress: " + anElement.cAdress);
                P.Logger.info("anElement.cId: " + anElement.cId);
            });
            aOnSuccess();
        });
    };
}
