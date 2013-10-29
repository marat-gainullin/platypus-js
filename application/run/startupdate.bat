@set CUR_DIR=%~dp0..
@set LAF_CP="com.jtattoo.plaf.fast.FastLookAndFeel"
@set LAF_JAR="%CUR_DIR%/lib/thirdparty/laf/JTattoo.jar"
@set MAIN_CP="com.eas.client.updater.Updater"
@set UPDATER_PATH="%CUR_DIR%/lib/own/Updater.jar"
@set URL_CONFIG=http://research.office.altsoft.biz/platypus/client/updates/NightlyBuild/update.xml
@set URL_UPDATE=http://research.office.altsoft.biz/platypus/client/updates/NightlyBuild/application.zip
@set CONFIG_NAME="%CUR_DIR%/run/update.xml"
@set TMP_UPDATE_NAME="%CUR_DIR%/app.zip"
@set PLATYPUS_HOME="%CUR_DIR%/"
@set RUN_COMMAND=CScript \"%CUR_DIR%/run/Platypus.js\"

java -cp %UPDATER_PATH%;%LAF_JAR% %MAIN_CP% -laf %LAF_CP% -curl %URL_CONFIG% -uurl %URL_UPDATE% -cname %CONFIG_NAME% -uname %TMP_UPDATE_NAME% -path %PLATYPUS_HOME% -wrun %RUN_COMMAND%
