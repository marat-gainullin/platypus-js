/**
 * 
 * @author mg
 * @name TestReport
 */
function TestReport() {
    var self = this,
            model = P.loadModel(this.constructor.name), 
            template = P.loadTemplate(this.constructor.name, model);

    self.show = function () {
        var report = template.generateReport();
        report.show();
    };
}