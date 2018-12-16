@echo off
set mainfolder=%CD%
set JAVA_HOME=%mainfolder%\java10

:checking_java
if not exist "%JAVA_HOME%\lib\modules" goto install_java
goto start

:install_java
echo Extracting java modules file...
echo.
cd "%mainfolder%\java10\lib"
..\..\tools\7za.exe e -y -spf modules.7z
goto checking_java

:start
cls
cd "%mainfolder%\game"
echo Starting gameserver...
echo Close this window to force shutdown the server.
echo.
"%JAVA_HOME%\bin\java.exe" -jar -server -Dfile.encoding=UTF-8 -Djava.util.logging.manager=com.l2jmobius.log.L2LogManager -Xnoclassgc -XX:+CMSParallelRemarkEnabled -XX:SurvivorRatio=8 -Xmx4g -Xms2g -Xmn1g "%mainfolder%\libs\GameServer.jar"
exit