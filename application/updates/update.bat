@echo off
set BASEDIR=%~dp0
set PLATYPUS_HOME=%BASEDIR%..
set UPDATER_PATH=%PLATYPUS_HOME%\lib\own\Updater.jar
set TMP_UPDATE_NAME=%PLATYPUS_HOME%\app.zip
set MAIN_CLASS=com.eas.client.updater.Updater
set LAF_CLASS=com.jtattoo.plaf.fast.FastLookAndFeel
set URL_CONFIG=http://platypus-platform.org/platform/updates/5.0/client/Release/version.xml 
set URL_UPDATE=http://platypus-platform.org/platform/updates/5.0/client/Release/application.zip 
set CONFIG_NAME_PATH=%BASEDIR%version.xml
set EXT_CLASSES=%PLATYPUS_HOME%\ext\*

javaw.exe -cp "%UPDATER_PATH%";"%EXT_CLASSES%" %MAIN_CLASS% newversion -laf %LAF_CLASS% -curl %URL_CONFIG% -uurl %URL_UPDATE% -cname "%CONFIG_NAME_PATH%" -uname "%TMP_UPDATE_NAME%" -path "%PLATYPUS_HOME%/"
if %errorLevel% EQU 10 javaw.exe -cp "%UPDATER_PATH%";"%EXT_CLASSES%" %MAIN_CLASS% update -laf %LAF_CLASS% -curl %URL_CONFIG% -uurl %URL_UPDATE% -cname "%CONFIG_NAME_PATH%" -uname "%TMP_UPDATE_NAME%" -path "%PLATYPUS_HOME%/"
