/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.aggdes.test.util;

import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.model.Aggregate;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TestResult implements Result {
  List<Aggregate> agglist = new ArrayList<>();

  public TestResult() {
  }

  public void addAggregate( Aggregate aggregate ) {
    agglist.add( aggregate );
  }

  public void describe( PrintWriter pw ) {
    // TODO Auto-generated method stub
  }

  public List<Aggregate> getAggregates() {
    return agglist;
  }

  public List<Algorithm.CostBenefit> getCostBenefits() {
    throw new UnsupportedOperationException();
  }
}
