/**
 * 
 * @author mg
 * @name BoxTest
 */

function BoxTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    self.show = function () {
        form.show();
    };

    form.btnAddButton.onActionPerformed = function (event) {
        form.pnlBox.add(new P.Button("Sample button", null, function (event) {
            event.source.parent.remove(event.source);
        }));
    };
    form.btnAddTextField.onActionPerformed = function (event) {
        var textField = new P.TextField("Sample text")
        form.pnlBox.add(textField,P.Orientation.HORIZONTAL)
//        form.pnlBox.add(textField);
    };
    form.btnAddTextArea.onActionPerformed = function (event) {
        var textField = new P.TextArea("Sample area")
        form.pnlBox.add(textField);
    };
    form.btnClear.onActionPerformed = function (event) {
        form.pnlBox.clear();
    };
    form.btnIncSize.onActionPerformed = function (event) {
        if (form.pnlBox.orientation == P.Orientation.HORIZONTAL)
            form.pnlBox.children[1].width += 20;
        else
            form.pnlBox.children[1].height += 20;
    };
    form.btnDecSize.onActionPerformed = function (event) {
        if (form.pnlBox.orientation == P.Orientation.HORIZONTAL)
            form.pnlBox.children[1].width -= 20;
        else
            form.pnlBox.children[1].height -= 20;
    };
}