/**
 * 
 * @author mg
 */
function ConstantSelector() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };
    
    self.showModal = function(aCallback) {
        form.showModal(aCallback);
    };
    
    form.button.onActionPerformed = function(event) {
        form.close(91);
    };
}
