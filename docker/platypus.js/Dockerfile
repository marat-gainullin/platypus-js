FROM ubuntu:14.04
MAINTAINER jskonst

ENV ANT_VERSION 1.9.7
ENV TOMCAT_VERSION 8.0.36
ENV ANT_HOME /usr/local/ant
ENV PATH ${PATH}:/usr/local/ant/bin

RUN DEBIAN_FRONTEND=noninteractive apt-get update && apt-get install wget git console-setup keyboard-configuration language-pack-ru -y  && \
    echo "export LC_ALL=ru_RU.UTF-8" >> /root/.bashrc && echo "export LANG=ru_RU.UTF-8" >> /root/.bashrc && echo "export LANGUAGE=ru_RU.UTF-8" >> /root/.bashrc
COPY locale /etc/default/locale
COPY installJava.sh /root/
RUN cd /root/ && ./installJava.sh && \
wget -q http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz && \
     tar -xzf apache-ant-${ANT_VERSION}-bin.tar.gz && \
     mv apache-ant-${ANT_VERSION} /usr/local/ant && \
     rm apache-ant-${ANT_VERSION}-bin.tar.gz && \
wget http://apache-mirror.rbc.ru/pub/apache/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
     tar -xzf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
     mv apache-tomcat-${TOMCAT_VERSION} /usr/local/ && \
     rm apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
wget http://search.maven.org/remotecontent?filepath=junit/junit/4.12/junit-4.12.jar -O ${ANT_HOME}/lib/junit4.jar && \
wget http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar -O ${ANT_HOME}/lib/hamcrest-core.jar
COPY apacheConf/server.xml /usr/local/apache-tomcat-${TOMCAT_VERSION}/conf/
COPY apacheConf/Catalina/localhost/p-tests.xml /usr/local/apache-tomcat-${TOMCAT_VERSION}/conf/Catalina/localhost/p-tests.xml
COPY testsConf /testsConf
COPY start.sh /start.sh
        
CMD [ "/start.sh" ]
