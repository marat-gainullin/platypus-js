/**
 * 
 * @author mg
 * @name fakeSelector
 */
function FakeSelector() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    var selectedValue = 8;
     self.show = function() {
       form.show();  
     };
     
    form.btnSelect.onActionPerformed = function(event) {
        form.close(++selectedValue);
    };
}