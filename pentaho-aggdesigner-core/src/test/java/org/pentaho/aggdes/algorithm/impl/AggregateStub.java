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


package org.pentaho.aggdes.algorithm.impl;

import java.util.List;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;

public class AggregateStub implements Aggregate {

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
        return null;
    }

    public String getCandidateTableName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Measure> getMeasures() {
        // TODO Auto-generated method stub
        return null;
    }

}
