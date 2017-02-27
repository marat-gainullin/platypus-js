/**
 * 
 * @author user
 */
function sliderTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    form.txtMin.text = form.slider.minimum;
    form.txtMax.text = form.slider.maximum;
    form.txtValue.text = form.slider.value;

            
    self.show = function () {
        form.show();
    };
    
    // TODO : place your code here
    
    model.requery(function () {
        // TODO : place your code here
    });
    
    form.txtMin.onActionPerformed = function(event) {
        form.slider.minimum = Number(form.txtMin.text) ;
    };
    form.txtMax.onActionPerformed = function(event) {
        form.slider.maximum = Number(form.txtMax.text) ;
    };
    form.txtValue.onActionPerformed = function(event) {
        form.slider.value = Number(form.txtValue.text);
    };
    
    form.slider.onActionPerformed = function(event) {
        form.txtValue.text = form.slider.value;
    };

    form.slider.onMouseClicked = function(event) {
        P.Logger.info("Hello");
    };

    form.slider.onFocusGained = function(event) {
        P.Logger.info("Hello");
    };
    
    form.slider.onMouseEntered = function(event) {
        P.Logger.info("Hello");
    };
    

    form.button.onActionPerformed = function(event) {
        form.txtValue.text = form.slider.value;
    };
}
