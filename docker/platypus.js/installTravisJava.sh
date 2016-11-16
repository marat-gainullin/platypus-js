#!/bin/sh
DIR=`dirname $0`
JAVA_NAME='jdk.tar.gz'
JAVA_RESULT_NAME='jdk1.8.0_92'
JAVA_DOWNLOAD_NAME='8u92-b14/jdk-8u92-linux-x64.tar.gz'
wget -O $DIR/$JAVA_NAME --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie"  http://download.oracle.com/otn-pub/java/jdk/$JAVA_DOWNLOAD_NAME && \
echo Expanding jdk tarball... && \ 
tar -xzf $DIR/$JAVA_NAME -C $DIR && \
mkdir -p $DIR/jvm/ && \
mv $DIR/$JAVA_RESULT_NAME $DIR/jvm/$JAVA_RESULT_NAME && \
rm $DIR/$JAVA_NAME && \
update-alternatives --install /usr/bin/javac javac $DIR/jvm/$JAVA_RESULT_NAME/bin/javac 1 && \
update-alternatives --install /usr/bin/java java $DIR/jvm/$JAVA_RESULT_NAME/bin/java 1 && \
update-alternatives --install /usr/bin/javaws javaws $DIR/jvm/$JAVA_RESULT_NAME/bin/javaws 1 && \
# cp $DIR/jvm/jdk1.8.0_92/jre/lib/security/cacerts /root/.keystore
