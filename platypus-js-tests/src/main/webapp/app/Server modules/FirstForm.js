/**
 * 
 * @author mg
 */
function FirstForm() {
    var self = this;
    var model = P.loadModel(this.constructor.name);
    var form = P.loadForm(this.constructor.name, model);

    self.show = function(){
        form.show();
    };
    
    form.btnStartAsyncTests.onActionPerformed = function(event) {
        var t = new AsyncServerModuleTests();
        t = null;
    };
    form.btnStartSyncTests.onActionPerformed = function(event) {
        var t = new ServerModuleTests();
        t = null;
    };
}
