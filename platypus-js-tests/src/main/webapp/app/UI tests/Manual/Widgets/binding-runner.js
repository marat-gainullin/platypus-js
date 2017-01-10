/**
 * 
 * @author mg
 */
function binding_runner() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function () {
        form.show();
    };
    
    
    form.button.onActionPerformed = function(event) {
        var b = new runtime_binding_test_1();
        b.show();
    };
}
