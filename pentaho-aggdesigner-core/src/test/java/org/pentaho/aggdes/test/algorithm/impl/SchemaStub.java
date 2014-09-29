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

package org.pentaho.aggdes.test.algorithm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.model.Dimension;
import org.pentaho.aggdes.model.Hierarchy;
import org.pentaho.aggdes.model.Level;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.StatisticsProvider;
import org.pentaho.aggdes.model.Table;

/** Mock implementation of {@link Schema} for testing. */
public class SchemaStub implements Schema {

    public static class TableStub implements Table {
      final String label;
      final Table parent;

      public TableStub(String label, Table parent) {
        this.label = label;
        this.parent = parent;
      }

      public String getLabel() {
        return label;
      }

      public Table getParent() {
        return parent;
      }

    }

    public static class MeasureStub extends AttributeStub implements Measure {

      public MeasureStub(double space, String colName, String datatype, String label, TableStub table) {
        super(space, colName, datatype, label, table);
      }

      public boolean isDistinct() {
        // TODO Auto-generated method stub
        return false;
      }

    }

    public static class AttributeStub implements Attribute {
      final double space;
      final String colName;
      final String datatype;
      final String label;
      final TableStub table;
      final List<Attribute> ancestors = Collections.emptyList();

      public AttributeStub(double space, String colName, String datatype, String label, TableStub table) {
        this.space = space;
        this.colName = colName;
        this.datatype = datatype;
        this.label = label;
        this.table = table;
      }

      @Override public String toString() {
        return label;
      }

      public double estimateSpace() {
        // TODO Auto-generated method stub
        return space;
      }

      public String getCandidateColumnName() {
        return colName;
      }

      public String getDatatype(Dialect dialect) {
        return datatype;
      }

      public String getLabel() {
        return label;
      }

      public Table getTable() {
        return table;
      }

      public List<Attribute> getAncestorAttributes() {
        return ancestors;
      }

    }


    public static class LevelStub implements Level {
      final Attribute attribute;
      final String name;
      final Level parent;

      public LevelStub(String name, Attribute attribute) {
        this.name = name;
        this.attribute = attribute;
        this.parent = null;
      }

      public Attribute getAttribute() {
        return attribute;
      }

      public String getName() {
        return name;
      }

      public Level getParent() {
        return parent;
      }
    }

    public static class HierarchyStub implements Hierarchy {
      final List<Level> levels = new ArrayList<Level>();
      final String name;

      public HierarchyStub(String name) {
        this.name = name;
      }

      public List<Level> getLevels() {
        return levels;
      }

      public String getName() {
        return name;
      }

    }

    public static class DimensionStub implements Dimension {
      final List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();
      final String name;

      public DimensionStub(String name) {
        this.name = name;
      }

      public List<Hierarchy> getHierarchies() {
          return hierarchies;
      }

      public String getName() {
        return name;
      }
    }


    public static class StatisticsProviderStub implements StatisticsProvider {

      public double getFactRowCount() {
        return 1000;
      }

      public double getLoadTime(List<Attribute> attributes) {
        return 1000;
      }

      public double getRowCount(List<Attribute> attributes) {
        if (attributes.size() == 0) {
          return 1;
        } else if (attributes.size() == 1) {
          return 10;
        } else if (attributes.size() == 2) {
          return 100;
        } else {
          return 1000;
        }
      }

      public double getSpace(List<Attribute> attributes) {
        return 1000;
      }

    }

    List<Attribute> attribs = new ArrayList<Attribute>();
    StatisticsProvider provider = null;
    List<Measure> measures = new ArrayList<Measure>();
    List<Dimension> dimensions = new ArrayList<Dimension>();
    Dialect dialect;

    public SchemaStub() {
        this.provider = init(attribs, measures, dimensions);
    }

    protected StatisticsProvider init(List<Attribute> attributes, List<Measure> measures, List<Dimension> dimensions) {
        TableStub fact = new TableStub("fact", null);
        attributes.add(new AttributeStub(0, "col1", "int", "col1", fact));
        attributes.add(new AttributeStub(0, "col2", "int", "col2", fact));
        attributes.add(new AttributeStub(0, "col3", "int", "col3", fact));

        measures.add(new MeasureStub(0, "mes1", "int", "mcol1", fact));

        LevelStub level1 = new LevelStub("Level 1", this.attribs.get(0));
        LevelStub level2 = new LevelStub("Level 2", this.attribs.get(1));

        HierarchyStub hierarchy = new HierarchyStub("Hierarchy 1");
        hierarchy.levels.add(level1);
        hierarchy.levels.add(level2);

        DimensionStub dimension = new DimensionStub("Dimension 1");
        dimension.hierarchies.add(hierarchy);

        dimensions.add(dimension);

        return new StatisticsProviderStub();
    }


    public String generateAggregateSql(Aggregate aggregate, List<String> columnNameList) {
      return null;
    }

    public List<Attribute> getAttributes() {
      return attribs;
    }

    public void setDialect(Dialect dialect) {
      this.dialect = dialect;
    }

    public Dialect getDialect() {
      return dialect;
    }

    public List<? extends Dimension> getDimensions() {
      return dimensions;
    }

    public List<Measure> getMeasures() {
      return measures;
    }

    public StatisticsProvider getStatisticsProvider() {
      return provider;
    }

    public List<? extends Table> getTables() {
      return null;
    }
}
