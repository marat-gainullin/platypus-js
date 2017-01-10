/**
 * 
 * @author mg
 */
function HorizontalBoxTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };
    var i=0;
    form.button.onActionPerformed = function(event) {
        var btn = new P.Button('' + ++i);
        btn.onActionPerformed = function(ev){
            btn.parent.remove(btn);
        };
        btn.width = 30;
        form.box1.add(btn);
    };
    form.btnIncHGap.onActionPerformed = function(event) {
        form.box.hgap++;
    };
    form.btnDecHGap.onActionPerformed = function(event) {
        form.box.hgap--;
    };
    form.btnAddBox.onActionPerformed = function(event) {
        var newBox = new P.BoxPane();
        newBox.background = P.Colors.GREEN;
        form.box.add(newBox);
        newBox.width = form.box1.width;        
    };
}
