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

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.output.Output;

/**
 * this is a java bean used to store the details of an agg table
 * that will be rendered as DDL, DML and Schema Artifacts
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class AggregateTableOutput implements Output {

    public static class ColumnOutput {
        private String name;
        private Attribute attribute;
        
        public ColumnOutput(String name, Attribute attribute) {
            this.name = name;
            this.attribute = attribute;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Attribute getAttribute() {
            return attribute;
        }
        
        public void setAttribute(Attribute attribute) {
            this.attribute = attribute;
        }
    }

    private String tableName;
    
    private String catalogName;
    
    private String schemaName; 
    
    private final Aggregate aggregate;
    private final List<ColumnOutput> columns;
    
    public AggregateTableOutput(Aggregate aggregate) {
        this.aggregate = aggregate;
        this.columns = new ArrayList<ColumnOutput>();
    }

    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getCatalogName() {
        return catalogName;
    }
    
    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }
    
    public String getSchemaName() {
        return schemaName;
    }
    
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
    
    public Aggregate getAggregate() {
        return aggregate;
    }
    
    public List<ColumnOutput> getColumnOutputs() {
        return columns;
    }
    
}
