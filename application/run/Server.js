var Shell = WScript.CreateObject("WScript.Shell");

var CURRENT_DIR = WScript.ScriptFullName.substring(0, WScript.ScriptFullName.length-WScript.ScriptName.length);
Shell.CurrentDirectory = CURRENT_DIR+"\..";

// Main section. Don't change
var MAIN_CLASS_JAR = "bin/Server.jar";

var command = "java -jar "+MAIN_CLASS_JAR+" -url jdbc:oracle:thin:@asvr:1521:adb -username transdemo -password transdemo -schema transdemo -log serverlog.txt -loglevel FINEST -iface 0.0.0.0:8500,0.0.0.0:8601 -protocols 8500:platypus,8601:asc5 -acceptorTask sensors:129179828114036 -backgroundTask documents:129222527165628";
Shell.Run(command, 0);