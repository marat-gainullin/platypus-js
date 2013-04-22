var Shell = WScript.CreateObject("WScript.Shell");
var CURRENT_DIR = WScript.ScriptFullName.substring(0, WScript.ScriptFullName.length-WScript.ScriptName.length);
Shell.CurrentDirectory = CURRENT_DIR+"\..";

var command = "java -jar Uninstaller/uninstaller.jar";
Shell.Run(command, 0);