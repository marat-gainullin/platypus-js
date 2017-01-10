/**
 * 
 * @author mg
 */
function runtime_binding_test() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };
    
    model.requery();
    
    form.btnBind.onActionPerformed = function(event) {
        form.modelCheckBox.field = model.params.schema.nvalue;
        form.modelCombo.field = model.params.schema.dirValue;
        form.modelCombo.valueField = model.appElements.schema.MDENT_ID;
        form.modelCombo.displayField = model.appElements.schema.MDENT_NAME;
        form.modelFormattedField.field = model.params.schema.dirValue;
        form.modelFormattedField1.field = model.params.schema.svalue;
        form.modelSpin.field = model.params.schema.nvalue;
        form.modelDate.field = model.params.schema.dvalue;
        form.modelTextArea.field = model.params.schema.svalue;
    };
    
    form.btnUnBind.onActionPerformed = function(event) {
        form.modelCheckBox.field = null;
        form.modelCombo.field = null;
        form.modelCombo.valueField = null;
        form.modelCombo.displayField = null;
        form.modelFormattedField.field = null;
        form.modelFormattedField1.field = null;
        form.modelSpin.field = null;
        form.modelDate.field = null;
        form.modelTextArea.field = null;
    };
}
