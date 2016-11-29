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

package org.pentaho.aggdes.output.impl;

import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.CreateScriptGenerator;
import org.pentaho.aggdes.output.Output;

public class CreateTableGenerator extends AbstractGenerator implements CreateScriptGenerator {

    public Class[] getSupportedOutputClasses() {
        return new Class[] {AggregateTableOutput.class};
    }
    
    public boolean canGenerate(Schema schema, Output output) {
        // this is the default generation implementation
        return (output instanceof AggregateTableOutput);
    }

    /**
     * generates the table output
     * @param schema
     * @param output
     * @return
     */
    public String generate(Schema schema, Output output) {
        
        AggregateTableOutput tableOutput = (AggregateTableOutput)output;
        
        final Dialect dialect = schema.getDialect();
        final StringBuilder buf = new StringBuilder();
        dialect.comment(
            buf,
            "Aggregate table " + tableOutput.getTableName());
        dialect.comment(
            buf,
            "Estimated "
                + new Double(output.getAggregate().estimateRowCount()).intValue()
                + " rows, "
                + new Double(output.getAggregate().estimateSpace()).intValue()
                + " bytes");
        buf.append("CREATE TABLE ");
        
        dialect.quoteIdentifier(buf, tableOutput.getCatalogName(), tableOutput.getSchemaName(), tableOutput.getTableName());
        buf.append(" (").append(ResultHandlerImpl.NL);
        int i = -1;
        for (AggregateTableOutput.ColumnOutput columns : tableOutput.getColumnOutputs())
        {
            ++i;
            if (i > 0) {
                buf.append(",").append(ResultHandlerImpl.NL);
            }
            String columnName = columns.getName();
            buf.append("    ");
            dialect.quoteIdentifier(buf, columnName);
            buf.append(" ");
            buf.append(columns.getAttribute().getDatatype(dialect));
        }
        buf.append(")");
        dialect.terminateCommand(buf);
        return buf.toString();
    }
    
}
