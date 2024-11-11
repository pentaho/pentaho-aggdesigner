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


package org.pentaho.aggdes.model.mondrian;

import java.util.List;

import mondrian.rolap.RolapAggregator;
import mondrian.rolap.RolapStar;

import org.pentaho.aggdes.Main;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.model.Measure;

public class MondrianMeasure implements Measure {
    private final MondrianTable table;
    private final RolapStar.Measure measure;

    public MondrianMeasure(MondrianTable table, RolapStar.Measure measure) {
        this.table = table;
        this.measure = measure;
    }

    public RolapStar.Measure getRolapStarMeasure() {
        return measure;
    }
    
    public boolean isDistinct() {
        return measure.getAggregator().isDistinct();
    }

    public String getLabel() {
        return table.getLabel() + "." + measure.getName();
    }

    public MondrianTable getTable() {
        return table;
    }

    public double estimateSpace() {
        return MondrianSchemaLoader.estimateSpaceForColumn(measure);
    }

    public String getCandidateColumnName() {
        return Main.depunctify(getLabel());
    }

    public String getDatatype(Dialect dialect) {
        final RolapAggregator aggregator = measure.getAggregator();
        String aggregatorName = aggregator.getName().toUpperCase();
        final mondrian.spi.Dialect mondrianDialect =
            ((MondrianDialect) dialect).getMondrianDialect();

        if (aggregator == RolapAggregator.Min
            || aggregator == RolapAggregator.Max) {
            return measure.getDatatypeString(mondrianDialect);
        } else if (aggregator == RolapAggregator.Count
            || aggregator == RolapAggregator.DistinctCount) {
            return dialect.getIntegerTypeString();
        } else if (aggregator == RolapAggregator.Sum
            || aggregator == RolapAggregator.Avg) {
            return dialect.getDoubleTypeString();
        } else {
            throw new RuntimeException(
                "Unknown aggregator " + aggregatorName);
        }
    }

    public List<Attribute> getAncestorAttributes() {
      // measures contain no ancestor attributes
      return null;
    }
    
}
