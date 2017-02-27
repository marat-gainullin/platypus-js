/**
 * 
 * @author mg
 */
function ProgressSlider() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };
    
    form.button.onActionPerformed = function(event) {
        P.Logger.info("form.slider.minimum: " + form.slider.minimum);
        P.Logger.info("form.slider.maximum: " + form.slider.maximum);
        P.Logger.info("form.slider.value:   " + form.slider.value);
        form.slider.value = 45;
        P.Logger.info("form.slider.value 1: " + form.slider.value);
        form.slider.minimum = 40;
        form.slider.value = 55;
        form.slider.maximum = 80;
        P.Logger.info("form.slider.minimum: " + form.slider.minimum);
        P.Logger.info("form.slider.maximum: " + form.slider.maximum);
        P.Logger.info("form.slider.value:   " + form.slider.value);
        
        P.Logger.info("form.progressBar.minimum: " + form.progressBar.minimum);
        P.Logger.info("form.progressBar.maximum: " + form.progressBar.maximum);
        P.Logger.info("form.progressBar.value:   " + form.progressBar.value);
        form.progressBar.value = 45;
        P.Logger.info("form.progressBar.value 1: " + form.progressBar.value);
        form.progressBar.minimum = 40;
        form.progressBar.value = 55;
        form.progressBar.maximum = 80;
        P.Logger.info("form.progressBar.minimum: " + form.progressBar.minimum);
        P.Logger.info("form.progressBar.maximum: " + form.progressBar.maximum);
        P.Logger.info("form.progressBar.value:   " + form.progressBar.value);
    };
}
