#!/bin/sh
# ${project.name}
# ${project.version}.${build.number}
# Copyright © ${project.inceptionYear} ${project.organization.name}
# Classpath is built by launcher. See ../lib/launcher.properties.

DIR_REL=`dirname $0`
cd $DIR_REL
DIR=`pwd`
cd -

. "$DIR/set-pentaho-java.sh"
setPentahoJava

"$_PENTAHO_JAVA" -jar "$DIR/lib/launcher-1.0.0.jar"