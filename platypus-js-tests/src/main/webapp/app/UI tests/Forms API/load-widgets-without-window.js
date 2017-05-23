/**
 * 
 * @author mgainullin
 */
define('load-widgets-without-window', ['orm', 'forms', 'forms/form', 'ui'], function (Orm, Forms, Form, Ui, ModuleName) {
    function module_constructor() {
        var self = this
                , model = Orm.loadModel(ModuleName)
                , widgets = Forms.loadWidgets(ModuleName, model);
                
        var form = new Form(widgets.view);

        self.execute = function (onSuccess, onFailure) {
            try {
                form.show();
                form.close();
            } catch (ex) {
                onFailure('form does not show');
                return;
            }
            if (!widgets) {
                onFailure('widgets is not defined');
                return;
            }
            if (!widgets.view) {
                onFailure('widgets.view is not defined');
                return;
            }
            if (!widgets.button) {
                onFailure('widgets.button is not defined');
                return;
            }
            if (!widgets.passwordField) {
                onFailure('widgets.passwordField is not defined');
                return;
            }
            if (!widgets.modelCombo) {
                onFailure('widgets.modelCombo is not defined');
                return;
            }
            if (!widgets.modelGrid) {
                onFailure('widgets.modelGrid is not defined');
                return;
            }
            onSuccess();
        };

    }
    return module_constructor;
});
