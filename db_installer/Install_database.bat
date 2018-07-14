@echo off
set mainfolder=%CD%
set JAVA_HOME=%mainfolder%\..\Java

echo Starting loginserver database installer...
echo.
"%JAVA_HOME%\bin\java.exe" -jar Database_Installer_LS.jar
echo.
echo Starting gameserver database installer...
echo.
"%JAVA_HOME%\bin\java.exe" -jar Database_Installer_GS.jar