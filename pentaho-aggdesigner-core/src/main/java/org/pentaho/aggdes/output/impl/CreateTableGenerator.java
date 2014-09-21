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
