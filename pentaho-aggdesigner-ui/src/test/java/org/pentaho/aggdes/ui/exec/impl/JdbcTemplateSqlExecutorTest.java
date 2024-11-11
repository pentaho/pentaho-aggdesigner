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


package org.pentaho.aggdes.ui.exec.impl;


import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.ui.exec.SqlExecutor.ExecutorCallback;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModelImpl;
import org.pentaho.di.core.database.DatabaseMeta;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@RunWith( MockitoJUnitRunner.class )
public class JdbcTemplateSqlExecutorTest extends TestCase {

  private static final Log logger = LogFactory.getLog( JdbcTemplateSqlExecutorTest.class );
  private boolean executionCompleteCalled;
  private ConnectionModel connectionModel = new ConnectionModelImpl();
  @Mock
  private DatabaseMeta dbMeta;

  private final JdbcTemplateSqlExecutor exec = new JdbcTemplateSqlExecutor();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks( this );
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testExecute() throws Exception {
    // set up
    Mockito.when( dbMeta.getName() ).thenReturn( "" );
    Mockito.when( dbMeta.getXML() ).thenReturn( null );
    Mockito.when( dbMeta.getURL() ).thenReturn( "jdbc:hsqldb:mem:test" );
    Mockito.when( dbMeta.getUsername() ).thenReturn( "sa" );
    Mockito.when( dbMeta.getPassword() ).thenReturn( "" );
    Mockito.when( dbMeta.getDriverClass() ).thenReturn( "org.hsqldb.jdbcDriver" );

    SchemaStub schemaStub = new SchemaStub();
    schemaStub.setDialect( new DialectStub() );
    connectionModel.setSchema( schemaStub );

    exec.setConnectionModel( connectionModel );
    connectionModel.setDatabaseMeta( dbMeta );
    executionCompleteCalled = false;
    exec.execute( new String[] { "", "" }, new ExecutorCallback() {
      public void executionComplete( Exception e ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "execution complete" );
        }
        executionCompleteCalled = true;
      }
    } );
    assertTrue( "Execution complete not called.", executionCompleteCalled );
  }

  @Test
  public void testSqlCommentRemoval() {
    SchemaStub schemaStub = new SchemaStub();
    schemaStub.setDialect( new DialectStub() );
    StringBuilder sb = new StringBuilder();
    schemaStub.getDialect().comment( sb, " my comment" );
    // sb.append();
    sb.append( "SELECT * FROM TBL;" );

    String sqlresults = exec.removeCommentsAndSemicolons( schemaStub, sb.toString() );

    assertEquals( sqlresults, "SELECT * FROM TBL" );

    String[] str = new String[] { sb.toString() };

    String[] results = exec.removeCommentsAndSemicolons( schemaStub, str );
    assertEquals( results.length, 1 );
    assertEquals( results[ 0 ], "SELECT * FROM TBL" );
  }

  public ConnectionModel getConnectionModel() {

    return connectionModel;
  }

  public void setConnectionModel( ConnectionModel connectionModel ) {

    this.connectionModel = connectionModel;
  }

  static class DialectStub implements Dialect {
    @Override public void quoteIdentifier( StringBuilder buf, String... names ) {

    }

    @Override public String getIntegerTypeString() {
      return null;
    }

    @Override public String getDoubleTypeString() {
      return null;
    }

    @Override public String removeInvalidIdentifierCharacters( String str ) {
      return null;
    }

    @Override public int getMaximumTableNameLength() {
      return 0;
    }

    @Override public int getMaximumColumnNameLength() {
      return 0;
    }

    public void comment( StringBuilder buf, String s ) {
      buf.append( "-- " + s + System.getProperty( "line.separator" ) );
    }

    @Override public void terminateCommand( StringBuilder buf ) {

    }

    @Override public boolean supportsPrecision( DatabaseMetaData meta, String type ) throws SQLException {
      return false;
    }


  }

}
