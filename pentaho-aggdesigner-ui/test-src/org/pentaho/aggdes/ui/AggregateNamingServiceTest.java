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

package org.pentaho.aggdes.ui;

import static org.pentaho.aggdes.test.util.TestUtils.getTestProperty;
import static org.pentaho.aggdes.test.util.TestUtils.registerDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.aggdes.ui.util.AggregateNamingServiceImpl;

public class AggregateNamingServiceTest extends TestCase {

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

  public void test() {

    MondrianSchemaLoader loader = new MondrianSchemaLoader();
    Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
    System.out.println("CONN STR: " + connectString);
    parameterValues.put(loader.getParameters().get(0), connectString);
    parameterValues.put(loader.getParameters().get(1), "Sales");
    Schema schema = loader.createSchema(parameterValues);

    AggregateNamingServiceImpl impl = new AggregateNamingServiceImpl();

    UIAggregateImpl agg = new UIAggregateImpl();
    agg.setName("dummy");

    impl.nameAggregate(agg, null, schema);

    assertEquals("FoodMart_Sales_1", agg.getName());

    UIAggregateImpl agg2 = new UIAggregateImpl();

    List<UIAggregate> existing = new ArrayList<UIAggregate>();

    agg2.setName("dummy");

    impl.nameAggregate(agg2, existing, schema);

    assertEquals("FoodMart_Sales_1", agg2.getName());

    existing.add(agg);

    impl.nameAggregate(agg2, existing, schema);

    assertEquals("FoodMart_Sales_2", agg2.getName());

    agg.setName("FoodMart_Sales_2x");

    impl.nameAggregate(agg2, existing, schema);

    assertEquals("FoodMart_Sales_1", agg2.getName());

    agg.setName("FoodMart_Sales_209");

    impl.nameAggregate(agg2, existing, schema);

    assertEquals("FoodMart_Sales_210", agg2.getName());

    UIAggregateImpl agg3 = new UIAggregateImpl();

    agg2.setName("dummy");
    agg3.setName("dummy");

    List<UIAggregate> newaggs = new ArrayList<UIAggregate>();
    newaggs.add(agg2);
    newaggs.add(agg3);

    impl.nameAggregates(newaggs, existing, schema);

    assertEquals("FoodMart_Sales_210", agg2.getName());
    assertEquals("FoodMart_Sales_211", agg3.getName());

  }

}
