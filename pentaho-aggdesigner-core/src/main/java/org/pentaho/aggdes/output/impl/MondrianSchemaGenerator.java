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

import java.util.ArrayList;
import java.util.List;

import mondrian.olap.MondrianDef;
import mondrian.rolap.RolapBaseCubeMeasure;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapCubeLevel;
import mondrian.rolap.RolapMeasure;
import mondrian.rolap.RolapMember;
import mondrian.rolap.RolapSchema;
import mondrian.rolap.RolapStar;

import org.eigenbase.xom.XOMException;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dimension;
import org.pentaho.aggdes.model.Hierarchy;
import org.pentaho.aggdes.model.Level;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.mondrian.MondrianLevel;
import org.pentaho.aggdes.model.mondrian.MondrianMeasure;
import org.pentaho.aggdes.model.mondrian.MondrianSchema;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.SchemaGenerator;

public class MondrianSchemaGenerator extends AbstractGenerator implements SchemaGenerator {

    public static final String NL = System.getProperty("line.separator");
    
    public Class[] getSupportedOutputClasses() {
        return new Class[] {AggregateTableOutput.class};
    }
    
    public boolean canGenerate(Schema schema, Output output) {
        return (output instanceof AggregateTableOutput);
    }

    public String generate(Schema schema, Output output) {
        return generateMondrianDef(schema, output).toXML();
    }


    private MondrianDef.AggName generateMondrianDef(Schema schema, Output output) {
        
        AggregateTableOutput tableOutput = (AggregateTableOutput)output;

        MondrianDef.AggName aggName = new MondrianDef.AggName();
        aggName.name = tableOutput.getTableName();
                
        List<MondrianDef.AggMeasure> measures = new ArrayList<MondrianDef.AggMeasure>();
        List<MondrianDef.AggLevel> levels = new ArrayList<MondrianDef.AggLevel>();
        
        int i = -1;
        
        for (AggregateTableOutput.ColumnOutput column : tableOutput.getColumnOutputs()) {
            ++i;
            String columnName = column.getName();
            Attribute attribute = column.getAttribute();
            if (attribute instanceof Measure) {
                Measure measure = (Measure)attribute;
                RolapStar.Measure rolapStarMeasure = ((MondrianMeasure)measure).getRolapStarMeasure();

                if (rolapStarMeasure.getName().equals("fact_count")) {
                    // add as fact count
                    MondrianDef.AggFactCount aggFactCount = new MondrianDef.AggFactCount();
                    aggFactCount.column = columnName;
                    aggName.factcount = aggFactCount;
                } else {
                    // add as regular measure
                    RolapMeasure rolapMeasure = findRolapMeasure(schema, measure);
                    MondrianDef.AggMeasure measureDef = new MondrianDef.AggMeasure();
                    measureDef.name = rolapMeasure.getUniqueName();
                    measureDef.column = columnName;
                    measures.add(measureDef);
                }
                
            } else {
                Level level = findLevel(schema, attribute);
                RolapCubeLevel rolapLevel = ((MondrianLevel)level).getRolapCubeLevel();
                
                MondrianDef.AggLevel levelDef = new MondrianDef.AggLevel();
                levelDef.name = rolapLevel.getUniqueName();
                levelDef.column = columnName;
                levels.add(levelDef);
            }
        }
        
        aggName.levels = (MondrianDef.AggLevel[])levels.toArray(new MondrianDef.AggLevel[0]);
        aggName.measures = (MondrianDef.AggMeasure[])measures.toArray(new MondrianDef.AggMeasure[0]);
        
        return aggName;
    }

    
    private Level findLevel(Schema schema, Attribute attribute) {
        for (Dimension dimension : schema.getDimensions()) {
            for (Hierarchy hierarchy : dimension.getHierarchies()) {
                for (Level level : hierarchy.getLevels()) {
                    if (level.getAttribute() == attribute) {
                        return level;
                    }
                }
            }
        }
        System.out.println("failed to locate level for attribute " + attribute.getLabel());
        return null;
    }
    
    private RolapBaseCubeMeasure findRolapMeasure(Schema schema, Measure measure) {
        RolapCube cube = ((MondrianSchema)schema).getRolapCube();
        for (RolapMember member : cube.getMeasuresMembers()) {
            // skip over calculated measures, etc
            if (member instanceof RolapBaseCubeMeasure) {
                RolapBaseCubeMeasure rolapMeasure = (RolapBaseCubeMeasure)member;
                if (rolapMeasure.getStarMeasure() == ((MondrianMeasure)measure).getRolapStarMeasure()) {
                    return rolapMeasure;
                }
            }
        }
        return null;
    }

    public String generateFull(Schema schema, List<? extends Output> outputs) {
        try {
            MondrianDef.Schema schemaDef = (MondrianDef.Schema)((RolapSchema)((MondrianSchema)schema).getRolapConnection().getSchema()).getXMLSchema().deepCopy();
            
            // locate the cube
            MondrianDef.Cube currentCube = null;
            for (MondrianDef.Cube cube : schemaDef.cubes) {
                if (cube.name.equals(((MondrianSchema)schema).getRolapCube().getName())) {
                    currentCube = cube;
                    break;
                }
            }
            
            if (!(currentCube.fact instanceof MondrianDef.Table)) {
                throw new RuntimeException("Fact Table must be of type TABLE");
            }

            MondrianDef.Table factTable = (MondrianDef.Table)currentCube.fact;

            List<MondrianDef.AggTable> aggTables = new ArrayList<MondrianDef.AggTable>();
            if (factTable.aggTables != null) {
                for (MondrianDef.AggTable aggTable : factTable.aggTables) {
                    aggTables.add(aggTable);
                }
            }
            
            for (Output output : outputs) {
                aggTables.add(generateMondrianDef(schema, output));
            }
            
            factTable.aggTables = (MondrianDef.AggTable[])aggTables.toArray(new MondrianDef.AggTable[0]);
            return schemaDef.toXML();
        } catch (XOMException e) {
            e.printStackTrace();
        }
        return null;
        
    }

}
