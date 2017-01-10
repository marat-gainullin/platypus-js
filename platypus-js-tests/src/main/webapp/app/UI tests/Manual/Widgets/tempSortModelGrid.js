/**
 * 
 * @author user
 */
// TODO  Add another data source
function tempModelCombo() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function () {
        form.show();
    };
    
    // TODO : place your code here
    
    model.requery(function () {
        // TODO : place your code here
    });
    
    form.btnAdd.onActionPerformed = function(event) {
        model.qPetTypes.push({});
    };
    form.btnSave.onActionPerformed = function(event) {
        model.save();
    };
    form.btnDelete.onActionPerformed = function(event) {
        
        for (var i in form.modelGrid.selected){
            model.qPetTypes.splice(model.qPetTypes.indexOf(form.modelGrid.selected[i]),1);
        }
    };
    form.button.onActionPerformed = function(event) {
        form.modelGrid.unsort();
        
    };
    form.button1.onActionPerformed = function(event) {
        form.modelGrid.colName.sort();
        form.modelGrid.redraw();
    };
    
    form.button2.onActionPerformed = function(event) {
       form.modelGrid.colName.sortDesc();
       form.modelGrid.redraw();
    };
    
    form.button3.onActionPerformed = function(event) {
        form.modelGrid.colName.unsort();
    };
}
