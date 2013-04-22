var Shell = WScript.CreateObject("WScript.Shell");

var CURRENT_DIR = WScript.ScriptFullName.substring(0, WScript.ScriptFullName.length-WScript.ScriptName.length);
Shell.CurrentDirectory = CURRENT_DIR;

// Main section. Don't change
var MAIN_CLASS_JAR = "../bin/Application.jar";
var MAIN_CLASS     = "com.eas.client.application.PlatypusClientApplication";

// LaF section. Change look and feel if you want
var EXT_CLASSES   = "../ext/*";
var LAF_CLASS = "com.jtattoo.plaf.fast.FastLookAndFeel";

var command = "java -cp "+MAIN_CLASS_JAR+";"+EXT_CLASSES+" "+MAIN_CLASS+" -laf "+LAF_CLASS+" -log log.txt -loglevel INFO";
Shell.Run(command, 0);