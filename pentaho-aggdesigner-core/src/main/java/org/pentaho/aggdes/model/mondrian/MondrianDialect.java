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
