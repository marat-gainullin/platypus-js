/* global Java*/
define(['boxing', 'common-utils/color', 'common-utils/cursor', 'common-utils/font'], function (B, Color, Cursor, Font) {
    var global = this;
    // gui imports
    var KeyEventClass = Java.type("java.awt.event.KeyEvent");
    var FileChooserClass = Java.type("javax.swing.JFileChooser");
    var FileFilter = Java.type("javax.swing.filechooser.FileNameExtensionFilter");
    var ColorChooserClass = Java.type("javax.swing.JColorChooser");
    var OptionPaneClass = Java.type("javax.swing.JOptionPane");
    var ColorClass = Java.type("com.eas.gui.ScriptColor");
    var IconResourcesClass = Java.type("com.eas.client.forms.IconResources");
    var HorizontalPositionClass = Java.type("com.eas.client.forms.HorizontalPosition");
    var VerticalPositionClass = Java.type("com.eas.client.forms.VerticalPosition");
    var OrientationClass = Java.type("com.eas.client.forms.Orientation");

    //
    function lookupCallerFile() {
        var calledFromFile = null;
        var error = new Error('path test error');
        if (error.stack) {
            var stack = error.stack.split('\n');
            if (stack.length > 1) {
                var sourceCall = stack[3];
                var stackFrameParsed = /\((.+):\d+\)/.exec(sourceCall);
                if (stackFrameParsed && stackFrameParsed.length > 1) {
                    calledFromFile = stackFrameParsed[1];
                }
            }
        }
        return calledFromFile;
    }

    var Icon = {};
    Object.defineProperty(Icon, "load", {
        value: function (aResName, onSuccess, onFailure) {
            var calledFromFile = lookupCallerFile();
            return IconResourcesClass.load(B.boxAsJava(aResName), B.boxAsJava(calledFromFile), B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        }
    });

    /**
     * Opens a file dialog box 
     *
     * @param curDir current directory [optional]
     * @param save flag tells whether this is a save dialog or not
     * @param aFileFilter
     * @return selected file or else null
     */
    function fileDialog(curDir, save, aFileFilter) {
        var result;
        function _fileDialog() {
            if (!curDir) {
                curDir = ".";
            }
            var dialog = new FileChooserClass(new FileClass(curDir));
            if (aFileFilter) {
                var name = aFileFilter;
                aFileFilter = aFileFilter.replace(/ /g, "");
                aFileFilter = aFileFilter.replace(/\./g, "");
                var array = [];
                array = aFileFilter.split(",");
                dialog.setFileFilter(new FileFilter(name, array));
            }
            var res = save ? dialog.showSaveDialog(null) :
                    dialog.showOpenDialog(null);
            if (res === FileChooserClass.APPROVE_OPTION) {
                result = dialog.getSelectedFile();
            } else {
                result = null;
            }
        }

        _fileDialog();
        return result !== null ? result : null;
    }
    fileDialog.docString = "show a file dialog box";

    /**
     * Opens a Select file dialog box 
     *
     * @param aCallback
     * @param aFileFilter name filter string
     * @param curDir current directory [optional]
     * @return selected file or else null
     */
    function selectFile(aCallback, aFileFilter, curDir) {
        invokeLater(function () {
            var file = fileDialog(curDir, false, aFileFilter);
            if (file) {
                aCallback(file);
            }
        });
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
            if (curDir === undefined) {
                curDir = new FileClass(".");
            }
            var dialog = new FileChooserClass(curDir);
            dialog.setFileSelectionMode(FileChooserClass.DIRECTORIES_ONLY);
            dialog.setAcceptAllFileFilterUsed(false);
            var res = dialog.showOpenDialog(null);
            if (res === FileChooserClass.APPROVE_OPTION) {
                result = dialog.getSelectedFile();
            } else {
                result = null;
            }
        }
        _dirDialog();
        return result;
    }
    directoryDialog.docString = "shows a directory dialog box";
    function selectDirectory(aCallback, aCurDir) {
        if (aCallback) {
            invokeLater(function () {
                var file = directoryDialog(aCurDir);
                aCallback(file);
            });
        } else {
            return directoryDialog(aCurDir);
        }
    }

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
                color = Color.BLACK;
            }
            var res = ColorChooserClass.showDialog(null, title, color ? color.unwrap() : null);
            result = res ? (new ColorClass(res)).getPublished() : null;
        }
        _colorDialog();
        return result;
    }
    colorDialog.docString = "shows a color chooser dialog box";
    function selectColor(aCallback, aOldColor, aTitle) {
        if (aCallback) {
            invokeLater(function () {
                var selected = colorDialog(aTitle, aOldColor);
                aCallback(selected);
            });
        } else {
            return colorDialog(aTitle, aOldColor);
        }
    }

    /**
     * Shows a message box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     * @param msgType type of message box [constants in JOptionPane]
     */
    function msgBox(msg, title, msgType) {

        function _msgBox() {
            if (msg === undefined)
                msg = "undefined";
            if (msg === null)
                msg = "null";
            if (!title)
                title = msg;
            if (!msgType)
                msgType = OptionPaneClass.INFORMATION_MESSAGE;
            OptionPaneClass.showMessageDialog(null, msg, title, msgType);
        }
        _msgBox();
    }
    msgBox.docString = "shows MessageBox to the user";

    /**
     * Shows an information alert box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     */
    function alert(msg, title) {
        msgBox(msg, title, OptionPaneClass.INFORMATION_MESSAGE);
    }
    alert.docString = "shows an alert message box to the user";
    Object.defineProperty(global, "alert", {
        value: alert
    });

    /**
     * Shows an error alert box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     */
    function error(msg, title) {
        msgBox(msg, title, OptionPaneClass.ERROR_MESSAGE);
    }
    error.docString = "shows an error message box to the user";

    /**
     * Shows a warning alert box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     */
    function warn(msg, title) {
        msgBox(msg, title, OptionPaneClass.WARNING_MESSAGE);
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
            if (answer === undefined)
                answer = "";
            if (title === undefined)
                title = "Input";
            result = OptionPaneClass.showInputDialog(null, question, title, OptionPaneClass.QUESTION_MESSAGE, null, null, answer);
            //result = OptionPaneClass.showInputDialog(null, question, answer);
        }
        _prompt();
        return result;
    }
    prompt.docString = "shows a prompt box to the user and returns the answer";
    Object.defineProperty(global, "prompt", {
        value: prompt
    });

    /**
     * Shows a confirmation dialog box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     * @return boolean (yes->true, no->false)
     */
    function confirm(msg, title) {
        var result;
        function _confirm() {
            if (title === undefined)
                title = msg;
            var optionType = OptionPaneClass.YES_NO_OPTION;
            result = OptionPaneClass.showConfirmDialog(null, msg, title, optionType);
        }
        _confirm();
        return result === OptionPaneClass.YES_OPTION;
    }
    confirm.docString = "shows a confirmation message box to the user";
    Object.defineProperty(global, "confirm", {
        value: confirm
    });

    var HorizontalPosition = {};
    Object.defineProperty(HorizontalPosition, "LEFT", {
        value: HorizontalPositionClass.LEFT
    });
    Object.defineProperty(HorizontalPosition, "CENTER", {
        value: HorizontalPositionClass.CENTER
    });
    Object.defineProperty(HorizontalPosition, "RIGHT", {
        value: HorizontalPositionClass.RIGHT
    });
//
    var VerticalPosition = {};
    Object.defineProperty(VerticalPosition, "TOP", {
        value: VerticalPositionClass.TOP
    });
    Object.defineProperty(VerticalPosition, "CENTER", {
        value: VerticalPositionClass.CENTER
    });
    Object.defineProperty(VerticalPosition, "BOTTOM", {
        value: VerticalPositionClass.BOTTOM
    });
//
    var Orientation = {};
    Object.defineProperty(Orientation, "HORIZONTAL", {
        value: OrientationClass.HORIZONTAL
    });
    Object.defineProperty(Orientation, "VERTICAL", {
        value: OrientationClass.VERTICAL
    });

    var ScrollBarPolicy = {};
    Object.defineProperty(ScrollBarPolicy, "AUTO", {
        value: 30
    });
    Object.defineProperty(ScrollBarPolicy, "NEVER", {
        value: 31
    });
    Object.defineProperty(ScrollBarPolicy, "ALLWAYS", {
        value: 32
    });

    //
    var FontStyleClass = Java.type("com.eas.gui.FontStyle");
    var FontStyle = {};
    Object.defineProperty(FontStyle, "ITALIC", {
        value: FontStyleClass.ITALIC
    });
    Object.defineProperty(FontStyle, "BOLD", {
        value: FontStyleClass.BOLD
    });
    Object.defineProperty(FontStyle, "BOLD_ITALIC", {
        value: FontStyleClass.BOLD_ITALIC
    });
    Object.defineProperty(FontStyle, "NORMAL", {
        value: FontStyleClass.NORMAL
    });
    
    var module = {};
    Object.defineProperty(module, 'Colors', {
        enumerable: true,
        value: Color
    });
    Object.defineProperty(module, 'Color', {
        enumerable: true,
        value: Color
    });
    Object.defineProperty(module, 'Cursor', {
        enumerable: true,
        value: Cursor
    });
    Object.defineProperty(module, 'Icon', {
        enumerable: true,
        value: Icon
    });
    Object.defineProperty(module, 'Icons', {
        enumerable: true,
        value: Icon
    });
    Object.defineProperty(module, 'Font', {
        enumerable: true,
        value: Font
    });
    Object.defineProperty(module, 'VK_ALT', {
        enumerable: true,
        value: KeyEventClass.VK_ALT
    });
    Object.defineProperty(module, 'VK_BACKSPACE', {
        enumerable: true,
        value: KeyEventClass.VK_BACK_SPACE
    });
    Object.defineProperty(module, 'VK_DELETE', {
        enumerable: true,
        value: KeyEventClass.VK_DELETE
    });
    Object.defineProperty(module, 'VK_DOWN', {
        enumerable: true,
        value: KeyEventClass.VK_DOWN
    });
    Object.defineProperty(module, 'VK_END', {
        enumerable: true,
        value: KeyEventClass.VK_END
    });
    Object.defineProperty(module, 'VK_ENTER', {
        enumerable: true,
        value: KeyEventClass.VK_ENTER
    });
    Object.defineProperty(module, 'VK_ESCAPE', {
        enumerable: true,
        value: KeyEventClass.VK_ESCAPE
    });
    Object.defineProperty(module, 'VK_HOME', {
        enumerable: true,
        value: KeyEventClass.VK_HOME
    });
    Object.defineProperty(module, 'VK_LEFT', {
        enumerable: true,
        value: KeyEventClass.VK_LEFT
    });
    Object.defineProperty(module, 'VK_PAGEDOWN', {
        enumerable: true,
        value: KeyEventClass.VK_PAGE_DOWN
    });
    Object.defineProperty(module, 'VK_PAGEUP', {
        enumerable: true,
        value: KeyEventClass.VK_PAGE_UP
    });
    Object.defineProperty(module, 'VK_RIGHT', {
        enumerable: true,
        value: KeyEventClass.VK_RIGHT
    });
    Object.defineProperty(module, 'VK_SHIFT', {
        enumerable: true,
        value: KeyEventClass.VK_SHIFT
    });
    Object.defineProperty(module, 'VK_TAB', {
        enumerable: true,
        value: KeyEventClass.VK_TAB
    });
    Object.defineProperty(module, 'VK_UP', {
        enumerable: true,
        value: KeyEventClass.VK_UP
    });
    Object.defineProperty(module, 'selectFile', {
        enumerable: true,
        value: selectFile
    });
    Object.defineProperty(module, 'selectDirectory', {
        enumerable: true,
        value: selectDirectory
    });
    Object.defineProperty(module, 'selectColor', {
        enumerable: true,
        value: selectColor
    });
    Object.defineProperty(module, 'msgBox', {
        enumerable: true,
        value: msgBox
    });
    Object.defineProperty(module, 'error', {
        enumerable: true,
        value: error
    });
    Object.defineProperty(module, 'warn', {
        enumerable: true,
        value: warn
    });
    Object.defineProperty(module, 'HorizontalPosition', {
        enumerable: true,
        value: HorizontalPosition
    });
    Object.defineProperty(module, 'VerticalPosition', {
        enumerable: true,
        value: VerticalPosition
    });
    Object.defineProperty(module, 'Orientation', {
        enumerable: true,
        value: Orientation
    });
    Object.defineProperty(module, 'ScrollBarPolicy', {
        enumerable: true,
        value: ScrollBarPolicy
    });
    Object.defineProperty(module, 'FontStyle', {
        enumerable: true,
        value: FontStyle
    });
    return module;
});