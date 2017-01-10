/**
 * 
 * @author mg
 * @name ORM_Test_View
 */
function ORM_Test_View() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function() {
        form.show();
    };

    form.btnTest.onActionPerformed = function(event) {
        var test = new ORM_Relations_Test();
        test.execute();
    };
}