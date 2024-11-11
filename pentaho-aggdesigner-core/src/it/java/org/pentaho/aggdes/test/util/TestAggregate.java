/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.test.util;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;

import java.util.ArrayList;
import java.util.List;

public class TestAggregate implements Aggregate {
  List<Attribute> attributeList = new ArrayList<>();
  List<Measure> measureList = new ArrayList<>();
  String candidateTableName;

  public TestAggregate() {
  }

  public void addAttribute( Attribute attrib ) {
    attributeList.add( attrib );
  }

  public void addMeasure( Measure measure ) {
    measureList.add( measure );
  }

  public double estimateRowCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  public double estimateSpace() {
    // TODO Auto-generated method stub
    return 0;
  }

  public List<Attribute> getAttributes() {
    // TODO Auto-generated method stub
    return attributeList;
  }

  public List<Measure> getMeasures() {
    // TODO Auto-generated method stub
    return measureList;
  }

  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getCandidateTableName() {
    return candidateTableName;
  }

  public void setCandidateTableName( String candidateTableName ) {
    this.candidateTableName = candidateTableName;
  }
}
