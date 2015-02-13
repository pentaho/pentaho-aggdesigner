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

package org.pentaho.aggdes.test;

import static org.pentaho.aggdes.test.util.TestUtils.getTestProperty;
import static org.pentaho.aggdes.test.util.TestUtils.registerDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.impl.AggregateTableOutput;
import org.pentaho.aggdes.output.impl.AggregateTableOutputFactory;
import org.pentaho.aggdes.output.impl.MondrianSchemaGenerator;
import org.pentaho.aggdes.test.util.TestAggregate;
import org.pentaho.aggdes.test.util.TestResult;
import org.pentaho.aggdes.test.util.TestUtils;

public class MondrianSchemaOutputTest extends TestCase {

    protected String connectString;

    public void setUp() throws Exception {
      // load resources/test.properties
      System.out.println("SETUP");
      connectString = getTestProperty("test.mondrian.foodmart.connectString", //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.provider"), //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.jdbc"), //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.username"), //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.password"), //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.catalog")); //$NON-NLS-1$

      System.out.println(connectString);
      registerDriver(getTestProperty("test.jdbc.driver.classpath"), getTestProperty("test.jdbc.driver.classname")); //$NON-NLS-1$//$NON-NLS-2$
    }

    public void testDummy() {}

    @Ignore
    public void _testBasicSchemaGen() {

        // load a mondrian schema

        MondrianSchemaLoader loader = new MondrianSchemaLoader();
        Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
        System.out.println("CONN STR: " + connectString);
        parameterValues.put(loader.getParameters().get(0), connectString);
        parameterValues.put(loader.getParameters().get(1), "Sales");
        Schema schema = loader.createSchema(parameterValues);

        TestResult result = new TestResult();
        TestAggregate aggregate = new TestAggregate();
        aggregate.setCandidateTableName("table_01");
        aggregate.addAttribute(schema.getAttributes().get(3)); // store name level
        aggregate.addAttribute(schema.getAttributes().get(12)); // product department level

        // add all measures
        for (Measure measure : schema.getMeasures()) {
            aggregate.addMeasure(measure);
        }

        result.addAggregate(aggregate);

        MondrianSchemaGenerator generator = new MondrianSchemaGenerator();

        Class clazzes[] = generator.getSupportedOutputClasses();
        assertEquals(clazzes.length, 1);
        assertEquals(clazzes[0], AggregateTableOutput.class);

        AggregateTableOutput aggTableOutput = new AggregateTableOutput(null);

        assertTrue(generator.canGenerate(schema, aggTableOutput));

        AggregateTableOutputFactory factory = new AggregateTableOutputFactory();

        // test basic factory calls
        assertEquals(factory.getOutputClass(), AggregateTableOutput.class);
        assertTrue(factory.canCreateOutput(schema));

        // test basic output calls
        AggregateTableOutput.ColumnOutput colOutput = new AggregateTableOutput.ColumnOutput(null, null);

        assertNull(colOutput.getName());
        assertNull(colOutput.getAttribute());

        colOutput.setName("testName");
        assertEquals(colOutput.getName(), "testName");

        colOutput.setAttribute(schema.getAttributes().get(3));
        assertEquals(colOutput.getAttribute(), schema.getAttributes().get(3));


        Output output = factory.createOutput(schema, aggregate);

        String results = generator.generate(schema, output);
        assertEquals(
                TestUtils.fold("<AggName name=\"table_01\">\n" +
                "\t<AggFactCount column=\"sales_fact_1997_fact_count\">\n" +
                "\t</AggFactCount>\n" +
                "\t<AggMeasure column=\"sales_fact_1997_Unit_Sales\" name=\"[Measures].[Unit Sales]\">\n" +
                "\t</AggMeasure>\n" +
                "\t<AggMeasure column=\"sales_fact_1997_Store_Cost\" name=\"[Measures].[Store Cost]\">\n" +
                "\t</AggMeasure>\n" +
                "\t<AggMeasure column=\"sales_fact_1997_Store_Sales\" name=\"[Measures].[Store Sales]\">\n" +
                "\t</AggMeasure>\n" +
                "\t<AggMeasure column=\"sales_fact_1997_Sales_Count\" name=\"[Measures].[Sales Count]\">\n" +
                "\t</AggMeasure>\n" +
                "\t<AggMeasure column=\"sales_fact_1997_Customer_Count\" name=\"[Measures].[Customer Count]\">\n" +
                "\t</AggMeasure>\n" +
                "\t<AggMeasure column=\"sales_fact_1997_Promotion_Sale\" name=\"[Measures].[Promotion Sales]\">\n" +
                "\t</AggMeasure>\n" +
                "\t<AggLevel column=\"store_Store_Name\" name=\"[Store].[Store Name]\">\n" +
                "\t</AggLevel>\n" +
                "\t<AggLevel column=\"product_class_Product_Departme\" name=\"[Product].[Product Department]\">\n" +
                "\t</AggLevel>\n" +
                "</AggName>\n"),
                results
        );

        TestAggregate aggregate2 = new TestAggregate();
        aggregate2.setCandidateTableName("table_02");
        aggregate2.addAttribute(schema.getAttributes().get(4)); // store name level
        aggregate2.addAttribute(schema.getAttributes().get(13)); // product family level

        for (Measure measure : schema.getMeasures()) {
            aggregate2.addMeasure(measure);
        }

        List<Aggregate> aggregates = new ArrayList<Aggregate>();
        aggregates.add(aggregate);
        aggregates.add(aggregate2);


        List<Output> outputs = factory.createOutputs(schema, aggregates);
        String schemaOutput = generator.generateFull(schema, outputs);

        assertTrue(schemaOutput.indexOf("<AggName name=\"table_01\">") >= 0);
        assertTrue(schemaOutput.indexOf("<AggName name=\"table_02\">") >= 0);
    }
}
