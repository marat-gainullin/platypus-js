/**
 * @name 128049769692168
 */
function MixedTestView() {

    var self = this;

function btnSaveActionPerformed(evt) {//GEN-FIRST:event_btnSaveActionPerformed
        self.model.save();
}//GEN-LAST:event_btnSaveActionPerformed

function formWindowClosing(evt) {//GEN-FIRST:event_formWindowClosing
        if (self.model.modified && confirm("Сохранить изменения?", form.title))
            self.model.save();
}//GEN-LAST:event_formWindowClosing

}