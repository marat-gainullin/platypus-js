/**
 * 
 * @author Andrew
 */
function SecureResource() {
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
    
    form.button.onActionPerformed = function(event) {
        self.execute();
    };
    
    self.execute = function(){
        P.Resource.load("secure/error.html", function(aData) {
            alert(aData);
        }, function (error) {
           throw error; 
        });
    };
}
