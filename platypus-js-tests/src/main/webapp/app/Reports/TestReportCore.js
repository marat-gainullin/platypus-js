/**
 * Platypus report script.
 * @public
 * @stateless
 */
function TestReportCore() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    self.getReport = function() {
        var template = P.loadTemplate(this.constructor.name, {model: model, name: "test"});
        return template.generateReport();
    };
    
    self.getCalcedReport = function(aStartValue, aIterations) {
        var result = 0;
        if (aStartValue && aIterations) {
            for (var i = 0; i < aIterations; i++) {
                result += aStartValue * i;
            }
        }
        var template = P.loadTemplate(this.constructor.name, {model: model, name: "test", value: result});
        return template.generateReport();
    };
    
    this.execute = function(aOnSuccess){
        model.requery();
        var generated = self.getReport();
        if(!generated)
            throw 'generated report violation';
        aOnSuccess();
    };
}