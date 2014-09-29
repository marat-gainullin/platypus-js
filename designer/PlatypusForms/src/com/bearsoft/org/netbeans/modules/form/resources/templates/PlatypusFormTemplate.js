/**
 * 
 * @author ${user}
 */
function ${appElementName}(){
    var self = this
    , model = P.loadModel(this.constructor.name)
    , form = P.loadForm(this.constructor.name, model);
    
    self.show = function(){
        form.show();
    };
    
    model.requery(/*function(){
            // TODO : place your code here
        }*/);
    
    // TODO : place your code here
}
