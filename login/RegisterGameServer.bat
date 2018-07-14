@echo off
set mainfolder=%CD%
set JAVA_HOME=%mainfolder%\..\Java
title Register Game Server
color 17
%JAVA_HOME%\bin\java -version:1.8 -Djava.util.logging.config.file=console.cfg -cp ./../libs/* com.l2jmobius.tools.gsregistering.BaseGameServerRegister -c
pause