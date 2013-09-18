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
