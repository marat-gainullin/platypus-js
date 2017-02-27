/**
 * 
 * @author mg
 */
function ORM_properties_test_view() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };
    
    model.requery();
    
    form.btnTestIt.onActionPerformed = function(event) {
        model.ORM_properties_names_test.forEach(function(anElement){
            P.Logger.info("anElement[0]: " + anElement[0]);
            P.Logger.info("anElement.cName: " + anElement.cName);
            P.Logger.info("anElement.cAdress: " + anElement.cAdress);
            P.Logger.info("anElement.cId: " + anElement.cId);
        });
    };
}
