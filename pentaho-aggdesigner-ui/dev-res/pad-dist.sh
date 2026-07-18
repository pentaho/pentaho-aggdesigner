#!/bin/sh
# ******************************************************************************
#
# Pentaho
#
# Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
#
# Use of this software is governed by the Business Source License included
# in the LICENSE.TXT file.
#
# Change Date: 2030-06-15
# ******************************************************************************



# Creates Pentaho Aggregation Designer distribution
# Add -Dmaven.test.skip=true to skip unit tests
cd ..
mvn clean package javadoc:javadoc assembly:assembly -Dmaven.test.skip=true