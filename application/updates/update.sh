#!/bin/bash
JRE_PATH=java
PLATYPUS_HOME=$(cd `dirname $0` && pwd)/../
UPDATER_PATH=$PLATYPUS_HOME/lib/own/Updater.jar
TMP_UPDATE_NAME=$PLATYPUS_HOME/app.zip
MAIN_CLASS=com.eas.client.updater.Updater
LAF_CLASS=de.muntjak.tinylookandfeel.TinyLookAndFeel
configFile="update.ini"
urlConfigRegEx="URLConfig\s*=\s*(.*)"
urlUpdateRegEx="URLUpdate\s*=\s*(.*)"
configNameRegEx="ConfigFileName\s*=\s*(.*)"
URL_CONFIG_DEFAULT=http://research.office.altsoft.biz/platypus/client/updates/NightlyBuildNewUi/version.xml
URL_CONFIG=$URL_CONFIG_DEFAULT
URL_UPDATE_DEFAULT=http://research.office.altsoft.biz/platypus/client/updates/NightlyBuildNewUi/application.zip
URL_UPDATE=$URL_UPDATE_DEFAULT
CONFIG_NAME_DEFAULT=version.xml
#CONFIG_NAME=$PLATYPUS_HOME/updates/version.xml
CONFIG_NAME=$CONFIG_NAME_DEFAULT

if [ -f $configFile ]; then
	while read line
	do
		name=$line
		if [[ $line =~ $urlConfigRegEx ]]; then
			URL_CONFIG="${BASH_REMATCH[1]}"
		fi
		if [[ $line =~ $urlUpdateRegEx ]]; then
			URL_UPDATE="${BASH_REMATCH[1]}"
		fi
		if [[ $line =~ $configNameRegEx ]]; then
			CONFIG_NAME="${BASH_REMATCH[1]}"
		fi
	done < $configFile
fi

$JRE_PATH -cp $UPDATER_PATH $MAIN_CLASS newversion -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname $CONFIG_NAME -uname $TMP_UPDATE_NAME -path $PLATYPUS_HOME 
if [ $? -eq 10 ]; then 
 $JRE_PATH -cp $UPDATER_PATH $MAIN_CLASS update -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname $CONFIG_NAME -uname $TMP_UPDATE_NAME -path $PLATYPUS_HOME
 if [ -f $PLATYPUS_HOME/lib/own/Updater-new.jar ]; then
  rm $PLATYPUS_HOME/lib/own/Updater.jar 
  mv -f $PLATYPUS_HOME/lib/own/Updater-new.jar $PLATYPUS_HOME/lib/own/Updater.jar;
 fi
fi	
