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
cd "%mainfolder%\login"
echo Starting loginserver...
echo Close this window to force shutdown the server.
echo.
"%JAVA_HOME%\bin\java.exe" -jar "%mainfolder%\libs\LoginServer.jar"
exit