/**
 * 
 * @author Алексей
 */
function SparsedArrayToGrid() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function () {
        form.show();
    };
    
    var testAr = [];
    testAr[1] = {el : "first element"};
    testAr[1000000000000000] = {el: "last element"};
    form.modelGrid.data = testAr;
    form.modelGrid.column.field = "el";
}
