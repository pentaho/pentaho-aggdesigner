@echo off

REM This program is free software; you can redistribute it and/or modify it under the
REM terms of the GNU General Public License, version 2 as published by the Free Software
REM Foundation.
REM
REM You should have received a copy of the GNU General Public License along with this
REM program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
REM or from the Free Software Foundation, Inc.,
REM 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
REM
REM This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
REM without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
REM See the GNU General Public License for more details.
REM
REM
REM Copyright 2008 - 2017 Hitachi Vantara.  All rights reserved.

REM ${project.name}
REM ${project.version}
REM Copyright © 2013 ${project.organization.name}
REM Classpath is built by launcher. See ..\lib\launcher.properties.

setlocal
cd /D %~dp0
set PENTAHO_JAVA=java
call "%~dp0set-pentaho-env.bat"

"%_PENTAHO_JAVA%" -jar "%~dp0lib\pentaho-application-launcher-${dependency.pentaho-launcher.revision}.jar"
