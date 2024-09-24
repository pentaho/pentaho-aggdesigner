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

package org.pentaho.aggdes.ui;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.ui.ext.SchemaProviderUiExtension;
import org.pentaho.aggdes.ui.form.controller.ConnectionController;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.aggdes.ui.xulstubs.XulDialogStub;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.XulEventHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class ConnectionControllerTest {

  private ConnectionController controller;

  @Mock
  private Document doc;

  @Mock
  private XulDomContainer container;

  @Mock
  private XulEventHandler dataHandler;

  @Mock
  private ConnectionModel model;

  @Mock
  private OutputService outputService;

  @Mock
  private SchemaProviderUiExtension aSchemaProvider;

  @Mock
  private SchemaModel providerModel;

  private List<String> cubeNames;

  @Before
  public void setUp() {
    controller = new ConnectionController();
    controller.setConnectionModel( model );
    controller.setOutputService( outputService );

    cubeNames = Arrays.asList( "testCube1", "testCube2" );

    when( container.getDocumentRoot() ).thenReturn( doc );

    controller.setXulDomContainer( container );
    controller.setDataHandler( dataHandler );
  }

  private void setupSchemaProviderDefaults() throws AggDesignerException {
    lenient().when( aSchemaProvider.isSelected() ).thenReturn( true );
    lenient().when( aSchemaProvider.getCubeNames() ).thenReturn( cubeNames );
    lenient().when( aSchemaProvider.getSchemaModel() ).thenReturn( providerModel );

    controller.setSchemaProviders( Collections.singletonList( aSchemaProvider ) );
  }

  @Test
  public void testReset() throws Exception {
    setupSchemaProviderDefaults();
    doNothing().when( model ).reset();
    doNothing().when( aSchemaProvider ).reset();

    controller.reset();
  }

  //  @Ignore
  //  @Test
  //  public void testOnLoad() {
  //    fail("Not yet implemented");
  //  }

  @Test
  public void testLoadDatabaseDialog() {
    final XulDialog dialog = mock( XulDialog.class );
    when( doc.getElementById( ConnectionController.GENERAL_DATASOURCE_WINDOW ) ).thenReturn( dialog );

    when( dataHandler.getData() ).thenReturn( new DatabaseMeta() );
    when( model.getDatabaseMeta() ).thenReturn( new DatabaseMeta() );

    controller.loadDatabaseDialog();

    verify( dialog ).show();
    verify( model ).setDatabaseMeta( any( DatabaseMeta.class ) );
  }

/*
  @Test
  public void testConnect_Success() throws AggDesignerException {
    setupSchemaProviderDefaults();
    controller.setSelectedSchemaProvider(); //mimics the apply having been pressed

    final XulDialog waitDialog = new XulDialogStub();
    when( doc.getElementById( ConnectionController.ANON_WAIT_DIALOG ) ).thenReturn( waitDialog );

    final XulDialog connDialog = mock( XulDialog.class );
    when( doc.getElementById( ConnectionController.CONNECTION_DIALOG ) ).thenReturn( connDialog );

    final Schema schema = mock( Schema.class );
    when( aSchemaProvider.loadSchema( "testCube" ) ).thenReturn( schema );
    when( model.getCubeName() ).thenReturn( "testCube" );

    controller.connect();

    verify( connDialog ).hide();
    verify( model ).setSchema( schema );
  }


  @Test
  public void testConnect_Success() throws AggDesignerException {
    setupSchemaProviderDefaults();
    controller.setSelectedSchemaProvider(); //mimics the apply having been pressed

    //now call connect
    context.checking( new Expectations() {
      {
        //using dialog stub here instead of a mock so we get thread blocking which is needed to have this test run
        sucessfully
        final XulDialog waitDialog = new XulDialogStub();
        allowing( doc ).getElementById( ConnectionController.ANON_WAIT_DIALOG );
        will( returnValue( waitDialog ) );

        final XulDialog connDialog = context.mock( XulDialog.class );
        allowing( doc ).getElementById( ConnectionController.CONNECTION_DIALOG );
        will( returnValue( connDialog ) );
        one( connDialog ).hide();

        final Schema schema = context.mock( Schema.class );
        one( aSchemaProvider ).loadSchema( "testCube" );
        will( returnValue( schema ) );

        one( model ).getCubeName();
        will( returnValue( "testCube" ) );
        one( model ).setSchema( schema );
        ignoring( model ).setSchemaUpToDate( with( any( Boolean.class ) ) );

        ignoring( outputService );
      }
    } );

    controller.connect();
    //make sure the all the aggdesigner functionality is enabled after a successful connection
    assertTrue( workspace.isApplicationUnlocked() );
  }


   */

  @Test
  public void testConnectErrorDialogDismiss_Visible() {
    final XulDialog dialog = mock( XulDialog.class );
    when( doc.getElementById( ConnectionController.CONNECT_ERROR_DIALOG ) ).thenReturn( dialog );
    when( dialog.isHidden() ).thenReturn( false );

    controller.connectErrorDialogDismiss();

    verify( dialog ).hide();
  }

  @Test
  public void testConnectErrorDialogDismiss_NotVisible() {
    final XulDialog dialog = mock( XulDialog.class );
    when( doc.getElementById( ConnectionController.CONNECT_ERROR_DIALOG ) ).thenReturn( dialog );
    when( dialog.isHidden() ).thenReturn( true );

    controller.connectErrorDialogDismiss();

    verify( dialog, never() ).hide();
  }

  @Test
  public void testShowConnectionDialog() {
    final XulDialog dialog = mock( XulDialog.class );
    when( doc.getElementById( ConnectionController.CONNECTION_DIALOG ) ).thenReturn( dialog );

    controller.showConnectionDialog();

    verify( dialog ).show();
  }

  @Test
  public void testHideConnectionDialog() {
    final XulDialog dialog = mock( XulDialog.class );
    when( doc.getElementById( ConnectionController.CONNECTION_DIALOG ) ).thenReturn( dialog );

    controller.hideConnectionDialog();

    verify( dialog ).hide();
  }

  @Test
  public void testApply_Success() throws XulException, AggDesignerException {
    setupSchemaProviderDefaults();

    final XulDialog waitDialog = new XulDialogStub();
    when( doc.getElementById( ConnectionController.ANON_WAIT_DIALOG ) ).thenReturn( waitDialog );

    controller.apply();

    verify( model ).setCubeNames( cubeNames );
    verify( model ).setSelectedSchemaModel( providerModel );
  }
}