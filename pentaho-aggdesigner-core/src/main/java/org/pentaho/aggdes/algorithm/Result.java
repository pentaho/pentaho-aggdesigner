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


package org.pentaho.aggdes.algorithm;

import java.util.List;
import java.io.PrintWriter;

import org.pentaho.aggdes.model.Aggregate;

/**
 * Recommendations produced by an aggregate table advisor job.
 */
public interface Result {
    /**
     * Returns a list of aggregate tables.
     *
     * @return list of aggregate tables
     */
    List<Aggregate> getAggregates();

    /**
     * Returns a list of cost/benefit for each aggregate.
     *
     * @return list of cost/benefit metrics
     */
    List<Algorithm.CostBenefit> getCostBenefits();

    /**
     * Prints a textual description of the result.
     *
     * @param pw Print writer
     */
    void describe(PrintWriter pw);
}

// End Result.java
