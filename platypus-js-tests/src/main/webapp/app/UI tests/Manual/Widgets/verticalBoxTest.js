/**
 * 
 * @author mg
 */
function VerticalBoxTest() {
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
        btn.height = 30;
        form.box1.add(btn);
    };
    form.btnIncVGap.onActionPerformed = function(event) {
        form.box.vgap++;
    };
    form.btnDecVGap.onActionPerformed = function(event) {
        form.box.vgap--;
    };
    form.button1.onActionPerformed = function(event) {
        var newBox = new P.BoxPane(P.Orientation.VERTICAL, 5, 5);
        newBox.background = P.Colors.GREEN;
        form.box.add(newBox);
        newBox.height = form.box1.height;        
    };
}
