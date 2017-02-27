/**
 * 
 * @author user
 */
function testAdd() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };
    
    self.getView = function () {
        return form.view;
    };
    
    model.requery(/*function(){}*/);
    


    form.button.onActionPerformed = function(event) {
        P.Logger.info(form.htmlArea1.value);
    };
    
   form.htmlArea1.onActionPerformed = function(event) {
        P.Logger.info(form.htmlArea1.value);
   };


    form.button1.onActionPerformed = function(event) {
        form.htmlArea1.value = "<i>hello</i>";
    };
    
    form.htmlArea1.onValueChange = function(event) {
        P.Logger.info(form.htmlArea1.value)
    };

    form.button2.onActionPerformed = function(event) {
        form.htmlArea1.focus();
    };
    
    form.htmlArea1.onFocusGained = function(event) {
        P.Logger.info("Focus");
    };
    
    form.htmlArea1.onFocusLost = function(event) {
        P.Logger.info("Focus lost");
    };


    form.button3.onActionPerformed = function(event) {
        form.formattedField.focus();
    };
    form.button4.onActionPerformed = function(event) {
        form.htmlArea1.enabled = false;
    };
    
    form.button5.onActionPerformed = function(event) {
        form.htmlArea1.enabled = true;
    };
}


