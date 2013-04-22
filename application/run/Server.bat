@rem Main section. Don't change

@set CURRENT_DIR=%~dp0/..
@set MAIN_CLASS_JAR=%CURRENT_DIR%/bin/Server.jar

@java -jar "%MAIN_CLASS_JAR%" -url jdbc:oracle:thin:@asvr:1521/adb -schema transdemo -password transdemo -username transdemo -log serverlog.txt -loglevel FINEST -iface 0.0.0.0:8500,0.0.0.0:8601 -protocols 8500:platypus,8601:asc5 -acceptorTask sensors:129179828114036 -backgroundTask documents:129222527165628