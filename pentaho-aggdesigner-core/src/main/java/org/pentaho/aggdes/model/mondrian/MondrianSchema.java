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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mondrian.olap.Connection;
import mondrian.olap.MondrianDef;
import mondrian.rolap.RolapAggregator;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapCubeLevel;
import mondrian.rolap.RolapStar;
import mondrian.server.Execution;
import mondrian.server.Locus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.StatisticsProvider;

public class MondrianSchema implements Schema {
  
  private static final Log logger = LogFactory.getLog(MondrianSchema.class);
  
  private final Connection connection;

  private final RolapCube cube;

  private final List<MondrianTable> tables = new ArrayList<MondrianTable>();

  private final List<MondrianDimension> dimensions = new ArrayList<MondrianDimension>();

  private final List<MondrianAttribute> attributes = new ArrayList<MondrianAttribute>();

  private final List<MondrianMeasure> measures = new ArrayList<MondrianMeasure>();

  private final StatisticsProvider statisticsProvider = new MondrianStatisticsProvider(this);

  private final Dialect dialect;

  public MondrianSchema(RolapConnection conn, RolapCube cube) {
    this.connection = conn;
    this.cube = cube;
    RolapStar star = cube.getStar();
    dialect = new MondrianDialect(star.getSqlQueryDialect());
    final RolapStar.Table factTable = star.getFactTable();
    final Locus locus =
        new Locus(
            new Execution(conn.getInternalStatement(),0),
            "MondrianSchema.init",
            "while loading the MondrianSchema into the Aggregation Designer.");
    Locus.push(locus);
    try {
        doTable(null, factTable);
        doCube(cube);
    } finally {
        Locus.pop(locus);
    }
  }

  public Connection getRolapConnection() {
    return connection;
  }

  public DatabaseMetaData getDatabaseMetaData() throws SQLException {
    return connection.getDataSource().getConnection().getMetaData();

  }

  /**
   * createAttribute is protected so that extenders of MondrianSchema may modify
   * certain characteristics of the attribute class, such as getDataType(), etc
   * 
   * @param table
   *          mondrian table parent
   * @param column
   *          mondrian column
   * @param distinctValueCount
   *          number of values in the attribute
   * @return
   */
  protected MondrianAttribute createAttribute(MondrianTable table, List<Attribute> ancestors, RolapStar.Column column,
      double distinctValueCount) {
    return new MondrianAttribute(table, ancestors, column, distinctValueCount);
  }
  
  /**
   * createMeasure is protected so that extenders of MondrianSchema may modify
   * certain characteristics of the measure class, such as getDataType(), etc
   * 
   * @param table
   *          mondrian table parent
   * @param column
   *          mondrian column
   * @param distinctValueCount
   *          number of values in the attribute
   * @return
   */
  protected MondrianMeasure createMeasure(MondrianTable table, RolapStar.Measure measure) {
    return new MondrianMeasure(table, measure);
  }

  private void doTable(MondrianTable parentTableImpl, RolapStar.Table table) {
    MondrianTable tableImpl = new MondrianTable(parentTableImpl, table);
    tables.add(tableImpl);
    for (RolapStar.Column column : table.getColumns()) {
      if (column instanceof RolapStar.Measure) {
        final RolapStar.Measure measure = (RolapStar.Measure) column;
        if (!measure.getCubeName().equals(cube.getName())) {
          // skip measure for different cube mapped to same
          // fact table
          continue;
        }
        
        if (measure.getName().equals("Fact Count")) {
        	// skip fact count if it exists, we create it later.
        	// Mondrian 3.2 introduced an internal Fact Count
        	// for writeback.
        	continue;
        }
        
        measures.add(createMeasure(tableImpl, measure));

      } else {
        // Generate and execute a query to find the number of
        // distinct values in the attribute.
        double valueCount = column.getCardinality();
        
        List<Attribute> ancestors = new ArrayList<Attribute>();
        
        attributes.add(createAttribute(tableImpl, ancestors, column, valueCount));
      }
    }

    //
    // add a fact_count measure to the schema, which is required for
    // mondrian aggregate tables.
    //

    if (parentTableImpl == null) {
      // pick the first star measure
      RolapStar.Measure cloneMeasure = null;
      for (RolapStar.Column column : table.getColumns()) {
        if (column instanceof RolapStar.Measure) {
          cloneMeasure = (RolapStar.Measure) column;
        }
      }
      MondrianDef.MeasureExpression expr = new MondrianDef.MeasureExpression();
      MondrianDef.SQL star = new MondrianDef.SQL();
      star.cdata = "*";
      star.dialect = "generic";
      expr.expressions = new MondrianDef.SQL[] { star };
      RolapStar.Measure factCount = null;
      try {
        factCount = new RolapStar.Measure("fact_count", cloneMeasure.getCubeName(),
            RolapAggregator.Count, cloneMeasure.getTable(), expr, mondrian.spi.Dialect.Datatype.Integer);
      } catch (IllegalAccessError e) {
        throw new RuntimeException(Messages
            .getString("MondrianSchemaLoader.ERROR_0001_MONDRIAN_DEPENDENCY_ERROR"), e);
      }

      measures.add(new MondrianMeasure(tableImpl, factCount));
    }

    for (RolapStar.Table childTable : table.getChildren()) {
      doTable(tableImpl, childTable);
    }
  }

  private void doCube(RolapCube cube) {
    List<MondrianAttribute> newAttributes = new ArrayList<MondrianAttribute>();
    for (mondrian.olap.Dimension dimension : cube.getDimensions()) {
      if (dimension.isMeasures()) {
        continue;
      }
      MondrianDimension dimensionImpl = new MondrianDimension(dimension.getName());
      for (mondrian.olap.Hierarchy hierarchy : dimension.getHierarchies()) {
        MondrianHierarchy hierarchyImpl = new MondrianHierarchy(hierarchy.getName());
        // TODO: how do we handle parent child rels?
        MondrianLevel parent = null;
        for (mondrian.olap.Level level : hierarchy.getLevels()) {
          RolapCubeLevel cubeLevel = (RolapCubeLevel) level;
          RolapStar.Column col = cubeLevel.getStarKeyColumn();
          MondrianAttribute attribImpl = null;
          for (MondrianAttribute attrib : attributes) {
            if (col == attrib.getRolapStarColumn()) {
              attribImpl = attrib;
              if (!newAttributes.contains(attrib)) {
                newAttributes.add(attrib);
              }
            }
          }
          if (!level.isAll() && attribImpl == null) {
            throw new RuntimeException("attribute not found for level: " + level.getName()
                + ", star column: " + col);
          }
          
          // populate attribute ancestors.  This is necessary because some attributes
          // are not unique within a mondrian schema.  For now, we include all level
          // parent attributes, but in the future, this may only include the attributes
          // necessary to make the attribute unique.  There will need to be 
          // verification that Mondrian Aggregations support that concept, and the 
          // Aggregation UI will need to support selecting Attributes in that manner before
          // this can utilize that approach.
          
          MondrianLevel ancestor = parent;
          while (ancestor != null) {
            if (ancestor.getAttribute() != null) {
              // insert at the beginning of the list so the ancestors appear in order
              attribImpl.getAncestorAttributes().add(0, ancestor.getAttribute());
            }
            ancestor = ancestor.getParent();
          }
          
          MondrianLevel levelImpl = new MondrianLevel(parent, cubeLevel, level.getName(),
              attribImpl);
          hierarchyImpl.addLevel(levelImpl);
          parent = levelImpl;
        }
        dimensionImpl.addHierarchy(hierarchyImpl);
      }
      dimensions.add(dimensionImpl);
    }

    // we only care about the attributes that are bound to levels
    attributes.clear();
    attributes.addAll(newAttributes);
    
    if (logger.isDebugEnabled()) {
      logger.debug("Schema Attributes: ");
      for (Attribute attribute : attributes) {
        logger.debug("   " + attribute.getLabel());
      }
    }
  }

  public StatisticsProvider getStatisticsProvider() {
    return statisticsProvider;
  }

  public List<MondrianTable> getTables() {
    return tables;
  }

  @SuppressWarnings( { "RedundantCast", "unchecked" })
  public List<Measure> getMeasures() {
    return (List) measures;
  }

  public List<MondrianDimension> getDimensions() {
    return dimensions;
  }

  @SuppressWarnings( { "RedundantCast", "unchecked" })
  public List<Attribute> getAttributes() {
    return (List) attributes;
  }

  public Dialect getDialect() {
    return dialect;
  }

  public String generateAggregateSql(Aggregate aggregate, List<String> columnNameList) {
    List<RolapStar.Column> list = new ArrayList<RolapStar.Column>();
    for (Attribute attribute : aggregate.getAttributes()) {
      list.add(((MondrianAttribute) attribute).getRolapStarColumn());
    }
    for (Measure measure : aggregate.getMeasures()) {
      list.add(((MondrianMeasure) measure).getRolapStarMeasure());
    }
    return cube.getStar().generateSql(list, columnNameList);
  }

  public RolapCube getRolapCube() {
    return cube;
  }
}
