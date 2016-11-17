#!/bin/bash
# build platypus
TOMCAT_VERSION=8.0.39
ant -Dlibs.javaee-web-api-7.0.classpath=/usr/local/apache-tomcat-$TOMCAT_VERSION/lib/servlet-api.jar:/usr/local/apache-tomcat-$TOMCAT_VERSION/lib/websocket-api.jar -f /platypus/application/src/group/build.xml clean compile

export LC_ALL=ru_RU.UTF-8
export LANG=ru_RU.UTF-8
export LANGUAGE=ru_RU.UTF-8
# copy compiled api
cp -r /testsConf/* /platypusTests/
mkdir -p /platypusTests/WEB-INF/classes
mkdir -p /platypusTests/WEB-INF/lib
cp -r /platypus/application/api/* /platypusTests/WEB-INF/classes/
find /platypus/application/bin /platypus/application/ext /platypus/application/lib -type f -exec cp {} /platypusTests/WEB-INF/lib/ \;

# start tomcat server
/usr/local/apache-tomcat-$TOMCAT_VERSION/bin/startup.sh

Xvfb :1 -screen 0 640x480x24 -fbdir /var/tmp &
# run TSA server
java -D.level=INFO -Dhandlers=java.util.logging.ConsoleHandler -Djava.util.logging.ConsoleHandler.level=INFO -Djava.util.logging.ConsoleHandler.formatter=com.eas.util.logging.PlatypusFormatter -Djava.util.logging.config.class=com.eas.util.logging.LoggersConfig -cp /platypus/application/api:/platypus/application/bin/Server.jar com.eas.server.ServerMain -datasource eas -dburl jdbc:mysql://testDb:3306/eas -dbuser test -dbpassword test -dbschema EAS -datasource easHR -dburl jdbc:mysql://testDb:3306/hr -dbuser test -dbpassword test -dbschema hr -default-datasource eas -source-path app -global-api -appelement start.js -url file:///platypusTests/ -iface 0.0.0.0:8500 -protocols 8500:platypus
