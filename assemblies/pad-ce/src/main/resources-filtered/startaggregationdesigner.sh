#!/bin/sh
# ******************************************************************************
#
# Pentaho
#
# Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
#
# Use of this software is governed by the Business Source License included
# in the LICENSE.TXT file.
#
# Change Date: 2029-07-20
# ******************************************************************************


# ${project.name}
# ${project.version}
# Copyright © 2008 - ${copyright.year} ${project.organization.name}
# Classpath is built by launcher. See ../lib/launcher.properties.

DIR=$( cd "$( dirname "$0" )" && pwd )

. "$DIR/set-pentaho-env.sh"
setPentahoEnv

JAVA_ADD_OPENS=
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.lang=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.io=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.lang.reflect=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.net=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.security=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.util=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.file=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.ftp=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.reflect.misc=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.management/javax.management=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.management/javax.management.openmbean=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.naming/com.sun.jndi.ldap=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.math=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.nio=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED"

"$_PENTAHO_JAVA" $JAVA_ADD_OPENS -jar "$DIR/lib/pentaho-application-launcher-${pentaho-launcher.version}.jar"
