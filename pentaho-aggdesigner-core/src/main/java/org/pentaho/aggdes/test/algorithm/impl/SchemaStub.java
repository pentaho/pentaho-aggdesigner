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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes.test.algorithm.impl;

import java.util.ArrayList;
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

public class SchemaStub implements Schema {

    public static class TableStub implements Table {
      String label;
      Table parent;
      
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
      double space;
      String colName;
      String datatype;
      String label;
      TableStub table;
      List<Attribute> ancestors = new ArrayList<Attribute>();

      public AttributeStub(double space, String colName, String datatype, String label, TableStub table) {
        this.space = space;
        this.colName = colName;
        this.datatype = datatype;
        this.label = label;
        this.table = table;
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
      
      Attribute attribute;
      String name;
      Level parent;
      
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
      List<Level> levels = new ArrayList<Level>();
      String name;
      public List<Level> getLevels() {
        return levels;
      }

      public String getName() {
        return name;
      }
      
    }
    
    public static class DimensionStub implements Dimension {
      List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();
      String name;
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
      TableStub fact = new TableStub("fact", null);
      attribs.add(new AttributeStub(0, "col1", "int", "col1", fact));
      attribs.add(new AttributeStub(0, "col2", "int", "col2", fact));
      attribs.add(new AttributeStub(0, "col3", "int", "col3", fact));
      
      measures.add(new MeasureStub(0, "mes1", "int", "mcol1", fact));
      
      LevelStub level1 = new LevelStub();
      level1.name = "Level 1";
      level1.attribute = attribs.get(0);
      
      LevelStub level2 = new LevelStub();
      level2.name = "Level 2";
      level2.attribute = attribs.get(1);
      
      HierarchyStub hierarchy = new HierarchyStub();
      hierarchy.name = "Hierarchy 1";
      hierarchy.levels.add(level1);
      hierarchy.levels.add(level2);
      
      DimensionStub dimension = new DimensionStub();
      dimension.name = "Dimension 1";
      dimension.hierarchies.add(hierarchy);
      
      dimensions.add(dimension);
     
      
      provider = new StatisticsProviderStub();
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
