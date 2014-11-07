/**
 * 
 * @author ${user}
 */
function ${appElementName}(){
    var self = this
    , model = P.loadModel(this.constructor.name)
    , template = P.loadTemplate(this.constructor.name, model);
    
    // TODO : place constructor code here
    
    self.execute = function(){
        
        model.requery();
        // TODO : place data processing code here
        
        var report = template.generateReport();
        // report.show(); | report.print(); | var savedTo = report.save(saveTo ?);
    };
}
