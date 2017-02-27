/**
 * 
 * @author mg
 */
function ModelGeneralTestsManualRunner() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function () {
        form.show();
    };
    model.requery(function () {
        // TODO : place your code here
    });
    
    form.btnLoadEntity.onActionPerformed = function(event) {
        var test = new Load_Entity_Test();
        test.execute();
    };
    form.btnCreateEntity.onActionPerformed = function(event) {
        var test = new Create_Entity_Test();
        test.execute();
    };
    form.btnModelAPI.onActionPerformed = function(event) {
        var test = new ModelAPI();
        test.execute();
    };
    form.modifiedTest.onActionPerformed = function(event) {
        var test = new ModelModyfiedTestClient();
        test.execute();
    };
}
