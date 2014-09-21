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
