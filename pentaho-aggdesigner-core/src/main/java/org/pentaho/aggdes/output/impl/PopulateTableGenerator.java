/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
