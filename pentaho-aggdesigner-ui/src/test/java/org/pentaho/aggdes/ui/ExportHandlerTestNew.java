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

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.output.CreateScriptGenerator;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.output.PopulateScriptGenerator;
import org.pentaho.aggdes.output.SchemaGenerator;
import org.pentaho.aggdes.ui.exec.SqlExecutor;
import org.pentaho.aggdes.ui.exec.SqlExecutor.ExecutorCallback;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaModel;
import org.pentaho.aggdes.ui.form.controller.ExportHandler;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.AggListImpl;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.components.XulFileDialog;
import org.pentaho.ui.xul.components.XulFileDialog.RETURN_CODE;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.containers.XulWindow;
import org.pentaho.ui.xul.dom.Document;

import java.io.File;

@RunWith( MockitoJUnitRunner.class )
public class ExportHandlerTestNew {

  private static final Log logger = LogFactory.getLog( ExportHandlerTestNew.class );

  @Mock
  private Document doc;

  @Mock
  private XulDomContainer container;

  @Mock
  private OutputService outputService;

  @Mock
  private SqlExecutor executor;

  private ExportHandler controller;

  private AggList aggList;

  private String tmpFilePath;

  @Before
  public void setUp() throws Exception {
    controller = new ExportHandler();
    aggList = new AggListImpl();

    controller.setOutputService( outputService );
    controller.setAggList( aggList );
    controller.setDdlDmlExecutor( executor );

    Mockito.when( container.getDocumentRoot() ).thenReturn( doc );

    controller.setXulDomContainer( container );
  }

  @After
  public void tearDown() throws Exception {
    if ( tmpFilePath != null ) {
      File f = new File( tmpFilePath );
      if ( f.exists() ) {
        f.delete();
      }
    }
  }

  @Test
  public void testOpenDialogNoAggs() throws Exception {
    XulMessageBox msgBox = Mockito.mock( XulMessageBox.class );
    Mockito.when( doc.createElement( "messagebox" ) ).thenReturn( msgBox );

    controller.openDialog();

    Mockito.verify( msgBox ).setMessage( Mockito.anyString() );
    Mockito.verify( msgBox ).setTitle( Mockito.anyString() );
    Mockito.verify( msgBox ).open();
  }

  @Test
  public void testOpenDialogNoEnabledAggs() throws Exception {
    XulMessageBox msgBox = Mockito.mock( XulMessageBox.class );
    Mockito.when( doc.createElement( "messagebox" ) ).thenReturn( msgBox );

    UIAggregate agg = new UIAggregateImpl();
    agg.setEnabled( false );
    aggList.addAgg( agg );

    controller.openDialog();

    Mockito.verify( msgBox ).setMessage( Mockito.anyString() );
    Mockito.verify( msgBox ).setTitle( Mockito.anyString() );
    Mockito.verify( msgBox ).open();
  }

  @Test
  public void testOpenDialogWithAggs() throws Exception {
    XulDialog diag = Mockito.mock( XulDialog.class );
    Mockito.when( doc.getElementById( Mockito.anyString() ) ).thenReturn( diag );

    aggList.addAgg( new UIAggregateImpl() );

    controller.openDialog();

    Mockito.verify( diag ).show();
  }

  @Test
  public void testShowPreviewWithAggs() throws Exception {
    XulDialog diag = Mockito.mock( XulDialog.class );
    XulTextbox textbox = Mockito.mock( XulTextbox.class );

    Mockito.when( doc.getElementById( Mockito.anyString() ) ).thenReturn( diag, textbox, textbox, textbox );

    Mockito.when( outputService.getFullArtifact( Mockito.anyList(), Mockito.eq( CreateScriptGenerator.class ) ) )
      .thenReturn( "DDL_SCRIPT" );
    Mockito.when( outputService.getFullArtifact( Mockito.anyList(), Mockito.eq( PopulateScriptGenerator.class ) ) )
      .thenReturn( "DML_SCRIPT" );
    Mockito.when( outputService.getFullArtifact( Mockito.anyList(), Mockito.eq( SchemaGenerator.class ) ) )
      .thenReturn( "OLAP_SCRIPT" );

    aggList.addAgg( new UIAggregateImpl() );

    controller.showPreview();

    Mockito.verify( diag ).show();
    Mockito.verify( textbox, Mockito.times( 3 ) ).setValue( Mockito.anyString() );
  }

  @Test
  public void testCopyToClipboard() throws Exception {
    XulTextbox textbox = Mockito.mock( XulTextbox.class );
    XulWindow window = Mockito.mock( XulWindow.class );

    Mockito.when( doc.getElementById( Mockito.anyString() ) ).thenReturn( textbox );
    Mockito.when( textbox.getValue() ).thenReturn( "TEST_STRING" );
    Mockito.when( doc.getRootElement() ).thenReturn( window );

    controller.copyDdlToClipboard();
    controller.copyDmlToClipboard();
    controller.copyToClipboardMultiDimPreview();

    Mockito.verify( window, Mockito.times( 3 ) ).copy( Mockito.anyString() );
  }

  @Test
  public void testStartExecuteDdl() throws Exception {
    XulDialog diag = Mockito.mock( XulDialog.class );
    String[] sql = new String[ 0 ];

    Mockito.when( doc.getElementById( Mockito.anyString() ) ).thenReturn( diag );

    controller.startExecuteDdl();

    Mockito.verify( executor ).execute( Mockito.eq( sql ), Mockito.any( ExecutorCallback.class ) );
    Mockito.verify( diag ).show();
  }

  @Test
  public void testStartExecuteDml() throws Exception {
    XulDialog diag = Mockito.mock( XulDialog.class );
    String[] sql = new String[ 0 ];

    Mockito.when( doc.getElementById( Mockito.anyString() ) ).thenReturn( diag );

    controller.startExecuteDml();

    Mockito.verify( executor ).execute( Mockito.eq( sql ), Mockito.any( ExecutorCallback.class ) );
    Mockito.verify( diag ).show();
  }

  @Test
  public void testSaveDdl() throws Exception {
    XulFileDialog fileBox = Mockito.mock( XulFileDialog.class );
    XulDialog diag = Mockito.mock( XulDialog.class );
    File tmpFile = File.createTempFile( "whatever", ".sql" );
    tmpFilePath = tmpFile.getAbsolutePath();

    Mockito.when( doc.createElement( Mockito.anyString() ) ).thenReturn( fileBox );
    Mockito.when( doc.getElementById( Mockito.anyString() ) ).thenReturn( diag );
    Mockito.when( fileBox.showSaveDialog() ).thenReturn( RETURN_CODE.OK );
    Mockito.when( fileBox.getFile() ).thenReturn( tmpFile );

    UIAggregate agg = new UIAggregateImpl();
    agg.setEnabled( false );
    aggList.addAgg( agg );

    controller.saveDdl();

    String fileContent = FileUtils.readFileToString( new File( tmpFilePath ) );
    System.out.println( fileContent );
  }

  @Test
  public void testSaveDml() throws Exception {
    XulFileDialog fileBox = Mockito.mock( XulFileDialog.class );
    XulDialog diag = Mockito.mock( XulDialog.class );
    File tmpFile = File.createTempFile( "whatever", ".sql" );
    tmpFilePath = tmpFile.getAbsolutePath();

    Mockito.when( doc.createElement( Mockito.anyString() ) ).thenReturn( fileBox );
    Mockito.when( doc.getElementById( Mockito.anyString() ) ).thenReturn( diag );
    Mockito.when( fileBox.showSaveDialog() ).thenReturn( RETURN_CODE.OK );
    Mockito.when( fileBox.getFile() ).thenReturn( tmpFile );

    UIAggregate agg = new UIAggregateImpl();
    agg.setEnabled( false );
    aggList.addAgg( agg );

    controller.saveDml();

    String fileContent = FileUtils.readFileToString( new File( tmpFilePath ) );
    System.out.println( fileContent );
  }

  @Test
  public void testSaveOlap() throws Exception {
    XulFileDialog fileBox = Mockito.mock( XulFileDialog.class );
    XulDialog diag = Mockito.mock( XulDialog.class );
    ConnectionModel connModel = Mockito.mock( ConnectionModel.class );
    MondrianFileSchemaModel schemaModel = Mockito.mock( MondrianFileSchemaModel.class );
    File tmpFile = File.createTempFile( "whatever", ".sql" );
    tmpFilePath = tmpFile.getAbsolutePath();

    Mockito.when( doc.createElement( Mockito.anyString() ) ).thenReturn( fileBox );
    Mockito.when( doc.getElementById( Mockito.anyString() ) ).thenReturn( diag );
    Mockito.when( fileBox.showSaveDialog( Mockito.any( File.class ) ) ).thenReturn( RETURN_CODE.OK );
    Mockito.when( fileBox.getFile() ).thenReturn( tmpFile );
    Mockito.when( connModel.getSelectedSchemaModel() ).thenReturn( schemaModel );
    Mockito.when( schemaModel.getMondrianSchemaFilename() ).thenReturn( tmpFile.getAbsolutePath() );
    Mockito.when( outputService.getFullArtifact( Mockito.anyList(), Mockito.eq( SchemaGenerator.class ) ) )
      .thenReturn( "OLAP_SCRIPT" );

    UIAggregate agg = new UIAggregateImpl();
    agg.setEnabled( false );
    aggList.addAgg( agg );

    controller.setConnectionModel( connModel );
    controller.saveOlap();

    String fileContent = FileUtils.readFileToString( new File( tmpFilePath ) );
    System.out.println( fileContent );
  }

}

