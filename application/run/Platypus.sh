#!/bin/sh
JRE_PATH=java
PLATYPUS_HOME=$(cd `dirname $0` && pwd)/..
MAIN_CLASS_JAR=$PLATYPUS_HOME/bin/Application.jar
MAIN_CLASS=com.eas.client.application.PlatypusClientApplication
EXT_CLASSES=$PLATYPUS_HOME/ext/*
LAF_CLASS=com.jtattoo.plaf.fast.FastLookAndFeel
$JRE_PATH -cp $MAIN_CLASS_JAR:$EXT_CLASSES $MAIN_CLASS -laf $LAF_CLASS $1 $2
