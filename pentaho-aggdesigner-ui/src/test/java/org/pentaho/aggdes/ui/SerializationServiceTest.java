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


package org.pentaho.aggdes.ui;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import junit.framework.TestCase;
import org.junit.Test;
import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.ui.ext.SchemaProviderUiExtension;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaModel;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaProvider;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModelImpl;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.impl.AggListImpl;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.aggdes.ui.util.SerializationService;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;

import java.util.ArrayList;
import java.util.List;

import static org.pentaho.aggdes.util.TestUtils.getTestProperty;

public class SerializationServiceTest extends TestCase {

  public static AggList getAggList( SchemaStub schemaStub ) {
    AggList aggList = new AggListImpl();

    UIAggregateImpl aggImpl1 = new UIAggregateImpl();
    aggImpl1.setName( "testAgg1" );
    aggImpl1.setDescription( "testAggDesc" );
    aggImpl1.getAttributes().add( schemaStub.getAttributes().get( 0 ) );
    aggImpl1.setOutput( null );
    aggList.addAgg( aggImpl1 );

    UIAggregateImpl aggImpl2 = new UIAggregateImpl();
    aggImpl2.setName( "testAgg2" );
    aggImpl2.setDescription( "testAggDesc" );
    aggImpl2.getAttributes().add( schemaStub.getAttributes().get( 1 ) );
    aggImpl2.setOutput( null );
    aggList.addAgg( aggImpl2 );

    UIAggregateImpl aggImpl3 = new UIAggregateImpl();
    aggImpl3.setName( "testAgg3" );
    aggImpl3.setDescription( "testAggDesc" );
    aggImpl3.getAttributes().add( schemaStub.getAttributes().get( 2 ) );
    aggImpl3.setOutput( null );
    aggList.addAgg( aggImpl3 );

    UIAggregateImpl aggImpl4 = new UIAggregateImpl();
    aggImpl4.setName( "testAgg4" );
    aggImpl4.setDescription( "testAggDesc" );
    aggImpl4.getAttributes().add( schemaStub.getAttributes().get( 0 ) );
    aggImpl4.getAttributes().add( schemaStub.getAttributes().get( 1 ) );
    aggImpl4.setOutput( null );
    aggList.addAgg( aggImpl4 );

    return aggList;

  }

  @Test
  public void test() {

    try {
      KettleClientEnvironment.init();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    // serialize and deserialize to make sure things are going back and forth

    ConnectionModel connectionModel = new ConnectionModelImpl();

    List<SchemaProviderUiExtension> providerList = new ArrayList<SchemaProviderUiExtension>();
    MondrianFileSchemaProvider mondrianProvider = new MondrianFileSchemaProvider();
    providerList.add( mondrianProvider );
    mondrianProvider.setSelected( true );
    connectionModel.setDatabaseMeta( new DatabaseMeta() );
    MondrianFileSchemaModel schemaModel = new MondrianFileSchemaModel();
    connectionModel.setSelectedSchemaModel( schemaModel );
    schemaModel.setMondrianSchemaFilename( getTestProperty( "test.mondrian.foodmart.connectString.catalog" ) );
    connectionModel.setCubeName( "Sales" );


    SchemaStub schemaStub = new SchemaStub();
    AggList aggList = getAggList( schemaStub );

    SerializationService service = new SerializationService();
    service.setConnectionModel( connectionModel );
    service.setAggList( aggList );
    String output = service.serializeWorkspace( schemaStub );

    connectionModel.setSelectedSchemaModel( null );
    aggList.clearAggs();

    assertEquals( aggList.getSize(), 0 );
    assertNull( connectionModel.getCubeName() );
    String[] items = null;
    try {
      items = service.getConnectionAndAggListElements( output );
    } catch ( AggDesignerException e ) {
      e.printStackTrace();
      fail();
    }

    service.deserializeConnection( schemaStub, items[ 0 ], items[ 1 ] );

    assertEquals( connectionModel.getCubeName(), "Sales" );
    assertEquals(
      ( (MondrianFileSchemaModel) connectionModel.getSelectedSchemaModel() ).getMondrianSchemaFilename(),
      getTestProperty( "test.mondrian.foodmart.connectString.catalog" )
    );
    XStream xstream = service.getXStream( schemaStub );
    xstream.addPermission( NoTypePermission.NONE ); //forbid everything
    xstream.addPermission( NullPermission.NULL );   // allow "null"
    xstream.addPermission( PrimitiveTypePermission.PRIMITIVES ); // allow primitive types
    xstream.addPermission( AnyTypePermission.ANY );
    xstream.allowTypes( new String[] { "org.pentaho.aggdes.test.algorithm.impl.SchemaStub$AttributeStub" } );
    service.deserializeAggList( schemaStub, items[ 2 ], xstream );

    assertEquals( 4, aggList.getSize() );

    assertEquals( "testAgg1", aggList.getAgg( 0 ).getName() );
    assertEquals( "testAgg2", aggList.getAgg( 1 ).getName() );
    assertEquals( "testAgg3", aggList.getAgg( 2 ).getName() );
    assertEquals( "testAgg4", aggList.getAgg( 3 ).getName() );

    assertEquals( aggList.getAgg( 0 ).getAttributes().get( 0 ),
      schemaStub.getAttributes().get( 0 )
    );

    assertEquals( aggList.getAgg( 3 ).getAttributes().get( 1 ),
      schemaStub.getAttributes().get( 1 )
    );

    connectionModel.setCubeName( Messages.getString( "select_cube" ) );
    assertNull( connectionModel.getCubeName() );
  }

}
