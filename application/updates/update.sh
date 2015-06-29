#!/bin/bash
IS_SILENT="false"
while [ 1 ]; do
	if [ "$1" = "-silent" ] ; then
		shift
		IS_SILENT="$1"
	elif [ "$1" = "newversion" ] ; then
		MODE="newversion"
	elif [ "$1" = "update" ] ; then
		MODE="update"
	elif [ -z "$1" ] ; then
		break #no morekeys
	fi
	shift
done
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
URL_CONFIG_DEFAULT=http://platypus-platform.org/platform/updates/5.0/client/Release/version.xml
URL_CONFIG=$URL_CONFIG_DEFAULT
URL_UPDATE_DEFAULT=http://platypus-platform.org/platform/updates/5.0/client/Release/application.zip
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
if [ -n "$MODE" ] ; then
    $JRE_PATH -cp "$UPDATER_PATH":"$EXT_CLASSES" $MAIN_CLASS $MODE -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname "$CONFIG_NAME" -uname "$TMP_UPDATE_NAME" -silent $IS_SILENT -path "${PLATYPUS_HOME}/"
   exit $? 
else 
     $JRE_PATH -cp "$UPDATER_PATH":"$EXT_CLASSES" $MAIN_CLASS newversion -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname "$CONFIG_NAME" -uname "$TMP_UPDATE_NAME" -silent $IS_SILENT -path "$PLATYPUS_HOME/" 
    if [ $? -eq 10 ]; then 
        $JRE_PATH -cp "$UPDATER_PATH":"$EXT_CLASSES" $MAIN_CLASS update -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname "$CONFIG_NAME" -uname "$TMP_UPDATE_NAME" -silent $IS_SILENT -path "${PLATYPUS_HOME}/"
    fi
fi	
