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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import mondrian.spi.Dialect.DatabaseProduct;

import org.pentaho.aggdes.model.Dialect;

public class MondrianDialect implements Dialect {
  private final mondrian.spi.Dialect dialect;

  public MondrianDialect(mondrian.spi.Dialect dialect) {
      this.dialect = dialect;
  }

  public void quoteIdentifier(
      StringBuilder buf,
      String... names)
  {
      dialect.quoteIdentifier(buf, names);
  }

  public String getIntegerTypeString() {
    if (dialect.getDatabaseProduct() == DatabaseProduct.POSTGRESQL) {
      return "INT8";
    } else {
      return "INTEGER";
    }
  }

  public String getDoubleTypeString() {
      if (dialect.getDatabaseProduct() == DatabaseProduct.ORACLE) {
          return "NUMBER";
      } else if (dialect.getDatabaseProduct() == DatabaseProduct.POSTGRESQL) {
          return "FLOAT8";
      } else {
          return "DOUBLE";
      }
  }

  public String removeInvalidIdentifierCharacters(String str) {
      return str.replaceAll("\\s", "_").replaceAll("[^\\w_]", "");
  }
  
  public int getMaximumTableNameLength() {
      return 30; // TODO: use JDBC metadata
  }
  
  public int getMaximumColumnNameLength() {
      return 30; // TODO: use JDBC metadata
  }
  
  public mondrian.spi.Dialect getMondrianDialect() {
      return dialect;
  }
  
  public void comment(
          StringBuilder buf, String s)
  {
      // TODO: add comment prefix to dialect
      buf.append("-- ").append(s).append(NL);
  }

  public void terminateCommand(StringBuilder buf) {
      buf.append(";").append(NL);
  }

  public boolean supportsPrecision(DatabaseMetaData meta, String type) throws SQLException {
    // return false if hypersonic
    // return false if postgres and not varchar or char
    // return true otherwise
    
    // hsqldb v1.8 returns 'HSQL Database Engine'
    return 
      (meta.getDatabaseProductName().toUpperCase().indexOf("HSQL") < 0) &&
      ((!(dialect.getDatabaseProduct() == DatabaseProduct.POSTGRESQL) && 
        !(dialect.getDatabaseProduct() == DatabaseProduct.LUCIDDB)) || 
       (type.toUpperCase().indexOf("CHAR") >= 0));

  }
}
