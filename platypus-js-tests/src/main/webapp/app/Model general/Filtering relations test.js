/**
 * @name 128049472406262
 */

function FilteringTestView() {

    var self = this;

    function actionPerformed(ev)
    {
        self.model.save();
    }

    function windowClosing(ev)
    {
        if (self.model.modified && confirm("Сохранить изменения?", form.title))
            self.model.save();
    }
}