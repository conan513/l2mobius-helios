@echo off
set mainfolder="%CD%"
set JAVA_HOME="%mainfolder%\java10"

cd "%mainfolder%\db_installer"
echo Starting the login server database installer...
echo.
"%JAVA_HOME%\bin\java.exe" -jar Database_Installer_LS.jar
echo.
echo Starting the game server database installer...
echo.
"%JAVA_HOME%\bin\java.exe" -jar Database_Installer_GS.jar