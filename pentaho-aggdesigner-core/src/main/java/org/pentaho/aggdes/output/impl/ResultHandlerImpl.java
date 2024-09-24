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

import org.pentaho.aggdes.algorithm.*;
import org.pentaho.aggdes.algorithm.impl.AlgorithmImpl;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

/**
 * Default implementation of the {@link ResultHandler} interface that prints
 * "CREATE TABLE", "CREATE INDEX" and "INSERT INTO ... SELECT" statements
 * for each aggregate discovered by the algorithm.
 *
 * @author jhyde
 * @version $Id: ResultHandlerImpl.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 15, 2008
 */
public class ResultHandlerImpl implements ResultHandler {
    public static final String NL = System.getProperty("line.separator");

    private final List<Parameter> parameterList =
        new ArrayList<Parameter>(Arrays.asList(ParameterEnum.values()));

    /**
     * Creates a ResultHandlerImpl.
     */
    public ResultHandlerImpl() {
    }

    public List<Parameter> getParameters() {
        return parameterList;
    }

    public String getName() {
        return AlgorithmImpl.getBaseName(getClass());
    }

    
    private PrintWriter getPrintWriter(String outputFileName) {
        PrintWriter pw;
        if (outputFileName != null) {
            try {
                final File outputFile = new File(outputFileName);
                final File parentFile = outputFile.getCanonicalFile().getParentFile();
                if (parentFile != null && parentFile.mkdirs()) {
                    System.out.println("Created folder " + parentFile);
                }
                final Writer writer = new FileWriter(outputFile);
                pw = new PrintWriter(writer);
            } catch (IOException e) {
                throw new RuntimeException(
                    "Error while opening output file " + outputFileName,
                    e);
            }
        } else {
            pw = new PrintWriter(System.out);
        }
        return pw;
    }
    
    private void closePrintWriter(String outputFileName, PrintWriter pw) {
      if (outputFileName != null) {
          pw.close();
      }
  }
        
    public void handle(
        Map<Parameter, Object> parameterValues,
        Schema schema, 
        Result result)
    {
       
        final boolean doTables =
            parameterValues.get(ParameterEnum.tables) != null
                && (Boolean) parameterValues.get(ParameterEnum.tables);
        final boolean doIndexes =
            parameterValues.get(ParameterEnum.indexes) != null
                && (Boolean) parameterValues.get(ParameterEnum.indexes);
        final boolean doPopulate =
            parameterValues.get(ParameterEnum.populate) != null
                && (Boolean) parameterValues.get(ParameterEnum.populate);
        final boolean doMondrianSchema =
            parameterValues.get(ParameterEnum.mondrianSchema) != null
                && (Boolean) parameterValues.get(ParameterEnum.mondrianSchema);
        
        AggregateTableOutputFactory outputFactory = new AggregateTableOutputFactory();
        
        List<Output> outputs = outputFactory.createOutputs(schema, result.getAggregates());
        
        if (doTables) {
            final String tableOutput = (String)parameterValues.get(ParameterEnum.tableOutput);
            final CreateTableGenerator generator = new CreateTableGenerator();
            PrintWriter pw = getPrintWriter(tableOutput);
            pw.println(generator.generateFull(schema, outputs));
            pw.flush();
            closePrintWriter(tableOutput, pw);
        }

        if (doPopulate) {
            final String populateOutput = (String)parameterValues.get(ParameterEnum.populateOutput);
            final PopulateTableGenerator generator = new PopulateTableGenerator();
            PrintWriter pw = getPrintWriter(populateOutput);
            pw.println(generator.generateFull(schema, outputs));
            pw.flush();
            closePrintWriter(populateOutput, pw);
        }
        
        if (doMondrianSchema) {
            final String mondrianOutput = (String)parameterValues.get(ParameterEnum.mondrianOutput);
            final MondrianSchemaGenerator generator = new MondrianSchemaGenerator();
            PrintWriter pw = getPrintWriter(mondrianOutput);
            pw.println(generator.generateFull(schema, outputs));
            pw.flush();
            closePrintWriter(mondrianOutput, pw);
        }
    }

    /**
     * Enumeration of parameters accepted by this result handler.
     */
    enum ParameterEnum implements Parameter {
        tables(
            "Whether to output CREATE TABLE statements.", false, Type.BOOLEAN),

        tableOutput(
            "File to write table output, defaults to system output",false, Type.STRING),
            
        indexes(
            "Whether to output CREATE INDEX statements.", false, Type.BOOLEAN),

        indexOutput(
            "File to write table output, defaults to system output",false, Type.STRING),
            
        populate(
            "Whether to output INSERT INTO ... SELECT statements.", false,
            Type.BOOLEAN),
        
        populateOutput(
            "File to write dml output, defaults to system output",false, Type.STRING),
            
        mondrianSchema(
            "Whether to output AggName elements within the Mondrian Schema", false, Type.BOOLEAN),

        mondrianOutput(
            "File to write dml output, defaults to mondrian.xml",false, Type.STRING);
        

        
        private final String description;
        private final boolean required;
        private final Type type;

        ParameterEnum(
            String description, boolean required, Type type)
        {
            this.description = description;
            this.required = required;
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public Type getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name();
        }
    }
}

// End ResultHandlerImpl.java
