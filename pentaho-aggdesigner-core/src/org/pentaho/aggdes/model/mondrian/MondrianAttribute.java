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
      query.addSelect(column.getExpression().getExpression(query));
      final String sql = query.toString();
      java.sql.Connection jdbcConnection = null;
      try {
          jdbcConnection = column.getStar().getDataSource().getConnection();
          final PreparedStatement pstmt = jdbcConnection.prepareStatement(sql);
          pstmt.setMaxRows(1);
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
              typeString = type + "(" + precision + ")";
          } else {
              typeString = type + "(" + precision + ", " + scale + ")";
          }
          pstmt.close();
          jdbcConnection.close();
          jdbcConnection = null;
          return typeString;
      } catch (SQLException e) {
          throw Util.newError(
              e,
              "Error while deriving type of column " + toString());
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
