#!/bin/sh

# @project.name@
# @project.version@.@build.number@
# Copyright © 2013 @project.organization.name@
# Classpath is built by launcher. See ../lib/launcher.properties.

DIR_REL=`dirname $0`
cd $DIR_REL
DIR=`pwd`
cd -

. "$DIR/set-pentaho-env.sh"
setPentahoEnv

"$_PENTAHO_JAVA" -jar "$DIR/lib/pentaho-application-launcher-@dependency.pentaho-launcher.revision@.jar"