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

/**
 * Represents the cost of materializing an
 * {@link org.pentaho.aggdes.model.Aggregate}.
 *
 * @author jhyde
 * @version $Id: Cost.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
public class Cost {
    public double cost;
    public int benefitCount;
    public double benefit;

    public void copyFrom(Cost other) {
        this.cost = other.cost;
        this.benefitCount = other.benefitCount;
        this.benefit = other.benefit;
    }

    public String toString() {
        return "{cost=" + cost +
            ", benefit=" + benefit +
            ", count=" + benefitCount + "}";
    }
}

// End Cost.java
