@echo off
set mainfolder=%CD%
set JAVA_HOME=%mainfolder%\Java

cd "%mainfolder%\login"
echo Starting loginserver...
echo.
start %JAVA_HOME%\bin\javaw.exe -jar ../libs/LoginServer.jar
exit