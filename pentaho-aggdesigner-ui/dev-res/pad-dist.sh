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
# Change Date: 2028-08-13
# ******************************************************************************


# Creates Pentaho Aggregation Designer distribution
# Add -Dmaven.test.skip=true to skip unit tests
cd ..
mvn clean package javadoc:javadoc assembly:assembly -Dmaven.test.skip=true