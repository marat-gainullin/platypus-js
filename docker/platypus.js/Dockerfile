FROM ubuntu:14.04
MAINTAINER jskonst

ENV ANT_VERSION 1.9.7
ENV TOMCAT_VERSION 8.0.36
RUN DEBIAN_FRONTEND=noninteractive apt-get update && apt-get install wget git -y
COPY installJava.sh /root/
RUN cd /root/ && ./installJava.sh && \
wget -q http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz && \
     tar -xzf apache-ant-${ANT_VERSION}-bin.tar.gz && \
     mv apache-ant-${ANT_VERSION} /usr/local/ant && \
     rm apache-ant-${ANT_VERSION}-bin.tar.gz && \
wget http://apache-mirror.rbc.ru/pub/apache/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
     tar -xzf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
     mv apache-tomcat-${TOMCAT_VERSION} /usr/local/ && \
     rm apache-tomcat-${TOMCAT_VERSION}.tar.gz 

ENV ANT_HOME /usr/local/ant
ENV PATH ${PATH}:/usr/local/ant/bin
    
        
# RUN apt-get update && apt-get install git ant -y
# RUN DEBIAN_FRONTEND=noninteractive apt-get install mysql-server -y && rm -rf /var/lib/mysql
# CMD [ "/start.sh" ]
