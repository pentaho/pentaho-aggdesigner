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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
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
