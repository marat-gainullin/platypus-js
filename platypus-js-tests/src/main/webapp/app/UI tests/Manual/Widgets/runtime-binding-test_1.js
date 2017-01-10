/**
 * 
 * @author mg
 */
function runtime_binding_test_1() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    model.requery();

    var samples = [
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"},
        {key: "1", txt: "Sample text 1"},
        {key: "2", txt: "Sample text 2"},
        {key: "3", txt: "Sample text 3"}
    ];
    samples.cursor = samples[1];

    form.btnBind.onActionPerformed = function (event) {
        form.modelFormattedField.data = samples;
        form.modelFormattedField.field = 'cursor.txt';
        form.modelFormattedField1.data = samples;
        form.modelFormattedField1.field = 'cursor.txt';
        form.modelGrid.data = samples;
    };

    form.btnUnbind.onActionPerformed = function (event) {
        form.modelFormattedField.data = null;
        form.modelFormattedField.field = null;
        form.modelFormattedField1.data = null;
        form.modelFormattedField1.field = null;
        form.modelGrid.data = null;
    };
    
    form.btnRedraw.onActionPerformed = function(event) {
        //form.modelFormattedField.redraw();
        form.modelGrid.redraw();
    };
}
