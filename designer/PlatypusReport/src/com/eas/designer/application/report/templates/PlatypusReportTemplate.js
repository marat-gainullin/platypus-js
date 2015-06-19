/* global P */

/**
 * 
 * @author ${user}
 */
function ${appElementName}(){
    var self = this
    , model = P.loadModel(this.constructor.name)
    , template = P.loadTemplate(this.constructor.name, model);
    
    // TODO : place constructor code here
    
    self.execute = function(onSuccess, onFailure){
        
        model.requery(function(){
            // TODO : place data processing code here
            var report = template.generateReport();
            // report.show(); | report.print(); | var savedTo = report.save(saveTo ?);
            onSuccess(report);
        }, onFailure);
        
    };
}
