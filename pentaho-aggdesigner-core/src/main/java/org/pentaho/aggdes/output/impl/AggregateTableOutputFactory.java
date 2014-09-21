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

import mondrian.olap.Util;
import mondrian.util.UnionIterator;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.OutputFactory;

public class AggregateTableOutputFactory implements OutputFactory {

    public Class getOutputClass() {
        return AggregateTableOutput.class;
    }
    
    // default implementation, always return true for all schemas
    public boolean canCreateOutput(Schema schema) {
        return true;
    }
    
    public AggregateTableOutput createOutput(Schema schema, Aggregate aggregate) {
        return createOutput(schema, aggregate, new ArrayList<String>());
    }
   
    public AggregateTableOutput createOutput(Schema schema, Aggregate aggregate, List<String> uniqueTableNames) {
        AggregateTableOutput output = new AggregateTableOutput(aggregate);
        String tableName = schema.getDialect().removeInvalidIdentifierCharacters(aggregate.getCandidateTableName());
        tableName = Util.uniquify(tableName, schema.getDialect().getMaximumTableNameLength(), uniqueTableNames);
        output.setTableName(tableName);
        
        final List<String> columnNameList = new ArrayList<String>();
        int maximumColumnNameLength =
            schema.getDialect().getMaximumColumnNameLength();
        for (Attribute attribute :
            UnionIterator.over(
                aggregate.getAttributes(), 
                aggregate.getMeasures()))
        {
            String name = Util.uniquify(
                attribute.getCandidateColumnName(),
                maximumColumnNameLength,
                columnNameList);
            output.getColumnOutputs().add(new AggregateTableOutput.ColumnOutput(name, attribute));
        }
        
        return output;
    }

    public List<Output> createOutputs(Schema schema, List<Aggregate> aggregates) {
        List<Output> outputs = new ArrayList<Output>();
        List<String> uniqueTableNames = new ArrayList<String>();
        for (Aggregate aggregate : aggregates) {
            outputs.add(createOutput(schema, aggregate, uniqueTableNames));
        }
        return outputs;
    }
}
