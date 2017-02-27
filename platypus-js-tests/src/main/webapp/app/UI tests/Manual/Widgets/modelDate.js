/**
 * 
 * @author user
 */
function modelDate() {
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
    
    form.modelDate.onValueChange = function(event) {
        P.Logger.info("Value = "+ form.modelDate.value);
        P.Logger.info("text = " + form.modelDate.text);
    };

    form.button.onActionPerformed = function(event) {
        form.modelDate.timePicker = true;
        form.modelDate.datePicker = true;
        P.Logger.info(form.modelDate.timePicker);
        P.Logger.info(form.modelDate.datePicker);
    };
    form.button1.onActionPerformed = function(event) {
        form.modelDate.timePicker = false;
        form.modelDate.datePicker = true;
        P.Logger.info(form.modelDate.timePicker);
        P.Logger.info(form.modelDate.datePicker);
    };
    form.button2.onActionPerformed = function(event) {
        form.modelDate.datePicker = false;
        form.modelDate.timePicker = true;
        P.Logger.info(form.modelDate.timePicker);
        P.Logger.info(form.modelDate.datePicker);
    };
    form.button3.onActionPerformed = function(event) {
        form.modelDate.timePicker = false;
        form.modelDate.datePicker = false;
        P.Logger.info(form.modelDate.timePicker);
        P.Logger.info(form.modelDate.datePicker);
    };
}
