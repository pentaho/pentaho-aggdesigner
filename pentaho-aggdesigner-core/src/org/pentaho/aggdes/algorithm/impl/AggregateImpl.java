/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License, version 2 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
*/
package org.pentaho.aggdes.algorithm.impl;

import java.util.*;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.util.BitSetPlus;

/**
 * Implementation of {@link org.pentaho.aggdes.model.Aggregate}.
 *
 * @author jhyde
 * @version $Id: AggregateImpl.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
public class AggregateImpl implements Aggregate {
   
    private final Schema schema;
    final BitSetPlus bits;
    double rowCount;
    boolean materialized;
    double cost;
    /**
     * Proportion of typical queries that use this aggregate. A number between
     * zero and one. Set on materialize.
     */
    double queryLoad;

    public AggregateImpl(Schema schema, BitSetPlus bits) {
        this.schema = schema;
        this.bits = bits;
        this.rowCount =
            schema.getStatisticsProvider().getRowCount(getAttributes());
        if (false) {
            System.out.println(
                "AggregateImpl: " + getDescription() + " bits=" + bits);
        }
    }

    public List<Attribute> getAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            attributes.add(schema.getAttributes().get(i));
        }
        return attributes;
    }

    public List<Measure> getMeasures() {
        return schema.getMeasures();
    }

    public double estimateRowCount() {
        return rowCount;
    }

    public double estimateSpace() {
        return estimateRowCount() *
            schema.getStatisticsProvider().getSpace(getAttributes());
    }
    
    public String getDescription() {
        StringBuilder buf = new StringBuilder("{");
        int i = 0;
        for (Attribute attribute : getAttributes()) {
            if (i++ > 0) {
                buf.append(", ");
            }
            buf.append(attribute.getLabel());
        }
        buf.append("}");
        return buf.toString();
    }

    public String getCandidateTableName() {
        StringBuilder buf = new StringBuilder("agg ");
        for (Attribute attribute : getAttributes()) {
            buf.append(attribute.getCandidateColumnName().charAt(0));
        }
        
        for (Attribute attribute : getMeasures()) {
            buf.append(attribute.getCandidateColumnName().charAt(0));
        }
        return buf.toString();
    }

    /**
     * Returns whether this Aggregate is closed with respect to ancestors.
     * If it contains an attribute but does not contain all of the ancestors
     * of that attribute (see
     * {@link org.pentaho.aggdes.model.Attribute#getAncestorAttributes()}),
     * return false.
     *
     * @param ancestorClosure List containing, for each attribute ordinal, the
     * ordinals of the ancestors of that attribute, and all of their ancestors,
     * and so forth
     *
     * @return whether this Aggegate is closed with respect to ancestors
     */
    public boolean hasCompleteAncestors(
        List<BitSetPlus> ancestorClosure)
    {
        // for each attribute
        for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            // check whether the attribute's ancestors are also present
            final BitSetPlus ancestorBitMap = ancestorClosure.get(i);
            if (!bits.contains(ancestorBitMap)) {
                return false;
            }
        }
        return true;
    }
}

// End AggregateImpl.java
