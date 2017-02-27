/*
 * Тест дерева.js
 *
 * @constructor
 * @author mg
 */
function LazyTreeTest() {
    var self = this;
    var model = P.loadModel(this.constructor.name);
    var form = P.loadForm(this.constructor.name, model);

    this.show = function(){
        form.show();
    };
    
    model.requery(function(){});
}