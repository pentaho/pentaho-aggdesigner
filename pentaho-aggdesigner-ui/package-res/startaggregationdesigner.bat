@echo off
REM ${project.name}
REM ${project.version}.${build.number}
REM Copyright © ${project.inceptionYear} ${project.organization.name}
REM Classpath is built by launcher. See ..\lib\launcher.properties.

setlocal

cd /D %~dp0

set PENTAHO_JAVA=java
call "%~dp0set-pentaho-env.bat"

"%_PENTAHO_JAVA%" -jar "%~dp0lib\launcher-1.0.0.jar"