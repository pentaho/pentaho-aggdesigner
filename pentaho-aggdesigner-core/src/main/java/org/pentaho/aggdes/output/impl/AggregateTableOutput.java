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
