@echo off
set mainfolder=%CD%
set JAVA_HOME=%mainfolder%\Java

cd "%mainfolder%\game"
echo Starting gameserver...
echo.
start ..\Java\bin\javaw.exe -jar -server -Dfile.encoding=UTF-8 -Djava.util.logging.manager=com.l2jmobius.log.L2LogManager -XX:+AggressiveOpts -Xnoclassgc -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseParNewGC -XX:SurvivorRatio=8 -Xmx4g -Xms2g -Xmn1g ../libs/GameServer.jar
exit