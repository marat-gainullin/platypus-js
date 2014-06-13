/**
 * Opens a file dialog box 
 *
 * @param curDir current directory [optional]
 * @param save flag tells whether this is a save dialog or not
 * @return selected file or else null
 */
function fileDialog(curDir, save) {
    var result;
    function _fileDialog() {
        if (curDir == undefined) {
            curDir = new java.io.File(".");
        }
        var JFileChooser = javax.swing.JFileChooser;
        var dialog = new JFileChooser(curDir);
        var res = save ? dialog.showSaveDialog(window) :
                dialog.showOpenDialog(window);
        if (res == JFileChooser.APPROVE_OPTION) {
            result = dialog.getSelectedFile();
        } else {
            result = null;
        }
    }

    if (isEventThread()) {
        _fileDialog();
    } else {
        _fileDialog.invokeAndWait();
    }
    return result;
}
fileDialog.docString = "show a file dialog box";

function selectFile(aCallback) {
    (function() {
        var file = fileDialog(null, false);
        aCallback(file);
    }).invokeLater();
}

/**
 * Opens a directory dialog box 
 *
 * @param curDir current directory [optional]
 * @return selected directory or else null
 */
function directoryDialog(curDir) {
    var result;
    function _dirDialog() {
        if (curDir == undefined) {
            curDir = new java.io.File(".");
        }
        var JFileChooser = javax.swing.JFileChooser;
        var dialog = new JFileChooser(curDir);
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialog.setAcceptAllFileFilterUsed(false);
        var res = dialog.showOpenDialog(window);
        if (res == JFileChooser.APPROVE_OPTION) {
            result = dialog.getSelectedFile();
        } else {
            result = null;
        }
    }

    if (isEventThread()) {
        _dirDialog();
    } else {
        _dirDialog.invokeAndWait();
    }
    return result;
}
directoryDialog.docString = "show a directory dialog box";

/**
 * Opens a color chooser dialog box 
 *
 * @param title of the dialog box [optional]
 * @param color default color [optional]
 * @return choosen color or default color
 */

function colorDialog(title, color) {
    var result;
    function _colorDialog() {
        if (!title) {
            title = "Choose Color";
        }
        if (!color) {
            color = java.awt.Color.BLACK;
        }
        var chooser = javax.swing.JColorChooser;
        var res = chooser.showDialog(window, title, color);
        result = res ? new com.eas.client.scripts.ScriptColor(res) : null;
    }

    if (isEventThread()) {
        _colorDialog();
    } else {
        _colorDialog.invokeAndWait();
    }
    return result;
}
colorDialog.docString = "shows a color chooser dialog box";

/**
 * Shows a message box
 *
 * @param msg message to be shown
 * @param title title of message box [optional]
 * @param msgType type of message box [constants in JOptionPane]
 */
function msgBox(msg, title, msgType) {

    function _msgBox() {
        var JOptionPane = javax.swing.JOptionPane;
        if (msg === undefined)
            msg = "undefined";
        if (msg === null)
            msg = "null";
        if (title == undefined)
            title = msg;
        if (msgType == undefined)
            type = JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(window, msg, title, msgType);
    }
    if (isEventThread()) {
        _msgBox();
    } else {
        _msgBox.invokeAndWait();
    }
}
msgBox.docString = "shows MessageBox to the user";

/**
 * Shows an information alert box
 *
 * @param msg message to be shown
 * @param title title of message box [optional]
 */
function alert(msg, title) {
    var JOptionPane = javax.swing.JOptionPane;
    msgBox(msg, title, JOptionPane.INFORMATION_MESSAGE);
}
alert.docString = "shows an alert message box to the user";

/**
 * Shows an error alert box
 *
 * @param msg message to be shown
 * @param title title of message box [optional]
 */
function error(msg, title) {
    var JOptionPane = javax.swing.JOptionPane;
    msgBox(msg, title, JOptionPane.ERROR_MESSAGE);
}
error.docString = "shows an error message box to the user";


/**
 * Shows a warning alert box
 *
 * @param msg message to be shown
 * @param title title of message box [optional]
 */
function warn(msg, title) {
    var JOptionPane = javax.swing.JOptionPane;
    msgBox(msg, title, JOptionPane.WARNING_MESSAGE);
}
warn.docString = "shows a warning message box to the user";


/**
 * Shows a prompt dialog box
 *
 * @param question question to be asked
 * @param answer default answer suggested [optional]
 * @param title title of message box [optional]
 * @return answer given by user
 */
function prompt(question, answer, title) {
    var result;
    function _prompt() {
        var JOptionPane = javax.swing.JOptionPane;
        if (answer == undefined)
            answer = "";
        if (title == undefined)
            title = "Input";
        result = JOptionPane.showInputDialog(window, question, title, JOptionPane.QUESTION_MESSAGE, null, null, answer);
        //result = JOptionPane.showInputDialog(window, question, answer);
    }
    if (isEventThread()) {
        _prompt();
    } else {
        _prompt.invokeAndWait();
    }
    return result;
}
prompt.docString = "shows a prompt box to the user and returns the answer";

/**
 * Shows a confirmation dialog box
 *
 * @param msg message to be shown
 * @param title title of message box [optional]
 * @return boolean (yes->true, no->false)
 */
function confirm(msg, title) {
    var result;
    var JOptionPane = javax.swing.JOptionPane;
    function _confirm() {
        if (title == undefined)
            title = msg;
        var optionType = JOptionPane.YES_NO_OPTION;
        result = JOptionPane.showConfirmDialog(window, msg, title, optionType);
    }
    if (isEventThread()) {
        _confirm();
    } else {
        _confirm.invokeAndWait();
    }
    return result == JOptionPane.YES_OPTION;
}
confirm.docString = "shows a confirmation message box to the user";

// forms API
Icon = com.eas.client.forms.IconResources;
Icons = Icon;
Orientation = com.eas.client.forms.api.Orientation;
VerticalPosition = com.eas.client.forms.api.VerticalPosition;
HorizontalPosition = com.eas.client.forms.api.HorizontalPosition;
Font = com.eas.gui.Font;
FontStyle = com.eas.gui.FontStyle;
Cursor = com.eas.gui.Cursor;
// key codes
VK_ENTER = java.awt.event.KeyEvent.VK_ENTER;
VK_BACK_SPACE = java.awt.event.KeyEvent.VK_BACK_SPACE;
VK_TAB = java.awt.event.KeyEvent.VK_TAB;
VK_CANCEL = java.awt.event.KeyEvent.VK_CANCEL;
VK_CLEAR = java.awt.event.KeyEvent.VK_CLEAR;
VK_SHIFT = java.awt.event.KeyEvent.VK_SHIFT;
VK_CONTROL = java.awt.event.KeyEvent.VK_CONTROL;
VK_ALT = java.awt.event.KeyEvent.VK_ALT;
VK_PAUSE = java.awt.event.KeyEvent.VK_PAUSE;
VK_CAPS_LOCK = java.awt.event.KeyEvent.VK_CAPS_LOCK;
VK_ESCAPE = java.awt.event.KeyEvent.VK_ESCAPE;
VK_SPACE = java.awt.event.KeyEvent.VK_SPACE;
VK_PAGE_UP = java.awt.event.KeyEvent.VK_PAGE_UP;
VK_PAGE_DOWN = java.awt.event.KeyEvent.VK_PAGE_DOWN;
VK_END = java.awt.event.KeyEvent.VK_END;
VK_HOME = java.awt.event.KeyEvent.VK_HOME;

Object.defineProperty(Form, "shown", {
    get: function() {
        var nativeArray = com.eas.client.forms.FormRunner.getShownForms();
        var res = [];
        for (var i = 0; i < nativeArray.length; i++)
            res[res.length] = nativeArray[i];
        return res;
    }
});

Form.getShownForm = function(aId) {
    return com.eas.client.forms.FormRunner.getShownForm(aId);
};

Object.defineProperty(Form, "onChange", {
    get: function() {
        return com.eas.client.forms.FormRunner.getOnChange();
    },
    set: function(aValue) {
        com.eas.client.forms.FormRunner.setOnChange(aValue);
    }
});