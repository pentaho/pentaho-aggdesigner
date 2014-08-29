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

import java.sql.SQLException;
import java.util.List;

import mondrian.olap.Cell;
import mondrian.olap.Query;
import mondrian.olap.Result;
import mondrian.rolap.RolapAggregator;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapMember;
import mondrian.rolap.RolapStoredMeasure;
import mondrian.rolap.SqlStatement;
import mondrian.rolap.sql.SqlQuery;

import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.StatisticsProvider;
/**
 * Implementation of {@link StatisticsProvider} that goes to the database.
 */
public class MondrianStatisticsProvider implements StatisticsProvider {
      private final MondrianSchema schema;
      private Double factRowCount;

      public MondrianStatisticsProvider(MondrianSchema schema) {
          this.schema = schema;
      }

      public double getFactRowCount() {
          // this assumes that there will be a count measure, if not, we need a work around
          
          if (factRowCount == null) {
              final RolapConnection connection =
                  schema.getRolapCube().getSchema().getInternalConnection();
              RolapMember m = findCountMeasure(schema.getRolapCube());
              // if there is no count measure, get the star count the old fashioned way
              if (m == null || true) {
                  java.sql.Connection conn = null;
                  java.sql.Statement stmt = null;
                  java.sql.ResultSet rs = null;
                  try {
                     conn = schema.getRolapCube().getSchema().getInternalConnection().getDataSource().getConnection();
                     stmt = conn.createStatement();
                     SqlQuery query = new SqlQuery(((MondrianDialect)schema.getDialect()).getMondrianDialect());
                     query.addSelect("count(*)", SqlStatement.Type.DOUBLE);
                     query.addFrom(
                             schema.getRolapCube().getStar().getFactTable().getRelation(),
                             null, true);
                     String sql = query.toString();
                     
                     rs = stmt.executeQuery(sql);
                     if (rs.next()) {
                         factRowCount = rs.getDouble(1);
                     }
                  } catch (SQLException e) {
                      throw new RuntimeException("Failed to get a count from the fact table", e );
                  } finally {
                      if (rs != null) try { rs.close(); } catch (Exception e) {}
                      if (stmt != null) try { stmt.close(); } catch (Exception e) {}
                      if (conn != null) try { conn.close(); } catch (Exception e) {}
                  }
                  
              } else {
                  final Query query = connection.parseQuery(
                      "select from "
                          + schema.getRolapCube().getUniqueName()
                          + " where " + m.getUniqueName());
                  final Result result = connection.execute(query);
                  final Cell cell = result.getCell(new int[0]);
                  factRowCount = ((Number) cell.getValue()).doubleValue();
              }
          }
          return factRowCount;
      }

      private static RolapMember findCountMeasure(final RolapCube cube) {
          for (RolapMember member : cube.getMeasuresMembers()) {
              if (member instanceof RolapStoredMeasure) {
                  RolapStoredMeasure measure = (RolapStoredMeasure) member;
                  if (measure.getAggregator() == RolapAggregator .Count) {
                      return member;
                  }
              }
          }
          return null;
          // throw new RuntimeException("Cube does not have a 'count' measure");
      }

      public double getRowCount(
          List<Attribute> attributes)
      {
          // Approximation: assume that attributes' values are independent.
          // TODO: Generate queries to get joint distribution; Use a cache.
          double comboCount = 1.0;
          for (Attribute attribute : attributes) {
              comboCount *= ((MondrianAttribute) attribute).getDistinctValueCount();
          }
          return MondrianSchemaLoader.estimateAggregateCount(
              comboCount,
              getFactRowCount());
      }

      public double getSpace(
          List<Attribute> attributes)
      {
          double space = 0.0;
          for (Attribute attribute : attributes) {
              space += attribute.estimateSpace();
          }
          return space;
      }

      public double getLoadTime(List<Attribute> attributes) {
          // Simple estimate of cost, in terms of I/O.
          // To load the aggregate, we need to read all rows in the fact
          // table, aggregate the rows (which we assume here has zero cost),
          // then write the aggregated rows.
          return getFactRowCount() * getSpace((List) schema.getAttributes())
              + getRowCount(attributes) * getSpace(attributes);
      }
}
