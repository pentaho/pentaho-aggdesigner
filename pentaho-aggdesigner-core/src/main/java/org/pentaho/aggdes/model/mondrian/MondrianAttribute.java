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

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import mondrian.olap.Util;
import mondrian.rolap.RolapStar;
import mondrian.rolap.sql.SqlQuery;

import org.pentaho.aggdes.Main;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.model.Table;

public class MondrianAttribute implements Attribute {

  private final MondrianTable table;
  private final RolapStar.Column column;
  private final double distinctValueCount;
  private final List<Attribute> ancestors;

  public MondrianAttribute(
      MondrianTable table,
      List<Attribute> ancestors,
      RolapStar.Column column,
      double distinctValueCount)
  {
      this.table = table;
      this.column = column;
      this.distinctValueCount = distinctValueCount;
      this.ancestors = ancestors;
  }

  public RolapStar.Column getRolapStarColumn() {
      return column;
  }

  public String getLabel() {
      return "[" + table.getLabel() + "].[" + column.getName() + "]";
  }

  public Table getTable() {
      return table;
  }

  public double getDistinctValueCount() {
    return distinctValueCount;
  }

  public double estimateSpace() {
      return MondrianSchemaLoader.estimateSpaceForColumn(column);
  }

  public String getCandidateColumnName() {
      return Main.depunctify(getLabel());
  }

  public String getDatatype(Dialect dialect) {
      //TODO: fix mondrian's RolapStar.Column.getDatatypeString() method
      // so Oracle can work with it.
      //      return column.getDatatypeString(
      //          ((MondrianDialectImpl) dialect).dialect);
      return internalGetDatatypeString(
            column, dialect);
  }

  /**
   * Returns a string representation of the datatype of this column, in
   * the dialect specified. For example, 'DECIMAL(10, 2) NOT NULL'.
   *
   * @param column RolapStar column
   * @param dialect Dialect
   * @return String representation of column's datatype
   */
  private String internalGetDatatypeString(RolapStar.Column column, Dialect dialect) {
      mondrian.spi.Dialect mondrianDialect = ((MondrianDialect) dialect).getMondrianDialect();
      final SqlQuery query = new SqlQuery(mondrianDialect);
      query.addFrom(column.getTable().getRelation(), column.getTable().getAlias(), false);
      query.addSelect(column.getExpression().getExpression(query), null);
      final String sql = query.toString();
      java.sql.Connection jdbcConnection = null;
      try {
          jdbcConnection = column.getStar().getDataSource().getConnection();
          final PreparedStatement pstmt = jdbcConnection.prepareStatement(sql);
          // Fails on some versions of MySQL:
          //pstmt.setMaxRows(1);
          pstmt.executeQuery();
          final ResultSetMetaData resultSetMetaData = pstmt.getMetaData();
          assert resultSetMetaData.getColumnCount() == 1;
          final String type = resultSetMetaData.getColumnTypeName(1);
          int precision = resultSetMetaData.getPrecision(1);
          final int scale = resultSetMetaData.getScale(1);
          if (type.equals("DOUBLE")) {
              precision = 0;
          }
          String typeString;
          if (precision == 0 || precision == Integer.MAX_VALUE ||
              !dialect.supportsPrecision(jdbcConnection.getMetaData(), type)) {
              typeString = type;
          } else if (scale == 0) {
            if(!resultSetMetaData.isSigned(1) && type.contains("UNSIGNED")) {
              String[] words = type.split("\\s+");
              if(words.length > 1) {
                String typeFirstWord = words[0];
                String typeSignWord = words[1];
                typeString = typeFirstWord + "(" + precision + ") " + typeSignWord;
              } else {
                typeString = type + "(" + precision + ")";
              }
            } else {
              typeString = type + "(" + precision + ")";
            }
          } else {
            if(!resultSetMetaData.isSigned(1) && type.contains("UNSIGNED")) {
              String[] words = type.split("\\s+");
              if(words.length > 1) {
                String typeFirstWord = words[0];
                String typeSignWord = words[1];
                typeString = typeFirstWord + "(" + precision + ", " + scale + ")" + " " + typeSignWord;
              } else {
                typeString = type + "(" + precision + ", " + scale + ")";
              }
            } else {
              typeString = type + "(" + precision + ", " + scale + ")";
            }
          }
          pstmt.close();
          jdbcConnection.close();
          jdbcConnection = null;
          return typeString;
      } catch (SQLException e) {
          throw Util.newError(
              e,
              "Error while deriving type of column " + toString() + ", sql: "
              + sql);
      } finally {
          if (jdbcConnection != null) {
              try {
                  jdbcConnection.close();
              } catch (SQLException e) {
                  // ignore
              }
          }
      }
  }

  public List<Attribute> getAncestorAttributes() {
    return ancestors;
  }

}
