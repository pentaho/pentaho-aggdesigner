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


package org.pentaho.aggdes.model;


/**
 * A quantity of interest in a star schema.
 *
 * @author jhyde
 * @version $Id: Measure.java 61 2008-03-17 05:34:55Z jhyde $
 * @since Mar 13, 2008
 */
public interface Measure extends Attribute {
    /**
     * Returns whether this measure is a distinct aggregation, such
     * as <code>distinct-count</code>.
     *
     * <p>Distinct aggregations cannot be rolled up, in general, so more
     * aggregate tables are required for the same performance gain.
     *
     * @return whether the measure is distinct
     */
    boolean isDistinct();
}

// End Measure.java
