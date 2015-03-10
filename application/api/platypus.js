(function () {
    load("classpath:internals.js");
    //this === global;
    var global = this;
    var oldP = global.P;
    global.P = {};
    /*
     global.P = this; // global scope of api - for legacy applications
     global.P.restore = function() {
     throw "Legacy api can't restore the global namespace.";
     };
     */

    // core imports
    var ExecutorsClass = Java.type("java.util.concurrent.Executors");
    var LockClass = Java.type("java.util.concurrent.locks.ReentrantLock");
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var JavaArrayClass = Java.type("java.lang.Object[]");
    var JavaStringArrayClass = Java.type("java.lang.String[]");
    var JavaCollectionClass = Java.type("java.util.Collection");
    var FileClass = Java.type("java.io.File");
    var JavaDateClass = Java.type("java.util.Date");
    var LoggerClass = Java.type("java.util.logging.Logger");
    var RowClass = Java.type("com.bearsoft.rowset.Row");
    var FieldsClass = Java.type("com.bearsoft.rowset.metadata.Fields");
    var IDGeneratorClass = Java.type("com.bearsoft.rowset.utils.IDGenerator");
    var PropertyChangeSupportClass = Java.type("java.beans.PropertyChangeSupport");
    var RowsetJsAdapterClass = Java.type("com.bearsoft.rowset.events.RowsetJsAdapter");
    var RowsComparatorClass = Java.type("com.bearsoft.rowset.sorting.RowsComparator");
    var ScriptTimerTaskClass = Java.type("com.eas.client.scripts.ScriptTimerTask");
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    var PlatypusPrincipalClass = Java.type("com.eas.client.login.PlatypusPrincipal");
    var ScriptUtilsClass = Java.type('com.eas.script.ScriptUtils');
    var FileUtilsClass = Java.type("com.eas.util.FileUtils");
    var MD5GeneratorClass = Java.type("com.eas.client.login.MD5Generator");

    var ModelLoaderClass = Java.type('com.eas.client.scripts.ApplicationModelLoader');
    var TwoTierModelClass = Java.type('com.eas.client.model.application.ApplicationDbModel');
    var ThreeTierModelClass = Java.type('com.eas.client.model.application.ApplicationPlatypusModel');

    //
    Object.defineProperty(P, "HTML5", {value: "HTML5 client"});
    Object.defineProperty(P, "J2SE", {value: "Java SE environment"});
    Object.defineProperty(P, "agent", {value: P.J2SE});

    var toPrimitive = ScriptUtilsClass.getToPrimitiveFunc();

    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    function boxAsJava(aValue) {
        //---------------------- Don't change to !== because of undefined 
        if (arguments.length > 0 && aValue != null) {
            if (aValue.unwrap) {
                aValue = aValue.unwrap();
            } else {
                aValue = toPrimitive(aValue);
            }
            return aValue;
        } else {// undefined -> null
            return null;
        }
    }
    ;
    Object.defineProperty(P, "boxAsJava", {
        value: boxAsJava
    });
    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    function boxAsJs(aValue) {
        if (aValue) {
            if (aValue.getPublished) {
                aValue = aValue.getPublished();
            } else if (aValue instanceof JavaDateClass) {
                aValue = new Date(aValue.time);
            } else if (aValue instanceof JavaArrayClass) {
                var converted = [];
                for (var i = 0; i < aValue.length; i++) {
                    converted[converted.length] = boxAsJs(aValue[i]);
                }
                return converted;
            } else if (aValue instanceof JavaCollectionClass) {
                var converted = [];
                for each (var v in aValue) {
                    converted[converted.length] = boxAsJs(v);
                }
                return converted;
            }
            aValue = EngineUtilsClass.unwrap(aValue);
        }
        return aValue;
    }

    Object.defineProperty(P, "boxAsJs", {
        value: boxAsJs
    });

    function invokeDelayed(aTimeout, aTarget) {
        if (arguments.length < 2)
            throw "invokeDelayed needs 2 arguments - timeout, callback.";
        //
        var lock = ScriptUtilsClass.getLock();
        var req = ScriptUtilsClass.getRequest();
        var resp = ScriptUtilsClass.getResponse();
        var session = ScriptUtilsClass.getSession();
        var principal = PlatypusPrincipalClass.getInstance();
        //
        ScriptTimerTaskClass.schedule(function () {
            ScriptUtilsClass.setLock(lock);
            ScriptUtilsClass.setRequest(req);
            ScriptUtilsClass.setResponse(resp);
            ScriptUtilsClass.setSession(session);
            PlatypusPrincipalClass.setInstance(principal);
            try {
                ScriptUtilsClass.locked(aTarget, lock);
            } finally {
                ScriptUtilsClass.setLock(null);
                ScriptUtilsClass.setRequest(null);
                ScriptUtilsClass.setResponse(null);
                ScriptUtilsClass.setSession(null);
                PlatypusPrincipalClass.setInstance(null);
            }
        }, aTimeout);
    }

    Object.defineProperty(P, "invokeDelayed", {get: function () {
            return invokeDelayed;
        }});

    var serverCoreClass;
    try {
        serverCoreClass = Java.type('com.eas.server.PlatypusServerCore');
        // in server (EE or standalone)

        function invokeLater(aTarget) {
            invokeDelayed(1, aTarget);
        }

        Object.defineProperty(P, "invokeLater", {get: function () {
                return invokeLater;
            }});
        load("classpath:server-deps.js");
    } catch (e) {
        serverCoreClass = null;
        // in client
        load("classpath:deps.js");
        // gui imports
        var KeyEventClass = Java.type("java.awt.event.KeyEvent");
        var SwingUtilitiesClass = Java.type("javax.swing.SwingUtilities");
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

        function invokeLater(aTarget) {
            SwingUtilitiesClass.invokeLater(aTarget);
        }

        Object.defineProperty(P, "invokeLater", {get: function () {
                return invokeLater;
            }});
        //
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

        var Icon = {};
        Object.defineProperty(P, "Icon", {value: Icon});
        Object.defineProperty(Icon, "load", {
            value: function (aResName, onSuccess, onFailure) {
                return IconResourcesClass.load(P.boxAsJava(aResName), P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
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
         * @param Callback
         * @param file name filter string
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
        function selectDirectory(curDir, aCallback) {
            if (aCallback) {
                invokeLater(function () {
                    var file = directoryDialog(curDir);
                    aCallback(file);
                });
            } else {
                return directoryDialog(curDir);
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
        function selectColor(title, color, aCallback) {
            if (aCallback) {
                invokeLater(function () {
                    var selected = colorDialog(title, color);
                    aCallback(selected);
                });
            } else {
                return colorDialog(title, color);
            }
        }

        Object.defineProperty(P, "selectColor", {
            value: selectColor
        });

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
        /**
         * @static
         * @param {type} aName
         * @param {type} aModel
         * @param {type} aTarget
         * @returns {P.loadForm.publishTo}
         */
        function loadForm(aName, aModel, aTarget) {
            var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
            var formDocument = ScriptedResourceClass.getApp().getForms().get(aName, files);
            var formFactory = FormLoaderClass.load(formDocument, ScriptedResourceClass.getApp(), aModel);
            var form = formFactory.form;
            if (aTarget) {
                P.Form.call(aTarget, null, aName, form);
            } else {
                aTarget = new P.Form(null, aName, form);
            }
            form.injectPublished(aTarget);
            if (!form.title)
                form.title = aName;
            var comps = formFactory.getWidgetsList();
            for (var c = 0; c < comps.length; c++) {
                (function () {
                    var comp = EngineUtilsClass.unwrap(boxAsJs(comps[c]));
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
        Object.defineProperty(P, "loadForm", {value: loadForm});
    }

    /**
     * @static
     * @param {type} deps
     * @param {type} aOnSuccess
     * @param {type} aOnFailure
     * @returns {undefined}
     */
    function require(deps, aOnSuccess, aOnFailure) {
        if (!Array.isArray(deps))
            deps = [deps];
        var strArray = new JavaStringArrayClass(deps.length);
        for (var i = 0; i < deps.length; i++)
            strArray[i] = deps[i] ? deps[i] + '' : null;
        if (aOnSuccess) {
            ScriptedResourceClass.require(strArray, P.boxAsJava(aOnSuccess), P.boxAsJava(aOnFailure));
        } else {
            ScriptedResourceClass.require(strArray);
        }
    }
    Object.defineProperty(P, "require", {value: require});
    function extend(Child, Parent) {
        var prevChildProto = {};
        for (var m in Child.prototype) {
            var member = Child.prototype[m];
            if (typeof member === 'function') {
                prevChildProto[m] = member;
            }
        }
        var F = function () {
        };
        F.prototype = Parent.prototype;
        Child.prototype = new F();
        for (var m in prevChildProto)
            Child.prototype[m] = prevChildProto[m];
        Child.prototype.constructor = Child;
        Child.superclass = Parent.prototype;
    }
    Object.defineProperty(P, "extend", {value: extend});

    var cached = {};
    function getModule(aName) {
        if (!cached[aName]) {
            var c = global[aName];
            if (c) {
                cached[aName] = new c();
            } else {
                P.require(aName);
                c = global[aName];
                if (c) {
                    cached[aName] = new c();
                } else {
                    throw 'No function: ' + aName + ' found while Modules.get(...).';
                }
            }
        }
        return cached[aName];
    }
    function createModule(aName) {
        var c = global[aName];
        if (c) {
            return new c();
        } else {
            P.require(aName);
            c = global[aName];
            if (c) {
                return new c();
            } else {
                throw 'No function: ' + aName + ' found while Modules.create(...).';
            }
        }
    }
    Object.defineProperty(P, "modules", {
        get: function () {
            if (serverCoreClass) {
                var core = serverCoreClass.getInstance();
                var systemSession = core.getSessionManager().getSystemSession();
                return systemSession.getModules();
            } else {
                return cached;
            }
        }
    });

    Object.defineProperty(P, "session", {
        get: function () {
            if (serverCoreClass) {
                return ScriptUtilsClass.getSession().getPublished();
            } else {
                return null;
            }
        }
    });

    Object.defineProperty(P, "principal", {
        get: function () {
            var clientSpacePrincipal = PlatypusPrincipalClass.getClientSpacePrincipal();
            var tlsPrincipal = PlatypusPrincipalClass.getInstance();
            return boxAsJs(clientSpacePrincipal !== null ? clientSpacePrincipal : tlsPrincipal);
        }
    });

    function publishRow(aDelegate, aTarget) {
        var nnFields = aDelegate.getFields();
        var instanceCTor = EngineUtilsClass.unwrap(nnFields.getInstanceConstructor());
        var target = arguments.length > 1 ? aTarget : (!!instanceCTor ? new instanceCTor() : {});
        var nFields = nnFields.toCollection();
        // plain mutable properties
        for (var n = 0; n < nFields.size(); n++) {
            (function () {
                var colIndex = n + 1;
                var nField = nFields[n];
                var valueAccessorDesc = {
                    get: function () {
                        return boxAsJs(aDelegate.getColumnObject(colIndex));
                    },
                    set: function (aValue) {
                        aDelegate.setColumnObject(colIndex, boxAsJava(aValue));
                    }
                };
                Object.defineProperty(target, nField.name, {get: valueAccessorDesc.get, set: valueAccessorDesc.set, enumerable: true});
                Object.defineProperty(target, n, valueAccessorDesc);
            })();
        }
        // ORM mutable scalar and readonly collection properties
        var define = function (aOrmDefs) {
            for each (var defsEntry in aOrmDefs.entrySet()) {
                var def = EngineUtilsClass.unwrap(defsEntry.getValue().getJsDef());
                Object.defineProperty(target, defsEntry.getKey(), def);
            }
        };
        define(nnFields.getOrmScalarDefinitions());
        define(nnFields.getOrmCollectionsDefinitions());
        Object.defineProperty(target, "unwrap", {
            value: function () {
                return aDelegate;
            }});
        return target;
        // WARNING!!! Don't define target.length, because of possible conflict with subject area data properties.
    }

    function BoundArray() {
        function copyProps(aObject) {
            var shadow = {};
            for (var pn in aObject) {
                var pName = pn + '';
                shadow[pName] = aObject[pName];
            }
            return shadow;
        }
        function applyProps(aShadow, aTarget) {
            for (var pn in aShadow) {
                var pName = pn + '';
                aTarget[pName] = aShadow[pName];
            }
        }
        if (BoundArray.superclass)
            BoundArray.superclass.constructor.apply(this, arguments);
        var target = this;
        var rowset = this.unwrap().getRowset();
        var adapter = new RowsetJsAdapterClass();
        rowset.addRowsetListener(adapter);
        adapter.rowsetFiltered = function () {
            var eventedRows = [];
            Array.prototype.splice.call(target, 0, target.length);
            var rows = rowset.getCurrent();
            for each (var nRow in rows) {
                Array.prototype.push.call(target, EngineUtilsClass.unwrap(nRow.getPublished()));
                eventedRows.push(nRow);
            }
            eventedRows.forEach(function (aEventedRow) {
                aEventedRow.fireChangesOfOppositeCollections();
                aEventedRow.fireChangesOfOppositeScalars();
            });
        };
        adapter.rowsetRequeried = function (event) {
            adapter.rowsetFiltered(null);
        };
        adapter.rowsetNextPageFetched = function (event) {
            adapter.rowsetFiltered(null);
        };
        adapter.rowsetSaved = function (event) {
            // ignore
        };
        adapter.rowsetRolledback = function (event) {
            adapter.rowsetFiltered(null);
        };
        adapter.rowsetScrolled = function (event) {
            // ignore
        };
        adapter.rowsetSorted = function (event) {
            adapter.rowsetFiltered(null);
        };
        Object.defineProperty(target, "fill", {
            value: function () {
                throw '\'fill\' is unsupported in BoundArray because of it\'s distinct values requirement';
            }
        });
        Object.defineProperty(target, "pop", {
            value: function () {
                if (!rowset.empty) {
                    var deletedRow = rowset.getRow(rowset.size());
                    rowset.deleteAt(rowset.size());
                    var res = Array.prototype.pop.call(target);
                    deletedRow.fireChangesOfOppositeCollections();
                    deletedRow.fireChangesOfOppositeScalars();
                    return res;
                }
            }
        });
        Object.defineProperty(target, "push", {
            value: function () {
                var eventedRows = [];
                var entityName = rowset.getFlowProvider().getEntityId();
                var nFields = rowset.getFields();
                for (var a = 0; a < arguments.length; a++) {
                    var shadow = copyProps(arguments[a]);// to avoid re initing by injected structure without values
                    var insertedRow = new RowClass(entityName, nFields);
                    insertedRow.setPublished(publishRow(insertedRow, arguments[a]));
                    rowset.insertAt(insertedRow, a < arguments.length - 1, rowset.size() + 1, null);
                    applyProps(shadow, arguments[a]);
                    eventedRows.push(insertedRow);
                }
                var res = Array.prototype.push.apply(target, arguments);
                eventedRows.forEach(function (aEventedRow) {
                    aEventedRow.fireChangesOfOppositeCollections();
                    aEventedRow.fireChangesOfOppositeScalars();
                });
                return res;
            }
        });
        Object.defineProperty(target, "reverse", {
            value: function () {
                rowset.reverse();
            }
        });
        Object.defineProperty(target, "shift", {
            value: function () {
                if (!rowset.empty) {
                    var deletedRow = rowset.getRow(1);
                    rowset.deleteAt(1);
                    var res = Array.prototype.shift.call(target);
                    deletedRow.fireChangesOfOppositeCollections();
                    deletedRow.fireChangesOfOppositeScalars();
                    return res;
                }
            }
        });
        var defaultCompareFunction = function (o1, o2) {
            var s1 = (o1 + '');
            var s2 = (o2 + '');
            return s1 > s2 ? 1 : s1 < s2 ? -1 : 0;
        };
        Object.defineProperty(target, "sort", {
            value: function () {
                if (arguments.length > 0 && arguments[0] instanceof RowsComparatorClass) {
                    rowset.sort(arguments[0]);
                } else {
                    var compareFunc = defaultCompareFunction;
                    if (arguments.length > 0 && typeof arguments[0] === 'function') {
                        compareFunc = arguments[0];
                    }
                    Array.prototype.sort.call(target, compareFunc);
                }
                return target;
            }
        });
        Object.defineProperty(target, "splice", {
            value: function () {
                var eventedRows = [];
                if (arguments.length > 0) {
                    var beginToDeleteAt = arguments[0];
                    var howManyToDelete = Number.MAX_VALUE;
                    if (arguments.length > 1) {
                        howManyToDelete = arguments[1];
                    }
                    var needToAdd = arguments.length > 2;
                    var deleted = 0;
                    while (!rowset.empty && deleted++ < howManyToDelete) {
                        var deletedRow = rowset.getRow(beginToDeleteAt + 1);
                        rowset.deleteAt(beginToDeleteAt + 1, needToAdd);
                        eventedRows.push(deletedRow);
                    }
                    var insertAt = beginToDeleteAt;
                    var entityName = rowset.getFlowProvider().getEntityId();
                    var nFields = rowset.getFields();
                    for (var a = 2; a < arguments.length; a++) {
                        var shadow = copyProps(arguments[a]);// to avoid re initing by injected structure without values
                        var insertedRow = new RowClass(entityName, nFields);
                        insertedRow.setPublished(publishRow(insertedRow, arguments[a]));
                        rowset.insertAt(insertedRow, a < arguments.length - 1, insertAt + 1, null);
                        applyProps(shadow, arguments[a]);
                        eventedRows.push(insertedRow);
                        insertAt++;
                    }
                }
                var res = Array.prototype.splice.apply(target, arguments);
                eventedRows.forEach(function (aEventedRow) {
                    aEventedRow.fireChangesOfOppositeCollections();
                    aEventedRow.fireChangesOfOppositeScalars();
                });
                return res;
            }
        });

        Object.defineProperty(target, "unshift", {
            value: function () {
                var eventedRows = [];
                var entityName = rowset.getFlowProvider().getEntityId();
                var nFields = rowset.getFields();
                for (var a = 0; a < arguments.length; a++) {
                    var shadow = copyProps(arguments[a]);// to avoid re initing by injected structure without values
                    var insertedRow = new RowClass(entityName, nFields);
                    insertedRow.setPublished(publishRow(insertedRow, arguments[a]));
                    rowset.insertAt(insertedRow, a < arguments.length - 1, a + 1, null);
                    applyProps(shadow, arguments[a]);
                    eventedRows.push(insertedRow);
                }
                var res = Array.prototype.unshift.apply(target, arguments);
                eventedRows.forEach(function (aEventedRow) {
                    aEventedRow.fireChangesOfOppositeCollections();
                    aEventedRow.fireChangesOfOppositeScalars();
                });
                return res;
            }
        });

        Object.defineProperty(target, "createFilter", {
            value: function (aConstraints) {
				var constraints = Array.isArray(aConstraints) ? aConstraints : [aConstraints];
                var nEntity = this.unwrap();
                return boxAsJs(nEntity.createFilter(constraints));
            }
        });

        Object.defineProperty(target, "createSorting", {
            value: function (aCriteria) {
				var criteria = Array.isArray(aCriteria) ? aCriteria : [aCriteria];
                var nEntity = this.unwrap();
                return boxAsJs(nEntity.createSorting(criteria));
            }
        });

        Object.defineProperty(target, "find", {
            value: function (aCriteria) {
                var nEntity = this.unwrap();
                return EngineUtilsClass.unwrap(nEntity.find(aCriteria));
            }
        });
    }

    RowClass.setPublisher(publishRow);
    FieldsClass.setPublisher(function (aDelegate) {
        var target = {};
        var nFields = aDelegate.toCollection();
        for (var n = 0; n < nFields.size(); n++) {
            (function () {
                var nField = nFields[n];
                var pField = EngineUtilsClass.unwrap(nField.getPublished());
                Object.defineProperty(target, nField.name, {
                    value: pField
                });
                Object.defineProperty(target, n, {
                    value: pField
                });
            })();
        }
        return target;
    });

    extend(BoundArray, Array);
    extend(P.ApplicationDbEntity, BoundArray);
    extend(P.ApplicationPlatypusEntity, BoundArray);

    P.Filter.prototype.apply = function () {
        var varargs = new JavaArrayClass(arguments.length);
        for (var v = 0; v < arguments.length; v++)
            varargs[v] = boxAsJava(arguments[v]);
        var nFilter = this.unwrap();
        nFilter.apply(varargs);
    };

    /**
     * @static
     * @param {type} aName
     * @param {type} aTarget
     * @returns {P.loadModel.publishTo}
     */
    function loadModel(aName, aTarget) {
        var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
        if (files) {
            var modelDocument = ScriptedResourceClass.getApp().getModels().get(aName, files);
            var model = ModelLoaderClass.load(modelDocument, ScriptedResourceClass.getApp());
            var modelCTor;
            if (model instanceof TwoTierModelClass) {
                modelCTor = P.ApplicationDbModel;
            } else if (model instanceof ThreeTierModelClass) {
                modelCTor = P.ApplicationPlatypusModel;
            } else {
                throw "Can't determine model's type.";
            }
            if (aTarget) {
                modelCTor.call(aTarget, model);
            } else {
                aTarget = new modelCTor(model);
            }
            function publishEntity(nEntity) {
                var published = EngineUtilsClass.unwrap(nEntity.getPublished());
                var pSchema = {};
                Object.defineProperty(published, "schema", {
                    value: pSchema
                });
                var nFields = nEntity.getFields().toCollection();
                for (var n = 0; n < nFields.size(); n++) {
                    (function () {
                        var nField = nFields[n];
                        // schema
                        var schemaDesc = {
                            value: nField.getPublished()
                        };
                        if (!pSchema[nField.name])
                            Object.defineProperty(pSchema, nField.name, schemaDesc);
                        else
                            throw "Duplicated field name found: " + nField.name + " in entity " + nEntity.name + (nEntity.title ? " [" + nEntity.title + "]" : "");
                        Object.defineProperty(pSchema, n, schemaDesc);
                    })();
                }
                // entity.params.p1 syntax
                var nParameters = nEntity.getQuery().getParameters();
                var ncParameters = nParameters.toCollection();
                var pParams = {};
                for (var p = 0; p < ncParameters.size(); p++) {
                    (function () {
                        var nParameter = ncParameters[p];
                        var pDesc = {
                            get: function () {
                                return boxAsJs(nParameter.jsValue/*because of UNDEFINED_SQL_VALUE*/);
                            },
                            set: function (aValue) {
                                nParameter.jsValue/*because of UNDEFINED_SQL_VALUE*/ = boxAsJava(aValue);
                            }
                        };
                        Object.defineProperty(pParams, nParameter.name, pDesc);
                        Object.defineProperty(pParams, p, pDesc);
                    })();
                }
                Object.defineProperty(published, "params", {value: pParams});
                // entity.params.schema.p1 syntax
                var pParamsSchema = EngineUtilsClass.unwrap(nParameters.getPublished());
                if (!pParams.schema)
                    Object.defineProperty(pParams, "schema", {value: pParamsSchema});
                return published;
            }
            var entities = model.entities();
            for each (var enEntity in entities) {
                enEntity.validateQuery();
                if (enEntity.name) {
                    (function () {
                        var ppEntity = publishEntity(enEntity);
                        Object.defineProperty(aTarget, enEntity.name, {
                            value: ppEntity,
                            enumerable: true
                        });
                    })();
                }
            }
            model.createORMDefinitions();
            aTarget.loadEntity = function (queryName) {
                var lnEntity = model.loadEntity(P.boxAsJava(queryName));
                return publishEntity(lnEntity);
            };
            aTarget.createEntity = function (sqlText, datasourceName) {
                var lnEntity = model.createEntity(P.boxAsJava(sqlText), P.boxAsJava(datasourceName));
                return publishEntity(lnEntity);
            };
            return aTarget;
        } else {
            throw "Model definition '" + aName + "' missing.";
        }
    }
    Object.defineProperty(P, "loadModel", {value: loadModel});
    /**
     * @static
     * @param {type} aName
     * @param {type} aData data
     * @param {type} aTarget
     * @returns {P.loadTemplate.publishTo}
     */
    function loadTemplate(aName, aData, aTarget) {
        var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
        if (files) {
            var reportConfig = ScriptedResourceClass.getApp().getReports().get(aName, files);
            if (aTarget) {
                P.ReportTemplate.call(aTarget, reportConfig, aData);
            } else {
                aTarget = new P.ReportTemplate(reportConfig, aData);
            }
            return aTarget;
        } else {
            throw "Report template '" + aName + "' missing.";
        }
    }
    Object.defineProperty(P, "loadTemplate", {value: loadTemplate});
    /**
     * Constructs server module network proxy.
     * @constructor
     * @param {String} aModuleName Name of server module (session stateless or statefull or rezident).
     */
    function ServerModule(aModuleName) {
        if (aModuleName != null && aModuleName != "") {
            var app = ScriptedResourceClass.getApp();
            if (app) {
                var proxy = app.getServerModules();
                if (proxy) {
                    var moduleInfo = proxy.getCachedStructure(aModuleName);
                    if (moduleInfo.isPermitted()) {
                        var functions = moduleInfo.getFunctionsNames();
                        var currentObject = this;
                        functions.forEach(function (aFunctionName) {
                            currentObject[aFunctionName] = function () {
                                var onSuccess = null;
                                var onFailure = null;
                                var argsLength = arguments.length;
                                if (arguments.length > 1 && typeof arguments[arguments.length - 1] === "function" && typeof arguments[arguments.length - 2] === "function") {
                                    onSuccess = arguments[arguments.length - 2];
                                    onFailure = arguments[arguments.length - 1];
                                    argsLength -= 2;
                                } else if (arguments.length > 0 && typeof arguments[arguments.length - 1] === "function") {
                                    onSuccess = arguments[arguments.length - 1];
                                    argsLength -= 1;
                                }
                                var params = new JavaArrayClass(argsLength);
                                for (var j = 0; j < argsLength; j++) {
                                    params[j] = arguments[j];
                                }
                                if (onSuccess) {
                                    proxy.callServerModuleMethod(aModuleName, aFunctionName, onSuccess, onFailure, params);
                                } else {
                                    var result = proxy.callServerModuleMethod(aModuleName, aFunctionName, null, null, params);
                                    return result && result.getPublished ? result.getPublished() : result;
                                }
                            };
                        });
                    } else {
                        throw "Access denied for module " + aModuleName + ". May be denied public access.";
                    }
                } else {
                    throw "This architecture does not support server modules.";
                }
            }
        } else {
            throw "Module Name could not be empty.";
        }
    }

    Object.defineProperty(P, "ServerModule", {value: ServerModule});

    var Resource = {};
    Object.defineProperty(Resource, "load", {
        value: function (aResName, onSuccess, onFailure) {
            return boxAsJs(ScriptedResourceClass.load(boxAsJava(aResName), boxAsJava(onSuccess), boxAsJava(onFailure)));
        }
    });
    Object.defineProperty(Resource, "upload", {
        value: function (aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback) {
            print("upload() is not implemented for J2SE");
        }
    });
    Object.defineProperty(Resource, "applicationPath", {
        get: function () {
            return ScriptedResourceClass.getApplicationPath();
        }
    });
    Object.defineProperty(P, "Resource", {
        value: Resource
    });
    Object.defineProperty(P, "logout", {
        value: function (onSuccess, onFailure) {
            return P.principal.logout(onSuccess, onFailure);
        }
    });

    var IDGenerator = {};

    Object.defineProperty(P, "IDGenerator", {value: IDGenerator});
    Object.defineProperty(IDGenerator, "genID", {
        value: function () {
            return IDGeneratorClass.genID();
        }
    });
    Object.defineProperty(P, "ID", {value: IDGenerator});
    Object.defineProperty(IDGenerator, "generate", {
        value: function () {
            return IDGeneratorClass.genID();
        }
    });
    var MD5Generator = {};
    Object.defineProperty(P, "MD5Generator", {value: MD5Generator});
    Object.defineProperty(MD5Generator, "generate", {
        value: function (aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });
    var MD5 = {};
    Object.defineProperty(P, "MD5", {value: MD5});
    Object.defineProperty(MD5, "generate", {
        value: function (aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });

    var applicationLogger = LoggerClass.getLogger("Application");
    var Logger = {};
    Object.defineProperty(P, "Logger", {value: Logger});
    Object.defineProperty(Logger, "config", {value: function (aMessage) {
            applicationLogger.config("" + aMessage);
        }});
    Object.defineProperty(Logger, "severe", {value: function (aMessage) {
            applicationLogger.severe("" + aMessage);
        }});
    Object.defineProperty(Logger, "warning", {value: function (aMessage) {
            applicationLogger.warning("" + aMessage);
        }});
    Object.defineProperty(Logger, "info", {value: function (aMessage) {
            applicationLogger.info("" + aMessage);
        }});
    Object.defineProperty(Logger, "fine", {value: function (aMessage) {
            applicationLogger.fine("" + aMessage);
        }});
    Object.defineProperty(Logger, "finer", {value: function (aMessage) {
            applicationLogger.finer("" + aMessage);
        }});
    Object.defineProperty(Logger, "finest", {value: function (aMessage) {
            applicationLogger.finest("" + aMessage);
        }});

    function async(aWorker, onSuccess, onFailure) {
        ScriptUtilsClass.jsSubmitTask(function () {
            try {
                var result = aWorker();
                try {
                    ScriptUtilsClass.jsAcceptTaskResult(function () {
                        onSuccess(result);
                    });
                } catch (e) {
                    applicationLogger.severe(e);
                }
            } catch (e) {
                if (onFailure)
                    onFailure('' + e);
            }
        });
    }
    Object.defineProperty(P, "async", {
        get: function () {
            return async;
        }
    });

    function readString(aFileName, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        return FileUtilsClass.readString(new FileClass(aFileName), encoding);
    }

    Object.defineProperty(P, "readString", {
        value: readString
    });
    function writeString(aFileName, aText, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        FileUtilsClass.writeString(new FileClass(aFileName), aText, encoding);
    }
    Object.defineProperty(P, "writeString", {
        value: writeString
    });
})();
if (!P) {
    /** 
     * Platypus library namespace global variable.
     * @namespace P
     */
    var P;
    P.HTML5 = "";
    P.J2SE = "";
    P.agent = "";

    /**
     * Dependencies resolver function.
     * Performs automatic dependencies resolving of modules mentioned in first parameter.
     * Can be used as synchronous or asynchronous dependencies resolver.
     * Module names may be platypus module name, e.g. 'Calculator'.
     * Platypus modules names do not depend on location of modules' files in project. Platypus modules names are global script names.
     * Module names may be plain scripts names in the following form: libs/leaflet.js from the root of the project's source.
     * Platypus project's source root is the "app" folder. Relative paths to plain scripts are not supported.
     * 
     * @param {Array} aDeps Array of modules names or a single module name.
     * @param {Function} aOnSuccess Success callback. Optional. If omitted, synchronous version of code will be used.
     * @param {Function} aOnFailure Failure callback. Optional. If omitted, no error information will be provided.
     * @returns {undefined}
     */
    P.require = function (aDeps, aOnSuccess, aOnFailure) {
    };
    /**
     * Classic js extend function.
     * @param {Function} aChild
     * @param {Function} aParent
     * @returns {undefined}
     */
    P.extend = function (aChild, aParent) {
    };
    /**
     * Global modules array available in server environment.
     * Theese modules are inaccessible via a network.
     * @type Array
     */
    P.modules = [];
    /**
     * Session object available in server environment.
     * It is inaccessible via a network.
     */
    P.session = {};
    /**
     * Modules array of session available in server environment.
     * Theese modules are accessible to network clients
     * @type Array
     */
    P.session.modules = [];
    /**
     * Parses *.model files and creates data model (entity manager) of a module.
     * @param aModuleName Name of the module, the data model will be loaded for.
     * @returns Model instance.
     */
    P.loadModel = function (aModuleName) {
        return {};
    };
    /**
     * Parses *.layout files and creates view of a module if it is a form module.
     * @param aModuleName Name of the module, the form will be loaded for.
     * @param aData Script data object, model widgets to be bound to. Currently is may be only data model.
     * @returns P.Form instance.
     */
    P.loadForm = function (aModuleName, aData) {
    };
    /**
     * Parses *.xlsx files and creates report template of a module if it is a report module.
     * @param aModuleName Name of the module, the template will be loaded for.
     * @param aData Script data object, that will be used while report generating.
     * @returns P.ReportTemplate instance.
     */
    P.loadTemplate = function (aModuleName, aData) {
    };
    /**
     * Consists of show forms.
     * @type Array
     */
    P.Form.shown = [];
    /**
     * Fast scan of P.Form.shown.
     * @param aFormKey form key value.
     * @returns {P.Form} instance
     */
    P.Form.getShownForm = function (aFormKey) {
    };
    /**
     * P.Form.shown change handler.
     * @returns {Function}
     */
    P.Form.onChange = function () {
    };
    /**
     * Constructs server module network proxy.
     * @constructor
     */
    P.ServerModule = function () {
    };
    /**
     * Utility class with resource load/upload/location methods.
     */
    P.Resource = {
        /**
         * Loads some plain resource from project or from network via http.
         * @param {String} aResName Platypus project's reource name e.g. "some-project-folder/some-resource.sr" or http url of the resource.
         * @param {Function} onSuccess Success callback. Optional. If omitted sycnhronous version of code will be used.
         * @param {Function} onFailure Failure callback. Optional. If omitted no error information will provided.
         * @returns Loaded content if synchronous version of code is used or null otherwise.
         */
        load: function (aResName, onSuccess, onFailure) {
        },
        /**
         * Points to application source root directory "app" in local filesystem.
         * To make full absolute path to some platypus project's resource in local filesystem, 
         * one should write such code: <code> var fullPath = P.applicationPath + "/" + "some-project-folder/some-resource.sr"</code>
         * Note, that folders separator char may vary in various operating systems.
         * @type String
         */
        applicationPath: "",
        /**
         * Available only in browser environment.
         * @param {Object} aFile
         * @param {String} aName
         * @param {Function} aCompleteCallback
         * @param {Function} aProgressCallback
         * @param {Function} aAbortCallback
         * @returns {undefined}
         */
        upload: function (aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback) {
        }
    };
    /**
     * Invalidates user's session at the connected server.
     * Available only in client environment.
     * In server environment does nothing.
     * @param {Function} onSuccess Success callback. Optional. If omitted sycnhronous version of code will be used.
     * @param {Function} onFailure Failure callback. Optional. If omitted no error information will provided.
     * @returns {undefined}
     */
    P.logout = function (onSuccess, onFailure) {
    };
    /**
     * Current logged in principal. ay system principal if called in resident module code context.
     */
    P.principal = {
        name: ""
    };
    /**
     * Utility class to maange icons and theirs underlying data.
     */
    P.Icon = {
        /**
         * Loads an icon from platypus project's plain resource or from a network via http.
         * @param {type} aResName Platypus project's reource name e.g. "some-project-folder/some-icon.png" or http url of the image.
         * @param {type} onSuccess Success callback. Optional. If omitted sycnhronous version of code will be used.
         * @param {type} onFailure Failure callback. Optional. If omitted no error information will provided.
         * @returns Loaded icon if synchronous version of code is used or null otherwise.
         * @returns {undefined}
         */
        load: function (aResName, onSuccess, onFailure) {
        }
    };
    /**
     * Id generator.
     * @type 
     */
    P.ID = {
        /**
         * Generates an id and returns it as a string comprised of numbers.
         * @returns {String} Generated id string
         */
        generate: function () {
            return "";
        }
    };

    /**
     * Md5 hash generator
     * @type type
     */
    P.MD5 = {
        /**
         * Generates MD5 hash for given value. 
         * @param aValue Value the hash is generated for. Converted to string.
         * @return Generated MD5 hash
         */
        generate: function (aValue) {
            return "";
        }
    };
    P.Logger = {};
    P.VK_ALT = 0;
    P.VK_BACKSPACE = 0;
    P.VK_DELETE = 0;
    P.VK_DOWN = 0;
    P.VK_END = 0;
    P.VK_ENTER = 0;
    P.VK_ESCAPE = 0;
    P.VK_HOME = 0;
    P.VK_LEFT = 0;
    P.VK_PAGEDOWN = 0;
    P.VK_PAGEUP = 0;
    P.VK_RIGHT = 0;
    P.VK_SHIFT = 0;
    P.VK_TAB = 0;
    P.VK_UP = 0;
    P.selectFile = function () {
    };
    P.selectDirectory = function () {
    };
    P.selectColor = function () {
    };
    P.readString = function () {
    };
    P.writeString = function () {
    };
    P.msgBox = function () {
    };
    P.warn = function () {
    };
    P.prompt = function () {
    };
    P.HorizontalPosition = {};
    P.VerticalPosition = {};
    P.Orientation = {};
    P.FontStyle = {};
    /**
     * Utility function, that allows some application code to become asynchronous in terms of platypus async io model.
     * WARNING!!! When one uses this function, aWorkerFunc will be run in a separate thread.
     * So make sure, that there are no any shared data will be used by aWorkerFunc's code.      
     * This method intended generally for input/output libraries writers.
     * The callbacks provided to the async function are guaranteed to be called according to Platypus.js parallelism levels.
     * @param {Function} aWorkerFunc A function with application logic, input, output, etc.
     * @param {Function} aOnSuccess Success callback to be called when aWorkerFunc will complete its work.
     * @param {Function} aOnFailure Failure callback to be called while aWorkerFunc will raise an exception.
     * @returns {undefined}
     */
    P.async = function (aWorkerFunc, aOnSuccess, aOnFailure) {
    };
    /**
     * Posts an execution request into browser's event loop.
     * @param {Function} aCallback
     * @returns {undefined}
     */
    P.invokeLater = function (aCallback) {
    };
    /**
     * Utilizes browser's setTimeput in a safe way to calls aCallback after some period of time.
     * @param {Number} aTimeout Period of time to delay.
     * @param {Function} aCallback
     * @returns {undefined}
     */
    P.invokeDelayed = function (aTimeout, aCallback) {
    };
}
