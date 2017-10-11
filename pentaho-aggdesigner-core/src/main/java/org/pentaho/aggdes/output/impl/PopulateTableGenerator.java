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

package org.pentaho.aggdes.output.impl;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.PopulateScriptGenerator;

public class PopulateTableGenerator extends AbstractGenerator implements PopulateScriptGenerator {

    public Class[] getSupportedOutputClasses() {
        return new Class[] {AggregateTableOutput.class};
    }
    
    public boolean canGenerate(Schema schema, Output output) {
        return (output instanceof AggregateTableOutput);
    }

    public String generate(Schema schema, Output output) {
        AggregateTableOutput tableOutput = (AggregateTableOutput)output;
        final Dialect dialect = schema.getDialect();
        final StringBuilder buf = new StringBuilder();
        dialect.comment(buf, "Populate aggregate table " + tableOutput.getTableName());
        buf.append("INSERT INTO ");
        dialect.quoteIdentifier(
            buf, tableOutput.getCatalogName(), tableOutput.getSchemaName(), tableOutput.getTableName());
        buf.append(" (").append(ResultHandlerImpl.NL);
        int k = -1;
        List<String> columnNameList = new ArrayList<String>();
        for (AggregateTableOutput.ColumnOutput column : tableOutput.getColumnOutputs()) {
            ++k;
            if (k > 0) {
                buf.append(",").append(ResultHandlerImpl.NL);
            }
            buf.append("    ");
            dialect.quoteIdentifier(buf, column.getName());
            columnNameList.add(column.getName());
        }
        buf.append(")").append(ResultHandlerImpl.NL);
        String sql =
            schema.generateAggregateSql(
                    tableOutput.getAggregate(), columnNameList);
        buf.append(sql);
        dialect.terminateCommand(buf);
        return buf.toString();
        
    }

}
