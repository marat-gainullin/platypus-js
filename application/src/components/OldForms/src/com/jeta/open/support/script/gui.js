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
}

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
        var res = save? dialog.showSaveDialog(window):
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
        if (title == undefined) {
            title = "Choose Color";
        }
        if (color == undefined) {
            color = java.awt.Color.BLACK;
        }
        var chooser = new javax.swing.JColorChooser();
        var res = chooser.showDialog(window, title, color);
        result = res ? res : null;
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
        if (msg === undefined) msg = "undefined";
        if (msg === null) msg = "null";
        if (title == undefined) title = msg;
        if (msgType == undefined) type = JOptionPane.INFORMATION_MESSAGE;
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
        if (answer == undefined) answer = "";
        if(title == undefined) title = "Input";
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
        if (title == undefined) title = msg;
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
    if (exitCode == undefined) exitCode = 0;
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

Font = java.awt.Font;
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

Icons = com.eas.resources.images.IconCache;

ok = "ok";
cancel = "cancel";

/**
 * Returns JDialog, JFrame, or JInternalFrame corresponding to the given
 * FormComponent, which is usually the value of "form" variable in form script.
 *
 * It can be used to control the appearance of frame.
 *
 * @param formComponent a FormComponent instance.
 */
function getWindow(formComponent)
{
	var internalFrameClass = java.lang.Class.forName("javax.swing.JInternalFrame"),
	    dialogClass = java.lang.Class.forName("javax.swing.JDialog"),
	    frameClass = java.lang.Class.forName("javax.swing.JFrame");
	var parent = formComponent.parent;
	while (parent != null)
	{
		var isWindow = false;
		if (internalFrameClass.isInstance(parent) || frameClass.isInstance(parent) || dialogClass.isInstance(parent))
			return parent;
		parent = parent.parent;
	}
	return null;
}
getWindow.docString = 'Returns JFrame, JDialog, or JInternalFrame for a FormComponent.';



// Выбиралка entitек из метаданных.
function _EntitySelector_selectEntity(parentComponent, title)
{
	const CLASS = com.eas.client.workspace.selectors.MtdEntitySelector;
	var s = CLASS.selectMtdEntity(this.selection, this.ET_ALL, CLASS.getFirstParentWindow(parentComponent), title);
	if (s != null && s != undefined)
		this.selection = s;
	return s
}

function _EntitySelector_selectEntityFiltered(allowedTypes, parentComponent, title)
{
	const CLASS = com.eas.client.workspace.selectors.MtdEntitySelector;
	var typesVector = null;
	if (allowedTypes instanceof Array)
	{
		typesVector = new java.util.ArrayList();
		for (var i = 0; i < allowedTypes.length; i++)
			typesVector.add(allowedTypes[i]);
	}
	else if (allowedTypes instanceof java.util.ArrayList)
		typesVector = allowedTypes;
	else
		throw "Аргумент allowedTypes должен быть либо Array, либо java.util.ArrayList.";
	var s = CLASS.selectMtdEntity(this.selection, typesVector, CLASS.getFirstParentWindow(parentComponent), title);
	if (s != null && s != undefined)
		this.selection = s;
	return s
}

function EntitySelector()
{
	this.ET_CONNECTION = new java.lang.Integer(com.eas.client.ClientConstants.ET_CONNECTION);
	this.ET_COMPONENT = new java.lang.Integer(com.eas.client.ClientConstants.ET_COMPONENT);
	this.ET_FORM = new java.lang.Integer(com.eas.client.ClientConstants.ET_FORM);
	this.ET_REPORT = new java.lang.Integer(com.eas.client.ClientConstants.ET_REPORT);
	this.ET_QUERY = new java.lang.Integer(com.eas.client.ClientConstants.ET_QUERY);
	this.ET_DB_SCHEME = new java.lang.Integer(com.eas.client.ClientConstants.ET_DB_SCHEME);
	this.ET_FOLDER = new java.lang.Integer(com.eas.client.ClientConstants.ET_FOLDER);
	this.ET_ALL = new java.util.ArrayList();
	this.ET_ALL.add(this.ET_CONNECTION);
	this.ET_ALL.add(this.ET_COMPONENT);
	this.ET_ALL.add(this.ET_FORM);
	this.ET_ALL.add(this.ET_REPORT);
	this.ET_ALL.add(this.ET_QUERY);
	this.ET_ALL.add(this.ET_DB_SCHEME);
	this.ET_ALL.add(this.ET_FOLDER);
	this.selection = null;
	this.selectEntity = _EntitySelector_selectEntity;
	this.selectEntityFiltered = _EntitySelector_selectEntityFiltered;
}

