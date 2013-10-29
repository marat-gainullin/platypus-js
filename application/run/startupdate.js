var LAF_CP="com.jtattoo.plaf.fast.FastLookAndFeel";
var LAF_JAR="../lib/thirdparty/laf/JTattoo.jar";
var MAIN_CP="com.eas.client.updater.Updater";
var UPDATER_PATH="../lib/own/Updater.jar";
var URL_CONFIG="http://research.office.altsoft.biz/platypus/client/updates/NightlyBuild/update.xml";
var URL_UPDATE="http://research.office.altsoft.biz/platypus/client/updates/NightlyBuild/application.zip";
var CONFIG_NAME="update.xml";
var TMP_UPDATE_NAME="../app.zip";
var PLATYPUS_HOME="../";
var RUN_COMMAND="CScript Platypus.js";
  
function findUpd (value) {
  var regV = /\/Updater\.jar;/i;     // шаблон
  return value.match(regV);  // поиск шаблона
}

function runUpdate(typeRun) {

  var command = "java -cp "+UPDATER_PATH+";"+LAF_JAR+" "+MAIN_CP+" "+typeRun+" -laf "+LAF_CP+" -curl "+URL_CONFIG+" -uurl "+URL_UPDATE+" -cname "+CONFIG_NAME+" -uname "
              +TMP_UPDATE_NAME+" -path "+PLATYPUS_HOME;

  return Shell.Run(command, 0, true);
}

function getWMI() {
  try  {
    //Соединяемся с WMI
    WMI=GetObject("winMgmts:");
  } catch (e) {  //Обрабатываем возможные ошибки
    if (e != 0) {
      //Выводим сообщение об ошибке
      Mess="Ошибка при соединении с WMI";
      WshShell.Popup(Mess,0,"Запуск процесса обновления",vbCritical);
      //Выходим из сценария
      WScript.Quit();
    }
  }
  return WMI;
}

function findUpdaterProcess (wmi) {
  var retval = null;
  processes = new Enumerator(WMI.ExecQuery("SELECT * FROM Win32_Process where Name='java.exe'"));  
  while (!processes.atEnd()) {
    retval = findUpd(processes.item().CommandLine);
	if (retval) {
	   return retval;
	}
    processes.moveNext();
  }
  return retval;
}

function replaceUpdaterFile() {
  var newFile =  "../lib/own/Updater-new.jar";
  var oldFile =  "../lib/own/Updater.jar";
  var fso = WScript.CreateObject("Scripting.FileSystemObject");
  if (fso.FileExists(newFile)) {
    fso.DeleteFile(oldFile,1);
	fso.MoveFile(newFile, oldFile);
  }
}

var Shell = WScript.CreateObject("WScript.Shell");

var CURRENT_DIR = WScript.ScriptFullName.substring(0, WScript.ScriptFullName.length-WScript.ScriptName.length);
Shell.CurrentDirectory = CURRENT_DIR;

replaceUpdaterFile(); //заменяем файл Updater.jar на новый если таковой имеется

var WMI = getWMI();

var retval = runUpdate("newversion");
if (retval == 1) {
  var objShell = WScript.CreateObject("Shell.Application");
  if (!findUpdaterProcess(WMI)) {
    objShell.ShellExecute("java", "-cp "+UPDATER_PATH+";"+LAF_JAR+" "+MAIN_CP+" update -laf "+LAF_CP+" -curl "+URL_CONFIG+" -uurl "+URL_UPDATE+" -cname "+CONFIG_NAME+" -uname "
                          +TMP_UPDATE_NAME+" -path "+PLATYPUS_HOME+" -wrun "+RUN_COMMAND, "", "runas", 0);
    WScript.sleep(500);
	if (!findUpdaterProcess(WMI)) {
	  Shell.Run("CScript.exe Platypus.js",0); 
	}
  } else {
    WshShell.Popup("Процесс обновления не может быть запушен т.к параллельно работает другое обновление. Попробуйте еще раз.",0,"Запуск процесса обновления",vbCritical);
	Shell.Run("CScript.exe Platypus.js",0);
  }
} else {
   Shell.Run("CScript.exe Platypus.js",0);
}
