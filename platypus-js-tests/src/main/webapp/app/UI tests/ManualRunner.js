/**
 * 
 * @author mg
 */
function ManualRunner() {
    var self = this
           , model = P.loadModel(this.constructor.name)
           , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };
    
    form.btnRun.onActionPerformed = function(event) {
//        var test = new WidgetsStructureTest();
//        var test = new MenusStructureTest();
//        var test = new GroupsStructureTest();
//        var test = new FormattingTest();
        var test = new PlainPropertiesTest();
//        var test = new UIGlobalScopeTest();
        test = null;
    };
}
