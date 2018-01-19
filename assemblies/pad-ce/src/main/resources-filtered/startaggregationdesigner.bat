@echo off
REM ${project.name}
REM ${project.version}
REM Copyright © 2013 ${project.organization.name}
REM Classpath is built by launcher. See ..\lib\launcher.properties.

setlocal
cd /D %~dp0
set PENTAHO_JAVA=java
call "%~dp0set-pentaho-env.bat"

"%_PENTAHO_JAVA%" -jar "%~dp0lib\pentaho-application-launcher-${dependency.pentaho-launcher.revision}.jar"
