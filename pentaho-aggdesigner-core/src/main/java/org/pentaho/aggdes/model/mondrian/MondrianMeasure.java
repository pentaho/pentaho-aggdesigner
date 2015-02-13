/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

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
