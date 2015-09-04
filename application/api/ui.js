/* global P, Java*/
(function () {
    var global = this;
    // core imports
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var Source2XmlDom = Java.type('com.eas.xml.dom.Source2XmlDom');
    // gui imports
    var KeyEventClass = Java.type("java.awt.event.KeyEvent");
    var FileChooserClass = Java.type("javax.swing.JFileChooser");
    var FileFilter = Java.type("javax.swing.filechooser.FileNameExtensionFilter");
    var ColorChooserClass = Java.type("javax.swing.JColorChooser");
    var OptionPaneClass = Java.type("javax.swing.JOptionPane");
    var ColorClass = Java.type("com.eas.gui.ScriptColor");
    var FormClass = Java.type("com.eas.client.forms.Form");
    var FormLoaderClass = Java.type('com.eas.client.scripts.ModelFormLoader');
    var IconResourcesClass = Java.type("com.eas.client.forms.IconResources");
    var HorizontalPositionClass = Java.type("com.eas.client.forms.HorizontalPosition");
    var VerticalPositionClass = Java.type("com.eas.client.forms.VerticalPosition");
    var OrientationClass = Java.type("com.eas.client.forms.Orientation");

    //
    P.require('common-utils/color.js');
    Object.defineProperty(P.Color, "black", {value: new P.Color(0, 0, 0)});
    Object.defineProperty(P.Color, "BLACK", {value: new P.Color(0, 0, 0)});
    Object.defineProperty(P.Color, "blue", {value: new P.Color(0, 0, 0xff)});
    Object.defineProperty(P.Color, "BLUE", {value: new P.Color(0, 0, 0xff)});
    Object.defineProperty(P.Color, "cyan", {value: new P.Color(0, 0xff, 0xff)});
    Object.defineProperty(P.Color, "CYAN", {value: new P.Color(0, 0xff, 0xff)});
    Object.defineProperty(P.Color, "DARK_GRAY", {value: new P.Color(0x40, 0x40, 0x40)});
    Object.defineProperty(P.Color, "darkGray", {value: new P.Color(0x40, 0x40, 0x40)});
    Object.defineProperty(P.Color, "gray", {value: new P.Color(0x80, 0x80, 0x80)});
    Object.defineProperty(P.Color, "GRAY", {value: new P.Color(0x80, 0x80, 0x80)});
    Object.defineProperty(P.Color, "green", {value: new P.Color(0, 0xff, 0)});
    Object.defineProperty(P.Color, "GREEN", {value: new P.Color(0, 0xff, 0)});
    Object.defineProperty(P.Color, "LIGHT_GRAY", {value: new P.Color(0xC0, 0xC0, 0xC0)});
    Object.defineProperty(P.Color, "lightGray", {value: new P.Color(0xC0, 0xC0, 0xC0)});
    Object.defineProperty(P.Color, "magenta", {value: new P.Color(0xff, 0, 0xff)});
    Object.defineProperty(P.Color, "MAGENTA", {value: new P.Color(0xff, 0, 0xff)});
    Object.defineProperty(P.Color, "orange", {value: new P.Color(0xff, 0xC8, 0)});
    Object.defineProperty(P.Color, "ORANGE", {value: new P.Color(0xff, 0xC8, 0)});
    Object.defineProperty(P.Color, "pink", {value: new P.Color(0xFF, 0xAF, 0xAF)});
    Object.defineProperty(P.Color, "PINK", {value: new P.Color(0xFF, 0xAF, 0xAF)});
    Object.defineProperty(P.Color, "red", {value: new P.Color(0xFF, 0, 0)});
    Object.defineProperty(P.Color, "RED", {value: new P.Color(0xFF, 0, 0)});
    Object.defineProperty(P.Color, "white", {value: new P.Color(0xFF, 0xff, 0xff)});
    Object.defineProperty(P.Color, "WHITE", {value: new P.Color(0xFF, 0xff, 0xff)});
    Object.defineProperty(P.Color, "yellow", {value: new P.Color(0xFF, 0xff, 0)});
    Object.defineProperty(P.Color, "YELLOW", {value: new P.Color(0xFF, 0xff, 0)});
    P.Colors = P.Color;

    P.require('common-utils/cursor.js');
    Object.defineProperty(P.Cursor, "CROSSHAIR", {value: new P.Cursor(1)});
    Object.defineProperty(P.Cursor, "DEFAULT", {value: new P.Cursor(0)});
    Object.defineProperty(P.Cursor, "AUTO", {value: new P.Cursor(0)});
    Object.defineProperty(P.Cursor, "E_RESIZE", {value: new P.Cursor(11)});
// help ?
// progress ?
    Object.defineProperty(P.Cursor, "HAND", {value: new P.Cursor(12)});
    Object.defineProperty(P.Cursor, "MOVE", {value: new P.Cursor(13)});
    Object.defineProperty(P.Cursor, "NE_RESIZE", {value: new P.Cursor(7)});
    Object.defineProperty(P.Cursor, "NW_RESIZE", {value: new P.Cursor(6)});
    Object.defineProperty(P.Cursor, "N_RESIZE", {value: new P.Cursor(8)});
    Object.defineProperty(P.Cursor, "SE_RESIZE", {value: new P.Cursor(5)});
    Object.defineProperty(P.Cursor, "SW_RESIZE", {value: new P.Cursor(4)});
    Object.defineProperty(P.Cursor, "S_RESIZE", {value: new P.Cursor(9)});
    Object.defineProperty(P.Cursor, "TEXT", {value: new P.Cursor(2)});
    Object.defineProperty(P.Cursor, "WAIT", {value: new P.Cursor(3)});
    Object.defineProperty(P.Cursor, "W_RESIZE", {value: new P.Cursor(10)});

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
    Object.defineProperty(P, "Icon", {value: Icon});
    Object.defineProperty(Icon, "load", {
        value: function (aResName, onSuccess, onFailure) {
            var calledFromFile = lookupCallerFile();
            return IconResourcesClass.load(P.boxAsJava(aResName), P.boxAsJava(calledFromFile), P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
        }
    });
    P.Icons = P.Icon;
    Object.defineProperty(P, "VK_ALT", {value: KeyEventClass.VK_ALT});
    Object.defineProperty(P, "VK_BACKSPACE", {value: KeyEventClass.VK_BACK_SPACE});
    Object.defineProperty(P, "VK_DELETE", {value: KeyEventClass.VK_DELETE});
    Object.defineProperty(P, "VK_DOWN", {value: KeyEventClass.VK_DOWN});
    Object.defineProperty(P, "VK_END", {value: KeyEventClass.VK_END});
    Object.defineProperty(P, "VK_ENTER", {value: KeyEventClass.VK_ENTER});
    Object.defineProperty(P, "VK_ESCAPE", {value: KeyEventClass.VK_ESCAPE});
    Object.defineProperty(P, "VK_HOME", {value: KeyEventClass.VK_HOME});
    Object.defineProperty(P, "VK_LEFT", {value: KeyEventClass.VK_LEFT});
    Object.defineProperty(P, "VK_PAGEDOWN", {value: KeyEventClass.VK_PAGE_DOWN});
    Object.defineProperty(P, "VK_PAGEUP", {value: KeyEventClass.VK_PAGE_UP});
    Object.defineProperty(P, "VK_RIGHT", {value: KeyEventClass.VK_RIGHT});
    Object.defineProperty(P, "VK_SHIFT", {value: KeyEventClass.VK_SHIFT});
    Object.defineProperty(P, "VK_TAB", {value: KeyEventClass.VK_TAB});
    Object.defineProperty(P, "VK_UP", {value: KeyEventClass.VK_UP});

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
//            if (aCallback) {
        invokeLater(function () {
            var file = fileDialog(curDir, false, aFileFilter);
            if (file) {
                aCallback(file);
            }
        });
//            } else {
//                return fileDialog(curDir, false, aFileFilter);
//            }
    }


    Object.defineProperty(P, "selectFile", {
        value: selectFile
    });

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

    Object.defineProperty(P, "selectDirectory", {
        value: selectDirectory
    });
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
                color = P.Color.BLACK;
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

    Object.defineProperty(P, "selectColor", {
        value: selectColor
    });

    P.require('forms/form.js');
    Object.defineProperty(P.Form, "shown", {
        get: function () {
            var nativeArray = FormClass.getShownForms();
            var res = [];
            for (var i = 0; i < nativeArray.length; i++)
                res[res.length] = nativeArray[i].getPublished();
            return res;
        }
    });

    Object.defineProperty(P.Form, "getShownForm", {
        value: function (aName) {
            var shownForm = FormClass.getShownForm(aName);
            return shownForm !== null ? shownForm.getPublished() : null;
        }
    });

    Object.defineProperty(P.Form, "onChange", {
        get: function () {
            return FormClass.getOnChange();
        },
        set: function (aValue) {
            FormClass.setOnChange(aValue);
        }
    });

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
    Object.defineProperty(P, "msgBox", {
        value: msgBox
    });

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
    Object.defineProperty(P, "error", {
        value: error
    });

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
    Object.defineProperty(P, "warn", {
        value: warn
    });

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
    Object.defineProperty(P, "HorizontalPosition", {
        value: HorizontalPosition
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
    Object.defineProperty(P, "VerticalPosition", {
        value: VerticalPosition
    });
//
    var Orientation = {};
    Object.defineProperty(Orientation, "HORIZONTAL", {
        value: OrientationClass.HORIZONTAL
    });
    Object.defineProperty(Orientation, "VERTICAL", {
        value: OrientationClass.VERTICAL
    });
    Object.defineProperty(P, "Orientation", {
        value: Orientation
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
    Object.defineProperty(P, "ScrollBarPolicy", {
        value: ScrollBarPolicy
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
    Object.defineProperty(P, "FontStyle", {
        value: FontStyle
    });
    function loadFormDocument(aDocument, aModel, aTarget) {
        var formFactory = FormLoaderClass.load(aDocument, ScriptedResourceClass.getApp(), arguments[1] ? aModel : null);
        var form = formFactory.form;
        if (aTarget) {
            P.Form.call(aTarget, null, null, form);
        } else {
            aTarget = new P.Form(null, null, form);
        }
        form.injectPublished(aTarget);
        var comps = formFactory.getWidgetsList();
        for (var c = 0; c < comps.length; c++) {
            (function () {
                var comp = EngineUtilsClass.unwrap(P.boxAsJs(comps[c]));
                if (comp.name) {
                    Object.defineProperty(aTarget, comp.name, {
                        get: function () {
                            return comp;
                        }
                    });
                }
            })();
        }
        return aTarget;
    }
    /**
     * @static
     * @param {type} aName
     * @param {type} aModel
     * @param {type} aTarget
     * @returns {P.loadForm.publishTo}
     */
    function loadForm(aName, aModel, aTarget) {
        P.require(['forms/index.js', 'grid/index.js']);
        var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
        var document = ScriptedResourceClass.getApp().getForms().get(aName, files);
        var form = loadFormDocument(document, aModel, aTarget);
        if (!form.title)
            form.title = aName;
        form.formKey = aName;
        return form;
    }

    function readForm(aContent, aModel, aTarget) {
        P.require(['forms/index.js', 'grid/index.js']);
        var document = Source2XmlDom.transform(aContent);
        return loadFormDocument(document, aModel, aTarget);
    }

    Object.defineProperty(P, "loadForm", {value: loadForm});
    Object.defineProperty(P, "readForm", {value: readForm});
})();