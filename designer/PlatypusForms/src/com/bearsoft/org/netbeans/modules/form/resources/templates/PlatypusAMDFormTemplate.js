/**
 * 
 * @author ${user}
 * @module ${appElementName}
 */
define(['orm', 'forms', 'ui'], function (Orm, Forms, Ui,  ModuleName) {
    return function () {
        var self = this
                , model = Orm.loadModel(ModuleName)
                , form = Forms.loadForm(ModuleName, model);

        self.show = function () {
            form.show();
        };

        // TODO : place your code here

        model.requery(function () {
            // TODO : place your code here
        });

    };
});
