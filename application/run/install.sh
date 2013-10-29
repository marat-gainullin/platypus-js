#!/bin/sh
JRE_PATH=java
BASEDIR="`dirname $0`"
PLATYPUS_HOME="`(cd \"$BASEDIR\"; pwd)`"
$JRE_PATH -jar "$PLATYPUS_HOME"/PlatypusInstall.jar
