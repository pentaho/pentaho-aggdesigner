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

package org.pentaho.aggdes.test;

import static org.pentaho.aggdes.test.util.TestUtils.getTestProperty;
import static org.pentaho.aggdes.test.util.TestUtils.registerDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.StatisticsProvider;
import org.pentaho.aggdes.model.mondrian.Messages;
import org.pentaho.aggdes.model.mondrian.MondrianAttribute;
import org.pentaho.aggdes.model.mondrian.MondrianSchema;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader;
import org.pentaho.aggdes.model.mondrian.MondrianTable;

public class MondrianSchemaLoaderTestIT extends TestCase {

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
    
    registerDriver(getTestProperty("test.jdbc.driver.classpath"), getTestProperty("test.jdbc.driver.classname")); //$NON-NLS-1$//$NON-NLS-2$
  }

  public void testFoodmartLoading() {
    MondrianSchemaLoader loader = new MondrianSchemaLoader();
    Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
    System.out.println("CONN STR: " + connectString);
    parameterValues.put(loader.getParameters().get(0), connectString);
    parameterValues.put(loader.getParameters().get(1), "Sales");
  }
  
  public void testInvalidConnectStringParameter() {
    try {
      MondrianSchemaLoader loader = new MondrianSchemaLoader();
      Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
      parameterValues.put(loader.getParameters().get(0), "badconnstr");
      //parameterValues.put(loader.getParameters().get(1), "cube");        
      loader.createSchema(parameterValues);
      fail();
    } catch (mondrian.olap.MondrianException e) {
      assertEquals(
          "Mondrian Error:Internal error: Connect string 'badconnstr=; Catalog='null'' must contain either 'Jdbc' or 'DataSource'",
          e.getMessage());
    }
  }

  public void testInvalidCubeParameter() {
    try {
      MondrianSchemaLoader loader = new MondrianSchemaLoader();
      Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
      parameterValues.put(loader.getParameters().get(0), connectString);
      parameterValues.put(loader.getParameters().get(1), "InvalidCube");
      loader.createSchema(parameterValues);
      fail();
    } catch (mondrian.olap.MondrianException e) {
      assertEquals(e.getMessage(), "Mondrian Error:MDX cube 'InvalidCube' not found");
    }
  }

    public void testMondrianSchemaModel() throws Exception {
        MondrianSchemaLoader loader = new MondrianSchemaLoader();
        Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
        System.out.println("CONN STR: " + connectString);
        parameterValues.put(loader.getParameters().get(0), connectString);
        parameterValues.put(loader.getParameters().get(1), "Sales");
        Schema schema = loader.createSchema(parameterValues);
        
        assertTrue(schema instanceof MondrianSchema);
        MondrianSchema mondrianSchema = (MondrianSchema)schema;

        assertNotNull(mondrianSchema.getDatabaseMetaData());

        // spot check measures, attributes, and tables
        assertEquals(7, schema.getMeasures().size());
        assertEquals("sales_fact_1997.Unit Sales", schema.getMeasures().get(0).getLabel());
        
        // spot check the fact_count measure
        assertEquals("sales_fact_1997.fact_count", schema.getMeasures().get(6).getLabel());
        
        assertEquals(schema.getMeasures().get(0).isDistinct(), false);
        // check that the fact table is the first table in table list
        assertEquals(schema.getMeasures().get(0).getTable(), schema.getTables().get(0));
        assertEquals(schema.getMeasures().get(0).estimateSpace(), 4.0);
        assertEquals(schema.getMeasures().get(0).getAncestorAttributes(), null);
        
        assertEquals(8, schema.getTables().size());
        assertEquals("sales_fact_1997", schema.getTables().get(0).getLabel());
        assertEquals(schema.getTables().get(1).getParent(), schema.getTables().get(0));

        assertTrue(schema.getTables().get(0) instanceof MondrianTable);
        assertNotNull(((MondrianTable)schema.getTables().get(0)).getStarTable());
        
        assertEquals(27, schema.getAttributes().size());
        assertEquals("[store].[Store Country]", schema.getAttributes().get(0).getLabel());
        assertEquals(schema.getAttributes().get(0).getTable(), schema.getTables().get(1));
        
        assertTrue(schema.getAttributes().get(0) instanceof MondrianAttribute);
        assertEquals(((MondrianAttribute)schema.getAttributes().get(0)).getDistinctValueCount(), 3.0);
        assertEquals(((MondrianAttribute)schema.getAttributes().get(0)).estimateSpace(), 20.0);
        
        // spot check a couple of attribute ancestor lists
        assertEquals(schema.getAttributes().get(3).getAncestorAttributes().size(), 3);
        assertEquals(schema.getAttributes().get(22).getAncestorAttributes().get(0).getLabel(), "[customer].[Country]");
        
        // spot check level mapping to attributes

        assertEquals(schema.getDimensions().get(1).getName(), "Store Size in SQFT");
        
        assertEquals(schema.getDimensions().get(0).getHierarchies().get(0).getName(), "Store");
        
        assertEquals(schema.getDimensions().get(2).getHierarchies().get(0).getLevels().get(1).getName(), "Store Type");
        
        // verifies quarter is the expected attribute in the level
        assertEquals(schema.getAttributes().get(7), schema.getDimensions().get(3).getHierarchies().get(0).getLevels().get(1).getAttribute());
    }
    
    public void testMessages() {
      assertEquals(Messages.getString("MondrianSchemaLoader.ERROR_0001_MONDRIAN_DEPENDENCY_ERROR"), "The version of mondrian on the classpath is incompatible with this version of aggregate designer.");
      assertEquals(Messages.getString("INVALID_STR"), "!INVALID_STR!");
      assertEquals(Messages.getString("INVALID_STR", "a", "b"), "!INVALID_STR!");
    }
    
    public void testMondrianStatisticsProvider() {
      MondrianSchemaLoader loader = new MondrianSchemaLoader();
      Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
      System.out.println("CONN STR: " + connectString);
      parameterValues.put(loader.getParameters().get(0), connectString);
      parameterValues.put(loader.getParameters().get(1), "Sales");
      Schema schema = loader.createSchema(parameterValues);
      StatisticsProvider statsProvider = schema.getStatisticsProvider();
      assertNotNull(statsProvider);
      
      assertEquals(statsProvider.getFactRowCount(), 86837.0);
      
      List<Attribute> attributes = new ArrayList<Attribute>();
      attributes.add(schema.getAttributes().get(0));
      
      // spot check that these methods return a meaningful value
      
      assertEquals(statsProvider.getRowCount(attributes), 3.0);
      assertEquals(statsProvider.getLoadTime(attributes), 3.8555688E7);
      assertEquals(statsProvider.getSpace(attributes), 20.0);
    }
}
