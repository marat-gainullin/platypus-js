/**
 * 
 * @author mg
 */
function TestReportsView() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };
    
    form.btnReportExecute.onActionPerformed = function (event) {
        var reportModule = new TestReportCore();
        var report = reportModule.getCalcedReport(2, 78);
        report.show();
    };
    
    self.execute = function(){
        var reportModule = new TestReportCore();
        reportModule.execute();
    };
}
