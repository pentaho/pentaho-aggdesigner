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

package org.pentaho.aggdes.ui.exec.impl;

import static org.junit.Assert.assertTrue;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.test.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.ui.exec.SqlExecutor.ExecutorCallback;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModelImpl;
import org.pentaho.di.core.database.DatabaseMeta;

/**
 * Tests template execution.
 *
 * <p>hsqldb.jar must be on classpath for test to succeed.
 */
@RunWith(JMock.class)
public class JdbcTemplateSqlExecutorTest extends TestCase {

  private boolean executionCompleteCalled;

  private ConnectionModel connectionModel = new ConnectionModelImpl();

  private static final Log logger = LogFactory.getLog(JdbcTemplateSqlExecutorTest.class);

  private Mockery context = new JUnit4Mockery() {
    {
      // necessary to mock non-interfaces (e.g. DatabaseMeta)
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private JdbcTemplateSqlExecutor exec = new JdbcTemplateSqlExecutor();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testExecute() throws Exception {
    // set up
    final DatabaseMeta dbMeta = context.mock(DatabaseMeta.class);
    context.checking(new Expectations() {
      {
        one(dbMeta).getName();
        will(returnValue(""));
        one(dbMeta).getXML();
        will(returnValue(null));
        allowing(dbMeta).getURL();
        will(returnValue("jdbc:hsqldb:mem:test"));
        one(dbMeta).getUsername();
        will(returnValue("sa"));
        one(dbMeta).getPassword();
        will(returnValue(""));
        one(dbMeta).getDriverClass();
        will(returnValue("org.hsqldb.jdbcDriver"));
      }
    });

    SchemaStub schemaStub = new SchemaStub();
    schemaStub.setDialect(new DialectStub());
    connectionModel.setSchema(schemaStub);

    exec.setConnectionModel(connectionModel);
    getConnectionModel().setDatabaseMeta(dbMeta);
    executionCompleteCalled = false;
    exec.execute(new String[]{"", ""}, new ExecutorCallback() {

      public void executionComplete(Exception e) {
        if (logger.isDebugEnabled()) {
          logger.debug("execution complete");
        }
        executionCompleteCalled = true;
      }

    });
    assertTrue("Execution complete not called.", executionCompleteCalled);
  }

  static class DialectStub implements Dialect {

    public void comment(StringBuilder buf, String s) {
      buf.append("-- " + s + System.getProperty("line.separator"));
    }

    public String getDoubleTypeString() {
      // TODO Auto-generated method stub
      return null;
    }

    public String getIntegerTypeString() {
      // TODO Auto-generated method stub
      return null;
    }

    public int getMaximumColumnNameLength() {
      // TODO Auto-generated method stub
      return 0;
    }

    public int getMaximumTableNameLength() {
      // TODO Auto-generated method stub
      return 0;
    }

    public void quoteIdentifier(StringBuilder buf, String... names) {
      // TODO Auto-generated method stub

    }

    public String removeInvalidIdentifierCharacters(String str) {
      // TODO Auto-generated method stub
      return null;
    }

    public boolean supportsPrecision(DatabaseMetaData meta, String type) throws SQLException {
      // TODO Auto-generated method stub
      return false;
    }

    public void terminateCommand(StringBuilder buf) {
      // TODO Auto-generated method stub

    }

  }


  @Test
  public void testSqlCommentRemoval() {
    SchemaStub schemaStub = new SchemaStub();
    schemaStub.setDialect(new DialectStub());
    StringBuilder sb = new StringBuilder();
    schemaStub.getDialect().comment(sb, " my comment");
    // sb.append();
    sb.append("SELECT * FROM TBL;");

    String sqlresults = exec.removeCommentsAndSemicolons(schemaStub, sb.toString());

    assertEquals(sqlresults, "SELECT * FROM TBL");

    String str[] = new String[] {sb.toString()};

    String results[] = exec.removeCommentsAndSemicolons(schemaStub, str);
    assertEquals(results.length, 1);
    assertEquals(results[0], "SELECT * FROM TBL");
  }

  public ConnectionModel getConnectionModel() {

    return connectionModel;
  }

  public void setConnectionModel(ConnectionModel connectionModel) {

    this.connectionModel = connectionModel;
  }

}
