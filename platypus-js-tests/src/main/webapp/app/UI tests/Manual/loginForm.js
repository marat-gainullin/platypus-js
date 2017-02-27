/**
 * 
 * @author mg
 * @name LoginForm
 */
function LoginForm() {

var self = this;

function validate() {
    self.txtPassword.error = self.txtPassword.text == null || self.txtPassword.text == "" ? "Password is required!" : null;
    self.txtUserName.error = self.txtUserName.text == null || self.txtUserName.text == "" ? "User name is required!" : null;
    return self.txtPassword.error == null && self.txtUserName.error == null;
}

function btnLoginActionPerformed(evt) {//GEN-FIRST:event_btnLoginActionPerformed
    if(validate()) {
        document.getElementById("txtusr").value = self.txtUserName.text;
        document.getElementById("txtpasswd").value = self.txtPassword.text;
        document.getElementById("btnsubmit").click();
    }
}//GEN-LAST:event_btnLoginActionPerformed

function txtPasswordKeyPressed(evt) {//GEN-FIRST:event_txtPasswordKeyPressed
    if(evt.key == VK_ENTER)
        btnLoginActionPerformed();
}//GEN-LAST:event_txtPasswordKeyPressed

function txtUserNameKeyPressed(evt) {//GEN-FIRST:event_txtUserNameKeyPressed
    if(evt.key == VK_ENTER)
        btnLoginActionPerformed();
}//GEN-LAST:event_txtUserNameKeyPressed

}