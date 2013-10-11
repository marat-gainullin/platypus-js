/*
 * @(#)gui.js	1.1 06/08/06
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * Few user interface utilities. 
 */

if (this.window === undefined) {
    this.window = null;
}

/** 
 * Swing invokeLater - invokes given function in AWT event thread
 */
Function.prototype.invokeLater = function() {
    var SwingUtilities = javax.swing.SwingUtilities;
    var func = this;
    var args = arguments;
    SwingUtilities.invokeLater(new java.lang.Runnable() {
        run: function() {
            func.apply(func, args);
        }
    });
};

/** 
 * Thread - schedules given function in the pool thread
 */
Function.prototype.invokeDelayed = function() {
    var func = this;
    var args = arguments;
    if (!args || !args.length || args.length < 1)
        throw "schedule needs at least 1 argument - timeout value.";
    var userArgs = [];
    for (var i = 1; i < args.length; i++) {
        userArgs.push(args[i]);
    }
    var cookie = com.eas.client.scripts.ScriptTimerTask.schedule(new java.lang.Runnable() {
        run: function() {
            var SwingUtilities = javax.swing.SwingUtilities;
            SwingUtilities.invokeLater(new java.lang.Runnable() {
                run: function() {
                    try {
                        func.apply(func, userArgs);
                    } catch (e) {
                        Logger.severe(e);
                    }
                }
            });
        }
    }, args[0]);
    /* HTML5 client doesn't support cancel feature and so, we don't support it too.
    return {
        cancel: function() {
            return cookie.cancel(false);
        }
    };
    */
};

/** 
 * Swing invokeAndWait - invokes given function in AWT event thread
 * and waits for it's completion
 */
Function.prototype.invokeAndWait = function() {
    var SwingUtilities = javax.swing.SwingUtilities;
    var func = this;
    var args = arguments;
    SwingUtilities.invokeAndWait(new java.lang.Runnable() {
        run: function() {
            func.apply(func, args);
        }
    });
}

/**
 * Am I running in AWT event dispatcher thread?
 */
function isEventThread() {
    var SwingUtilities = javax.swing.SwingUtilities;
    return SwingUtilities.isEventDispatchThread();
}
isEventThread.docString = "returns whether the current thread is GUI thread";

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

/**
 * Exit the process after confirmation from user 
 * 
 * @param exitCode return code to OS [optional]
 */
function exit(exitCode) {
    if (exitCode == undefined)
        exitCode = 0;
    if (confirm("Do you really want to exit?")) {
        java.lang.System.exit(exitCode);
    }
}
exit.docString = "exits jconsole";

// synonym to exit
var quit = exit;

// if echo function is not defined, define it as synonym
// for println function
if (this.echo == undefined) {
    function echo(str) {
        println(str);
    }
}

SwingConstants = javax.swing.SwingConstants;
Dimension = java.awt.Dimension;
Point = java.awt.Point;
Rectangle = java.awt.Rectangle;
BorderLayout = java.awt.BorderLayout;
BoxLayout = javax.swing.BoxLayout;
FlowLayout = java.awt.FlowLayout;
GridLayout = java.awt.GridLayout;
SpringLayout = javax.swing.SpringLayout;
AlphaComposite = java.awt.AlphaComposite;
MouseEvent = java.awt.event.MouseEvent;
KeyEvent = java.awt.event.KeyEvent;
JFileChooser = javax.swing.JFileChooser;
FileFilter = javax.swing.filechooser.FileFilter;
JDesktopPane = javax.swing.JDesktopPane;
JInternalFrame = javax.swing.JInternalFrame;
JDialog = javax.swing.JDialog;
JFrame = javax.swing.JFrame;
JPanel = javax.swing.JPanel;
JScrollPane = javax.swing.JScrollPane;
WindowConstants = javax.swing.WindowConstants;

PlotOrientation = org.jfree.chart.plot.PlotOrientation;
ChartFactory = org.jfree.chart.ChartFactory;
ChartPanel = org.jfree.chart.ChartPanel;
AbstractXYDataset = org.jfree.data.xy.AbstractXYDataset;
XYDataset = org.jfree.data.xy.XYDataset;

// forms API
Icon = com.eas.client.forms.IconResources;
Icons = Icon;
Orientation = com.eas.client.forms.api.Orientation;
VerticalPosition = com.eas.client.forms.api.VerticalPosition;
HorizontalPosition = com.eas.client.forms.api.HorizontalPosition;
Anchors = com.eas.client.forms.api.Anchors;
Font = com.eas.gui.Font;
FontStyle = com.eas.gui.FontStyle;
Cursor = com.eas.gui.Cursor;
// components
Button = com.eas.client.forms.api.components.Button;
CheckBox = com.eas.client.forms.api.components.CheckBox;
DesktopPane = com.eas.client.forms.api.components.DesktopPane;
DropDownButton = com.eas.client.forms.api.components.DropDownButton;
FormattedField = com.eas.client.forms.api.components.FormattedField;
HtmlArea = com.eas.client.forms.api.components.HtmlArea;
Label = com.eas.client.forms.api.components.Label;
PasswordField = com.eas.client.forms.api.components.PasswordField;
ProgressBar = com.eas.client.forms.api.components.ProgressBar;
RadioButton = com.eas.client.forms.api.components.RadioButton;
Slider = com.eas.client.forms.api.components.Slider;
TextArea = com.eas.client.forms.api.components.TextArea;
TextField = com.eas.client.forms.api.components.TextField;
ToggleButton = com.eas.client.forms.api.components.ToggleButton;
// model components
ModelCheckBox = com.eas.client.forms.api.components.model.ModelCheckBox;
ModelCombo = com.eas.client.forms.api.components.model.ModelCombo;
ModelDate = com.eas.client.forms.api.components.model.ModelDate;
ModelGrid = com.eas.client.forms.api.components.model.ModelGrid;
ModelImage = com.eas.client.forms.api.components.model.ModelImage;
ModelMap = com.eas.client.forms.api.components.model.ModelMap;
ModelScheme = com.eas.client.forms.api.components.model.ModelScheme;
ModelSpin = com.eas.client.forms.api.components.model.ModelSpin;
ModelFormattedField = com.eas.client.forms.api.components.model.ModelFormattedField;
ModelTextArea = com.eas.client.forms.api.components.model.ModelTextArea;
// containers
AnchorsPane = com.eas.client.forms.api.containers.AnchorsPane;
BorderPane = com.eas.client.forms.api.containers.BorderPane;
BoxPane = com.eas.client.forms.api.containers.BoxPane;
ButtonGroup = com.eas.client.forms.api.containers.ButtonGroup;
CardPane = com.eas.client.forms.api.containers.CardPane;
FlowPane = com.eas.client.forms.api.containers.FlowPane;
GridPane = com.eas.client.forms.api.containers.GridPane;
ScrollPane = com.eas.client.forms.api.containers.ScrollPane;
SplitPane = com.eas.client.forms.api.containers.SplitPane;
TabbedPane = com.eas.client.forms.api.containers.TabbedPane;
ToolBar = com.eas.client.forms.api.containers.ToolBar;
AbsolutePane = com.eas.client.forms.api.containers.AbsolutePane;
// menu
CheckMenuItem = com.eas.client.forms.api.menu.CheckMenuItem;
Menu = com.eas.client.forms.api.menu.Menu;
MenuBar = com.eas.client.forms.api.menu.MenuBar;
MenuItem = com.eas.client.forms.api.menu.MenuItem;
PopupMenu = com.eas.client.forms.api.menu.PopupMenu;
RadioMenuItem = com.eas.client.forms.api.menu.RadioMenuItem;

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
}

Object.defineProperty(Form, "onChange", {
    get: function() {
        return com.eas.client.forms.FormRunner.getOnChange();
    },
    set: function(aValue) {
        com.eas.client.forms.FormRunner.setOnChange(aValue);
    }
});