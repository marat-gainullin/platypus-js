/**
 * 
 * @author mg
 * @name TextsTest
 * @rolesAllowed role1
 */

function TextsTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    form.button1.onActionPerformed = function (event) {
        form.modelText.format = null;
    };
    form.btnCreateFormattedField.onActionPerformed = function (event) {
        var ff = new P.FormattedField("753-3579");
        ff.format = "###-####"
        form.view.add(ff,{"right": 19, "top": 45, "width": event.source.width, "height": 25})

    };
    form.btnSetValue.onActionPerformed = function (event) {
        form.formattedField.value = "258-6454";//new Date();
        form.modelText.value = form.formattedField.value;
    };
    
    form.btnSetSelectors.onActionPerformed = function (event) {
        form.modelGrid.columnDate.onSelect = function (aEditor) {
            aEditor.value = new Date();
        };
        var n = 0;
        form.modelGrid.columnMask.onSelect = function (aEditor) {
            aEditor.value = "456-885" + (++n);
        };

        form.modelGrid.columnNumber.onSelect = function (aEditor) {
            aEditor.value++;
        };

        form.modelGrid.columnPercent.onSelect = function (aEditor) {
            aEditor.value++;
        };

        form.modelGrid.columnCurrency.onSelect = function (aEditor) {
            aEditor.value++;
        };
    };
    form.btnSetParams.onActionPerformed = function (event) {
        model.params.Param1 = new Date();
        model.params.Param2 = "789-9876";
        model.params.Param3 = 91;
    };
    form.button.onActionPerformed = function (event) {
        form.modelText.value = form.formattedField.value;
    };
}