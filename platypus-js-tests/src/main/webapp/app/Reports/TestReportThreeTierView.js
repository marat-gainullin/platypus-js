/**
 * 
 * @author mg
 */
function TestReportsThreeTierView() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };
    
    form.btnReportExecute.onActionPerformed = function (event) {
        var reportModule = new P.ServerModule('TestReportCore');
        reportModule.getCalcedReport(2, 78, function(aReport){
            aReport.show();
        });
    };
    
    self.execute = function(){
        /*
        var reportModule = new TestReportCore();
        reportModule.execute();
        */
    };
}
