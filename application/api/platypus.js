(function () {
    if (typeof Set === 'undefined') {
        var LinkedHashSetClass = Java.type('java.util.LinkedHashSet');
        Set = function () {
            var container = new LinkedHashSetClass();
            this.add = function (aValue) {
                container.add(aValue);
            };
            this.delete = function (aValue) {
                container.remove(aValue);
            };
            Object.defineProperty(this, 'size', {get: function () {
                    return container.size();
                }});
            this.forEach = function (aCallback) {
                container.forEach(aCallback);
            };
        };
    }

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
    load("classpath:internals.js");
    load("classpath:managed.js");
    load("classpath:orderer.js");

    // core imports
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var JavaArrayClass = Java.type("java.lang.Object[]");
    var JavaStringArrayClass = Java.type("java.lang.String[]");
    var JavaCollectionClass = Java.type("java.util.Collection");
    var FileClass = Java.type("java.io.File");
    var JavaDateClass = Java.type("java.util.Date");
    var LoggerClass = Java.type("java.util.logging.Logger");
    var FieldsClass = Java.type("com.eas.client.metadata.Fields");
    var IDGeneratorClass = Java.type("com.eas.util.IDGenerator");
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

    var addListenerName = '-platypus-listener-add-func';
    var removeListenerName = '-platypus-listener-remove-func';
    var fireChangeName = '-platypus-change-fire-func';
    function listenable(aTarget) {
        var listeners = new Set();
        Object.defineProperty(aTarget, addListenerName, {value: function (aListener) {
                listeners.add(aListener);
            }});
        Object.defineProperty(aTarget, removeListenerName, {value: function (aListener) {
                listeners.delete(aListener);
            }});
        Object.defineProperty(aTarget, fireChangeName, {value: function (aChange) {
                Object.freeze(aChange);
                listeners.forEach(function (aListener) {
                    aListener(aChange);
                });
            }});
        return function () {
            unlistenable(aTarget);
        };
    }

    function unlistenable(aTarget) {
        delete aTarget[addListenerName];
        delete aTarget[removeListenerName];
    }

    function listen(aTarget, aListener) {
        aTarget[addListenerName](aListener);
        return function () {
            aTarget[removeListenerName](aListener);
        };
    }

    function unlisten(aTarget, aListener) {
        aTarget[removeListenerName](aListener);
    }

    function fire(aTarget, aChange) {
        try {
            aTarget[fireChangeName](aChange);
        } catch (e) {
            Logger.severe(e);
        }
    }

    function fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange, nnFields) {
        var ormDefs = nnFields.getOrmScalarExpandings().get(aChange.propertyName);
        if (ormDefs) {
            var expandingsOldValues = aChange.beforeState.selfScalarsOldValues;
            ormDefs.forEach(function (ormDef) {
                if (ormDef.getName()) {
                    var expandingOldValue = expandingsOldValues[ormDef.getName()];
                    var expandingNewValue = aSubject[ormDef.getName()];
                    fire(aSubject, {source: aChange.source, propertyName: ormDef.getName(), oldValue: expandingOldValue, newValue: expandingNewValue});
                    if (ormDef.getOppositeName()) {
                        if (expandingOldValue) {
                            fire(expandingOldValue, {source: expandingOldValue, propertyName: ormDef.getOppositeName()});
                        }
                        if (expandingNewValue) {
                            fire(expandingNewValue, {source: expandingNewValue, propertyName: ormDef.getOppositeName()});
                        }
                    }
                }
            });
        }
    }

    function prepareSelfScalarsChanges(aSubject, aChange, nnFields) {
        var ormDefs = nnFields.getOrmScalarExpandings().get(aChange.propertyName);
        var oldScalarValues = [];
        if (ormDefs) {
            ormDefs.forEach(function (ormDef) {
                if (ormDef && ormDef.getName()) {
                    oldScalarValues[ormDef.getName()] = aSubject[ormDef.getName()];
                }
            });
        }
        return oldScalarValues;
    }

    function fireOppositeScalarsSelfCollectionsChanges(aSubject, aChange, nnFields) {
        var oppositeScalarsFirerers = aChange.beforeState.oppositeScalarsFirerers;
        if (oppositeScalarsFirerers) {
            oppositeScalarsFirerers.forEach(function (aFirerer) {
                aFirerer();
            });
        }
        var collectionsDefs = nnFields.getOrmCollectionsDefinitions().entrySet();
        if (collectionsDefs) {
            collectionsDefs.forEach(function (aEntry) {
                var collectionName = aEntry.getKey();
                var ormDef = aEntry.getValue();
                var collection = aSubject[collectionName];
                collection.forEach(function (item) {
                    fire({source: item, propertyName: ormDef.getOppositeName()});
                });
            });
            collectionsDefs.forEach(function (aEntry) {
                var collectionName = aEntry.getKey();
                fire(aSubject, {source: aSubject, propertyName: collectionName});
            });
        }
    }

    function prepareOppositeScalarsChanges(aSubject, nnFields) {
        var firerers = [];
        var collectionsDefs = nnFields.getOrmCollectionsDefinitions().entrySet();
        collectionsDefs.forEach(function (aEntry) {
            var collectionName = aEntry.getKey();
            var ormDef = aEntry.getValue();
            var collection = aSubject[collectionName];
            collection.forEach(function (item) {
                if (ormDef.getOppositeName()) {
                    firerers.push(function () {
                        fire({source: item, propertyName: ormDef.getOppositeName()});
                    });
                }
            });
        });
        return firerers;
    }

    function fireOppositeScalarsChanges(aSubject, nnFields) {
        var collected = prepareOppositeScalarsChanges(aSubject, nnFields);
        collected.forEach(function (aFirerer) {
            aFirerer();
        });
    }

    function fireOppositeCollectionsChanges(aSubject, nnFields) {
        var scalarsDefs = nnFields.getOrmScalarDefinitions().entrySet();
        scalarsDefs.forEach(function (aEntry) {
            var scalarName = aEntry.getKey();
            var ormDef = aEntry.getValue();
            var scalar = aSubject[scalarName];
            if (ormDef.getOppositeName()) {
                fire({source: scalar, propertyName: ormDef.getOppositeName()});
            }
        });
    }

    function generateChangeLogKeys(keys, fields, propName, aSubject, oldValue) {
        if (fields) {
            for (var i = 1; i <= fields.getFieldsCount(); i++) {
                var field = fields.get(i);
                if (field.isPk()) {
                    var fieldName = field.getName();
                    var value = aSubject[fieldName];
                    // Some tricky processing of primary keys modification case ...
                    if (fieldName == propName) {
                        value = oldValue;
                    }
                    keys.add(new ChangeValueClass(fieldName, value, field.getTypeInfo()));
                }
            }
        }
    }

    ScriptUtilsClass.setCollectionDefFunc(
            function (sourcePublishedEntity, targetFieldName, sourceFieldName) {
                var _self = this;
                _self.enumerable = false;
                _self.configurable = true;
                _self.get = function () {
                    var criterion = {};
                    var targetKey = this[targetFieldName];
                    criterion[sourceFieldName] = targetKey;
                    var found = sourcePublishedEntity.find(criterion);
                    P.manageArray(found, {
                        spliced: function (added, deleted) {
                            added.forEach(function (item) {
                                item[sourceFieldName] = targetKey;
                            });
                            deleted.forEach(function (item) {
                                item[sourceFieldName] = null;
                            });
                        },
                        scrolled: function (aSubject, oldCursor, newCursor) {
                            fire(found, {source: found, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
                        }
                    });
                    return found;
                };
            });

    var InsertClass = Java.type('com.eas.client.changes.Insert');
    var DeleteClass = Java.type('com.eas.client.changes.Delete');
    var UpdateClass = Java.type('com.eas.client.changes.Update');
    var ValueClass = Java.type('com.eas.client.changes.ChangeValue');

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
                var entityCTor;
                if (model instanceof TwoTierModelClass) {
                    entityCTor = P.ApplicationDbEntity;
                } else if (model instanceof ThreeTierModelClass) {
                    entityCTor = P.ApplicationPlatypusEntity;
                } else {
                    throw "Can't determine model's type.";
                }
                var justInserted = null;
                var justInsertedChange = null;
                var orderers = {};
                var published = [];

                function managedOnChange(aSubject, aChange) {
                    if (!tryToComplementInsert(aSubject, aChange)) {
                        var updateChange = new UpdateClass(nEntity.getQueryName());
                        generateChangeLogKeys(updateChange.keys, nEntity.getFileds(), aChange.propertyName, aSubject, aChange.oldValue);
                        updateChange.data.add(new ValuleClass(aChange.propertyName, aChange.newValue, noFields[aChange.propertyName].getTypeInfo()));
                        nEntity.getChangeLog().add(updateChange);
                    }
                    Object.keys(orderers).forEach(function (aOrdererKey) {
                        var aOrderer = orderers[aOrdererKey];
                        if (aOrderer.inKeys(aChange.propertyName)) {
                            aOrderer.delete(aChange.source);
                            aOrderer.add(aChange.source);
                        }
                    });
                    fire(aSubject, aChange);
                    fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange, nEntity.getFields());// Expanding change
                    var field = noFields[aChange.propertyName];
                    if (field && field.pk) {
                        fireOppositeScalarsSelfCollectionsChanges(aSubject, aChange, nEntity.getFields());
                    }
                }
                function managedBeforeChange(aSubject, aChange) {
                    var oldScalars = prepareSelfScalarsChanges(aSubject, aChange, nEntity.getFields());
                    var oppositeScalarsFirerers = prepareOppositeScalarsChanges(aSubject, nEntity.getFields());
                    return {selfScalarsOldValues: oldScalars, oppositeScalarsFirerers: oppositeScalarsFirerers};
                }
                function tryToComplementInsert(aSubject, aChange) {
                    var complemented = false;
                    if (aSubject === justInserted && !noFields[aChange.propertyName].nullable) {
                        var met = false;
                        for (var d = 0; d < justInsertedChange.data.length; d++) {
                            var iv = justInsertedChange.data[d];
                            if (iv.name == aChange.propertyName) {
                                met = true;
                                break;
                            }
                        }
                        if (!met) {
                            justInsertedChange.getData().add(new ValueClass(aChange.propertyName, aChange.newValue, noFields[aChange.propertyName].getTypeInfo()));
                            complemented = true;
                        }
                    }
                    return complemented;
                }
                function acceptInstance(aSubject) {
                    P.manageObject(aSubject, managedOnChange, managedBeforeChange);
                    listenable(aSubject);
                    // ORM mutable scalar and collection properties
                    var define = function (aOrmDefs) {
                        for each (var defsEntry in aOrmDefs.entrySet()) {
                            var def = EngineUtilsClass.unwrap(defsEntry.getValue().getJsDef());
                            Object.defineProperty(aSubject, defsEntry.getKey(), def);
                        }
                    };
                    define(nnFields.getOrmScalarDefinitions());
                    define(nnFields.getOrmCollectionsDefinitions());
                }

                var _onInserted = null;
                var _onDeleted = null;
                var _onScrolled = null;
                P.manageArray(published, {
                    spliced: function (added, deleted) {
                        added.forEach(function (aAdded) {
                            justInserted = aAdded;
                            justInsertedChange = new InsertClass(nEntity.getQueryName());
                            for (var nf = 0; nf < nnFields.size(); nf++) {
                                var nField = nnFields[nf];
                                if (nField.pk) {
                                    aAdded[nField.name] = nField.getTypeInfo().generateValue();
                                }
                            }
                            for (var na in aAdded) {
                                var nField = noFields[na];
                                if (nField) {
                                    var v = aAdded[na];
                                    var cv = new ValueClass(nField.name, v, nField.getTypeInfo());
                                    justInsertedChange.data.add(cv);
                                }
                            }
                            nEntity.getChangeLog().add(justInsertedChange);
                            Object.keys(orderers).forEach(function (aOrdererKey) {
                                var aOrderer = orderers[aOrdererKey];
                                aOrderer.add(aAdded);
                            });
                            acceptInstance(aAdded);
                            fireOppositeScalarsChanges(aAdded, nnFields);
                            fireOppositeCollectionsChanges(aAdded, nnFields);
                        });
                        deleted.forEach(function (aDeleted) {
                            if (aDeleted === justInserted) {
                                justInserted = null;
                                justInsertedChange = null;
                            }
                            var deleteChange = new DeleteClass(nEntity.getQueryName());
                            generateChangeLogKeys(deleteChange.keys, nEntity.getFields(), null, aDeleted, null);
                            nEntity.getChangeLog().add(deleteChange);
                            Object.keys(orderers).forEach(function (aOrdererKey) {
                                var aOrderer = orderers[aOrdererKey];
                                aOrderer.delete(aDeleted);
                            });
                            fireOppositeScalarsChanges(aDeleted, nnFields);
                            fireOppositeCollectionsChanges(aDeleted, nnFields);
                            unlistenable(aDeleted);
                            P.unmanageObject(aDeleted);
                        });
                        if (_onInserted) {
                            try {
                                _onInserted({source: published, items: added});
                            } catch (e) {
                                Logger.severe(e);
                            }
                        }
                        if (_onDeleted) {
                            try {
                                _onDeleted({source: published, items: deleted});
                            } catch (e) {
                                Logger.severe(e);
                            }
                        }
                    },
                    scrolled: function (aSubject, oldCursor, newCursor) {
                        if (_onScrolled) {
                            try {
                                _onScrolled({source: published, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
                            } catch (e) {
                                Logger.severe(e);
                            }
                        }
                        fire(published, {source: published, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
                    }
                });
                entityCTor.call(published, nEntity);
                var pSchema = {};
                Object.defineProperty(published, "schema", {
                    value: pSchema
                });
                var pkFieldName = '';
                var nnFields = nEntity.getFields().toCollection();
                var noFields = {};
                for (var n = 0; n < nnFields.size(); n++) {
                    (function () {
                        var nField = nnFields[n];
                        noFields[nField.name] = nField;
                        if (nField.isPk())
                            pkFieldName = nField.name;
                        // schema
                        var schemaDesc = {
                            value: nField.getPublished()
                        };
                        if (!pSchema[nField.name]) {
                            Object.defineProperty(pSchema, nField.name, schemaDesc);
                        } else {
                            var eTitle = nEntity.title ? " [" + nEntity.title + "]" : "";
                            throw "Duplicated field name found: " + nField.name + " in entity " + nEntity.name + eTitle;
                        }
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
                                return nParameter.jsValue;
                            },
                            set: function (aValue) {
                                nParameter.jsValue = aValue;
                            }
                        };
                        Object.defineProperty(pParams, nParameter.name, pDesc);
                        Object.defineProperty(pParams, p, pDesc);
                    })();
                }
                Object.defineProperty(published, 'params', {value: pParams});
                // entity.params.schema.p1 syntax
                var pParamsSchema = EngineUtilsClass.unwrap(nParameters.getPublished());
                if (!pParams.schema)
                    Object.defineProperty(pParams, 'schema', {value: pParamsSchema});
                Object.defineProperty(published, 'find', {value: function (aCriteria) {
                        var keys = Object.keys(aCriteria);
                        keys = keys.sort();
                        var ordererKey = keys.join(' | ');
                        var orderer = orderers[ordererKey];
                        if (!orderer) {
                            orderer = new P.Orderer(keys);
                            orderers[ordererKey] = orderer;
                        }
                        var found = orderer.find(aCriteria);
                        return found;
                    }});
                Object.defineProperty(published, 'findByKey', {value: function (aKeyValue) {
                        var criteria = {};
                        criteria[pkFieldName] = aKeyValue;
                        var found = published.find(criteria);
                        return found.length > 0 ? found[0] : null;
                    }});
                Object.defineProperty(published, 'findById', {value: function (aKeyValue) {
                        P.Logger.warning('findById() is deprecated. Use findByKey() instead.');
                        return published.findByKey(aKeyValue);
                    }});
                var toBeDeletedMark = '-platypus-to-be-deleted-mark';
                Object.defineProperty(published, 'remove', {value: function (toBeDeleted) {
                        toBeDeleted = toBeDeleted.forEach ? toBeDeleted : [toBeDeleted];
                        toBeDeleted.forEach(function (anInstance) {
                            anInstance[toBeDeletedMark] = true;
                        });
                        for (var d = published.length - 1; d >= 0; d--) {
                            if (d[toBeDeletedMark]) {
                                published.splice(d, 1);
                            }
                        }
                        toBeDeleted.forEach(function (anInstance) {
                            delete anInstance[toBeDeletedMark];
                        });
                    }});
                Object.defineProperty(published, 'onScrolled', {
                    get: function () {
                        return _onScrolled;
                    },
                    set: function (aValue) {
                        _onScrolled = aValue;
                    }
                });
                Object.defineProperty(published, 'onInserted', {
                    get: function () {
                        return _onInserted;
                    },
                    set: function (aValue) {
                        _onInserted = aValue;
                    }
                });
                Object.defineProperty(published, 'onDeleted', {
                    get: function () {
                        return _onDeleted;
                    },
                    set: function (aValue) {
                        _onDeleted = aValue;
                    }
                });
                nEntity.setSnapshotConsumer(function (aSnapshot) {
                    Array.prototype.splice.call(published, 0, published.length);
                    if (nEntity.getElementClass()) {
                        var instanceCtor = nEntity.getElementClass();
                        for (var s = 0; s < aSnapshot.length; s++) {
                            var snapshotInstance = aSnapshot[s];
                            var accepted = new instanceCtor();
                            for (var sp in snapshotInstance) {
                                accepted[sp] = snapshotInstance[sp];
                            }
                            Array.prototype.push.call(published, accepted);
                            acceptInstance(accepted);
                        }
                    } else {
                        for (var s = 0; s < aSnapshot.length; s++) {
                            var snapshotInstance = aSnapshot[s];
                            Array.prototype.push.call(published, snapshotInstance);
                            acceptInstance(snapshotInstance);
                        }
                    }
                });
                return published;
            }
            model.createORMDefinitions();
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
