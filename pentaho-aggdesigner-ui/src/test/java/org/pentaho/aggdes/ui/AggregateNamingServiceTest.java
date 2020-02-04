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
 * Copyright 2006 - 2020 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.aggdes.ui;

import junit.framework.TestCase;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapSchema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.aggdes.model.mondrian.MondrianSchema;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.aggdes.ui.util.AggregateNamingServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class AggregateNamingServiceTest extends TestCase {

  @Mock MondrianSchema schema;
  @Mock RolapConnection conn;
  @Mock RolapSchema monSchema;
  @Mock RolapCube rolapCube;

  @Before
  public void before() {
    when( schema.getRolapConnection() ).thenReturn( conn );
    when( conn.getSchema() ).thenReturn( monSchema );
    when( monSchema.getName() ).thenReturn( "FoodMart" );
    when( schema.getRolapCube() ).thenReturn( rolapCube );
    when( rolapCube.getName() ).thenReturn( "Sales" );
  }

  @Test
  public void test() {
    AggregateNamingServiceImpl impl = new AggregateNamingServiceImpl();

    UIAggregateImpl agg = new UIAggregateImpl();
    agg.setName( "dummy" );
    impl.nameAggregate( agg, null, schema );

    assertEquals( "FoodMart_Sales_1", agg.getName() );

    UIAggregateImpl agg2 = new UIAggregateImpl();

    List<UIAggregate> existing = new ArrayList<>();

    agg2.setName( "dummy" );

    impl.nameAggregate( agg2, existing, schema );

    assertEquals( "FoodMart_Sales_1", agg2.getName() );

    existing.add( agg );

    impl.nameAggregate( agg2, existing, schema );

    assertEquals( "FoodMart_Sales_2", agg2.getName() );

    agg.setName( "FoodMart_Sales_2x" );

    impl.nameAggregate( agg2, existing, schema );

    assertEquals( "FoodMart_Sales_1", agg2.getName() );

    agg.setName( "FoodMart_Sales_209" );

    impl.nameAggregate( agg2, existing, schema );

    assertEquals( "FoodMart_Sales_210", agg2.getName() );

    UIAggregateImpl agg3 = new UIAggregateImpl();

    agg2.setName( "dummy" );
    agg3.setName( "dummy" );

    List<UIAggregate> newAggs = new ArrayList<>();
    newAggs.add( agg2 );
    newAggs.add( agg3 );

    impl.nameAggregates( newAggs, existing, schema );

    assertEquals( "FoodMart_Sales_210", agg2.getName() );
    assertEquals( "FoodMart_Sales_211", agg3.getName() );
  }

}
