/**
 * 
 * @author mg
 * @name TestView
 */
function TestView() {
    var self = this,
            model = P.loadModel(this.constructor.name),
            form = P.loadForm(this.constructor.name, model);

    form.message1("sample1");
    form.message2("sample2");
    form.message3("sample3");

    var repCallback = arguments[0];
    var moduleCallback = arguments[1];

    form.button.onActionPerformed = function (event) {
        repCallback();
    };
    form.button1.onActionPerformed = function (event) {
        moduleCallback();
    };

    self.show = function () {
        form.show();
    };
}