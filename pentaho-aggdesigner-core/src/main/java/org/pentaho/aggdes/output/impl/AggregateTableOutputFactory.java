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
