/**
 * 
 * @author user
 */
function comboTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    // TODO : place your code here

    model.requery(function () {
        // TODO : place your code here
    });


    var one = new menuObject();
    var two = new menuObject();
    var three = new menuObject();
    one.name = "One";
    two.name = "Two";
    three.name = "Three"

    self.arr = [one, two, three];

    form.modelCombo.data = self.arr;
    form.modelCombo.displayField = "name";
    form.modelCombo.displayList = self.arr;

    form.button.onActionPerformed = function (event) {
        var four = new menuObject();
        four.name = "Four";
        self.arr.push(four);
    };
}
