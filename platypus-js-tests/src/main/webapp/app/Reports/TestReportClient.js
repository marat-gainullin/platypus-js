/**
 * 
 * @author mg
 * @constructor
 */ 
function TestReportClient() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    self.execute = function (aOnSuccess) {
        var r = new P.ServerModule('TestReportCore');
        r.execute(function () {
            aOnSuccess();
        });
    };
}
