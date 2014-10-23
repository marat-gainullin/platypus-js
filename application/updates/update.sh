#!/bin/bash
JRE_PATH=java
BASEDIR="`dirname "$0"`/.."
PLATYPUS_HOME=`(cd "$BASEDIR"; pwd)`
UPDATER_PATH=${PLATYPUS_HOME}/lib/own/Updater.jar
TMP_UPDATE_NAME=${PLATYPUS_HOME}/app.zip
MAIN_CLASS=com.eas.client.updater.Updater
LAF_CLASS=com.jtattoo.plaf.fast.FastLookAndFeel
configFile="update.ini"
urlConfigRegEx="URLConfig\s*=\s*([\S]+)"
urlUpdateRegEx="URLUpdate\s*=\s*([\S]+)"
configNameRegEx="ConfigFileName\s*=\s*([\S]+)"
URL_CONFIG_DEFAULT=http://research.office.altsoft.biz/platypus/updates/5.0/client/NightlyBuild/version.xml
URL_CONFIG=$URL_CONFIG_DEFAULT
URL_UPDATE_DEFAULT=http://research.office.altsoft.biz/platypus/updates/5.0/client/NightlyBuild/application.zip
URL_UPDATE=$URL_UPDATE_DEFAULT
CONFIG_NAME_DEFAULT=version.xml
CONFIG_NAME_PATH=${PLATYPUS_HOME}/updates/
CONFIG_NAME=$CONFIG_NAME_DEFAULT
EXT_CLASSES=${PLATYPUS_HOME}/ext/*

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
			CONFIG_NAME=${BASH_REMATCH[1]}
		fi
	done < $configFile
fi
CONFIG_NAME="${CONFIG_NAME_PATH}${CONFIG_NAME}"
$JRE_PATH -cp "$UPDATER_PATH":"$EXT_CLASSES" $MAIN_CLASS newversion -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname "$CONFIG_NAME" -uname "$TMP_UPDATE_NAME" -path "$PLATYPUS_HOME/" 
if [ $? -eq 10 ]; then 
$JRE_PATH -cp "$UPDATER_PATH":"$EXT_CLASSES" $MAIN_CLASS update -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname "$CONFIG_NAME" -uname "$TMP_UPDATE_NAME" -path "${PLATYPUS_HOME}/"
 if [ -f "${PLATYPUS_HOME}/lib/own/Updater-new.jar" ]; then
  rm "${PLATYPUS_HOME}/lib/own/Updater.jar" 
  mv -f "${PLATYPUS_HOME}/lib/own/Updater-new.jar" "${PLATYPUS_HOME}/lib/own/Updater.jar";
 fi
fi	
