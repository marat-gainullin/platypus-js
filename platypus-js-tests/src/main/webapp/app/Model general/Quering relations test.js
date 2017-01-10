/**
 * @name 128049741937589
 */

function QueringTestView() {

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