/**
 * 
 * @author mg
 * @name ControlsEventsTestView
 */

function ControlsEventsTestView() {

    var self = this;

    /**
     * 
     * @param evt
     * @returns undefined
     */
function formWindowOpened(evt) {//GEN-FIRST:event_formWindowOpened
        self.view.children.forEach(function(aChild) {
            if (aChild == self.scrollPane || aChild == self.scrollPane1)
                aChild = aChild.children[0];
            aChild.onFocusGained = _onFocusGained;
            aChild.onFocusLost = _onFocusLost;
            aChild.onKeyPressed = _onKeyPressed;
            aChild.onKeyReleased = _onKeyReleased;
            aChild.onKeyTyped = _onKeyTyped;
        });
}//GEN-LAST:event_formWindowOpened

function modelFormattedFieldOnSelect(aEditor) {//GEN-FIRST:event_modelFormattedFieldOnSelect
	// TODO Добавьте свой код:
}//GEN-LAST:event_modelFormattedFieldOnSelect

function button1ActionPerformed(evt) {//GEN-FIRST:event_button1ActionPerformed
        self.modelSpin.error = null;
}//GEN-LAST:event_button1ActionPerformed

function button2ActionPerformed(evt) {//GEN-FIRST:event_button2ActionPerformed
        self.modelSpin.error = "Some error";
}//GEN-LAST:event_button2ActionPerformed


    function _onFocusGained(evt) {
        Logger.info("onFocusGained on " + evt.source.name);
    }
    function _onFocusLost(evt) {
        Logger.info("onFocusLost on " + evt.source.name);
    }
    function _onKeyPressed(evt) {
        Logger.info("onKeyPressed on " + evt.source.name);
    }
    function _onKeyReleased(evt) {
        Logger.info("onKeyReleased on " + evt.source.name);
    }
    function _onKeyTyped(evt) {
        Logger.info("onKeyTyped on " + evt.source.name);
    }

}