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
 * Copyright 2006 - 2024 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.aggdes.ui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaProvider;
import org.pentaho.aggdes.ui.xulstubs.XulSupressingBindingFactoryProxy;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.dom.Document;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
@ContextConfiguration( locations = { "/applicationContext.xml", "/plugins.xml" } )
public class MondrianFileSchemaProviderTest {

  private MondrianFileSchemaProvider schemaProvider;

  @Mock
  private Document doc;

  @Mock
  private XulDomContainer container;

  @Mock
  private BindingFactory bindingFactory;

  private EventRecorder eventRecorder;

  @Before
  public void setUp() throws Exception {
    KettleClientEnvironment.init();
    schemaProvider = new MondrianFileSchemaProvider();

    when( container.getDocumentRoot() ).thenReturn( doc );
    lenient().when( doc.getElementById( any( String.class ) ) ).thenReturn( mock( XulComponent.class ) );

    schemaProvider.setXulDomContainer( container );

    XulSupressingBindingFactoryProxy proxy = new XulSupressingBindingFactoryProxy();
    proxy.setProxiedBindingFactory( bindingFactory );
    schemaProvider.setBindingFactory( proxy );

    schemaProvider.onLoad();

    eventRecorder = new EventRecorder();
    eventRecorder.setLogging( true );
    eventRecorder.record( schemaProvider );
  }

  @Test
  public void testSchemaDefined_DefaultState() {
    schemaProvider.setSelected( true );

    assertEquals( getDefaultDefinedState(), schemaProvider.isSchemaDefined() );
  }

  @Test
  public void testSchemaDefined_Defined() {
    undefineSchema();

    defineSchema();

    assertEquals( Boolean.TRUE, eventRecorder.getLastValue( "schemaDefined" ) );
  }

  @Test
  public void testSchemaDefined_UnDefined() {
    defineSchema();

    undefineSchema();

    assertEquals( Boolean.FALSE, eventRecorder.getLastValue( "schemaDefined" ) );
  }

  private void defineSchema() {
    schemaProvider.setMondrianSchemaFilename( "abc" );
  }

  private void undefineSchema() {
    schemaProvider.setMondrianSchemaFilename( "" );
  }

  private boolean getDefaultDefinedState() {
    return schemaProvider.getMondrianSchemaFilename() != null
      && schemaProvider.getMondrianSchemaFilename().length() > 0;
  }
}
