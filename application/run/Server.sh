#!/bin/sh
JRE_PATH=java
PLATYPUS_HOME=$(cd `dirname $0` && pwd)/..
MAIN_CLASS_JAR=$PLATYPUS_HOME/bin/Server.jar
$JRE_PATH -jar $MAIN_CLASS_JAR  -url jdbc:oracle:thin:@asvr:1521:adb -username trans -password trans -schema trans -log platypusServerLog.txt -loglevel INFO
