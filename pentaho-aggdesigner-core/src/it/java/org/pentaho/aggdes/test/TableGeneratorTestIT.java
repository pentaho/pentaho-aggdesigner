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


package org.pentaho.aggdes.test;

import static org.pentaho.aggdes.test.util.TestUtils.getTestProperty;
import static org.pentaho.aggdes.test.util.TestUtils.registerDriver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import mondrian.spi.Dialect.DatabaseProduct;

import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.mondrian.MondrianDialect;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.impl.AggregateTableOutput;
import org.pentaho.aggdes.output.impl.CreateTableGenerator;
import org.pentaho.aggdes.output.impl.PopulateTableGenerator;
import org.pentaho.aggdes.output.impl.ResultHandlerImpl;
import org.pentaho.aggdes.test.util.TestAggregate;
import org.pentaho.aggdes.test.util.TestResult;
import org.pentaho.aggdes.test.util.TestUtils;

public class TableGeneratorTestIT extends TestCase {

  protected String connectString;

  @Override public void setUp() throws Exception {
    // load resources/test.properties
    connectString = getTestProperty( "test.mondrian.foodmart.connectString", //$NON-NLS-1$
      getTestProperty( "test.mondrian.foodmart.connectString.provider" ), //$NON-NLS-1$
      getTestProperty( "test.mondrian.foodmart.connectString.jdbc" ), //$NON-NLS-1$
      getTestProperty( "test.mondrian.foodmart.connectString.username" ), //$NON-NLS-1$
      getTestProperty( "test.mondrian.foodmart.connectString.password" ), //$NON-NLS-1$
      getTestProperty( "test.mondrian.foodmart.connectString.catalog" ) ); //$NON-NLS-1$

    registerDriver( getTestProperty( "test.jdbc.driver.classpath" ),
      getTestProperty( "test.jdbc.driver.classname" ) ); //$NON-NLS-1$//$NON-NLS-2$
  }

  public void testBasicTableGen() {

    // load a mondrian schema

    MondrianSchemaLoader loader = new MondrianSchemaLoader();
    Map<Parameter, Object> parameterValues = new HashMap<>();
    parameterValues.put( loader.getParameters().get( 0 ), connectString );
    parameterValues.put( loader.getParameters().get( 1 ), "Sales" );
    Schema schema = loader.createSchema( parameterValues );

    TestResult result = new TestResult();
    TestAggregate aggregate = new TestAggregate();
    aggregate.addAttribute( schema.getAttributes().get( 3 ) ); // store name level
    aggregate.addAttribute( schema.getAttributes().get( 12 ) ); // product name level

    // add all measures
    for ( Measure measure : schema.getMeasures() ) {
      aggregate.addMeasure( measure );
    }

    result.addAggregate( aggregate );

    AggregateTableOutput output = new AggregateTableOutput( aggregate );
    output.setTableName( "table_01" );
    output.setCatalogName( "cat_01" );
    output.setSchemaName( "schema_01" );
    assertEquals( output.getTableName(), "table_01" );
    assertEquals( output.getCatalogName(), "cat_01" );
    assertEquals( output.getSchemaName(), "schema_01" );

    output.setCatalogName( null );
    output.setSchemaName( null );


    output.getColumnOutputs()
      .add( new AggregateTableOutput.ColumnOutput( "column_01", aggregate.getAttributes().get( 0 ) ) );
    output.getColumnOutputs()
      .add( new AggregateTableOutput.ColumnOutput( "column_02", aggregate.getAttributes().get( 1 ) ) );

    int n = 1;
    for ( Measure measure : aggregate.getMeasures() ) {
      output.getColumnOutputs().add( new AggregateTableOutput.ColumnOutput( "measure_0" + n, measure ) );
      n++;
    }

    List<Output> outputs = new ArrayList<>();
    outputs.add( output );

    CreateTableGenerator createGenerator = new CreateTableGenerator();

    Class clazzes[] = createGenerator.getSupportedOutputClasses();
    assertEquals( clazzes.length, 1 );
    assertEquals( clazzes[ 0 ], AggregateTableOutput.class );

    AggregateTableOutput aggTableOutput = new AggregateTableOutput( null );

    assertTrue( createGenerator.canGenerate( schema, aggTableOutput ) );


    if ( ( (MondrianDialect) schema.getDialect() ).getMondrianDialect().getDatabaseProduct()
      == DatabaseProduct.ORACLE ) {
      String expectedResults = TestUtils.fold( "-- Aggregate table table_01\n" +
        "-- Estimated 0 rows, 0 bytes\n" +
        "CREATE TABLE \"table_01\" (\n" +
        "    \"column_01\" VARCHAR2(30),\n" +
        "    \"column_02\" VARCHAR2(60),\n" +
        "    \"measure_01\" DECIMAL(10,4),\n" +
        "    \"measure_02\" DECIMAL(10,4),\n" +
        "    \"measure_03\" DECIMAL(10,4),\n" +
        "    \"measure_04\" INTEGER,\n" +
        "    \"measure_05\" INTEGER,\n" +
        "    \"measure_06\" DECIMAL(10,4),\n" +
        "    \"measure_07\" INTEGER);\n" );

      assertEquals( expectedResults, createGenerator.generate( schema, output ) );
      assertEquals( expectedResults, createGenerator.generateFull( schema, outputs ) );

    } else if ( ( (MondrianDialect) schema.getDialect() ).getMondrianDialect().getDatabaseProduct()
      == DatabaseProduct.MYSQL ) {
      String expectedResults = TestUtils.fold( "-- Aggregate table table_01\n" +
        "-- Estimated 0 rows, 0 bytes\n" +
        "CREATE TABLE `table_01` (\n" +
        "    `column_01` VARCHAR,\n" +
        "    `column_02` VARCHAR,\n" +
        "    `measure_01` DOUBLE,\n" +
        "    `measure_02` DOUBLE,\n" +
        "    `measure_03` DOUBLE,\n" +
        "    `measure_04` INTEGER,\n" +
        "    `measure_05` INTEGER,\n" +
        "    `measure_06` DOUBLE,\n" +
        "    `measure_07` INTEGER);\n" );
      assertEquals( expectedResults, createGenerator.generate( schema, output ) );
    }

    PopulateTableGenerator popGenerator = new PopulateTableGenerator();

    clazzes = popGenerator.getSupportedOutputClasses();
    assertEquals( clazzes.length, 1 );
    assertEquals( clazzes[ 0 ], AggregateTableOutput.class );

    aggTableOutput = new AggregateTableOutput( null );

    assertTrue( popGenerator.canGenerate( schema, aggTableOutput ) );

    if ( ( (MondrianDialect) schema.getDialect() ).getMondrianDialect().getDatabaseProduct()
      == DatabaseProduct.ORACLE ) {
      assertEquals(
        TestUtils.fold( "-- Populate aggregate table table_01\n" +
          "INSERT INTO \"table_01\" (\n" +
          "    \"column_01\",\n" +
          "    \"column_02\",\n" +
          "    \"measure_01\")\n" +
          "select \n" +
          "    \"store\".\"store_name\" as \"column_01\", \n" +
          "    \"product_class\".\"product_family\" as \"column_02\", \n" +
          "    sum(\"sales_fact_1997\".\"unit_sales\") as \"measure_01\", \n" +
          "    sum(\"sales_fact_1997\".\"store_cost\") as \"measure_02\", \n" +
          "    sum(\"sales_fact_1997\".\"store_sales\") as \"measure_03\", \n" +
          "    count(\"sales_fact_1997\".\"product_id\") as \"measure_04\", \n" +
          "    count(distinct \"sales_fact_1997\".\"customer_id\") as \"measure_05\", \n" +
          "    sum((case when \"sales_fact_1997\".\"promotion_id\" = 0 then 0 else \"sales_fact_1997\""
          + ".\"store_sales\" end)) as \"measure_06\", \n"
          +
          "    count(*) as \"measure_07\"\n" +
          "from \n" +
          "    \"sales_fact_1997\" \"sales_fact_1997\", \n" +
          "    \"store\" \"store\", \n" +
          "    \"product_class\" \"product_class\",\n" +
          "    \"product\" \"product\"\n" +
          "where \n" +
          "    \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\" and \n" +
          "    \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\" and \n" +
          "    \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n" +
          "group by \n" +
          "    \"store\".\"store_name\", \n" +
          "    \"product_class\".\"product_family\";\n" ),
        popGenerator.generate( schema, output )
      );
    } else if ( ( (MondrianDialect) schema.getDialect() ).getMondrianDialect().getDatabaseProduct()
      == DatabaseProduct.MYSQL ) {
      assertEquals(
        TestUtils.fold( "-- Populate aggregate table table_01\n" +
          "INSERT INTO `table_01` (\n" +
          "    `column_01`,\n" +
          "    `column_02`,\n" +
          "    `measure_01`,\n" +
          "    `measure_02`,\n" +
          "    `measure_03`,\n" +
          "    `measure_04`,\n" +
          "    `measure_05`,\n" +
          "    `measure_06`,\n" +
          "    `measure_07`)\n" +
          "select\n" +
          "    `store`.`store_name` as `column_01`,\n" +
          "    `product_class`.`product_department` as `column_02`,\n" +
          "    sum(`sales_fact_1997`.`unit_sales`) as `measure_01`,\n" +
          "    sum(`sales_fact_1997`.`store_cost`) as `measure_02`,\n" +
          "    sum(`sales_fact_1997`.`store_sales`) as `measure_03`,\n" +
          "    count(`sales_fact_1997`.`product_id`) as `measure_04`,\n" +
          "    count(distinct `sales_fact_1997`.`customer_id`) as `measure_05`,\n" +
          "    sum((case when `sales_fact_1997`.`promotion_id` = 0 then 0 else `sales_fact_1997`.`store_sales` end)) "
          + "as `measure_06`,\n"
          +
          "    count(*) as `measure_07`\n" +
          "from\n" +
          "    `sales_fact_1997` as `sales_fact_1997`,\n" +
          "    `store` as `store`,\n" +
          "    `product_class` as `product_class`,\n" +
          "    `product` as `product`\n" +
          "where\n" +
          "    `sales_fact_1997`.`store_id` = `store`.`store_id`\n" +
          "and\n" +
          "    `sales_fact_1997`.`product_id` = `product`.`product_id`\n" +
          "and\n" +
          "    `product`.`product_class_id` = `product_class`.`product_class_id`\n" +
          "group by\n" +
          "    `store`.`store_name`,\n" +
          "    `product_class`.`product_department`;\n" ),
        popGenerator.generate( schema, output )
      );
    }
  }

  public void testResultHandlerImpl() {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream pw = new PrintStream( baos );
    PrintStream orig = System.out;
    System.setOut( pw );

    MondrianSchemaLoader loader = new MondrianSchemaLoader();
    Map<Parameter, Object> parameterValues = new HashMap<>();
    System.out.println( "CONN STR: " + connectString );
    parameterValues.put( loader.getParameters().get( 0 ), connectString );
    parameterValues.put( loader.getParameters().get( 1 ), "Sales" );
    Schema schema = loader.createSchema( parameterValues );

    TestResult result = new TestResult();
    TestAggregate aggregate = new TestAggregate();
    aggregate.setCandidateTableName( "candidate_01" );
    aggregate.addAttribute( schema.getAttributes().get( 3 ) ); // store name level
    aggregate.addAttribute( schema.getAttributes().get( 12 ) ); // product name level

    // add all measures
    for ( Measure measure : schema.getMeasures() ) {
      aggregate.addMeasure( measure );
    }

    result.addAggregate( aggregate );

    ResultHandlerImpl resultHandler = new ResultHandlerImpl();

    assertEquals( resultHandler.getName(), "ResultHandlerImpl" );

    assertEquals( resultHandler.getParameters().size(), 8 );

    assertEquals( resultHandler.getParameters().get( 0 ).isRequired(), false );
    assertEquals( resultHandler.getParameters().get( 0 ).getType(), Parameter.Type.BOOLEAN );
    assertEquals( resultHandler.getParameters().get( 0 ).getDescription(),
      "Whether to output CREATE TABLE statements." );
    assertEquals( resultHandler.getParameters().get( 0 ).getName(), "tables" );


    Map<Parameter, Object> params = new HashMap<>();
    params.put( resultHandler.getParameters().get( 0 ), true );
    params.put( resultHandler.getParameters().get( 4 ), true );
    params.put( resultHandler.getParameters().get( 6 ), true );

    // no output file params, write to system.out
    resultHandler.handle( params, schema, result );

    String results = baos.toString();

    // verify ddl, dml and schema output are available
    assertTrue( results.indexOf( "CREATE TABLE" ) >= 0 );
    assertTrue( results.indexOf( "INSERT INTO" ) >= 0 );
    assertTrue( results.indexOf( "<Schema" ) >= 0 );
    System.setOut( orig );

    File outputFile = new File( "resulthandlerimpl_output.txt" );
    if ( outputFile.exists() ) {
      outputFile.delete();
    }

    params.clear();
    params.put( resultHandler.getParameters().get( 0 ), true );
    params.put( resultHandler.getParameters().get( 1 ), "resulthandlerimpl_output.txt" );

    // output file param first element, write to file
    resultHandler.handle( params, schema, result );

    assertTrue( outputFile.exists() );
  }

}
