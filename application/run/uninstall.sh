#!/bin/sh
JRE_PATH=java
PLATYPUS_HOME=$(cd `dirname $0` && pwd)/..
$JRE_PATH -jar $PLATYPUS_HOME/Uninstaller/uninstaller.jar
