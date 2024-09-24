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
