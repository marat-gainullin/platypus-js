@rem Main section. Don't change

@set CURRENT_DIR=%~dp0..

@set MAIN_CLASS_JAR="%CURRENT_DIR%/bin/Application.jar"
@set MAIN_CLASS=com.eas.client.application.PlatypusClientApplication

@rem LaF section. Change look and feel if you want
@set EXT_CLASSES="%CURRENT_DIR%/ext/*";
@set LAF_CLASS=com.jtattoo.plaf.fast.FastLookAndFeel

@java -cp %MAIN_CLASS_JAR%;%EXT_CLASSES% %MAIN_CLASS% -laf %LAF_CLASS% %1 %2 
